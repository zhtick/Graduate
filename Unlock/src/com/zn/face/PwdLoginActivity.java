package com.zn.face;

/********************************************
 @author zn
 time:2017.03.16
账号密码登录界面
 *********************************************/


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zn.dao.InfoDao;
import com.zn.dao.InfoDaoImpl;
import com.zn.unlock.R;

public class PwdLoginActivity extends Activity{
	private Button login;
	private EditText username,password;
	private TextView register;
	String name,passwd;
	InfoDao dao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pwdlogin);
		login=(Button)findViewById(R.id.buttonLogin);
		username=(EditText)findViewById(R.id.username1);
		password=(EditText)findViewById(R.id.password1);
		register=(TextView)findViewById(R.id.register_link);
		dao=new InfoDaoImpl(this);
		login.setOnClickListener(new LoginListener());
		register.setOnClickListener(new RegisterListener());
	}
	public class RegisterListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(PwdLoginActivity.this, RegisterActivity.class);
			startActivity(intent);
		}
		
	}
	public class LoginListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			name=username.getText().toString();
			passwd=password.getText().toString();
			if(name==null || passwd==null || "".equals(name) || "".equals(passwd)){
				Toast.makeText(PwdLoginActivity.this, "请输入完整的信息",Toast.LENGTH_LONG).show();
			}
			else{
			String result=dao.login(name, passwd);
            
			if("True".equals(result)){
				Intent intent=new Intent();
				intent.setClass(PwdLoginActivity.this, SuccessActivity.class);
				intent.putExtra("name", name);
				startActivity(intent);
			}
			else{
				Toast.makeText(PwdLoginActivity.this, "用户名或密码错误",Toast.LENGTH_LONG).show();
			}
			}
		}
		
		
	}
	

}
