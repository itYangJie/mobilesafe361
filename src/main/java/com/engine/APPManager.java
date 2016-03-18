package com.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.domain.APPInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/5.
 */
public class APPManager {

    public static List<APPInfo> getAppInfo(Context context){
        List<APPInfo> appInfos = new ArrayList<APPInfo>();
        //�õ���������
        PackageManager pm = context.getPackageManager();
        //�õ����а�װӦ�ó�����Ϣ
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        //����
        for(PackageInfo info:packageInfos){
            APPInfo appInfo = new APPInfo();
            //�õ�����,��������ͼ��
            appInfo.setPackageName(info.packageName);
            appInfo.setAppLuncher(info.applicationInfo.loadIcon(pm));
            appInfo.setAppName(info.applicationInfo.loadLabel(pm).toString());

            int flags = info.applicationInfo.flags;//Ӧ�ó�����Ϣ�ı�� �൱���û��ύ�Ĵ��
            if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
                //�û�����
                appInfo.setIsUserApp(true);
            }else{
                //ϵͳ����
                appInfo.setIsUserApp(false);
            }

            if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
                //�ֻ����ڴ�
                appInfo.setIsInRom(true);
            }else{
                //�ֻ���洢�豸
                appInfo.setIsInRom(false);
            }
            appInfos.add(appInfo);
        }
        return appInfos;
    }

}
