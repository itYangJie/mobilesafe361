package com.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.utils.MD5Utils;

public class AppLockEnterPwdActivity extends Activity {
    private String packName ;
    private ImageView iv_app_icon = null;
    private TextView tv_app_name = null;
    private EditText tv_pwd = null;
    private PackageManager pm = null;
    private SharedPreferences sp = null;
    private Vibrator vibrator = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_lock_enter_pwd);
        pm = getPackageManager();
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        iv_app_icon = (ImageView)findViewById(R.id.iv_app_icon);
        tv_app_name = (TextView)findViewById(R.id.tv_app_name);
        tv_pwd = (EditText)findViewById(R.id.tv_pwd);

        Intent intent = getIntent();
        packName = intent.getStringExtra("packName");

        try {
            ApplicationInfo info = pm.getApplicationInfo(packName, 0);
            iv_app_icon.setImageDrawable(info.loadIcon(pm));
            tv_app_name.setText(info.loadLabel(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void confirm(View view){

        String pwd =tv_pwd.getText().toString().trim();

        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(AppLockEnterPwdActivity.this,"密码输入为空",Toast.LENGTH_SHORT).show();
            Animation am = AnimationUtils.loadAnimation(this, R.anim.shake);
            tv_pwd.startAnimation(am);
            //震动提醒
            long[] pattern = {100, 200, 200, 300};
            //-1不重复 0循环振动 1
            vibrator.vibrate(pattern, -1);
            return;
        }
        if(sp.getString("applockpwd","").equals(MD5Utils.md5Password(pwd))){  //密码正确
            Intent intent = new Intent();
            intent.setAction("com.mobilesafe.watchdog");
            intent.putExtra("packName",packName);
            sendBroadcast(intent);
            finish();
        }else {
            Toast.makeText(AppLockEnterPwdActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
            Animation am = AnimationUtils.loadAnimation(this, R.anim.shake);
            tv_pwd.startAnimation(am);
            //震动提醒
            long[] pattern = {100, 200, 200, 300};
            //-1不重复 0循环振动 1
            vibrator.vibrate(pattern, -1);
        }

    }

    //按返回键 应该返回桌面
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
