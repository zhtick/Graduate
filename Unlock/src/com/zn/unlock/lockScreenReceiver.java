package com.zn.unlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class lockScreenReceiver extends BroadcastReceiver  {
	 public static boolean wasScreenOn = true;

	@Override
	public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

        	wasScreenOn=false;
        	Intent intent11 = new Intent(context,FaceUnLockActivity.class);
        	intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        	context.startActivity(intent11);

            // do whatever you need to do here
            //wasScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

        	wasScreenOn=true;
        	Intent intent11 = new Intent(context,FaceUnLockActivity.class);
        	intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	context.startActivity(intent11);
            // and do whatever you need to do here
           // wasScreenOn = true;
        }
       else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
        	Intent intent11 = new Intent(context, FaceUnLockActivity.class);

        	intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent11);

       }

    }


}
