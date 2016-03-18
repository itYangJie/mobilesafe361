package com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.ui.SettingItemView;


public class MobileGuardSet2 extends BaseSetupActivity {

    private SettingItemView sim_select=null;
    private TelephonyManager tp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mobile_guard_set2);

        sim_select=(SettingItemView)findViewById(R.id.sim_select);

        tp= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //ȡ����sim��ֵ
        String bundleSim=sp.getString("bundleSim","");
        if("".equals(bundleSim)){//Ϊ��˵��δ���øù���
            sim_select.setChecked(false);
        }else {//˵��δ���øù���
            sim_select.setChecked(true);
        }

        sim_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =sp.edit();
                //�õ����ǰ��״̬
                boolean isCheck = sim_select.isChecked();
                if(isCheck){ //���ǰΪѡ��״̬
                    sim_select.setChecked(false);
                    editor.putString("bundleSim","");
                }else { //���ǰΪδѡ��״̬
                    sim_select.setChecked(true);
                    String simSerialNumber=tp.getSimSerialNumber();
                    editor.putString("bundleSim",simSerialNumber);
                }
                editor.commit();
            }
        });
    }

    /**
     * ������һ���򵼽���
     * @param v
     */
    @Override
    public void showNext() {
        //û�а�sim�����ܽ�����һ��
        String bundleSim =sp.getString("bundleSim", "");
        if(bundleSim.equals("")){
            Toast.makeText(MobileGuardSet2.this,"����û�а�sim��,�����Խ�����һ��ҳ��",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this,MobileGuardSet3.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
    /**
     * ������һ���򵼽���
     * @param
     */
    @Override
    public void showPrevious() {
        Intent intent = new Intent();
        intent.setClass(this, MobileGuardSet1.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in,R.anim.pre_out);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(MobileGuardSet2.this,"����������򵼹���",Toast.LENGTH_LONG).show();
    }
}