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
        //开启线程不断检查用户当前使用的应用是否需要保护
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag){
                    List<ActivityManager.RunningTaskInfo> taskList= am.getRunningTasks(100);
                    //最近的一个任务栈
                    String packName  = taskList.get(0).topActivity.getPackageName();
                    //查询数据库
                    if(protectPacknames.contains(packName)){//查询内存效率高很多
                        // 判断这个应用程序是否需要临时的停止保护。
                        if (packName.equals(tempStopGuard)) {

                        } else {
                            // 当前应用需要保护。蹦出来，弹出来一个输入密码的界面。
                            // 设置要保护程序的包名
                            Intent intent = new Intent(WatchDogService.this,AppLockEnterPwdActivity.class);
                            intent.putExtra("packName", packName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(50);   //休眠50毫秒
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
     * 广播接受者 程序锁密码输入正确
     */
    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopGuard = intent.getStringExtra("packName"); //暂时停止对该应用的保护
        }
    }
    /**
     * 接收锁屏广播
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
            //System.out.println("数据库的内容变化了。。。");
            protectPacknames = dao.findAll();
        }
    }
}
