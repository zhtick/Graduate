package com.zn.unlock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PwdInputActivity extends Activity
 
{
  private String dir=Environment.getExternalStorageDirectory() + "/Unlock";
  private static final String Passwd = Environment.getExternalStorageDirectory() + "/Unlock/passwd.txt";
  private int cnt = 0;
  private Button mBtn_num_0;
  private Button mBtn_num_1;
  private Button mBtn_num_2;
  private Button mBtn_num_3;
  private Button mBtn_num_4;
  private Button mBtn_num_5;
  private Button mBtn_num_6;
  private Button mBtn_num_7;
  private Button mBtn_num_8;
  private Button mBtn_num_9;
  private Button mBtn_num_clear;
  private Button mBtn_num_exit;
  private ImageView mImgView_passwd_show;
  private TextView mText_passwd_tip;
  private String passwd1;
  private String passwd2;
  private StringBuffer passwdBuffer =new StringBuffer();
  private int count = 0;//点击数字键的次数

  
  @Override
protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    getWindow().setFlags(1024, 1024);
    setContentView(R.layout.activity_digital_passwd);
    mBtn_num_0 = (Button)findViewById(R.id.btn_num_0);
    mBtn_num_1 = (Button)findViewById(R.id.btn_num_1);
    mBtn_num_2 = (Button)findViewById(R.id.btn_num_2);
    mBtn_num_3 = (Button)findViewById(R.id.btn_num_3);
    mBtn_num_4 = (Button)findViewById(R.id.btn_num_4);
    mBtn_num_5 = (Button)findViewById(R.id.btn_num_5);
    mBtn_num_6 = (Button)findViewById(R.id.btn_num_6);
    mBtn_num_7 = (Button)findViewById(R.id.btn_num_7);
    mBtn_num_8 = (Button)findViewById(R.id.btn_num_8);
    mBtn_num_9 = (Button)findViewById(R.id.btn_num_9);
    mBtn_num_clear = (Button)findViewById(R.id.btn_num_clear);
    mBtn_num_exit = (Button)findViewById(R.id.btn_num_exit);
    mImgView_passwd_show = (ImageView)findViewById(R.id.passwd_show);
    mText_passwd_tip = (TextView)findViewById(R.id.text_passwd_tip);
    mText_passwd_tip.setText("请输入密码");
    mBtn_num_0.setOnClickListener(new NumButtonListener());
    mBtn_num_1.setOnClickListener(new NumButtonListener());
    mBtn_num_2.setOnClickListener(new NumButtonListener());
    mBtn_num_3.setOnClickListener(new NumButtonListener());
    mBtn_num_4.setOnClickListener(new NumButtonListener());
    mBtn_num_5.setOnClickListener(new NumButtonListener());
    mBtn_num_6.setOnClickListener(new NumButtonListener());
    mBtn_num_7.setOnClickListener(new NumButtonListener());
    mBtn_num_8.setOnClickListener(new NumButtonListener());
    mBtn_num_9.setOnClickListener(new NumButtonListener());
    mBtn_num_clear.setOnClickListener(new FunButtonListener());
    mBtn_num_exit.setOnClickListener(new FunButtonListener());
  }
  public class FunButtonListener implements OnClickListener{

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0 == mBtn_num_clear){
			passwdBuffer=null;
			count=0;
			mImgView_passwd_show.setImageResource(R.drawable.passwd0);
		}
		if(arg0==mBtn_num_exit){
			PwdInputActivity.this.finish();
		}
	}
	  
  }
  public class NumButtonListener implements OnClickListener{

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0 == mBtn_num_0){
			passwdBuffer.append("0");
			count++;
		}
		if(arg0 == mBtn_num_1){
			passwdBuffer.append("1");
			count++;
		}
		if(arg0 == mBtn_num_2){
			passwdBuffer.append("2");
			count++;
		}
		if(arg0 == mBtn_num_3){
			passwdBuffer.append("3");
			count++;
		}
		if(arg0 == mBtn_num_4){
			passwdBuffer.append("4");
			count++;
		}
		if(arg0 == mBtn_num_5){
			passwdBuffer.append("5");
			count++;
		}
		if(arg0 == mBtn_num_6){
			passwdBuffer.append("6");
			count++;
		}
		if(arg0 == mBtn_num_7){
			passwdBuffer.append("7");
			count++;
		}
		if(arg0 == mBtn_num_8){
			passwdBuffer.append("8");
			count++;
		}
		if(arg0 == mBtn_num_9){
			passwdBuffer.append("9");
			count++;
		}
		if(count==1){
			mImgView_passwd_show.setImageResource(R.drawable.passwd1);
		}
		if(count==2){
			mImgView_passwd_show.setImageResource(R.drawable.passwd2);
		}
		if(count==3){
			mImgView_passwd_show.setImageResource(R.drawable.passwd3);
		}
		if(count==4){
			mImgView_passwd_show.setImageResource(R.drawable.passwd4);
			File file=new File(dir);
			
			if(!file.exists())
				file.mkdir();
			File file2=new File(Passwd);
			if(!file2.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
			String str=passwdBuffer.toString();
			PrintStream ps;
			try {
				ps = new PrintStream(new FileOutputStream(file2));
				ps.println(str);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PwdInputActivity.this.finish();
           
		}
	}
	  
  }
}