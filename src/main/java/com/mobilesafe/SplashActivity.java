package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends Activity {
    //����
    private static final int ENTER_HOME = 0;
    private static final int SHOW_UPDATE_DIALOG = 1;
    private static final int JSON_ERROR = 2;
    private static final int IO_ERROR = 3;
    private static final int NET_ERROR = 4;

    private String description=null;
    private String apkUrl=null;
    private String version=null;
    private  HttpURLConnection conn=null;
    private TextView tv_splash_version = null;
    private android.os.Handler myHandler = null;
    private TextView tv_splash_developer=null;
    private SharedPreferences  sharedPreferences = null;
    private final  static String appId="a90db6021591dd9b";
    private final  static String appSecret="1465a401440f11ae";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        setContentView(R.layout.activity_splash);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("version" + getVersionName());
        tv_splash_developer=(TextView)findViewById(R.id.tv_splash_developer);

        //�������ݿ�
        copyDB("address.db");
        copyDB("antivirus.db");
        //��ʼ�����
        //AdManager.getInstance(this).init(appId , appSecret);
        //OffersManager.getInstance(this).onAppLaunch();
         sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
        boolean isNeedUpdate=sharedPreferences.getBoolean("update",false);
        //������Ϣ��handler
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case ENTER_HOME: {
                        enterHome();
                        break;
                    }
                    case SHOW_UPDATE_DIALOG: {
                        showUpdateDialog();
                        break;
                    }
                    case JSON_ERROR: {
                        enterHome();
                        Toast.makeText(SplashActivity.this, "JSON��������", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case IO_ERROR: {
                        enterHome();
                        Toast.makeText(SplashActivity.this, "�����쳣", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case NET_ERROR: {
                        enterHome();
                        Toast.makeText(getApplicationContext(), "URL����", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }
            }
        };

        //���ö���Ч��
        AlphaAnimation am =new AlphaAnimation(0.0f,1.0f);
        am.setDuration(1000);
        findViewById(R.id.rl_splash).startAnimation(am);

        //Ϊdeveloper�ı����ö���
        AnimationSet set = new AnimationSet(false);

        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -0.5f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        ta.setDuration(1000);
        ta.setRepeatCount(0);
        ta.setRepeatMode(Animation.REVERSE);

        ScaleAnimation sa = new ScaleAnimation(0.1f, 2.0f, 0.1f, 2.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(1000);
        sa.setRepeatCount(0);
        sa.setRepeatMode(Animation.REVERSE);

        set.addAnimation(ta);
        set.addAnimation(sa);

        tv_splash_developer.startAnimation(set);

        if(isNeedUpdate) {
            //�����°汾
            checkUpdate();
        }else {
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHome();
                }
            },2000);
        }
        //�������ͼ��
        installShortCut();

    }

    /**
     * ���������ݿ⵼��
     */
    private void copyDB(String path) {
        //ֻ�ڵ�һ�βſ���
        File file = new File(getFilesDir(),path);
        //�ж��Ƿ���ڸ��ļ���������ھͲ��ÿ�������ʡ��Դ
        if(file.exists()&&file.length()>0){
            return;
        }else {
            try {
                //������
                InputStream is = getAssets().open(path);
                //File file = new File(getFilesDir(),"address.db");
                //������data/data/����/address.db  �����
                FileOutputStream fos = new FileOutputStream(file);
                //������
                byte[] buffer = new byte[1024];
                int len = 0;

                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                //�ر���
                is.close();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * �����°汾�����Ի���
     */
    private void showUpdateDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("�����°汾��");

        //����������json������descriptionչʾ�ڶԻ�����
        builder.setMessage(description);

        //�û�ѡ������
        builder.setPositiveButton("��Ҫ����", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //�ж�ʹ�����ڴ濨
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //���ؽ��ȶԻ���
                    final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
                    progressDialog.setTitle("��������");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                    //����ѡ��Ի�������
                    dialog.dismiss();
                    //��������apk��FinalHttp����׼������
                    FinalHttp finalHttp = new FinalHttp();
                    //���ر���·��
                    File file = new File(Environment.getExternalStorageDirectory(), "361�ֻ���ʿ" + version);
                    progressDialog.show();
                    //����
                    System.out.println(description);
                    finalHttp.download(apkUrl, file.getAbsolutePath(), new AjaxCallBack<File>() {
                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            progressDialog.setMax(100);
                            int progress = (int) (100 * current / count);
                            progressDialog.setProgress(progress);
                        }

                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "���سɹ�,׼����װ", Toast.LENGTH_SHORT).show();
                            //��װ�°汾apk
                            installAPK(file);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            t.printStackTrace();
                            Toast.makeText(getApplicationContext(), "��Ǹ�����ع��̳�������", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            enterHome();
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });
                } else {
                    Toast.makeText(SplashActivity.this, "������ڴ濨������", Toast.LENGTH_SHORT).show();
                    //������ҳ
                    dialog.dismiss();
                    enterHome();
                }
            }
        });

        //�û���������
        builder.setNegativeButton("�´�����", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //������ҳ
                dialog.dismiss();
                enterHome();
            }
        });

        //�û�δ����ѡ��Ҳ������ҳ
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                //������ҳ��
                dialog.dismiss();
                enterHome();
            }
        });

        builder.show();

    }


    /**
     * ��װ���µ�apk
     */
    private void installAPK(File file){
        Intent intent=new Intent();
        intent.setAction(intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
        enterHome();
    }



    /**
     * ����HomeActivity
     */
    private void enterHome(){
        Intent intent =new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * �����߳��з��������ȡ���°汾�ĺ���
     */
    private void checkUpdate() {
        final long startTime = System.currentTimeMillis();
        final Message msg = myHandler.obtainMessage();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(getString(R.string.updateUrl));
                    conn = (HttpURLConnection) url.openConnection();
                    //����conn�������
                    conn.setConnectTimeout(2500);
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(3000);

                    conn.connect();
                    //�õ������
                    int code = conn.getResponseCode();
                    //���ʳɹ�
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readFromStream(is);
                        //��������
                        JSONObject jsonObject = new JSONObject(result);
                        //�õ�json���ݰ�������Ϣ
                        version = (String) jsonObject.get("version");
                        description = (String) jsonObject.get("description");
                        apkUrl = (String) jsonObject.get("apkUrl");
                        //�ж��Ƿ���Ҫ���°汾
                        if (version.equals(getVersionName())) {
                            //����Ҫ����
                            msg.what = ENTER_HOME;
                        } else {
                            //��Ҫ��ui�е����Ի������û�ѡ��
                            msg.what = SHOW_UPDATE_DIALOG;
                        }
                    }
                }  catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = NET_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                }catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                }
                finally {
                    long endTime = System.currentTimeMillis();
                    // ���ǻ��˶���ʱ��
                    long dTime = endTime - startTime;
                    // 2000
                    if (dTime < 2000) {
                        try {
                            Thread.sleep(2000 - dTime);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                    myHandler.sendMessage(msg);


                }
            }
        }

        ).start();
    }


    /**
     * �õ��汾��
     *
     * @return
     */
    private String getVersionName() {

        PackageManager pm = (PackageManager) getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * �������ͼ��
     */
    private void installShortCut() {
        boolean shortcut = sharedPreferences.getBoolean("shortcut", false);
        if(shortcut) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //���͹㲥����ͼ�� ���һ���������棬Ҫ�������ͼ����
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //��ݷ�ʽ  Ҫ����3����Ҫ����Ϣ 1������ 2.ͼ�� 3.��ʲô����
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "361�ֻ�С��ʿ");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,BitmapFactory.decodeResource(getResources(),R.drawable.luncher361));
        //������ͼ���Ӧ����ͼ��
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction("android.intent.action.MAIN");
        shortcutIntent.addCategory("android.intent.category.LAUNCHER");
        shortcutIntent.setClassName(getPackageName(), "com.mobilesafe.SplashActivity");
//		shortcutIntent.setAction("com.itheima.xxxx");
//		shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        sendBroadcast(intent);
        editor.putBoolean("shortcut", true);
        editor.commit();
        Toast.makeText(SplashActivity.this,"��ݷ�ʽ�Ѵ���,�������춼�������",Toast.LENGTH_SHORT).show();
    }
}
