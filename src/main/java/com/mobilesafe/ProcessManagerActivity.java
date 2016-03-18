package com.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.domain.ProcessInfo;
import com.engine.ProcessManger;
import com.utils.ProcessMangerUtil;

import java.util.ArrayList;
import java.util.List;


public class ProcessManagerActivity extends Activity {
    private TextView tv_process_count = null;
    private TextView tv_mem_info = null;
    private int runingProcessCount;
    private long availMemSize;
    private long totalMemSize;
    private MyAdapter adapter = null;
    private LinearLayout ll_loading = null;
    private ListView lv_task_manager = null;
    private List<ProcessInfo> taskInfos = null;
    private List<ProcessInfo> userTaskInfos = null;
    private List<ProcessInfo> systemTaskInfos = null;
    private TextView tv_status = null;
    private SharedPreferences sp =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_process_manager);
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        tv_status = (TextView) findViewById(R.id.tv_status);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        setTitle();
        fillDate();
        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            // 滚动的时候调用的方法。
            // firstVisibleItem 第一个可见条目在listview集合里面的位置。
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (userTaskInfos != null && systemTaskInfos != null) {
                    if (firstVisibleItem > userTaskInfos.size()) {
                        tv_status.setText("系统进程：" + systemTaskInfos.size() + "个");
                        tv_status.setTextColor(Color.RED);
                        tv_status.setBackgroundColor(Color.GREEN);
                    } else {
                        tv_status.setText("用户进程：" + userTaskInfos.size() + "个");
                        tv_status.setTextColor(Color.BLUE);
                        tv_status.setBackgroundColor(Color.YELLOW);
                    }
                }
            }
        });
        /**
         * 选项点击事件
         */
        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProcessInfo processInfo;
                if (position == 0) {// 用户进程的标签
                    return;
                } else if (position == (userTaskInfos.size() + 1)) {
                    return;
                } else if (position <= userTaskInfos.size()) {
                    processInfo = userTaskInfos.get(position - 1);
                } else {
                    processInfo = systemTaskInfos.get(position - 1
                            - userTaskInfos.size() - 1);
                }
                if (getPackageName().equals(processInfo.getPackageName())) {
                    return;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (processInfo.isChecked()) {
                    processInfo.setChecked(false);
                    holder.checked.setChecked(false);
                } else {
                    processInfo.setChecked(true);
                    holder.checked.setChecked(true);
                }
            }
        });

    }

    /**
     * 德奥当前运行线程属性，剩余/总内存  信息
     */
    private void setTitle() {
        runingProcessCount = ProcessMangerUtil.getRunningProcessCount(ProcessManagerActivity.this);
        tv_process_count.setText("运行中的进程：" +
                runingProcessCount + "个");
        availMemSize = ProcessMangerUtil.getAvailMem(this);
        totalMemSize = ProcessMangerUtil.getTotalMem(this);
        tv_mem_info.setText("剩余/总内存："
                + Formatter.formatFileSize(this, availMemSize) + "/"
                + Formatter.formatFileSize(this, totalMemSize));
    }


    /**
     * 开启线程获取数据
     */
    private void fillDate() {
        //显示加载进度条
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                taskInfos = ProcessManger.getRuningProcessInfo(ProcessManagerActivity.this);
                userTaskInfos = new ArrayList<ProcessInfo>();
                systemTaskInfos = new ArrayList<ProcessInfo>();
                for (ProcessInfo processInfo : taskInfos) {
                    if (processInfo.isUseerProcess()) {//用户进程
                        userTaskInfos.add(processInfo);
                    } else {//系统进程
                        systemTaskInfos.add(processInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        tv_status.setText("用户进程：" + userTaskInfos.size() + "个");
                        tv_status.setBackgroundColor(Color.YELLOW);
                        if (adapter == null) {
                            adapter = new MyAdapter();
                            lv_task_manager.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        }).start();
    }

    /**
     * 全选
     *
     * @param view
     */
    public void selectAll(View view) {
        for (ProcessInfo info : taskInfos) {
            if (getPackageName().equals(info.getPackageName())) {
                continue;
            }
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param view
     */
    public void selectOppo(View view) {
        for (ProcessInfo info : taskInfos) {
            if (getPackageName().equals(info.getPackageName())) {
                continue;
            }
            if (info.isChecked()) {
                info.setChecked(false);
            } else {
                info.setChecked(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 一键清理
     *
     * @param view
     */
    public void killAll(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        long savedMem = 0;
        // 记录那些被杀死的条目
        List<ProcessInfo> killedTaskinfos = new ArrayList<ProcessInfo>();
        for (ProcessInfo info : taskInfos) {
            if (info.isChecked()) { // 被勾选的，杀死这个进程。
                am.killBackgroundProcesses(info.getPackageName());
                if (info.isUseerProcess()) {
                    userTaskInfos.remove(info);
                } else {
                    systemTaskInfos.remove(info);
                }
                killedTaskinfos.add(info);
                count++;
                savedMem += info.getMemorySize();
            }
        }

        taskInfos.removeAll(killedTaskinfos);
        adapter.notifyDataSetChanged();
        Toast.makeText(
                this,
                "杀死了" + count + "个进程，释放了"
                        + Formatter.formatFileSize(this, savedMem) + "的内存", Toast.LENGTH_SHORT)
                .show();
        runingProcessCount -= count;
        availMemSize += savedMem;
        tv_process_count.setText("运行中的进程：" + runingProcessCount + "个");
        tv_mem_info.setText("剩余/总内存："
                + Formatter.formatFileSize(this, availMemSize) + "/"
                + Formatter.formatFileSize(this, totalMemSize));
    }

    /**
     * 进入设置
     *
     * @param view
     */
    public void enterSetting(View view) {
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            if (sp.getBoolean("showSystemProcess", true)) {
                return userTaskInfos.size() + systemTaskInfos.size() + 1 + 1;
            }else {
                return userTaskInfos.size()+1;
            }
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
            ProcessInfo processInfo;
            ViewHolder holder;
            if (position == 0) {
                // 显示的是用程序有多少个的小标签
                TextView tv = new TextView(getApplicationContext());
                //tv.setTextColor(Color.WHITE);
               // tv.setBackgroundColor(Color.GREEN);
                //tv.setTextSize(20);
                tv.setText("用户进程：" + userTaskInfos.size() + "个");
                return tv;
            } else if (position == userTaskInfos.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(20);
                tv.setBackgroundColor(Color.GREEN);
                tv.setText("系统进程：" + systemTaskInfos.size() + "个");
                return tv;
            } else if (position <= userTaskInfos.size()) {//用户进程
                processInfo = userTaskInfos.get(position - 1);
            } else {//系统进程
                processInfo = systemTaskInfos.get(position - userTaskInfos.size() - 1 - 1);
            }
            if (convertView != null && convertView instanceof RelativeLayout) { //复用对象
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(ProcessManagerActivity.this, R.layout.list_item_process, null);
                holder = new ViewHolder();
                holder.appLuncher = (ImageView) view
                        .findViewById(R.id.iv_task_icon);
                holder.appName = (TextView) view
                        .findViewById(R.id.tv_task_name);
                holder.memorySize = (TextView) view
                        .findViewById(R.id.tv_task_memsize);
                holder.checked = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            holder.appLuncher.setImageDrawable(processInfo.getAppLuncher());
            holder.appName.setText(processInfo.getAppName());
            holder.memorySize.setText("内存占用："
                    + Formatter.formatFileSize(getApplicationContext(),
                    processInfo.getMemorySize()));
            holder.checked.setChecked(processInfo.isChecked());
            if (getPackageName().equals(processInfo.getPackageName())) {
                holder.checked.setVisibility(View.INVISIBLE);
            } else {
                holder.checked.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }
    class ViewHolder {
        ImageView appLuncher;
        TextView appName;
        TextView memorySize;
        CheckBox checked;
    }
}
