package com.zn.face;

/********************************************
 @author zn
 time:2017.03.16
 ×¢²á£¬ÆÕÍ¨µÇÂ¼£¬ÈËÁ³µÇÂ¼Ñ¡ÔñÒ³Ãæ
 *********************************************/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zn.unlock.R;

public class AppMainActivity extends Activity {
	private Button register,pwdLogin,faceLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		register=(Button)findViewById(R.id.register);
		pwdLogin=(Button)findViewById(R.id.pwdLogin);
		faceLogin=(Button)findViewById(R.id.faceLogin);
		register.setOnClickListener(new RegisterListener());
		pwdLogin.setOnClickListener(new PwdLoginListener());
		faceLogin.setOnClickListener(new FaceLoginListener());
		
	}
  public class RegisterListener implements OnClickListener{

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.setClass(AppMainActivity.this, RegisterActivity.class);
		startActivity(intent);
	}
	  
  }
  
  public class PwdLoginListener implements OnClickListener{

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.setClass(AppMainActivity.this, PwdLoginActivity.class);
		startActivity(intent);
	}
	  
  }
  
  public class FaceLoginListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(AppMainActivity.this, FaceLoginActivity.class);
			startActivity(intent);
		}
		  
	  }
  
	
}
