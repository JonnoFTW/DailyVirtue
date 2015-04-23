package au.com.virtueoftheday.virtuedaily.database;

import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;



import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VirtueDatabase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_NAME = "virtues";
	private static final String TABLE_NAME = "virtues";
	private static final String COL_ID = "_id";
	public static final String COL_TITLE = "title";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_SEEN = "seen";
	private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + TABLE_NAME;
	private static final String TAG = VirtueDatabase.class.getSimpleName();
	private Context mContext;
	public VirtueDatabase(Context context) {
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + "("
				+ COL_ID+ " INTEGER PRIMARY KEY," 
				+ COL_TITLE+ " TEXT NOT NULL DEFAULT '',"
				+ COL_DESCRIPTION	+ " TEXT NOT NULL DEFAULT ''," 
				+ COL_SEEN	+ " INT DEFAULT 0)");
		Log.i(TAG,"Creating database");
		
		try {
			Scanner in = new Scanner(mContext.getAssets().open("virtues.dat"));
			while(in.hasNext()) {
				ContentValues cv = new ContentValues();
				cv.put(COL_TITLE, in.nextLine());
				cv.put(COL_DESCRIPTION, in.nextLine());
				db.insert(TABLE_NAME, null, cv);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG,"Upgrading database");
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	public class Virtue {
		public String title,description;
		public Virtue(Cursor c) {
			title = c.getString(1);
			description = c.getString(2);
		}
	}

	private void resetSeen(int id) {
		final SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		if(id!=-1) { 
			Calendar c = Calendar.getInstance(TimeZone.getDefault());
			//cv.put(COL_SEEN, "date('now','localtime')");
			String sql = String.format("UPDATE %s SET %s = date('now','localtime') WHERE %s = %d", 
					TABLE_NAME,COL_SEEN,COL_ID,id); 
			db.execSQL(sql);
			//int u = db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{Integer.toString(id)});
			Log.i(TAG,"Executing: "+sql);
		} else {
			cv.put(COL_SEEN, 0);
			int u = db.update(TABLE_NAME, cv, null, null);
			Log.i(TAG,"Setting as unseen:"+u);
		}
		
	}

	private static final String[] SELECT_COLS = new String[]{COL_ID,COL_TITLE,COL_DESCRIPTION,COL_SEEN};
	private Cursor virtueQuery(SQLiteDatabase db) {
		return db.query(TABLE_NAME,
				SELECT_COLS,
				COL_SEEN +" == ? ",
				new String[]{"0"},
				null,
				null, 
				"RANDOM()",
				"1");
	
	}
	public Virtue getTodaysVirtue() {
		final SQLiteDatabase db = getReadableDatabase();
		Cursor todays = db.query(TABLE_NAME, 
				SELECT_COLS,
				"date(datetime("+COL_SEEN + "/ 1000 ,'unixepoch', 'localtime')) = date('now','localtime')", 
				null,
				null, 
				null,
				null);
		if(todays.getCount()==0) {
			Log.i(TAG,"Getting next virtue");
			return getNextVirtue();
		}
		todays.moveToFirst();
		Log.i(TAG,"Getting today's virtue:"+todays.getLong(3));
		
		return new Virtue(todays);
	}
	private Virtue getNextVirtue() {
		final SQLiteDatabase db = getReadableDatabase();
		Cursor c = virtueQuery(db);
		Log.i(TAG,"Query returned "+c.getCount());
		if (c.getCount()==0) {
			resetSeen(-1);
			c = virtueQuery(db);
		}
		c.moveToFirst();
		resetSeen(c.getInt(0));
		return new Virtue(c);
	}

}
