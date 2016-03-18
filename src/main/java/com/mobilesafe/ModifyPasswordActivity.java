package com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
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


public class ModifyPasswordActivity extends Activity {
    private EditText old_pwd,new_pwd,new_confirmPwd;
    SharedPreferences sp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modify_password);

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
            Toast.makeText(ModifyPasswordActivity.this,"ԭ��������Ϊ��",Toast.LENGTH_SHORT).show();
            old_pwd.setText("");
            new_pwd.setText("");
            new_confirmPwd.setText("");
            return;
        }
        if(!(MD5Utils.md5Password(oldPwdString).equals(sp.getString("password", "")))){
            Toast.makeText(ModifyPasswordActivity.this,"ԭ�����������",Toast.LENGTH_SHORT).show();
            old_pwd.setText("");
            new_pwd.setText("");
            new_confirmPwd.setText("");
            return;
        }else {
            if (TextUtils.isEmpty(newPwdString) || TextUtils.isEmpty(newConfirmPwdString)) {
                Toast.makeText(ModifyPasswordActivity.this, "�ף����벻������Ϊ��", Toast.LENGTH_LONG).show();
                old_pwd.setText("");
                new_pwd.setText("");
                new_confirmPwd.setText("");
                return;
            }
            //�ж��������������Ƿ���ͬ
            if (newPwdString.equals(newConfirmPwdString)) {
                //���������������,���Ա�������
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("password", MD5Utils.md5Password(newPwdString));
                editor.putBoolean("needPwd",true);
                editor.commit();
                Toast.makeText(ModifyPasswordActivity.this, "�޸�����ɹ�,��ȥ��һ�԰�", Toast.LENGTH_LONG).show();
            } else {
                //�����������벻���,��ʾ�û�
                Toast.makeText(ModifyPasswordActivity.this, "�ף������������벻���", Toast.LENGTH_LONG).show();
                old_pwd.setText("");
                new_pwd.setText("");
                new_confirmPwd.setText("");
                return;
            }
        }

    }


}