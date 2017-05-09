package com.zn.face;

import java.util.Timer;
import java.util.TimerTask;

import com.zn.unlock.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		final Intent it = new Intent(WelcomeActivity.this, AppMainActivity.class); //你要转向的Activity   
		Timer timer = new Timer(); 
		TimerTask task = new TimerTask() {  
		    @Override  
		    public void run() {   
		    startActivity(it); //执行  
		     } 
		 };
		timer.schedule(task, 1000 * 3); //10秒后
	}
	
}
