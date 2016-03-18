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
     * ��ȡ�������еĽ��̵�����
     * @param context ������
     * @return
     */
    public static int getRunningProcessCount(Context context){
        //PackageManager //�������� �൱�ڳ������������̬�����ݡ�
        //ActivityManager  ���̹�������������ֻ��Ļ��Ϣ����̬�����ݡ�
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }
    /**
     * ��ȡ�ֻ����õ�ʣ���ڴ�
     * @param context ������
     * @return
     */
    public static long getAvailMem(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }
    /**
     * ��ȡ�ֻ����õ����ڴ�
     * @param context ������
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
