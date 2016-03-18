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
        //��Ž�����Ϣ�ļ���
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
        //����
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
            ProcessInfo processInfo = new ProcessInfo();
            //��������Ϊ����
            String packageNmae = runningAppProcessInfo.processName;
            //���ð���
            processInfo.setPackageName(packageNmae);
            //�����ڴ��С
            android.os.Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            long memSize = memoryInfos[0].getTotalPrivateDirty() * 1024;
            processInfo.setMemorySize(memSize);
            try {
                //�ɰ����õ�Ӧ�ó�����Ϣ
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageNmae, 0);
                //����Ӧ����
                processInfo.setAppName(applicationInfo.loadLabel(pm).toString());
                //�����Ƿ�Ϊ�û�����
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //�û�����
                    processInfo.setUseerProcess(true);
                } else {
                    //ϵͳ����
                    processInfo.setUseerProcess(false);
                }
                //����ͼ��
                processInfo.setAppLuncher(applicationInfo.loadIcon(pm));
            } catch (PackageManager.NameNotFoundException e) {
                //���Ҳ�������������Ĭ��ֵ
                e.printStackTrace();
                processInfo.setAppLuncher(context.getResources().getDrawable(R.drawable.ic_default));
                processInfo.setAppName(packageNmae);
            }
            processInfos.add(processInfo);
        }
        return processInfos;
    }
}
