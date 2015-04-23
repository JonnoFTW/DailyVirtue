package au.com.virtueoftheday.virtuedaily;

import java.util.Calendar;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import au.com.virtueoftheday.virtuedaily.database.VirtueDatabase;
import au.com.virtueoftheday.virtuedaily.database.VirtueDatabase.Virtue;

public class Alarm extends BroadcastReceiver {

	public static final String NOTIFY_HOUR = "notify_hour";
	public static final String NOTIFY_MINUTE = "notify_minute";
	private static final String TAG = Alarm.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();
         
         Virtue virtue = (new VirtueDatabase(context)).getTodaysVirtue();
         
         // should probably get it from tinysql thingo
         // mark it as read
         // try to fetch them from online
         Intent i = new Intent(context, VirtueDialog.class);
         i.putExtra(VirtueDatabase.COL_TITLE, virtue.title);
         i.putExtra(VirtueDatabase.COL_DESCRIPTION, virtue.description);
         PendingIntent pi =PendingIntent.getActivity(context,0, i, PendingIntent.FLAG_UPDATE_CURRENT );
         NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).
        		 setSmallIcon(R.drawable.ic_launcher)
        		 .setContentTitle(virtue.title)
        		 .setContentText(virtue.description)
        		 .setContentIntent(pi)
        		 .setOngoing(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(0,mBuilder.build());
        Log.i(TAG,"Showing notification");
        wl.release();
	}
	public void cancelAlarm(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
		Intent i = new Intent(context,Alarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public void setAlarm(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i= new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);	
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		int hour = settings.getInt(NOTIFY_HOUR, 9);
		int minute = settings.getInt(NOTIFY_MINUTE, 0);
		//should probably get this from the settings
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
				
	}

}
