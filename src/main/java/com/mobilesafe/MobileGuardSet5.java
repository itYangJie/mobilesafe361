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
                    Toast.makeText(MobileGuardSet5.this,"ѡ������������",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MobileGuardSet5.this,"ѡ��رշ�������",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * �򵼽�������������ֻ���������
     * @param view
     */
    @Override
    public void showNext() {
        //�򵼽�����������沼��ֵ���´�ֱ�ӽ����ֻ���������
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("configed",true);
        editor.putBoolean("useGuard",cb_guardState.isChecked());
        editor.commit();
        //�����ֻ���������
        Intent intent = new Intent();
        intent.setClass(this, MobileGuardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
    /**
     * ������һ���򵼽���
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
        Toast.makeText(MobileGuardSet5.this,"����������򵼹���",Toast.LENGTH_LONG).show();
    }
}
