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
     * �����豸����Ȩ��
     * @param view
     */
    public void adminActive(View view){
        //����һ��Intent
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //��Ҫ����˭
        ComponentName mDeviceAdminSample = new ComponentName(this,MyAdmin.class);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        //Ȱ˵�û���������ԱȨ��
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "�����ù���,�������ܸ���ݵķ���");
        startActivity(intent);
        //Toast.makeText(MobileGuardSet4.this, "�Ѽ����豸������",Toast.LENGTH_SHORT).show();
    }
}
