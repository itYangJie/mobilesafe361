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

    private final String [] items = {"��͸��","������","��ʿ��","������","ƻ����"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seetting_center);

        set_appLock  = (SettingItemView)findViewById(R.id.set_appLock);
        set_appLock.setChecked(ServiceRunning.getServiceIsRunning(this,"com.service.WatchDogService"));

        //SharedPreferences����洢������Ϣ
          sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //���Ի�ѡ������ȥ����ʾ�򱳾���Ͽؼ�
        set_numberAddShowStyle=(SettingClickView)findViewById(R.id.set_numberAddShowStyle);
        //�õ����غ�������Ͽؼ�
        set_blackNumStop = (SettingItemView)findViewById(R.id.set_blackNumStop);
        //��ʾ��ǰ��ʾ����
        set_numberAddShowStyle.setDesc(items[sharedPreferences.getInt("which",0)]);
        set_numberAddShowStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dd = sharedPreferences.getInt("which", 0);
                // ����һ���Ի���
                AlertDialog.Builder builder = new AlertDialog.Builder(SeettingCenterActivity.this);
                builder.setTitle("��������ʾ����");
                builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //����ѡ�����
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("which", which);
                        editor.commit();
                        //���µ�ǰ������Ϣ
                        set_numberAddShowStyle.setDesc(items[sharedPreferences.getInt("which", 0)]);
                        //ȡ���Ի���
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("ȡ��", null);
                builder.show();

            }
        });
        //ѡ���Ƿ��Զ��������Զ�����Ͽռ�
        set_update = (SettingItemView) findViewById(R.id.set_update);
        //�Ƿ���ʾ����ȥ����������
        set_numberAdd = (SettingItemView) findViewById(R.id.set_numberAddress);
        //�ж���ʾ����ȥ���������صķ����Ƿ�������
        boolean isRunning = ServiceRunning.getServiceIsRunning(this,"com.service.NumberAddressShowService");
        set_numberAdd.setChecked(isRunning);

        //�ж����غ���������ķ����Ƿ�������
        boolean isRunningStopBlack = ServiceRunning.getServiceIsRunning(this,"com.service.BlackNumStopService");
        set_blackNumStop.setChecked(isRunningStopBlack);

        //�õ��Ƿ���Ҫ�Զ������µĲ���ֵ�����ø��ؼ�
        boolean isNeedUpdate = sharedPreferences.getBoolean("update", false);
        set_update.setChecked(isNeedUpdate);

        //�ɵõ��Ĳ���ֵ��ʼ����Ͽؼ�
        if(isNeedUpdate){
            set_update.getDes().setText("�Զ��������ѿ���");
            set_update.getDes().setTextColor(Color.RED);
        }else {
            set_update.getDes().setText("�Զ��������ѹر�");
            set_update.getDes().setTextColor(Color.GRAY);
        }
        set_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //�жϵ��֮ǰ�Ƿ�ѡ���˿���
                if (set_update.isChecked()) {
                    //�����˼����£���Ҫ�ر�
                    set_update.setChecked(false);
                    set_update.getDes().setText("�Զ��������ѹر�");
                    set_update.getDes().setTextColor(Color.GRAY);
                    //�洢�Ƿ���Ҫ�Զ������µĲ���ֵΪfalse
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("update", false);
                    editor.commit();
                } else {
                    //�ر��˼����£���Ҫ����
                    set_update.setChecked(true);
                    set_update.getDes().setText("�Զ��������ѿ���");
                    set_update.getDes().setTextColor(Color.RED);
                    //�洢�Ƿ���Ҫ�Զ������µĲ���ֵΪtrue
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("update", true);
                    editor.commit();
                }
            }
        });
        /**
         * ����ȥ����ʾ���������������Ͽؼ�����¼�
         */
        set_numberAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //�жϵ��֮ǰ�Ƿ�ѡ��
                if(set_numberAdd.isChecked()){//֮ǰѡ�У�����Ӧ�ùرշ���
                    set_numberAdd.setChecked(false);
                    Intent intent = new Intent(SeettingCenterActivity.this, NumberAddressShowService.class);
                    SeettingCenterActivity.this.stopService(intent);


                }else {//֮ǰδѡ�У����ڿ�������
                    set_numberAdd.setChecked(true);
                    Intent intent = new Intent(SeettingCenterActivity.this, NumberAddressShowService.class);
                    SeettingCenterActivity.this.startService(intent);
                }
            }
        });


        /**
         * ����������������Ͽؼ�����¼�
         */
        set_blackNumStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //�жϵ��֮ǰ�Ƿ�ѡ��
                if (set_blackNumStop.isChecked()) {//֮ǰѡ�У�����Ӧ�ùرշ���
                    set_blackNumStop.setChecked(false);
                    Intent intent = new Intent(SeettingCenterActivity.this, BlackNumStopService.class);
                    SeettingCenterActivity.this.stopService(intent);

                } else {//֮ǰδѡ�У����ڿ�������
                    set_blackNumStop.setChecked(true);
                    Intent intent = new Intent(SeettingCenterActivity.this, BlackNumStopService.class);
                    SeettingCenterActivity.this.startService(intent);
                }
            }
        });
        //Ӧ�ó�����
        set_appLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //��һ�ε����Ҫ��������
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
     * ���ó���������Ի�����
     */
    Dialog dialogSetPwd=null;
    private void showSetPwdDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(SeettingCenterActivity.this);

        //�ɲ����ļ��õ�view
        View view=View.inflate(SeettingCenterActivity.this,R.layout.dialog_applock_pwdset,null);
        //��view�õ������еĿؼ�
        final EditText et_setPwd=(EditText)view.findViewById(R.id.et_setup_pwd);
        final EditText et_setPwdConfirm=(EditText)view.findViewById(R.id.et_setup_confirm);
        final Button ok=(Button)view.findViewById(R.id.ok);
        final Button cancel=(Button)view.findViewById(R.id.cancel);

        //���ȡ��
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetPwd.dismiss();
                return;
            }
        });
        //���ȷ��
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_setPwd.getText().toString().trim();
                String pwd_confirm = et_setPwdConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
                    Toast.makeText(SeettingCenterActivity.this, "�ף����벻������Ϊ��", Toast.LENGTH_LONG).show();
                    et_setPwd.setText("");
                    et_setPwdConfirm.setText("");
                    return;
                }
                //�ж��������������Ƿ���ͬ
                if (pwd.equals(pwd_confirm)) {
                    //���������������,���Ա�������
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("applockpwd", MD5Utils.md5Password(pwd));
                    editor.commit();

                    Toast.makeText(SeettingCenterActivity.this, "��������ɹ�", Toast.LENGTH_LONG).show();
                    set_appLock.setChecked(true);
                    Intent intent = new Intent(SeettingCenterActivity.this, WatchDogService.class);
                    startService(intent);
                    //ȡ���Ի���
                    dialogSetPwd.dismiss();

                } else {
                    //�����������벻���,��ʾ�û�
                    Toast.makeText(SeettingCenterActivity.this, "�ף������������벻���", Toast.LENGTH_LONG).show();
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
     * ��дonResume����  �����home��  bug
     */
    @Override
    protected void onResume() {
        super.onResume();

        set_numberAddShowStyle.setDesc(items[sharedPreferences.getInt("which", 0)]);
        //�ж���ʾ����ȥ���������صķ����Ƿ�������
        boolean isRunning = ServiceRunning.getServiceIsRunning(this,"com.service.NumberAddressShowService");
        set_numberAdd.setChecked(isRunning);
        //�õ��Ƿ���Ҫ�Զ������µĲ���ֵ�����ø��ؼ�
        boolean isNeedUpdate = sharedPreferences.getBoolean("update", false);
        set_update.setChecked(isNeedUpdate);
        //�ж����غ���������ķ����Ƿ�������
        boolean isRunningStopBlack = ServiceRunning.getServiceIsRunning(this, "com.service.BlackNumStopService");
        set_blackNumStop.setChecked(isRunningStopBlack);

        boolean isRunningAppLock = ServiceRunning.getServiceIsRunning(this, "com.service.WatchDogService");
        set_appLock.setChecked(isRunningAppLock);
    }

    /**
     * ���������ֻ���������
     * @param view
     */
    public void setPassword(View view){
        Intent intent=new Intent();
        intent.setClass(SeettingCenterActivity.this,ModifyPasswordActivity.class);
        startActivity(intent);
    }

    /**
     * �������ó���������
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
        builder.setTitle("ж��Ӧ�ó���");
        builder.setMessage("���Ҫ������˵�õ�Ҫ��һ����?");
        builder.setNegativeButton("������һ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("���̷���", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1.���������ԱȨ��
                ComponentName mDeviceAdminSample = new ComponentName(SeettingCenterActivity.this, MyAdmin.class);
                dpm.removeActiveAdmin(mDeviceAdminSample);
                //2.��ͨӦ�õ�ж��
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
