package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.db.dao.ScanVirsuDao;
import com.domain.ProcessInfo;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


public class ScanVirusActivity extends Activity {
    protected static final int SCANING = 0;
    protected static final int FINISH = 1;
    private ImageView iv_scan;
    private ProgressBar progressBar1;
    private PackageManager pm;
    private TextView tv_scan_status;
    private ListView ll_container;
    private Handler handler;
    private MyAdapter adapter;
    private List<AppVirusInfo> appVirusInfos;
    private AppVirusInfo virusInfo;
    private TextView tv_scan;
    private int appCount;
    private int progress = 0;
    // private int virusCount=0;
    private List<AppVirusInfo> virusApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_virus);

        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        tv_scan = (TextView) findViewById(R.id.tv_scan);
        //tv_scan.setVisibility(View.INVISIBLE);
        ll_container = (ListView) findViewById(R.id.ll_container);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        pm = getPackageManager();
        virusApp = new ArrayList<AppVirusInfo>();
        appVirusInfos = new ArrayList<AppVirusInfo>();
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scan.startAnimation(ra);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        //��Ϣ��·
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SCANING:
                        AppVirusInfo scanInfo = (AppVirusInfo) msg.obj;
                        tv_scan_status.setText("����ɨ�裺" + scanInfo.appName);
                        tv_scan.setText("����" + appCount + "�������" + "����ɨ���" + (1 + progress) + "�����");
                        //����list����
                        appVirusInfos.add(0, scanInfo);
                        if (adapter == null) {
                            adapter = new MyAdapter();
                            ll_container.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                        break;
                    case FINISH:
                        tv_scan_status.setText("ɨ�����");
                        tv_scan.setText("��ɶ�" + appCount + "������Ĳ�ɱ:����" + virusApp.size() + "����������");
                        iv_scan.clearAnimation();
                        //���ֲ��� �����û�ж�����
                        if (virusApp.size() > 0) {
                            //���
                            appVirusInfos.removeAll(appVirusInfos);
                            //ֻ��ʾ��������
                            appVirusInfos.addAll(virusApp);
                            adapter.notifyDataSetChanged();
                            //�û���������ж��
                            ll_container.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    virusInfo=appVirusInfos.get(position);
                                    uninstallAppcation();
                                }
                            });
                        }
                        break;
                }
            }
        };
        scanVirus();
    }

    /**
     * ж��Ӧ��
     */
    private void uninstallAppcation() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ж��Ӧ�ó���");
            builder.setMessage("ж��"+virusInfo.appName);
            builder.setNegativeButton("����", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("ж��", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + virusInfo.packName));
                    startActivityForResult(intent, 0);

                }
            });
            builder.show();


    }

    /**
     * ж�����˸���ui
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        appVirusInfos.remove(virusInfo);
        adapter.notifyDataSetChanged();
    }

    /**
     * ɨ�財��
     */
    private void scanVirus() {
        tv_scan_status.setText("���ڳ�ʼ���Ʋ�ɱɱ������");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                appCount = packageInfos.size();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                progressBar1.setMax(appCount);

                for (PackageInfo packageInfo : packageInfos) {
                    AppVirusInfo appVirusInfo = new AppVirusInfo();
                   /* appVirusInfo.packName = applicationInfo.packageName;
                    appVirusInfo.appName = applicationInfo.loadLabel(pm).toString();
                    appVirusInfo.appLuncher = applicationInfo.loadIcon(pm);*/
                    appVirusInfo.packName = packageInfo.applicationInfo.packageName;
                    appVirusInfo.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                    appVirusInfo.appLuncher = packageInfo.applicationInfo.loadIcon(pm);
                    //�õ�Ӧ�ó���souceDir·��
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    //�����Ӧ�ó���md5ֵ
                    String md5 = getFileMd5(sourceDir);
                    //System.out.println(appVirusInfo.appName+":"+md5);
                    //��ѯ�������ݿ�
                    if (ScanVirsuDao.isVirsu(md5)) {      //�ǲ���
                        appVirusInfo.isVirus = true;
                        //�Ѳ�������İ�����������
                        virusApp.add(appVirusInfo);

                    } else {
                        appVirusInfo.isVirus = false;
                    }
                    //ɨ��һ��Ӧ������˸���ui
                    Message msg = Message.obtain();
                    msg.obj = appVirusInfo;
                    msg.what = SCANING;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress++;
                    progressBar1.setProgress(progress);
                }
                Message msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }).start();


    }


    /**
     * ��ȡ�ļ���md5ֵ
     *
     * @param path �ļ���ȫ·������
     * @return
     */
    private String getFileMd5(String path) {
        try {
            // ��ȡһ���ļ���������Ϣ��ǩ����Ϣ��
            File file = new File(path);
            // md5
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                // ������
                int number = b & 0xff;// ����
                String str = Integer.toHexString(number);
                // System.out.println(str);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    class AppVirusInfo {
        String packName;
        String appName;
        Drawable appLuncher;
        boolean isVirus;
    }

    class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return appVirusInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;

            if (convertView != null) { //���ö���
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(ScanVirusActivity.this, R.layout.list_item_scanvirus, null);
                holder = new ViewHolder();
                holder.appLuncher = (ImageView) view
                        .findViewById(R.id.iv_app_icon);
                holder.appName = (TextView) view
                        .findViewById(R.id.tv_app_name);
                holder.appsecurity = (TextView) view
                        .findViewById(R.id.tv_app_security);
                view.setTag(holder);
            }
            AppVirusInfo info = appVirusInfos.get(position);
            holder.appLuncher.setImageDrawable(info.appLuncher);
            holder.appName.setText(info.appName);
            if (info.isVirus) {
                holder.appsecurity.setText("���ֲ���,����ж��");
                holder.appsecurity.setTextColor(Color.RED);
            } else {
                holder.appsecurity.setText("ɨ�谲ȫ");
                holder.appsecurity.setTextColor(Color.BLACK);
            }
            return view;
        }
    }

    class ViewHolder {
        ImageView appLuncher;
        TextView appName;
        TextView appsecurity;
    }
}
