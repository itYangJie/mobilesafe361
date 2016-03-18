package com.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.engine.ProcessManger;
import com.mobilesafe.R;
import com.receiver.MyWidget;
import com.utils.ProcessMangerUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WidgetService extends Service {
    private AppWidgetManager awm = null;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private ScreenOffReceiver offReceiver = null;
    private ScreenOnReceiver onReceiver = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        onReceiver = new ScreenOnReceiver();
        offReceiver = new ScreenOffReceiver();
        registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        awm = AppWidgetManager.getInstance(this);
        startTime();
        super.onCreate();
    }

    private void startTime() {
        if (timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    //更新widget
                    ComponentName componentName = new ComponentName(WidgetService.this, MyWidget.class);
                    RemoteViews views = new RemoteViews(getPackageName(),
                            R.layout.process_widget);
                    views.setTextViewText(
                            R.id.process_count,
                            "正在运行的进程:"
                                    + ProcessMangerUtil
                                    .getRunningProcessCount(getApplicationContext())
                                    + "个");
                    long size = ProcessMangerUtil
                            .getAvailMem(getApplicationContext());
                    views.setTextViewText(
                            R.id.process_memory,
                            "可用内存:"
                                    + Formatter.formatFileSize(
                                    getApplicationContext(), size));
                    Intent intent = new Intent();

                    intent.setAction("com.mobilesafe.killall");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(), 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                    awm.updateAppWidget(componentName, views);

                }
            };
            timer.schedule(timerTask, 0, 3600);
        }
    }

    private void stopTime() {
        if (timer != null && timerTask != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

    /**
     * 接收锁屏广播 关闭更新widget
     */
    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopTime();
        }
    }

    /**
     * 接收开屏广播 更新widget
     */
    private class ScreenOnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            startTime();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onReceiver);
        unregisterReceiver(offReceiver);
        onReceiver = null;
        offReceiver = null;
        stopTime();
    }
}
