package com.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;


public class MobileGuardSet5 extends BaseSetupActivity {
    private CheckBox cb_guardState = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mobile_guard_set5);

        cb_guardState=(CheckBox)findViewById(R.id.cb_guardState);

        cb_guardState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(MobileGuardSet5.this,"选择开启防盗功能",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MobileGuardSet5.this,"选择关闭防盗功能",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 向导界面结束，进入手机防盗界面
     * @param view
     */
    @Override
    public void showNext() {
        //向导界面结束，保存布尔值，下次直接进入手机防盗界面
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("configed",true);
        editor.putBoolean("useGuard",cb_guardState.isChecked());
        editor.commit();
        //进入手机防盗界面
        Intent intent = new Intent();
        intent.setClass(this, MobileGuardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
    /**
     * 进入上一个向导界面
     * @param view
     */
    @Override
    public void showPrevious() {
        Intent intent = new Intent();
        intent.setClass(this, MobileGuardSet4.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(MobileGuardSet5.this,"请完成整个向导过程",Toast.LENGTH_LONG).show();
    }
}
