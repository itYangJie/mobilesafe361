package com.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class KillAllReceiver extends BroadcastReceiver {

    /**
     * ���չ㲥ɱ����̨����
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("�Զ���Ĺ㲥��Ϣ���յ���..");
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info:infos){
            am.killBackgroundProcesses(info.processName);
        }
    }
}
