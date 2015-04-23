package au.com.virtueoftheday.virtuedaily;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import au.com.virtueoftheday.virtuedaily.database.VirtueDatabase;

public class VirtueDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context context = this;;
		Bundle extras = getIntent().getExtras();
		new AlertDialog.Builder(this)
			.setTitle(extras.getString(VirtueDatabase.COL_TITLE))
			.setMessage(extras.getString(VirtueDatabase.COL_DESCRIPTION))
			.setNegativeButton(R.string.dialog_remove, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			        mNotificationManager.cancelAll();
					dialog.dismiss();
					finish();
				}
			})
			.setPositiveButton(R.string.dialog_ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		}).create().show();
		
	
	}
}
