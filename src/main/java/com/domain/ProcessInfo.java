package com.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/8/6.
 */
public class ProcessInfo {
    private String packageName;
    private String appName;
    private Drawable appLuncher;
    private long memorySize;
    private boolean useerProcess;
    private boolean checked;
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

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public Drawable getAppLuncher() {
        return appLuncher;
    }

    public void setAppLuncher(Drawable appLuncher) {
        this.appLuncher = appLuncher;
    }

    public boolean isUseerProcess() {
        return useerProcess;
    }

    public void setUseerProcess(boolean useerProcess) {
        this.useerProcess = useerProcess;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
