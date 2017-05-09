//开机广告接收器BootReceiver
package com.zn.unlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		arg0.startService(new Intent(arg0,MyService.class));
	}

}
