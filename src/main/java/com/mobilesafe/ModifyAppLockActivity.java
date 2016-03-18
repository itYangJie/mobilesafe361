package com.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.utils.MD5Utils;


public class ModifyAppLockActivity extends Activity {
    private EditText old_pwd,new_pwd,new_confirmPwd;
    SharedPreferences sp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modify_app_lock);

        sp=getSharedPreferences("config",MODE_PRIVATE);
        old_pwd=(EditText)findViewById(R.id.et_old_pwd);
        new_pwd=(EditText)findViewById(R.id.et_new_pwd);
        new_confirmPwd=(EditText)findViewById(R.id.et_new_confirmPwd);
    }

    public void  modifyPwd(View view){
        String oldPwdString = old_pwd.getText().toString().trim();
        String newPwdString = new_pwd.getText().toString().trim();
        String newConfirmPwdString = new_confirmPwd.getText().toString().trim();
        if(TextUtils.isEmpty(oldPwdString)){
            Toast.makeText(ModifyAppLockActivity.this, "原密码输入为空", Toast.LENGTH_SHORT).show();
            old_pwd.setText("");
            new_pwd.setText("");
            new_confirmPwd.setText("");
            return;
        }
        if(!(MD5Utils.md5Password(oldPwdString).equals(sp.getString("applockpwd", "")))){
            Toast.makeText(ModifyAppLockActivity.this,"原密码输入错误",Toast.LENGTH_SHORT).show();
            old_pwd.setText("");
            new_pwd.setText("");
            new_confirmPwd.setText("");
            return;
        }else {
            if (TextUtils.isEmpty(newPwdString) || TextUtils.isEmpty(newConfirmPwdString)) {
                Toast.makeText(ModifyAppLockActivity.this, "亲，密码不能设置为空", Toast.LENGTH_LONG).show();
                old_pwd.setText("");
                new_pwd.setText("");
                new_confirmPwd.setText("");
                return;
            }
            //判断两次输入密码是否相同
            if (newPwdString.equals(newConfirmPwdString)) {
                //两次输入密码相等,可以保存密码
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("applockpwd", MD5Utils.md5Password(newPwdString));
                editor.commit();
                Toast.makeText(ModifyAppLockActivity.this, "修改密码成功", Toast.LENGTH_LONG).show();
            } else {
                //两次输入密码不相等,提示用户
                Toast.makeText(ModifyAppLockActivity.this, "亲，两次密码输入不相等", Toast.LENGTH_LONG).show();
                old_pwd.setText("");
                new_pwd.setText("");
                new_confirmPwd.setText("");
                return;
            }
        }

    }




}
