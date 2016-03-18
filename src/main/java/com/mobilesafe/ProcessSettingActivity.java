package com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.service.AutoCleanService;
import com.utils.ServiceRunning;


public class ProcessSettingActivity extends Activity {

    private CheckBox cb_show_system,cb_auto_clean;
    private SharedPreferences sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_process_setting);
        cb_show_system=(CheckBox)findViewById(R.id.cb_show_system);
        cb_auto_clean=(CheckBox)findViewById(R.id.cb_auto_clean);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        //true表示显示系统进程，flase表示不显示
        boolean showSystemProcess =sp.getBoolean("showSystemProcess",true);
        cb_show_system.setChecked(showSystemProcess);

        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked) {
                    editor.putBoolean("showSystemProcess", true);
                } else {
                    editor.putBoolean("showSystemProcess", false);
                }
                editor.commit();
            }
        });
        //判断服务是否在运行
        cb_auto_clean.setChecked(ServiceRunning.getServiceIsRunning(ProcessSettingActivity.this, "com.service.AutoCleanService"));
        cb_auto_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(ProcessSettingActivity.this, AutoCleanService.class);
                if(isChecked){
                    startService(intent);
                }else {
                    stopService(intent);
                }
            }
        });
    }




}
