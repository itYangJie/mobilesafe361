package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.receiver.MyAdmin;
import com.service.BlackNumStopService;
import com.service.NumberAddressShowService;
import com.service.WatchDogService;
import com.ui.SettingClickView;
import com.ui.SettingItemView;
import com.utils.MD5Utils;
import com.utils.ServiceRunning;


public class SeettingCenterActivity extends Activity {

    private SettingItemView set_update = null;
    private SettingItemView set_numberAdd = null;
    private SettingClickView set_numberAddShowStyle = null;
    private SettingItemView set_blackNumStop = null;
    private SharedPreferences sharedPreferences = null;
    private SettingItemView set_appLock = null;

    private final String [] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seetting_center);

        set_appLock  = (SettingItemView)findViewById(R.id.set_appLock);
        set_appLock.setChecked(ServiceRunning.getServiceIsRunning(this,"com.service.WatchDogService"));

        //SharedPreferences对象存储配置信息
          sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //个性化选择来电去电显示框背景组合控件
        set_numberAddShowStyle=(SettingClickView)findViewById(R.id.set_numberAddShowStyle);
        //得到拦截黑名单组合控件
        set_blackNumStop = (SettingItemView)findViewById(R.id.set_blackNumStop);
        //显示当前显示框风格
        set_numberAddShowStyle.setDesc(items[sharedPreferences.getInt("which",0)]);
        set_numberAddShowStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dd = sharedPreferences.getInt("which", 0);
                // 弹出一个对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(SeettingCenterActivity.this);
                builder.setTitle("归属地提示框风格");
                builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //保存选择参数
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("which", which);
                        editor.commit();
                        //更新当前描述信息
                        set_numberAddShowStyle.setDesc(items[sharedPreferences.getInt("which", 0)]);
                        //取消对话框
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();

            }
        });
        //选择是否自动检查更新自定义组合空间
        set_update = (SettingItemView) findViewById(R.id.set_update);
        //是否显示来电去电号码归属地
        set_numberAdd = (SettingItemView) findViewById(R.id.set_numberAddress);
        //判断显示来电去电号码归属地的服务是否还在运行
        boolean isRunning = ServiceRunning.getServiceIsRunning(this,"com.service.NumberAddressShowService");
        set_numberAdd.setChecked(isRunning);

        //判断拦截黑名单号码的服务是否还在运行
        boolean isRunningStopBlack = ServiceRunning.getServiceIsRunning(this,"com.service.BlackNumStopService");
        set_blackNumStop.setChecked(isRunningStopBlack);

        //得到是否需要自动检查更新的布尔值并设置给控件
        boolean isNeedUpdate = sharedPreferences.getBoolean("update", false);
        set_update.setChecked(isNeedUpdate);

        //由得到的布尔值初始化组合控件
        if(isNeedUpdate){
            set_update.getDes().setText("自动检查更新已开启");
            set_update.getDes().setTextColor(Color.RED);
        }else {
            set_update.getDes().setText("自动检查更新已关闭");
            set_update.getDes().setTextColor(Color.GRAY);
        }
        set_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断点击之前是否选择了开启
                if (set_update.isChecked()) {
                    //开启了检查更新，则要关闭
                    set_update.setChecked(false);
                    set_update.getDes().setText("自动检查更新已关闭");
                    set_update.getDes().setTextColor(Color.GRAY);
                    //存储是否需要自动检查更新的布尔值为false
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("update", false);
                    editor.commit();
                } else {
                    //关闭了检查更新，则要开启
                    set_update.setChecked(true);
                    set_update.getDes().setText("自动检查更新已开启");
                    set_update.getDes().setTextColor(Color.RED);
                    //存储是否需要自动检查更新的布尔值为true
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("update", true);
                    editor.commit();
                }
            }
        });
        /**
         * 来电去电显示号码归属地设置组合控件点击事件
         */
        set_numberAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断点击之前是否被选中
                if(set_numberAdd.isChecked()){//之前选中，现在应该关闭服务
                    set_numberAdd.setChecked(false);
                    Intent intent = new Intent(SeettingCenterActivity.this, NumberAddressShowService.class);
                    SeettingCenterActivity.this.stopService(intent);


                }else {//之前未选中，现在开启服务
                    set_numberAdd.setChecked(true);
                    Intent intent = new Intent(SeettingCenterActivity.this, NumberAddressShowService.class);
                    SeettingCenterActivity.this.startService(intent);
                }
            }
        });


        /**
         * 黑名单拦截设置组合控件点击事件
         */
        set_blackNumStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断点击之前是否被选中
                if (set_blackNumStop.isChecked()) {//之前选中，现在应该关闭服务
                    set_blackNumStop.setChecked(false);
                    Intent intent = new Intent(SeettingCenterActivity.this, BlackNumStopService.class);
                    SeettingCenterActivity.this.stopService(intent);

                } else {//之前未选中，现在开启服务
                    set_blackNumStop.setChecked(true);
                    Intent intent = new Intent(SeettingCenterActivity.this, BlackNumStopService.class);
                    SeettingCenterActivity.this.startService(intent);
                }
            }
        });
        //应用程序锁
        set_appLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一次点击需要设置密码
                if(sharedPreferences.getString("applockpwd","")==null||sharedPreferences.getString("applockpwd","").equals("")){
                    showSetPwdDialog();
                }else {//
                    Intent intent = new Intent(SeettingCenterActivity.this, WatchDogService.class);
                    if(set_appLock.isChecked()){
                        set_appLock.setChecked(false);
                        stopService(intent);
                    }else {
                        set_appLock.setChecked(true);
                        startService(intent);
                    }
                }
            }
        });
    }

    /**
     * 设置程序锁密码对话框函数
     */
    Dialog dialogSetPwd=null;
    private void showSetPwdDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(SeettingCenterActivity.this);

        //由布局文件得到view
        View view=View.inflate(SeettingCenterActivity.this,R.layout.dialog_applock_pwdset,null);
        //由view得到布局中的控件
        final EditText et_setPwd=(EditText)view.findViewById(R.id.et_setup_pwd);
        final EditText et_setPwdConfirm=(EditText)view.findViewById(R.id.et_setup_confirm);
        final Button ok=(Button)view.findViewById(R.id.ok);
        final Button cancel=(Button)view.findViewById(R.id.cancel);

        //点击取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetPwd.dismiss();
                return;
            }
        });
        //点击确定
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_setPwd.getText().toString().trim();
                String pwd_confirm = et_setPwdConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
                    Toast.makeText(SeettingCenterActivity.this, "亲，密码不能设置为空", Toast.LENGTH_LONG).show();
                    et_setPwd.setText("");
                    et_setPwdConfirm.setText("");
                    return;
                }
                //判断两次输入密码是否相同
                if (pwd.equals(pwd_confirm)) {
                    //两次输入密码相等,可以保存密码
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("applockpwd", MD5Utils.md5Password(pwd));
                    editor.commit();

                    Toast.makeText(SeettingCenterActivity.this, "设置密码成功", Toast.LENGTH_LONG).show();
                    set_appLock.setChecked(true);
                    Intent intent = new Intent(SeettingCenterActivity.this, WatchDogService.class);
                    startService(intent);
                    //取消对话框
                    dialogSetPwd.dismiss();

                } else {
                    //两次输入密码不相等,提示用户
                    Toast.makeText(SeettingCenterActivity.this, "亲，两次密码输入不相等", Toast.LENGTH_LONG).show();
                    et_setPwd.setText("");
                    et_setPwdConfirm.setText("");
                    return;
                }
            }
        });
        builder.setView(view);
        dialogSetPwd= builder.show();
    }

    /**
     * 重写onResume方法  解决按home键  bug
     */
    @Override
    protected void onResume() {
        super.onResume();

        set_numberAddShowStyle.setDesc(items[sharedPreferences.getInt("which", 0)]);
        //判断显示来电去电号码归属地的服务是否还在运行
        boolean isRunning = ServiceRunning.getServiceIsRunning(this,"com.service.NumberAddressShowService");
        set_numberAdd.setChecked(isRunning);
        //得到是否需要自动检查更新的布尔值并设置给控件
        boolean isNeedUpdate = sharedPreferences.getBoolean("update", false);
        set_update.setChecked(isNeedUpdate);
        //判断拦截黑名单号码的服务是否还在运行
        boolean isRunningStopBlack = ServiceRunning.getServiceIsRunning(this, "com.service.BlackNumStopService");
        set_blackNumStop.setChecked(isRunningStopBlack);

        boolean isRunningAppLock = ServiceRunning.getServiceIsRunning(this, "com.service.WatchDogService");
        set_appLock.setChecked(isRunningAppLock);
    }

    /**
     * 重新设置手机防盗密码
     * @param view
     */
    public void setPassword(View view){
        Intent intent=new Intent();
        intent.setClass(SeettingCenterActivity.this,ModifyPasswordActivity.class);
        startActivity(intent);
    }

    /**
     * 重新设置程序锁密码
     * @param view
     */
    public void setAppLockPwd(View view){
        Intent intent=new Intent();
        intent.setClass(SeettingCenterActivity.this,ModifyAppLockActivity.class);
        startActivity(intent);
    }



    public void unInstall(View view){
        final DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setTitle("卸载应用程序");
        builder.setMessage("真的要分手吗，说好的要在一起呢?");
        builder.setNegativeButton("继续在一起", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("残忍分手", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1.先清除管理员权限
                ComponentName mDeviceAdminSample = new ComponentName(SeettingCenterActivity.this, MyAdmin.class);
                dpm.removeActiveAdmin(mDeviceAdminSample);
                //2.普通应用的卸载
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
        builder.show();

    }
}
