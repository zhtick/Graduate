/********************************************
 @author zn
 µÇÂ½Ê§°ÜÒ³Ãæ                                 2017.4.2
 *********************************************/
package com.zn.face;

import com.zn.unlock.R;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FailActivity extends Activity{
	private Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginfail);
		button=(Button)findViewById(R.id.try_again);
		button.setOnClickListener(new TryListener());
	}
	public class TryListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent =new Intent();
			intent.setClass(FailActivity.this,FaceLoginActivity.class);
			startActivity(intent);
		}

	
		
	}

}
