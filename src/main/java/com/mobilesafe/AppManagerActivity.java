package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.db.dao.AppLockSQLiteDao;
import com.domain.APPInfo;
import com.engine.APPManager;
import com.receiver.MyAdmin;
import com.utils.DensityUtil;
import com.utils.ServiceRunning;

import java.io.File;
import java.text.Format;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


public class AppManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_avail_rom = null;
    private TextView tv_avail_sd = null;
    private List<APPInfo> appInfos = null;
    private List<APPInfo> userAppInfos = null;
    private List<APPInfo> systemAppInfos = null;
    private LinearLayout ll_loading = null;
    private MyAdapter adapter = null;
    private ListView lv_app_manager = null;
    private TextView tv_status = null;
    private PopupWindow popupWindow = null;
    private APPInfo appInformation = null;
    private AppLockSQLiteDao dao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_manager);

        dao = new AppLockSQLiteDao(this);
        tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        tv_status = (TextView) findViewById(R.id.tv_status);
        //��ʾ���ؽ������ͼ�����Ϣ
        ll_loading.setVisibility(View.VISIBLE);

        tv_avail_rom.setText("�ڲ����ã�" + getAvailSpace(Environment.getDataDirectory().getAbsolutePath()));
        tv_avail_sd.setText("�ⲿ���ã�" + getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()));
        fillDate();

        // ��listviewע��һ�������ļ�����
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            // ������ʱ����õķ�����
            // firstVisibleItem ��һ���ɼ���Ŀ��listview���������λ�á�
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                popWindowDismiss();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        tv_status.setText("ϵͳ�Դ���Ӧ�ã�" + systemAppInfos.size() + "��");
                        tv_status.setTextColor(Color.RED);
                        tv_status.setBackgroundColor(Color.GREEN);
                        //tv_status.setBackgroundResource(R.drawable.background2);
                    } else {
                        tv_status.setText("����װ��Ӧ�ã�" + userAppInfos.size() + "��");
                        tv_status.setTextColor(Color.BLUE);
                        tv_status.setBackgroundColor(Color.GRAY);
                        //tv_status.setBackgroundResource(R.drawable.background1);
                    }
                }
            }
        });
        //����¼�
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //�������TextView����������
                if (position == 0 || position == userAppInfos.size() + 1) {
                    return;
                } else if (position <= userAppInfos.size()) {//������û�Ӧ��
                    appInformation = userAppInfos.get(position - 1);
                } else {
                    appInformation = systemAppInfos.get(position - 1 - userAppInfos.size() - 1);
                }
                popWindowDismiss();
                View contentView = View.inflate(getApplicationContext(),
                        R.layout.list_popwindow, null);
                LinearLayout ll_start = (LinearLayout) contentView
                        .findViewById(R.id.ll_start);
                LinearLayout ll_share = (LinearLayout) contentView
                        .findViewById(R.id.ll_share);
                LinearLayout ll_uninstall = (LinearLayout) contentView
                        .findViewById(R.id.ll_uninstall);
                LinearLayout ll_detail = (LinearLayout) contentView
                        .findViewById(R.id.ll_detail);
                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_detail.setOnClickListener(AppManagerActivity.this);

                popupWindow = new PopupWindow(contentView, -2, -2);
                // ����Ч���Ĳ��ű���Ҫ�����б�����ɫ��
                // ͸����ɫҲ����ɫ
                popupWindow.setBackgroundDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                // �ڴ����������õĿ��ֵ �������ء�---��dip
                int dip = 60;
                int px = DensityUtil.dip2px(getApplicationContext(), dip);
                System.out.println("px=" + px);
                popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
                        px, location[1]);
                //����Ч��
                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(300);
                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(300);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);
                contentView.startAnimation(set);

            }
        });

        lv_app_manager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //�������TextView����������
                ViewHolder holder;
                if (position == 0 || position == userAppInfos.size() + 1) {
                    return true;
                } else if (position <= userAppInfos.size()) {//������û�Ӧ��
                    appInformation = userAppInfos.get(position - 1);
                } else {
                    appInformation = systemAppInfos.get(position - 1 - userAppInfos.size() - 1);
                }
                holder = (ViewHolder) view.getTag();
                if (dao.find(appInformation.getPackageName())) {   //����
                    dao.delete(appInformation.getPackageName());
                    holder.iv_lock.setImageResource(R.drawable.unlock);

                } else { //����
                    dao.insert(appInformation.getPackageName());
                    holder.iv_lock.setImageResource(R.drawable.lock);
                }
                if (ServiceRunning.getServiceIsRunning(AppManagerActivity.this, "com.service.WatchDogService")) {
                    Intent intent = new Intent();
                    intent.setAction("com.mobilesafe.applockchange");
                    sendBroadcast(intent);
                }
                return true;
            }
        });

    }

    /**
     * �Ѿɵĵ�������رյ���
     */
    private void popWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * ���߳��еõ���װ������Ϣ
     */
    private void fillDate() {
        //�����̼߳��س�����Ϣ
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfos = APPManager.getAppInfo(AppManagerActivity.this);
                userAppInfos = new ArrayList<APPInfo>();
                systemAppInfos = new ArrayList<APPInfo>();
                //�ֱ�õ��û������ϵͳ����
                for (APPInfo appInfo : appInfos) {
                    //�û�����
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter == null) {
                            adapter = new MyAdapter();
                            lv_app_manager.setAdapter(adapter);
                            tv_status.setText("����װ��Ӧ�ã�" + userAppInfos.size() + "��");
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    /**
     * �����еĵ���¼�����
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //����Ӧ��
            case R.id.ll_start:
                //Toast.makeText(AppManagerActivity.this,"����"+appInformation.getAppName(),Toast.LENGTH_SHORT).show();
                startAppcation();
                break;
            //����Ӧ��
            case R.id.ll_share:
                //Toast.makeText(AppManagerActivity.this,"����"+appInformation.getAppName(),Toast.LENGTH_SHORT).show();
                shareAppcation();
                break;
            //ж��Ӧ��
            case R.id.ll_uninstall:
                //Toast.makeText(AppManagerActivity.this,"ж��"+appInformation.getAppName(),Toast.LENGTH_SHORT).show();
                uninstallAppcation();
                break;
            case R.id.ll_detail:
                detailAppcation();
                break;
        }
    }
    /**
     * ������������ҳ��
     */
    private void detailAppcation(){
        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        String pkg = "com.android.settings";
        String cls = "com.android.settings.applications.InstalledAppDetails";
        i.setComponent(new ComponentName(pkg, cls));
        i.setData(Uri.parse("package:" + appInformation.getPackageName()));
        startActivity(i);
    }
    /**
     * ж��Ӧ��
     */
    private void uninstallAppcation() {
        if (!appInformation.isUserApp()) {
            Toast.makeText(AppManagerActivity.this, appInformation.getAppName() +
                    "ΪϵͳӦ�ã�����ж�أ�ֻ�л�ȡrootȨ�޲ſ���ж��", Toast.LENGTH_SHORT).show();
            return;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ж��Ӧ�ó���");
            builder.setMessage("�����Ҫж�ظ�Ӧ�ó�����");
            builder.setNegativeButton("���ּ���", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("��Ҫж��", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + appInformation.getPackageName()));
                    startActivityForResult(intent, 0);

                }
            });
            builder.show();
        }

    }

    /**
     * ����Ӧ��
     */
    private void startAppcation() {
        // ��ѯ���Ӧ�ó�������activity�� ��������������
        PackageManager pm = getPackageManager();
        // Intent intent = new Intent();
        // intent.setAction("android.intent.action.MAIN");
        // intent.addCategory("android.intent.category.LAUNCHER");
        // //��ѯ���������е��ֻ��Ͼ�������������activity��
        // List<ResolveInfo> infos = pm.queryIntentActivities(intent,
        // PackageManager.GET_INTENT_FILTERS);
        Intent intent = pm.getLaunchIntentForPackage(appInformation.getPackageName());
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "�Բ��𣬲���������ǰӦ��", Toast.LENGTH_SHORT).show();
        }
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
        fillDate();

        tv_avail_rom.setText("�ڲ����ã�" + getAvailSpace(Environment.getDataDirectory().getAbsolutePath()));
        tv_avail_sd.setText("�ⲿ���ã�" + getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()));
    }

    /**
     * ����Ӧ��
     */
    private void shareAppcation() {
        // Intent { act=android.intent.action.SEND typ=text/plain flg=0x3000000 cmp=com.android.mms/.ui.ComposeMessageActivity (has extras) } from pid 256
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "���������Ƽ���ʹ��һ�����,���ƽУ�" + appInformation.getAppName() +
                "����������Ŷ��һ��Ҫȥ����Ӵ");
        startActivity(intent);
    }


    /**
     * �Զ���BaseAdapter
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //��2����Ϊ��Ҫ��ʾ������ʾ��Ϣ��ϵͳ��������û�����
            return appInfos.size() + 2;
            //return appInfos.size()+1;
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
            View view = null;
            ViewHolder holder = null;
            APPInfo appInfo = null;
            if (position == 0) {// ��ʾ�����ó����ж��ٸ���С��ǩ
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                //tv.setBackgroundResource(R.drawable.background1);
                //tv.setHeight(32);
                //tv.setBackgroundColor(Color.GREEN);
                //tv.setTextSize(20);
                tv.setText("����װ��Ӧ�ã�" + userAppInfos.size() + "��");
                return tv;
            } else if (position == (userAppInfos.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(20);
                //tv.setBackgroundResource(R.drawable.background2);
                //tv.setHeight(32);
                tv.setBackgroundColor(Color.GREEN);
                tv.setText("ϵͳ�Դ���Ӧ�ã�" + systemAppInfos.size() + "��");
                return tv;
            } else if (position <= userAppInfos.size()) {// �û�����
                int newposition = position - 1;// ��Ϊ����һ��textview���ı�ռ����λ��
                //int newposition = position;
                appInfo = userAppInfos.get(newposition);
            } else {// ϵͳ����
                int newposition = position - 1 - userAppInfos.size() - 1;
                // int newposition = position - 1 - userAppInfos.size();
                appInfo = systemAppInfos.get(newposition);
            }

            if (convertView != null && convertView instanceof RelativeLayout) {
                // ������Ҫ����Ƿ�Ϊ�գ���Ҫ�ж��Ƿ��Ǻ��ʵ�����ȥ����
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(),
                        R.layout.list_item_appinfo, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view
                        .findViewById(R.id.iv_app_icon);
                holder.tv_location = (TextView) view
                        .findViewById(R.id.tv_app_location);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.iv_lock = (ImageView) view.findViewById(R.id.iv_lock);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(appInfo.getAppLuncher());
            holder.tv_name.setText(appInfo.getAppName());
            //��ѯ���ݿ�õ���Ӧ���Ƿ�������״̬
            if (dao.find(appInfo.getPackageName())) {
                holder.iv_lock.setImageResource(R.drawable.lock);
            } else {
                holder.iv_lock.setImageResource(R.drawable.unlock);
            }
            if (appInfo.isInRom()) {
                holder.tv_location.setText("�ֻ��ڴ�");
            } else {
                holder.tv_location.setText("�ⲿ�洢");
            }
            return view;
        }
    }

    /**
     * ����·���õ�·���µĵĿ��ÿռ�
     *
     * @param path
     * @return
     */
    public String getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        //�õ�������С�Ϳ�����������
        long blockSize = statFs.getBlockSizeLong();
        long availBlockCount = statFs.getAvailableBlocksLong();
        //��˼�Ϊ���ÿռ��С
        long availSpace = blockSize * availBlockCount;

        return android.text.format.Formatter.formatFileSize(this, availSpace);
    }

    /**
     * ����
     */
    static class ViewHolder {
        TextView tv_name;
        TextView tv_location;
        ImageView iv_icon;
        ImageView iv_lock;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        popWindowDismiss();
    }
}
