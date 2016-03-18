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
    //常量
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

        //拷贝数据库
        copyDB("address.db");
        copyDB("antivirus.db");
        //初始化广告
        //AdManager.getInstance(this).init(appId , appSecret);
        //OffersManager.getInstance(this).onAppLaunch();
         sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
        boolean isNeedUpdate=sharedPreferences.getBoolean("update",false);
        //处理消息的handler
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
                        Toast.makeText(SplashActivity.this, "JSON解析出错", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case IO_ERROR: {
                        enterHome();
                        Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case NET_ERROR: {
                        enterHome();
                        Toast.makeText(getApplicationContext(), "URL错误", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }
            }
        };

        //设置动画效果
        AlphaAnimation am =new AlphaAnimation(0.0f,1.0f);
        am.setDuration(1000);
        findViewById(R.id.rl_splash).startAnimation(am);

        //为developer文本设置动画
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
            //检查更新版本
            checkUpdate();
        }else {
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHome();
                }
            },2000);
        }
        //创建快捷图标
        installShortCut();

    }

    /**
     * 将所需数据库导入
     */
    private void copyDB(String path) {
        //只在第一次才拷贝
        File file = new File(getFilesDir(),path);
        //判断是否存在该文件，如果存在就不用拷贝，节省资源
        if(file.exists()&&file.length()>0){
            return;
        }else {
            try {
                //输入流
                InputStream is = getAssets().open(path);
                //File file = new File(getFilesDir(),"address.db");
                //拷贝到data/data/包名/address.db  输出流
                FileOutputStream fos = new FileOutputStream(file);
                //缓冲区
                byte[] buffer = new byte[1024];
                int len = 0;

                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                //关闭流
                is.close();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发现新版本弹出对话框
     */
    private void showUpdateDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("发现新版本喲");

        //将解析出的json数据中description展示在对话框上
        builder.setMessage(description);

        //用户选择升级
        builder.setPositiveButton("我要尝鲜", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //判断使用有内存卡
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //下载进度对话框
                    final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
                    progressDialog.setTitle("正在下载");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                    //下载选择对话框隐藏
                    dialog.dismiss();
                    //创建下载apk的FinalHttp对象，准备下载
                    FinalHttp finalHttp = new FinalHttp();
                    //下载保存路径
                    File file = new File(Environment.getExternalStorageDirectory(), "361手机卫士" + version);
                    progressDialog.show();
                    //下载
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
                            Toast.makeText(getApplicationContext(), "下载成功,准备安装", Toast.LENGTH_SHORT).show();
                            //安装新版本apk
                            installAPK(file);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            t.printStackTrace();
                            Toast.makeText(getApplicationContext(), "抱歉，下载过程出现意外", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            enterHome();
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });
                } else {
                    Toast.makeText(SplashActivity.this, "请插入内存卡再升级", Toast.LENGTH_SHORT).show();
                    //进入主页
                    dialog.dismiss();
                    enterHome();
                }
            }
        });

        //用户放弃升级
        builder.setNegativeButton("下次升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //进入主页
                dialog.dismiss();
                enterHome();
            }
        });

        //用户未作出选择也进入主页
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                //进入主页面
                dialog.dismiss();
                enterHome();
            }
        });

        builder.show();

    }


    /**
     * 安装更新的apk
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
     * 进入HomeActivity
     */
    private void enterHome(){
        Intent intent =new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 在子线程中访问网络获取更新版本的函数
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
                    //设置conn对象参数
                    conn.setConnectTimeout(2500);
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(3000);

                    conn.connect();
                    //得到结果码
                    int code = conn.getResponseCode();
                    //访问成功
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readFromStream(is);
                        //解析数据
                        JSONObject jsonObject = new JSONObject(result);
                        //得到json数据包裹的信息
                        version = (String) jsonObject.get("version");
                        description = (String) jsonObject.get("description");
                        apkUrl = (String) jsonObject.get("apkUrl");
                        //判断是否需要更新版本
                        if (version.equals(getVersionName())) {
                            //不需要更新
                            msg.what = ENTER_HOME;
                        } else {
                            //需要在ui中弹出对话框让用户选择
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
                    // 我们花了多少时间
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
     * 得到版本名
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
     * 创建快捷图标
     */
    private void installShortCut() {
        boolean shortcut = sharedPreferences.getBoolean("shortcut", false);
        if(shortcut) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //发送广播的意图， 大吼一声告诉桌面，要创建快捷图标了
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷方式  要包含3个重要的信息 1，名称 2.图标 3.干什么事情
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "361手机小卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,BitmapFactory.decodeResource(getResources(),R.drawable.luncher361));
        //桌面点击图标对应的意图。
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
        Toast.makeText(SplashActivity.this,"快捷方式已创建,我们天天都可以相见",Toast.LENGTH_SHORT).show();
    }
}
