package com.mobilesafe;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.receiver.MyAdmin;


public class MobileGuardSet4 extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mobile_guard_set4);
    }

    @Override
    public void showNext() {
        Intent intent = new Intent();
        intent.setClass(this,MobileGuardSet5.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    @Override
    public void showPrevious() {
        Intent intent = new Intent();
        intent.setClass(this, MobileGuardSet3.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }

    /**
     *
     * 激活设备管理权限
     * @param view
     */
    public void adminActive(View view){
        //创建一个Intent
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //我要激活谁
        ComponentName mDeviceAdminSample = new ComponentName(this,MyAdmin.class);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        //劝说用户开启管理员权限
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "开启该功能,您将享受更便捷的服务");
        startActivity(intent);
        //Toast.makeText(MobileGuardSet4.this, "已激活设备管理功能",Toast.LENGTH_SHORT).show();
    }
}
