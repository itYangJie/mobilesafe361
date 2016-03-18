package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.domain.BlackNumberEntity;
import com.db.dao.BlackNumberSQLiteDao;

import java.util.List;


public class ContactSafeActivity extends Activity {

    private ListView list_blackNum = null;
    private BlackNumberSQLiteDao dao = null;
    private List<BlackNumberEntity> list =null;
    private RelativeLayout rl_blackNum = null;
    private MyBaseAdapter adapter=null;
    private ImageView img_404=null;
    private TextView tv_404=null;
    private ProgressBar pb = null;
    private TextView tv = null;
    private Dialog dialogUpdate; //修改拦截模式是弹出的对话框
    private Button btn_updateBOk,btn_updateBCancel;
    private CheckBox cbupdate_phone,cbupdate_sms;
    private TextView tv_blackNum;
    private int offset=0;
    //private View view404 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_safe);
        pb = (ProgressBar)findViewById(R.id.pb);
        tv = (TextView)findViewById(R.id.tv);
        dao = new BlackNumberSQLiteDao(this);
        rl_blackNum = (RelativeLayout)findViewById(R.id.rl_blackNum);
        list_blackNum=(ListView)findViewById(R.id.list_blackNum);
        //当数据库中无黑名单号码是界面显示的img tv
        img_404 = (ImageView)findViewById(R.id.img_404);
        tv_404 = (TextView)findViewById(R.id.tv_404);

        queryPart(offset, 20);

        // listview注册一个滚动事件的监听器。
        list_blackNum.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 当滚动的状态发生变化的时候。
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
                        // 判断当前listview滚动的位置
                        // 获取最后一个可见条目在集合里面的位置。
                        int lastposition = list_blackNum.getLastVisiblePosition();

                        // 集合里面有20个item 位置从0开始的 最后一个条目的位置 19
                        if (lastposition == (list.size() - 1)) {
                            System.out.println("列表被移动到了最后一个位置，加载更多的数据。。。");
                            offset += 20 ;  //在加载20条;
                            queryPart(offset,20);
                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 手指触摸滚动
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 惯性滑行状态
                        break;
                }
            }
            // 滚动的时候调用的方法。
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        list_blackNum.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new  AlertDialog.Builder(ContactSafeActivity.this);
                //将布局文件加载为view对象
                View dialogView =View .inflate(ContactSafeActivity.this,R.layout.dialog_updateblacknum,null);

                tv_blackNum=(TextView)dialogView.findViewById(R.id.tv_blackNum);
                btn_updateBOk=(Button)dialogView.findViewById(R.id.ok);
                btn_updateBCancel=(Button)dialogView.findViewById(R.id.cancel);
                cbupdate_phone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
                cbupdate_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);
                //显示当前号码
                tv_blackNum.setText(list.get(position).getNumber());
                //拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
                switch (Integer.valueOf(list.get(position).getMode())){
                    case 1:
                        cbupdate_phone.setChecked(true);
                        cbupdate_sms.setChecked(false);
                        break;
                    case 2:
                        cbupdate_sms.setChecked(true);
                        cbupdate_phone.setChecked(false);
                        break;
                    case 3:
                        cbupdate_phone.setChecked(true);
                        cbupdate_sms.setChecked(true);
                        break;
                }
                //取消修改拦截模式
                btn_updateBCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUpdate.dismiss();
                    }
                });
                //修改拦截模式
                btn_updateBOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //没有选择拦截模式
                        if((!cbupdate_phone.isChecked())&&(!cbupdate_sms.isChecked())){
                            Toast.makeText(ContactSafeActivity.this,"请选择拦截模式",Toast.LENGTH_SHORT).show();
                            return;
                        }  //拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
                        if((cbupdate_phone.isChecked())&&(!cbupdate_sms.isChecked())){
                            //拦截模式 1
                            dao.update(list.get(position).getNumber(), "1");
                            //更新ui
                            list.get(position).setMode("1");
                            adapter.notifyDataSetChanged();
                        }else
                        if((!cbupdate_phone.isChecked())&&(cbupdate_sms.isChecked())){
                            //拦截模式 2
                            dao.update(list.get(position).getNumber(),"2");
                            //更新ui
                            list.get(position).setMode("2");
                            adapter.notifyDataSetChanged();
                        }else
                        if((cbupdate_phone.isChecked())&&(cbupdate_sms.isChecked())){
                            //拦截模式 3
                            dao.update(list.get(position).getNumber(),"3");
                            //更新ui
                            list.get(position).setMode("3");
                            adapter.notifyDataSetChanged();
                        }
                        dialogUpdate.dismiss();
                    }
                });
                builder.setView(dialogView);
                dialogUpdate = builder.show();
                return true;
            }
        });

    }

    /**
     * 得到20个记录
     * @param offset
     * @param i
     * @return
     */
    private void queryPart( final int offset, final int i) {
        pb.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
      new Thread(){
          @Override
          public void run() {
              //模拟在网络中记载数据耗时操作
             /* try {
                  Thread.sleep(1000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }*/
              List<BlackNumberEntity> queryList= dao.findPart(offset, i);
              if (list == null) {//第一次记载
                  list = queryList;
                  if(list.size()==0||list==null){// 数据库中没有信息
                      img_404.setVisibility(View.VISIBLE);
                      tv_404.setVisibility(View.VISIBLE);
                  }
              } else { // 原来已经加载过数据了。
                  if(queryList==null||queryList.size()==0){//加载到底了
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Toast.makeText(ContactSafeActivity.this,"对不起，已经到底了",Toast.LENGTH_SHORT).show();
                          }
                      });

                  }else {
                      list.addAll(queryList);
                  }
              }
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      pb.setVisibility(View.INVISIBLE);
                      tv.setVisibility(View.INVISIBLE);
                      if (adapter == null) {
                          adapter = new MyBaseAdapter();
                          list_blackNum.setAdapter(adapter);
                      }else{
                          adapter.notifyDataSetChanged();
                      }
                  }
              });

          }
      }.start();
    }

    /**
     * 添加黑名单
     * @param view
     */
    private EditText et_addBlack;
    private Button btn_addBOk,btn_addBCancel;
    private Dialog addBlackDialog;
    private CheckBox cb_phone,cb_sms;
    public void addBlackNumber(final View view){
        AlertDialog.Builder builder = new  AlertDialog.Builder(ContactSafeActivity.this);
        //将布局文件加载为view对象
        View dialogView =View .inflate(ContactSafeActivity.this,R.layout.dialog_addblacknum,null);

        et_addBlack=(EditText)dialogView.findViewById(R.id.et_blackNum);
        btn_addBOk=(Button)dialogView.findViewById(R.id.ok);
        btn_addBCancel=(Button)dialogView.findViewById(R.id.cancel);
        cb_phone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
        cb_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);

        //取消添加
        btn_addBCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBlackDialog.dismiss();
            }
        });
        //添加
        btn_addBOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String blacknumber = et_addBlack.getText().toString().trim();
                if(TextUtils.isEmpty(blacknumber)){
                    Toast.makeText(getApplicationContext(), "黑名单号码不能为空", 0).show();
                    return;
                }
                String mode ;
                if(cb_phone.isChecked()&&cb_sms.isChecked()){
                    //全部拦截
                    mode = "3";
                }else if(cb_phone.isChecked()){
                    //电话拦截
                    mode = "1";
                }else if(cb_sms.isChecked()){
                    //短信拦截
                    mode = "2";
                }else{
                    Toast.makeText(getApplicationContext(), "请选择拦截模式", 0).show();
                    return;
                }
                //数据被加到数据库
                dao.insert(blacknumber, mode);
                //更新listview集合里面的内容。
                BlackNumberEntity info = new BlackNumberEntity();
                info.setMode(mode);
                info.setNumber(blacknumber);
                list.add(0, info);
                //数据库中有数据
                if(list.size()==1){
                    /*rl_blackNum.removeView(img_404);
                    rl_blackNum.removeView(tv_404);*/
                    img_404.setVisibility(View.INVISIBLE);
                    tv_404.setVisibility(View.INVISIBLE);
                }
                //通知listview数据适配器数据更新了。
                adapter.notifyDataSetChanged();
                addBlackDialog.dismiss();
                Toast.makeText(ContactSafeActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(dialogView);

        addBlackDialog = builder.show();

    }

    /**
     * 自定义BaseAdapter
     */
  /*  Dialog dialogUpdate; //修改拦截模式是弹出的对话框
    Button btn_updateBOk,btn_updateBCancel;
    CheckBox cbupdate_phone,cbupdate_sms;
    TextView tv_blackNum;*/
    class MyBaseAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
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
            if(convertView==null) {
                view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
            }else{
                view=convertView;
            }
            //得到控件
            final TextView number = (TextView)view.findViewById(R.id.tv_black_number);
            TextView mode = (TextView) view.findViewById(R.id.tv_block_mode);
            //起删除功能的图标
            ImageView iv_delete = (ImageView)view.findViewById(R.id.iv_delete);
            //点击将号码移除黑名单
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定要移除黑名单吗？");

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定删除
                            //删除数据库的内容
                            dao.delete(list.get(position).getNumber());
                            //更新界面。
                            list.remove(position);
                            //通知listview数据适配器更新
                            adapter.notifyDataSetChanged();
                            //数据库中没有数据了
                            if(list.size()==0){
                                img_404.setVisibility(View.VISIBLE);
                                tv_404.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });
            number.setText(list.get(position).getNumber());
            String modeString = list.get(position).getMode();
            if("1".equals(modeString)){
                mode.setText("电话拦截");
                mode.setTextColor(Color.BLUE);
            }else if("2".equals(modeString)){
                mode.setText("短信拦截");
                mode.setTextColor(Color.GRAY);
            }else{
                mode.setText("全部拦截");
                mode.setTextColor(Color.RED);
            }
           /* //点击是可以修改黑名单拦截模式
           view.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   AlertDialog.Builder builder = new  AlertDialog.Builder(ContactSafeActivity.this);
                   //将布局文件加载为view对象
                   View dialogView =View .inflate(ContactSafeActivity.this,R.layout.dialog_updateblacknum,null);

                   tv_blackNum=(TextView)dialogView.findViewById(R.id.tv_blackNum);
                   btn_updateBOk=(Button)dialogView.findViewById(R.id.ok);
                   btn_updateBCancel=(Button)dialogView.findViewById(R.id.cancel);
                   cbupdate_phone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
                   cbupdate_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);
                   //显示当前号码
                   tv_blackNum.setText(list.get(position).getNumber());
                   //拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
                   switch (Integer.valueOf(list.get(position).getMode())){
                       case 1:
                           cbupdate_phone.setChecked(true);
                           cbupdate_sms.setChecked(false);
                           break;
                       case 2:
                           cbupdate_sms.setChecked(true);
                           cbupdate_phone.setChecked(false);
                           break;
                       case 3:
                           cbupdate_phone.setChecked(true);
                           cbupdate_sms.setChecked(true);
                           break;
                   }
                   //取消修改拦截模式
                   btn_updateBCancel.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialogUpdate.dismiss();
                       }
                   });
                   //修改拦截模式
                   btn_updateBOk.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           //没有选择拦截模式
                           if((!cbupdate_phone.isChecked())&&(!cbupdate_sms.isChecked())){
                               Toast.makeText(ContactSafeActivity.this,"请选择拦截模式",Toast.LENGTH_SHORT).show();
                               return;
                           }  //拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
                           if((cbupdate_phone.isChecked())&&(!cbupdate_sms.isChecked())){
                               //拦截模式 1
                               dao.update(list.get(position).getNumber(), "1");
                               //更新ui
                               list.get(position).setMode("1");
                               adapter.notifyDataSetChanged();
                           }else
                           if((!cbupdate_phone.isChecked())&&(cbupdate_sms.isChecked())){
                               //拦截模式 2
                               dao.update(list.get(position).getNumber(),"2");
                               //更新ui
                               list.get(position).setMode("2");
                               adapter.notifyDataSetChanged();
                           }else
                           if((cbupdate_phone.isChecked())&&(cbupdate_sms.isChecked())){
                               //拦截模式 3
                               dao.update(list.get(position).getNumber(),"3");
                               //更新ui
                               list.get(position).setMode("3");
                               adapter.notifyDataSetChanged();
                           }
                           dialogUpdate.dismiss();
                       }
                   });
                   builder.setView(dialogView);
                   dialogUpdate = builder.show();
                   return true;
               }
           });*/
            return view;
        }
    }
}
