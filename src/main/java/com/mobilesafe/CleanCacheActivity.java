package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class CleanCacheActivity extends Activity {
    private ProgressBar pb;
    private TextView tv_scan_status;
    private PackageManager pm;
    private ListView ll_container;
    private MyAdapter adapter;
    private List<AppCacheInfo> appCacheInfos;
    private boolean flag = false;
    private LinearLayout ll_loading;
    private AppCacheInfo deleteCacheApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_clean_cache);

        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        pb = (ProgressBar) findViewById(R.id.pb);
        pm = getPackageManager();
        ll_container = (ListView) findViewById(R.id.ll_container);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        appCacheInfos = new ArrayList<AppCacheInfo>();
        scanCache();
    }


    /**
     * 扫描手机里面所有应用程序的缓存信息
     */
    private void scanCache() {
        new Thread() {
            public void run() {
                Method getPackageSizeInfoMethod = null;
                try {
                    getPackageSizeInfoMethod = PackageManager.class.getDeclaredMethod("getPackageSizeInfo",
                            String.class, IPackageStatsObserver.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                /*Method[] methods = PackageManager.class.getMethods();
                for (Method method : methods) {
                    if ("getPackageSizeInfo".equals(method.getName())) {
                        getPackageSizeInfoMethod = method;
                    }
                }*/
                //List<PackageInfo> packInfos = pm.getInstalledPackages(0);
                List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);
                pb.setMax(applicationInfos.size());
                int progress = 0;
                for (ApplicationInfo applicationInfo : applicationInfos) {
                    int uid = applicationInfo.uid;
                    try {

                        getPackageSizeInfoMethod.invoke(pm, applicationInfo.packageName, new MyDataObserver());
                        Thread.sleep(60);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progress++;
                    pb.setProgress(progress);
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tv_scan_status.setText("扫描完毕...");
                        flag = true;
                    }
                });
            }
        }.start();

    }

    private class MyDataObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            final long cache = pStats.cacheSize;
            //System.out.println("缓存大小"+pStats.cacheSize);
            long code = pStats.codeSize;
            long data = pStats.dataSize;
            final String packname = pStats.packageName;
            final ApplicationInfo appInfo;
            try {
                appInfo = pm.getApplicationInfo(packname, 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_scan_status.setText("正在扫描：" + appInfo.loadLabel(pm));
                        if (cache > 0) {
                            //需要更新ui
                            AppCacheInfo appCacheInfo = new AppCacheInfo();
                            appCacheInfo.packName = packname;
                            appCacheInfo.appName = appInfo.loadLabel(pm).toString();
                            appCacheInfo.appLuncher = appInfo.loadIcon(pm);
                            appCacheInfo.cacheSize = cache;

                            appCacheInfos.add(0, appCacheInfo);
                            if (adapter == null) {
                                adapter = new MyAdapter();
                                ll_container.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Dialog dialog;

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appCacheInfos.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;

            if (convertView != null) { //复用对象
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(CleanCacheActivity.this, R.layout.list_item_cleancache, null);
                holder = new ViewHolder();
                holder.appLuncher = (ImageView) view
                        .findViewById(R.id.iv_app_icon);
                holder.appName = (TextView) view
                        .findViewById(R.id.tv_app_name);
                holder.cacheSize = (TextView) view
                        .findViewById(R.id.tv_cache_size);
                holder.delete = (ImageView) view
                        .findViewById(R.id.iv_delete);
                view.setTag(holder);
            }
            final AppCacheInfo info = appCacheInfos.get(position);
            holder.appLuncher.setImageDrawable(info.appLuncher);
            holder.appName.setText(info.appName);
            holder.cacheSize.setText("缓存大小:" + Formatter.formatFileSize(getApplicationContext(), info.cacheSize));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CleanCacheActivity.this);
                    builder.setTitle("清理垃圾");
                    builder.setMessage("点击确定将进入软件设置界面，您可以在那里清理应用程序的垃圾。为了避免麻烦，建议您一键清理");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           /* Intent intent = new Intent();
                            intent.setAction("android.settings.APPLICATION_DETAILS.SETTINGS");
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:"+info.packName));
                            startActivity(intent);*/
                            /*Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts(SCHEME, packageName, null);
                            intent.setData(uri);
                            startActivity(intent);*/
                            Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            String pkg = "com.android.settings";
                            String cls = "com.android.settings.applications.InstalledAppDetails";
                            i.setComponent(new ComponentName(pkg, cls));
                            i.setData(Uri.parse("package:" + info.packName));
                            deleteCacheApp = info;
                            startActivityForResult(i, 0);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.show();
                }
            });
            return view;
        }
    }

    /**
     * 用户进入设置界面清理缓存返回后
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Method getPackageSizeInfoMethod = null;
                try {
                    getPackageSizeInfoMethod = PackageManager.class.getDeclaredMethod("getPackageSizeInfo",
                            String.class, IPackageStatsObserver.class);
                    getPackageSizeInfoMethod.invoke(pm, deleteCacheApp.packName, new IPackageStatsObserver.Stub() {
                        @Override
                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                            final long cache = pStats.cacheSize;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (cache == 0) {   //用户清理了缓存 更新ui
                                        appCacheInfos.remove(deleteCacheApp);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(CleanCacheActivity.this, "缓存已清理", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class AppCacheInfo {
        String packName;
        String appName;
        Drawable appLuncher;
        long cacheSize;
    }

    class ViewHolder {
        ImageView appLuncher;
        TextView appName;
        TextView cacheSize;
        ImageView delete;
    }

    private class MypackDataObserver extends IPackageDataObserver.Stub {
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded)
                throws RemoteException {
            System.out.println(packageName + succeeded);

        }
    }

    private static long getEnvironmentSize() {
        File localFile = Environment.getDataDirectory();
        long l1;
        if (localFile == null)
            l1 = 0L;
        while (true) {

            String str = localFile.getPath();
            StatFs localStatFs = new StatFs(str);
            long l2 = localStatFs.getBlockSize();
            l1 = localStatFs.getBlockCount() * l2;
            return l1;
        }
    }


    /**
     * 清理手机的全部缓存.
     *
     * @param view
     */
    public void clearAll(View view) {
        if (!flag) {
            Toast.makeText(CleanCacheActivity.this, "还在扫描垃圾中，不要猴急嘛！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (appCacheInfos.size() == 0) {
            Toast.makeText(CleanCacheActivity.this, "已结很干净了哦，下次再来吧", Toast.LENGTH_SHORT).show();
            return;
        }
        ll_loading.setVisibility(View.VISIBLE);
        Class[] arrayOfClass = new Class[2];
        Class localClass2 = Long.TYPE;
        arrayOfClass[0] = localClass2;
        arrayOfClass[1] = IPackageDataObserver.class;
        Method localMethod = null;
        try {
            localMethod = pm.getClass().getMethod("freeStorageAndNotify", arrayOfClass);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Long localLong = Long.valueOf(getEnvironmentSize() - 1L);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = localLong;
        try {
            localMethod.invoke(pm, localLong, new IPackageDataObserver.Stub() {
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    // TODO Auto-generated method stub
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        appCacheInfos.removeAll(appCacheInfos);
        if (adapter == null) {
            adapter = new MyAdapter();
        }
        adapter.notifyDataSetChanged();
        ll_loading.setVisibility(View.INVISIBLE);
        Toast.makeText(CleanCacheActivity.this, "恭喜，垃圾清理完成，手机飞一般的快", Toast.LENGTH_SHORT).show();
        //真机测试报异常
       /* Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm,Integer.MAX_VALUE, new MypackDataObserver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                appCacheInfos.removeAll(appCacheInfos);
                adapter.notifyDataSetChanged();
                Toast.makeText(CleanCacheActivity.this, "清理完成", Toast.LENGTH_SHORT).show();
                return;
            }
        }*/

        //真机测试报异常
       /* try {
            Method method = PackageManager.class.getDeclaredMethod("freeStorageAndNotify",
                    long.class, IPackageDataObserver.class);
            method.invoke(pm, Integer.MAX_VALUE,new MypackDataObserver());

        } catch (Exception e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appCacheInfos.removeAll(appCacheInfos);
                adapter.notifyDataSetChanged();
                Toast.makeText(CleanCacheActivity.this,"清理完成",Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}
