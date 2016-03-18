package com.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2015/8/1.
 */
public class ServiceRunning {

    public static boolean getServiceIsRunning(Context context,String serviceName){
        //得到系统服务对象
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //得到当前运行的所以服务
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(100);
        //遍历正在运行的所有服务
        for (ActivityManager.RunningServiceInfo runningServiceInfo : list) {
            //得到服务名
            String name= runningServiceInfo.service.getClassName();
            if(serviceName.equals(name)){
                return true;
            }
        }
        return false;
    }

}
