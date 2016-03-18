package com.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.domain.ProcessInfo;
import com.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/6.
 */
public class ProcessManger {

    public static List<ProcessInfo> getRuningProcessInfo(Context context) {
        //存放进程信息的集合
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
        //遍历
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
            ProcessInfo processInfo = new ProcessInfo();
            //进程名即为包名
            String packageNmae = runningAppProcessInfo.processName;
            //设置包名
            processInfo.setPackageName(packageNmae);
            //设置内存大小
            android.os.Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            long memSize = memoryInfos[0].getTotalPrivateDirty() * 1024;
            processInfo.setMemorySize(memSize);
            try {
                //由包名得到应用程序信息
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageNmae, 0);
                //设置应用名
                processInfo.setAppName(applicationInfo.loadLabel(pm).toString());
                //设置是否为用户进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //用户进程
                    processInfo.setUseerProcess(true);
                } else {
                    //系统进程
                    processInfo.setUseerProcess(false);
                }
                //设置图标
                processInfo.setAppLuncher(applicationInfo.loadIcon(pm));
            } catch (PackageManager.NameNotFoundException e) {
                //当找不到名称是设置默认值
                e.printStackTrace();
                processInfo.setAppLuncher(context.getResources().getDrawable(R.drawable.ic_default));
                processInfo.setAppName(packageNmae);
            }
            processInfos.add(processInfo);
        }
        return processInfos;
    }
}
