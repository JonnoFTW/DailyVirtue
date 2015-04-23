package au.com.virtueoftheday.virtuedaily;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import au.com.virtueoftheday.virtuedaily.database.VirtueDatabase;
import au.com.virtueoftheday.virtuedaily.database.VirtueDatabase.Virtue;

public class MainActivity extends Activity {
	private final Alarm alarm = new Alarm();
	private SharedPreferences sharedPrefs;
	private static final String TAG = MainActivity.class.getSimpleName();
	private Context context;
	private static final String FIRSTRUN = "FIRSTRUN";
	
	private void setNotificationTime() {
		int hour = sharedPrefs.getInt(Alarm.NOTIFY_HOUR, 9);
		int minute = sharedPrefs.getInt(Alarm.NOTIFY_MINUTE, 0);
		TimePickerDialog timePicker =new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putInt(Alarm.NOTIFY_HOUR, hourOfDay);
				editor.putInt(Alarm.NOTIFY_MINUTE, minute);
				editor.commit();
				alarm.cancelAlarm(context);
				alarm.setAlarm(context);
				Toast.makeText(context, String.format("Notification set for %02d:%02d",hourOfDay,minute), Toast.LENGTH_LONG).show();
			}
		}, hour, minute, true);
		
		
		timePicker.setTitle("Notification Time");
		timePicker.show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(TAG,getLocalClassName());
		context = this;
		//sharedPrefs = getPreferences(Context.MODE_PRIVATE);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean firstRun = sharedPrefs.getBoolean(FIRSTRUN, true);
		if(firstRun) {
			setNotificationTime();
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.putBoolean(FIRSTRUN, false);
			editor.apply();
		}
		
		final VirtueDatabase vdb = new VirtueDatabase(context);
		
		TextView textTitle = (TextView) findViewById(R.id.textViewTitle);
		TextView textDescription = (TextView) findViewById(R.id.textViewDescription);
		Virtue v = vdb.getTodaysVirtue();
		textTitle.setText(v.title);
		textDescription.setText(v.description);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_about);
			builder.setTitle(R.string.dialog_about_title);
			builder.setPositiveButton(R.string.dialog_ok, null);
			builder.create().show();
			return true;
		} else if (id== R.id.action_setTime) {
			setNotificationTime();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
