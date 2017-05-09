package com.zn.unlock;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public  class MainActivity extends Activity{
	private Button btn_lock,btn_input,btn_digital;
	private String photoPath="/sdcard/Unlock/face";
	private String pwdPath="/sdcard/Unlock/passwd.txt";
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
	 * @return true代表正在运行，false代表服务没有正在运行
	 */
	public boolean isServiceWork(Context mContext, String serviceName) {
		serviceName="com.zn.unlock.LockService";
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				System.out.println("yyyyyyyyyyyyyyyyyyyyy");
				isWork = true;
				break;
			}
		}
		return isWork;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		  if (isServiceWork(this, "com.Zhao.facerecognizelock.LockService"))
		      this.btn_lock.setBackgroundResource(R.drawable.btn_lock_on);
		  else{
		      btn_lock.setBackgroundResource(R.drawable.btn_lock_off);
		  }
		  File file1=new File("/sdcard/Unlock/face");
		  File file2=new File("/sdcard/Unlock/passwd.txt");
		  if(file1.exists() && file1.listFiles().length==3){
				btn_input.setEnabled(false);
				
				btn_input.setBackgroundResource(R.drawable.btn_input_p);
		  }
		  if(file2.exists()){
				btn_digital.setEnabled(false);
				
				btn_digital.setBackgroundResource(R.drawable.btn_digital_p);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn_lock=(Button)findViewById(R.id.btn_lock);
		btn_input=(Button)findViewById(R.id.btn_input);
		btn_digital=(Button)findViewById(R.id.btn_digital);
		btn_lock.setOnClickListener(new ButtonListener());
		btn_input.setOnClickListener(new ButtonListener());
		btn_digital.setOnClickListener(new ButtonListener());
		this.mNotificationManager = ((NotificationManager)getSystemService("notification"));
	    NotificationCompat.Builder localBuilder = new NotificationCompat.Builder(this);
	    PendingIntent localPendingIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);
	    localBuilder.setSmallIcon(R.drawable.ic_launcher).setTicker("启动保护").setContentTitle("Unlock正在保护您的屏幕").setContentIntent(localPendingIntent);
	    this.mNotification = localBuilder.build();
	    this.mNotification.flags = 2;
	    this.mNotification.defaults = 2;
	    this.mNotification.when = System.currentTimeMillis();
		File file=new File("/sdcard/Unlock");
		file.mkdir();
		File file1=new File("/sdcard/Unlock/face");
		File file2=new File("/sdcard/Unlock/passwd.txt");
		if(file1.exists() && file1.listFiles().length==3){
			btn_input.setEnabled(false);
			
			btn_input.setBackgroundResource(R.drawable.btn_input_p);
		}
		if(file2.exists()){
			btn_digital.setEnabled(false);
			
			btn_digital.setBackgroundResource(R.drawable.btn_digital_p);
		}
	}
	
	class ButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0==btn_lock){
				//先判断照片是否录入，数字密码是否设置
				if(Util.isFileExist(photoPath, pwdPath)){
					boolean value=isServiceWork(MainActivity.this, "com.zn.unlock.LockService");
					System.out.println(value);
					if(isServiceWork(MainActivity.this, "com.zn.unlock.LockService")){//停止服务
						System.out.println("进入停止服务");
						btn_lock.setBackgroundResource(R.drawable.btn_lock_off);
						stopService(new Intent(MainActivity.this,LockService.class));
						Util.deleteFile(new File(photoPath));
						Util.deleteFile(new File(pwdPath));
						btn_digital.setEnabled(true);						
						btn_digital.setBackgroundResource(R.drawable.btn_digital);
						btn_input.setEnabled(true);						
						btn_input.setBackgroundResource(R.drawable.btn_input);
						mNotificationManager.cancel(100);
					}
					else{//启动服务
						System.out.println("进入启动服务");
						btn_lock.setBackgroundResource(R.drawable.btn_lock_on);
						startService(new Intent(MainActivity.this,LockService.class));					
						mNotificationManager.notify(100,mNotification);
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "请先录入人脸和设置数字密码！", 0).show();
				}
			}
			else if(arg0==btn_input){
					Intent intent =new Intent();
					intent.setClass(MainActivity.this, FaceEntryActivity.class);
					startActivity(intent);
			}
			else{
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, PwdInputActivity.class);
				startActivity(intent);
			}
			}
		}
		
		
	}
	
	
