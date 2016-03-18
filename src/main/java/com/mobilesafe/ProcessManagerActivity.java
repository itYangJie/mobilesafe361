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

            // ������ʱ����õķ�����
            // firstVisibleItem ��һ���ɼ���Ŀ��listview���������λ�á�
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (userTaskInfos != null && systemTaskInfos != null) {
                    if (firstVisibleItem > userTaskInfos.size()) {
                        tv_status.setText("ϵͳ���̣�" + systemTaskInfos.size() + "��");
                        tv_status.setTextColor(Color.RED);
                        tv_status.setBackgroundColor(Color.GREEN);
                    } else {
                        tv_status.setText("�û����̣�" + userTaskInfos.size() + "��");
                        tv_status.setTextColor(Color.BLUE);
                        tv_status.setBackgroundColor(Color.YELLOW);
                    }
                }
            }
        });
        /**
         * ѡ�����¼�
         */
        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProcessInfo processInfo;
                if (position == 0) {// �û����̵ı�ǩ
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
     * �°µ�ǰ�����߳����ԣ�ʣ��/���ڴ�  ��Ϣ
     */
    private void setTitle() {
        runingProcessCount = ProcessMangerUtil.getRunningProcessCount(ProcessManagerActivity.this);
        tv_process_count.setText("�����еĽ��̣�" +
                runingProcessCount + "��");
        availMemSize = ProcessMangerUtil.getAvailMem(this);
        totalMemSize = ProcessMangerUtil.getTotalMem(this);
        tv_mem_info.setText("ʣ��/���ڴ棺"
                + Formatter.formatFileSize(this, availMemSize) + "/"
                + Formatter.formatFileSize(this, totalMemSize));
    }


    /**
     * �����̻߳�ȡ����
     */
    private void fillDate() {
        //��ʾ���ؽ�����
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                taskInfos = ProcessManger.getRuningProcessInfo(ProcessManagerActivity.this);
                userTaskInfos = new ArrayList<ProcessInfo>();
                systemTaskInfos = new ArrayList<ProcessInfo>();
                for (ProcessInfo processInfo : taskInfos) {
                    if (processInfo.isUseerProcess()) {//�û�����
                        userTaskInfos.add(processInfo);
                    } else {//ϵͳ����
                        systemTaskInfos.add(processInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        tv_status.setText("�û����̣�" + userTaskInfos.size() + "��");
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
     * ȫѡ
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
     * ��ѡ
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
     * һ������
     *
     * @param view
     */
    public void killAll(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        long savedMem = 0;
        // ��¼��Щ��ɱ������Ŀ
        List<ProcessInfo> killedTaskinfos = new ArrayList<ProcessInfo>();
        for (ProcessInfo info : taskInfos) {
            if (info.isChecked()) { // ����ѡ�ģ�ɱ��������̡�
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
                "ɱ����" + count + "�����̣��ͷ���"
                        + Formatter.formatFileSize(this, savedMem) + "���ڴ�", Toast.LENGTH_SHORT)
                .show();
        runingProcessCount -= count;
        availMemSize += savedMem;
        tv_process_count.setText("�����еĽ��̣�" + runingProcessCount + "��");
        tv_mem_info.setText("ʣ��/���ڴ棺"
                + Formatter.formatFileSize(this, availMemSize) + "/"
                + Formatter.formatFileSize(this, totalMemSize));
    }

    /**
     * ��������
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
                // ��ʾ�����ó����ж��ٸ���С��ǩ
                TextView tv = new TextView(getApplicationContext());
                //tv.setTextColor(Color.WHITE);
               // tv.setBackgroundColor(Color.GREEN);
                //tv.setTextSize(20);
                tv.setText("�û����̣�" + userTaskInfos.size() + "��");
                return tv;
            } else if (position == userTaskInfos.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(20);
                tv.setBackgroundColor(Color.GREEN);
                tv.setText("ϵͳ���̣�" + systemTaskInfos.size() + "��");
                return tv;
            } else if (position <= userTaskInfos.size()) {//�û�����
                processInfo = userTaskInfos.get(position - 1);
            } else {//ϵͳ����
                processInfo = systemTaskInfos.get(position - userTaskInfos.size() - 1 - 1);
            }
            if (convertView != null && convertView instanceof RelativeLayout) { //���ö���
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
            holder.memorySize.setText("�ڴ�ռ�ã�"
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
