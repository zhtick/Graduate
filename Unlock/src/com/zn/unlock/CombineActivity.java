package com.zn.unlock;

import com.zn.face.AppMainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CombineActivity extends Activity{
	private Button phone_lock,app_lock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combine);
		phone_lock=(Button)findViewById(R.id.btn_phonelock);
		app_lock=(Button)findViewById(R.id.btn_applock);
		phone_lock.setOnClickListener(new ButtonListener());
		app_lock.setOnClickListener(new ButtonListener());
	}
	public class ButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent =new Intent();
			if(arg0==phone_lock){
				intent.setClass(CombineActivity.this, MainActivity.class);
				startActivity(intent);
			}
			if(arg0==app_lock){
				intent.setClass(CombineActivity.this, AppMainActivity.class);
				startActivity(intent);
			}
		}
		
	}

}
