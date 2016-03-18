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
        //显示记载进度条和加载信息
        ll_loading.setVisibility(View.VISIBLE);

        tv_avail_rom.setText("内部可用：" + getAvailSpace(Environment.getDataDirectory().getAbsolutePath()));
        tv_avail_sd.setText("外部可用：" + getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()));
        fillDate();

        // 给listview注册一个滚动的监听器
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            // 滚动的时候调用的方法。
            // firstVisibleItem 第一个可见条目在listview集合里面的位置。
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                popWindowDismiss();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        tv_status.setText("系统自带的应用：" + systemAppInfos.size() + "个");
                        tv_status.setTextColor(Color.RED);
                        tv_status.setBackgroundColor(Color.GREEN);
                        //tv_status.setBackgroundResource(R.drawable.background2);
                    } else {
                        tv_status.setText("您安装的应用：" + userAppInfos.size() + "个");
                        tv_status.setTextColor(Color.BLUE);
                        tv_status.setBackgroundColor(Color.GRAY);
                        //tv_status.setBackgroundResource(R.drawable.background1);
                    }
                }
            }
        });
        //点击事件
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击的是TextView，不做处理
                if (position == 0 || position == userAppInfos.size() + 1) {
                    return;
                } else if (position <= userAppInfos.size()) {//点击的用户应用
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
                // 动画效果的播放必须要求窗体有背景颜色。
                // 透明颜色也是颜色
                popupWindow.setBackgroundDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                // 在代码里面设置的宽高值 都是像素。---》dip
                int dip = 60;
                int px = DensityUtil.dip2px(getApplicationContext(), dip);
                System.out.println("px=" + px);
                popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
                        px, location[1]);
                //动画效果
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
                //点击的是TextView，不做处理
                ViewHolder holder;
                if (position == 0 || position == userAppInfos.size() + 1) {
                    return true;
                } else if (position <= userAppInfos.size()) {//点击的用户应用
                    appInformation = userAppInfos.get(position - 1);
                } else {
                    appInformation = systemAppInfos.get(position - 1 - userAppInfos.size() - 1);
                }
                holder = (ViewHolder) view.getTag();
                if (dao.find(appInformation.getPackageName())) {   //解锁
                    dao.delete(appInformation.getPackageName());
                    holder.iv_lock.setImageResource(R.drawable.unlock);

                } else { //锁定
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
     * 把旧的弹出窗体关闭掉。
     */
    private void popWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * 子线程中得到安装程序信息
     */
    private void fillDate() {
        //开启线程加载程序信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfos = APPManager.getAppInfo(AppManagerActivity.this);
                userAppInfos = new ArrayList<APPInfo>();
                systemAppInfos = new ArrayList<APPInfo>();
                //分别得到用户程序和系统程序
                for (APPInfo appInfo : appInfos) {
                    //用户程序
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
                            tv_status.setText("您安装的应用：" + userAppInfos.size() + "个");
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
     * 弹窗中的点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //启动应用
            case R.id.ll_start:
                //Toast.makeText(AppManagerActivity.this,"启动"+appInformation.getAppName(),Toast.LENGTH_SHORT).show();
                startAppcation();
                break;
            //分享应用
            case R.id.ll_share:
                //Toast.makeText(AppManagerActivity.this,"分享"+appInformation.getAppName(),Toast.LENGTH_SHORT).show();
                shareAppcation();
                break;
            //卸载应用
            case R.id.ll_uninstall:
                //Toast.makeText(AppManagerActivity.this,"卸载"+appInformation.getAppName(),Toast.LENGTH_SHORT).show();
                uninstallAppcation();
                break;
            case R.id.ll_detail:
                detailAppcation();
                break;
        }
    }
    /**
     * 进入程序的详情页面
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
     * 卸载应用
     */
    private void uninstallAppcation() {
        if (!appInformation.isUserApp()) {
            Toast.makeText(AppManagerActivity.this, appInformation.getAppName() +
                    "为系统应用，不能卸载，只有获取root权限才可以卸载", Toast.LENGTH_SHORT).show();
            return;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("卸载应用程序");
            builder.setMessage("您真的要卸载该应用程序吗");
            builder.setNegativeButton("我手贱了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("我要卸载", new DialogInterface.OnClickListener() {
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
     * 启动应用
     */
    private void startAppcation() {
        // 查询这个应用程序的入口activity。 把他开启起来。
        PackageManager pm = getPackageManager();
        // Intent intent = new Intent();
        // intent.setAction("android.intent.action.MAIN");
        // intent.addCategory("android.intent.category.LAUNCHER");
        // //查询出来了所有的手机上具有启动能力的activity。
        // List<ResolveInfo> infos = pm.queryIntentActivities(intent,
        // PackageManager.GET_INTENT_FILTERS);
        Intent intent = pm.getLaunchIntentForPackage(appInformation.getPackageName());
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "对不起，不能启动当前应用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 卸载完了更新ui
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillDate();

        tv_avail_rom.setText("内部可用：" + getAvailSpace(Environment.getDataDirectory().getAbsolutePath()));
        tv_avail_sd.setText("外部可用：" + getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()));
    }

    /**
     * 分享应用
     */
    private void shareAppcation() {
        // Intent { act=android.intent.action.SEND typ=text/plain flg=0x3000000 cmp=com.android.mms/.ui.ComposeMessageActivity (has extras) } from pid 256
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "哈哈哈！推荐你使用一款软件,名称叫：" + appInformation.getAppName() +
                "，超级好玩哦，一定要去下载哟");
        startActivity(intent);
    }


    /**
     * 自定义BaseAdapter
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //加2是因为还要显示两个提示信息，系统程序或者用户程序
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
            if (position == 0) {// 显示的是用程序有多少个的小标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                //tv.setBackgroundResource(R.drawable.background1);
                //tv.setHeight(32);
                //tv.setBackgroundColor(Color.GREEN);
                //tv.setTextSize(20);
                tv.setText("您安装的应用：" + userAppInfos.size() + "个");
                return tv;
            } else if (position == (userAppInfos.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(20);
                //tv.setBackgroundResource(R.drawable.background2);
                //tv.setHeight(32);
                tv.setBackgroundColor(Color.GREEN);
                tv.setText("系统自带的应用：" + systemAppInfos.size() + "个");
                return tv;
            } else if (position <= userAppInfos.size()) {// 用户程序
                int newposition = position - 1;// 因为多了一个textview的文本占用了位置
                //int newposition = position;
                appInfo = userAppInfos.get(newposition);
            } else {// 系统程序
                int newposition = position - 1 - userAppInfos.size() - 1;
                // int newposition = position - 1 - userAppInfos.size();
                appInfo = systemAppInfos.get(newposition);
            }

            if (convertView != null && convertView instanceof RelativeLayout) {
                // 不仅需要检查是否为空，还要判断是否是合适的类型去复用
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
            //查询数据库得到该应用是否处于锁定状态
            if (dao.find(appInfo.getPackageName())) {
                holder.iv_lock.setImageResource(R.drawable.lock);
            } else {
                holder.iv_lock.setImageResource(R.drawable.unlock);
            }
            if (appInfo.isInRom()) {
                holder.tv_location.setText("手机内存");
            } else {
                holder.tv_location.setText("外部存储");
            }
            return view;
        }
    }

    /**
     * 根据路径得到路径下的的可用空间
     *
     * @param path
     * @return
     */
    public String getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        //得到扇区大小和可用扇区数量
        long blockSize = statFs.getBlockSizeLong();
        long availBlockCount = statFs.getAvailableBlocksLong();
        //相乘即为可用空间大小
        long availSpace = blockSize * availBlockCount;

        return android.text.format.Formatter.formatFileSize(this, availSpace);
    }

    /**
     * 容器
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
