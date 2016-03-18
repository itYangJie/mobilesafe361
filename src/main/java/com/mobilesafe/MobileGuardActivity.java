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

        //得到是否进入过手机防盗向导界面
       boolean configed= sp.getBoolean("configed",false);
        if(configed){
            //进入过向导界面,则直接显示手机防盗界面
            setContentView(R.layout.activity_mobile_guard);

            secPhoneNum=(TextView)findViewById(R.id.secPhoneNum);
            guard=(RelativeLayout)findViewById(R.id.guard);
            useGuard=(TextView)findViewById(R.id.useGuard);
            useGuardImg=(ImageView)findViewById(R.id.useGuardImg);
            //取出保存数据
            secPhoneNum.setText(sp.getString("phoneNumSec",""));
            if(sp.getBoolean("useGuard",false)){
                useGuard.setText("防盗保护已开启");
                useGuard.setTextColor(Color.RED);
                useGuardImg.setImageResource(R.drawable.lock);
            }else {
                useGuard.setText("防盗保护未开启");
                useGuard.setTextColor(Color.GRAY);
                useGuardImg.setImageResource(R.drawable.unlock);
            }

            guard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sp.edit();
                    if(useGuard.getText().toString().equals("防盗保护未开启")){
                        Toast.makeText(MobileGuardActivity.this,"防盗保护已开启",Toast.LENGTH_LONG).show();
                        useGuard.setText("防盗保护已开启");
                        useGuard.setTextColor(Color.RED);
                        useGuardImg.setImageResource(R.drawable.lock);
                        editor.putBoolean("useGuard", true);
                    }else {
                        Toast.makeText(MobileGuardActivity.this,"防盗保护已关闭",Toast.LENGTH_LONG).show();
                        useGuard.setText("防盗保护未开启");
                        useGuard.setTextColor(Color.GRAY);
                        useGuardImg.setImageResource(R.drawable.unlock);
                        editor.putBoolean("useGuard", false);
                    }
                    editor.commit();
                }
            });

        }else {
            //进入向导界面
            Intent intent=new Intent();
            intent.setClass(this, MobileGuardSet1.class);
            startActivity(intent);
            finish();
        }


    }

    /**
     * 重新进入设置向导界面
     * @param view
     */
    public  void reEnterSetup(View view){
        //进入向导界面
        Intent intent=new Intent();
        intent.setClass(this, MobileGuardSet1.class);
        startActivity(intent);
        finish();
    }
}
