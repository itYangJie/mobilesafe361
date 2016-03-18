package com.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2015/8/1.
 */
public class ServiceRunning {

    public static boolean getServiceIsRunning(Context context,String serviceName){
        //�õ�ϵͳ�������
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //�õ���ǰ���е����Է���
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(100);
        //�����������е����з���
        for (ActivityManager.RunningServiceInfo runningServiceInfo : list) {
            //�õ�������
            String name= runningServiceInfo.service.getClassName();
            if(serviceName.equals(name)){
                return true;
            }
        }
        return false;
    }

}
