package com.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Administrator on 2015/8/6.
 */
public class ProcessMangerUtil {

    /**
     * 获取正在运行的进程的数量
     * @param context 上下文
     * @return
     */
    public static int getRunningProcessCount(Context context){
        //PackageManager //包管理器 相当于程序管理器。静态的内容。
        //ActivityManager  进程管理器。管理的手机的活动信息。动态的内容。
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }
    /**
     * 获取手机可用的剩余内存
     * @param context 上下文
     * @return
     */
    public static long getAvailMem(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }
    /**
     * 获取手机可用的总内存
     * @param context 上下文
     * @return long byte
     */
    public static long getTotalMem(Context context){
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		MemoryInfo outInfo = new MemoryInfo();
//		am.getMemoryInfo(outInfo);
//		return outInfo.totalMem;
        try {
            File file = new File("/proc/meminfo");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            //MemTotal:         513000 kB
            StringBuilder sb = new StringBuilder();
            for(char c: line.toCharArray()){
                if(c>='0'&&c<='9'){
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
