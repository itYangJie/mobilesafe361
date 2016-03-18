package com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MobileGuardActivity extends Activity {
    SharedPreferences sp=null;
    private TextView secPhoneNum=null;
    private RelativeLayout guard=null;
    private  TextView useGuard=null;
    private ImageView useGuardImg=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sp=getSharedPreferences("config",MODE_PRIVATE);

        //�õ��Ƿ������ֻ������򵼽���
       boolean configed= sp.getBoolean("configed",false);
        if(configed){
            //������򵼽���,��ֱ����ʾ�ֻ���������
            setContentView(R.layout.activity_mobile_guard);

            secPhoneNum=(TextView)findViewById(R.id.secPhoneNum);
            guard=(RelativeLayout)findViewById(R.id.guard);
            useGuard=(TextView)findViewById(R.id.useGuard);
            useGuardImg=(ImageView)findViewById(R.id.useGuardImg);
            //ȡ����������
            secPhoneNum.setText(sp.getString("phoneNumSec",""));
            if(sp.getBoolean("useGuard",false)){
                useGuard.setText("���������ѿ���");
                useGuard.setTextColor(Color.RED);
                useGuardImg.setImageResource(R.drawable.lock);
            }else {
                useGuard.setText("��������δ����");
                useGuard.setTextColor(Color.GRAY);
                useGuardImg.setImageResource(R.drawable.unlock);
            }

            guard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sp.edit();
                    if(useGuard.getText().toString().equals("��������δ����")){
                        Toast.makeText(MobileGuardActivity.this,"���������ѿ���",Toast.LENGTH_LONG).show();
                        useGuard.setText("���������ѿ���");
                        useGuard.setTextColor(Color.RED);
                        useGuardImg.setImageResource(R.drawable.lock);
                        editor.putBoolean("useGuard", true);
                    }else {
                        Toast.makeText(MobileGuardActivity.this,"���������ѹر�",Toast.LENGTH_LONG).show();
                        useGuard.setText("��������δ����");
                        useGuard.setTextColor(Color.GRAY);
                        useGuardImg.setImageResource(R.drawable.unlock);
                        editor.putBoolean("useGuard", false);
                    }
                    editor.commit();
                }
            });

        }else {
            //�����򵼽���
            Intent intent=new Intent();
            intent.setClass(this, MobileGuardSet1.class);
            startActivity(intent);
            finish();
        }


    }

    /**
     * ���½��������򵼽���
     * @param view
     */
    public  void reEnterSetup(View view){
        //�����򵼽���
        Intent intent=new Intent();
        intent.setClass(this, MobileGuardSet1.class);
        startActivity(intent);
        finish();
    }
}
