package com.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.utils.SmsUtil;


public class HighToolActivity extends Activity {
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_high_tool);
    }

    /**
     * 手机号码归属地查询
     * @param view
     */
    public void numberAddressQuary(View view){
        Intent intent = new Intent(this,NumberAddressQuaryActivity.class);
        startActivity(intent);
    }

    /**
     * 短信还原
     * @param view
     */
    public void smsRestore(View view){
        pd = new ProgressDialog(HighToolActivity.this);

        //设置为水平进度对话框
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("正在还原");
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsUtil.restoreSms(getApplicationContext(), new SmsUtil.BackUpCallBack() {
                        @Override
                        public void beforeBackup(int max) {
                            pd.setMax(max);
                        }

                        @Override
                        public void onSmsBackup(int progress) {
                            pd.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HighToolActivity.this,"还原成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HighToolActivity.this, "对不起，还原失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
                    pd.dismiss();
                }
            }
        }).start();


    }

    /**
     * 短信备份
     * @param view
     */

    public void smsBackUp(View view){
        pd = new ProgressDialog(HighToolActivity.this);

        //设置为水平进度对话框
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("正在备份短信");
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsUtil.backupSms(getApplicationContext(), new SmsUtil.BackUpCallBack() {
                        @Override
                        public void beforeBackup(int max) {
                            pd.setMax(max);
                        }

                        @Override
                        public void onSmsBackup(int progress) {
                            pd.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HighToolActivity.this,"备份成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HighToolActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
                    pd.dismiss();
                }
            }
        }).start();
    }
}
