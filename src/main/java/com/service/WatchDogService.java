package com.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.db.dao.AppLockSQLiteDao;
import com.mobilesafe.AppLockEnterPwdActivity;

import java.util.List;

public class WatchDogService extends Service {

    private boolean flag  = true;
    private ActivityManager am  =null;
    private AppLockSQLiteDao dao = null;
    private String tempStopGuard=null;
    private ScreenOffReceiver offReceiver = null;
    private List<String> protectPacknames;
    private MyReceiver receiver = null;
    private DataChangeReceiver dataChangeReceiver = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new MyReceiver();
        offReceiver = new ScreenOffReceiver();
        dataChangeReceiver = new DataChangeReceiver();
        dao = new AppLockSQLiteDao(WatchDogService.this);
        protectPacknames=dao.findAll();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        registerReceiver(receiver ,new IntentFilter("com.mobilesafe.watchdog"));
        registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(dataChangeReceiver,new IntentFilter("com.mobilesafe.applockchange"));
        //�����̲߳��ϼ���û���ǰʹ�õ�Ӧ���Ƿ���Ҫ����
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag){
                    List<ActivityManager.RunningTaskInfo> taskList= am.getRunningTasks(100);
                    //�����һ������ջ
                    String packName  = taskList.get(0).topActivity.getPackageName();
                    //��ѯ���ݿ�
                    if(protectPacknames.contains(packName)){//��ѯ�ڴ�Ч�ʸߺܶ�
                        // �ж����Ӧ�ó����Ƿ���Ҫ��ʱ��ֹͣ������
                        if (packName.equals(tempStopGuard)) {

                        } else {
                            // ��ǰӦ����Ҫ�������ĳ�����������һ����������Ľ��档
                            // ����Ҫ��������İ���
                            Intent intent = new Intent(WatchDogService.this,AppLockEnterPwdActivity.class);
                            intent.putExtra("packName", packName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(50);   //����50����
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        unregisterReceiver(offReceiver);
        unregisterReceiver(dataChangeReceiver);
        dataChangeReceiver  = null;
        receiver = null;
        offReceiver= null;
    }

    /**
     * �㲥������ ����������������ȷ
     */
    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopGuard = intent.getStringExtra("packName"); //��ʱֹͣ�Ը�Ӧ�õı���
        }
    }
    /**
     * ���������㲥
     */
    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopGuard= null;
        }
    }

    private class DataChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //System.out.println("���ݿ�����ݱ仯�ˡ�����");
            protectPacknames = dao.findAll();
        }
    }
}
