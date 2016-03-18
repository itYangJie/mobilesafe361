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
        //得到包管理器
        PackageManager pm = context.getPackageManager();
        //得到所有安装应用程序信息
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        //遍历
        for(PackageInfo info:packageInfos){
            APPInfo appInfo = new APPInfo();
            //得到包名,程序名，图标
            appInfo.setPackageName(info.packageName);
            appInfo.setAppLuncher(info.applicationInfo.loadIcon(pm));
            appInfo.setAppName(info.applicationInfo.loadLabel(pm).toString());

            int flags = info.applicationInfo.flags;//应用程序信息的标记 相当于用户提交的答卷
            if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
                //用户程序
                appInfo.setIsUserApp(true);
            }else{
                //系统程序
                appInfo.setIsUserApp(false);
            }

            if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
                //手机的内存
                appInfo.setIsInRom(true);
            }else{
                //手机外存储设备
                appInfo.setIsInRom(false);
            }
            appInfos.add(appInfo);
        }
        return appInfos;
    }

}
