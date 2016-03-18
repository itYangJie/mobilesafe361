package com.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/8/5.
 */
public class APPInfo {
    private String packageName;
    private String appName;
    private Drawable appLuncher;
    private Boolean isUserApp;
    private Boolean isInRom;

    public Drawable getAppLuncher() {
        return appLuncher;
    }

    public void setAppLuncher(Drawable appLuncher) {
        this.appLuncher = appLuncher;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Boolean isUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(boolean isUserApp) {
        this.isUserApp = isUserApp;
    }

    public Boolean isInRom() {
        return isInRom;
    }

    public void setIsInRom(boolean isInRom) {
        this.isInRom = isInRom;
    }



}
