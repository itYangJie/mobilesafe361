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
     * �ֻ���������ز�ѯ
     * @param view
     */
    public void numberAddressQuary(View view){
        Intent intent = new Intent(this,NumberAddressQuaryActivity.class);
        startActivity(intent);
    }

    /**
     * ���Ż�ԭ
     * @param view
     */
    public void smsRestore(View view){
        pd = new ProgressDialog(HighToolActivity.this);

        //����Ϊˮƽ���ȶԻ���
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("���ڻ�ԭ");
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
                            Toast.makeText(HighToolActivity.this,"��ԭ�ɹ�",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HighToolActivity.this, "�Բ��𣬻�ԭʧ��", Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
                    pd.dismiss();
                }
            }
        }).start();


    }

    /**
     * ���ű���
     * @param view
     */

    public void smsBackUp(View view){
        pd = new ProgressDialog(HighToolActivity.this);

        //����Ϊˮƽ���ȶԻ���
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("���ڱ��ݶ���");
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
                            Toast.makeText(HighToolActivity.this,"���ݳɹ�",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HighToolActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
                    pd.dismiss();
                }
            }
        }).start();
    }
}
