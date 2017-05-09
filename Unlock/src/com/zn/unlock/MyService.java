package com.zn.unlock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * ����Service�����ڼ�����Ļ������䰵�¼�
 * @author tongleer.com
 *
 */
public class MyService extends Service{
    private ScreenOnReceiver screenOnReceiver;
    private ScreenOffReceiver screenOffReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        screenOnReceiver=new ScreenOnReceiver();
        IntentFilter screenOnFilter=new IntentFilter();
        screenOnFilter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(screenOnReceiver, screenOnFilter);
        screenOffReceiver=new ScreenOffReceiver();
        IntentFilter screenOffFilter=new IntentFilter();
        screenOffFilter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(screenOffReceiver, screenOffFilter);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
    /**
     * ������Ļ�����Ĺ㲥������������������ϵͳ����
     * @author tongleer.com
     *
     */
    private class ScreenOnReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals("android.intent.action.SCREEN_ON")){
                /*
                 * �˷�ʽ�Ѿ���ʱ����activtiy�б�д
                 * getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                 * getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                 * ������Դ���˷�ʽ
                 */
                /*KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardLock lock = keyguardManager.newKeyguardLock(����);
                lock.disableKeyguard();*/
            }
        }
    }
    /**
     * ������Ļ�䰵�Ĺ㲥���������䰵������Ӧ����������activity
     * @author tongleer.com
     *
     */
    private class ScreenOffReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals("android.intent.action.SCREEN_OFF")){
                Intent i1=new Intent(context,FaceUnLockActivity.class);
                i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i1);
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenOnReceiver);
        unregisterReceiver(screenOffReceiver);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}