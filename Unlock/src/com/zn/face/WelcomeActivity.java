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
		final Intent it = new Intent(WelcomeActivity.this, AppMainActivity.class); //��Ҫת���Activity   
		Timer timer = new Timer(); 
		TimerTask task = new TimerTask() {  
		    @Override  
		    public void run() {   
		    startActivity(it); //ִ��  
		     } 
		 };
		timer.schedule(task, 1000 * 3); //10���
	}
	
}
