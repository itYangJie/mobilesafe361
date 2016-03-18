package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ui.SlideMenu;
import com.utils.MD5Utils;

import java.util.ArrayList;


public class HomeActivity extends Activity  {
    private GridView gv_home_item = null;
    private SharedPreferences sp = null;
    private ViewPager vp = null;
    private TextView home_text  = null;
    private LinearLayout ll_home  = null;
    private SlideMenu slideMenu = null;
    private ImageView btn_back = null;
    private ArrayList<ImageView> imageViews;
    private static String[] names = {
            "手机防盗", "通讯卫士", "软件管理",
            "进程管理", "手机杀毒",
            "垃圾清理", "高级工具", "设置中心"

    };



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            vp.setCurrentItem(vp.getCurrentItem()+1);
            handler.sendEmptyMessageDelayed(0,1500);
        }
    };
    private static int[] ids = {
            R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.taskmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings

    };
    private static int[] imageResIds = new int[]{R.drawable.smile,
            R.drawable.b,R.drawable.d,R.drawable.f,R.drawable.h,
            R.drawable.g
    };
    private String[] picDes = {"微笑面对生活","美丽的风景在心里边","人生其实就是骑自行车，不努力前进就会摔倒"
    ,"在乎我们的没有几个","读书给你一个更好的视角","往南往北，人生就是旅途呀哈"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        gv_home_item = (GridView) findViewById(R.id.gv_home_item);
        vp = (ViewPager) findViewById(R.id.vp);

        home_text = (TextView)findViewById(R.id.home_text);
        ll_home  =(LinearLayout)findViewById(R.id.ll_home);

        slideMenu = (SlideMenu)findViewById(R.id.slideMenu);
        btn_back = (ImageView)findViewById(R.id.btn_back);

        //点击打开或者关闭侧滑菜单
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideMenu.switchMenu();
            }
        });
        //滑动的图片
        imageViews = new ArrayList<ImageView>();
        for (int i = 0; i < imageResIds.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageResIds[i]);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageViews.add(imageView);
        }



        //给viewPager添加数据
        vp.setAdapter(new MyAdapter());


        //给viewPager设置监听器
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                home_text.setText(picDes[position%imageResIds.length]);
                for(int i=0;i<imageResIds.length;i++){
                    if(i==position%imageResIds.length){
                        ll_home.getChildAt(i).setEnabled(true);
                    }else {
                        ll_home.getChildAt(i).setEnabled(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for(int i=0;i<imageResIds.length;i++){
            View view = new View(this);
            LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(25,25);
            if(i==0){
                params.leftMargin=20;

            }
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.selector_dot);
            ll_home.addView(view);
        }
        vp.setCurrentItem(Integer.MAX_VALUE/2-(Integer.MAX_VALUE/2)%(imageResIds.length));
        home_text.setText(picDes[0]);

        for(int i=0;i<imageResIds.length;i++){
            if(i==0){
                ll_home.getChildAt(i).setEnabled(true);
            }else {
                ll_home.getChildAt(i).setEnabled(false);
            }
        }
        //位GridView设置adapter
        gv_home_item.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return names.length;
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

                View view = getLayoutInflater().inflate(R.layout.home_item, null);

                TextView tx_item = (TextView) view.findViewById(R.id.tv_item);
                ImageView lv_item = (ImageView) view.findViewById(R.id.iv_item);

                lv_item.setImageResource(ids[position]);
                tx_item.setText(names[position]);
                return view;
            }
        });


      /*  //为主页设置出现的动画效果
        ScaleAnimation am = new ScaleAnimation(0.1f, 2.0f, 0.1f, 2.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(600);
        am.setRepeatCount(1);*/
        AlphaAnimation am = new AlphaAnimation(0.3f,1.0f);
        am.setDuration(800);

        //第一次启动程序时弹出欢迎对话框
        boolean isFirstUse = sp.getBoolean("isFirstUse", true);
        if (isFirstUse) {
            showWelcomeDialog();
        }

        //为GridView设置监听器
        gv_home_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //点击了 手机防盗
                    case 0: {
                        //判断是否设置过密码
                        if (TextUtils.isEmpty(sp.getString("password", ""))) {
                            //未设置过密码，弹出设置密码对话框
                            showSetPwdDialog();

                        } else { //设置过密码，弹出输入密码对话框

                            //判断用户是否在未退出应用期间再次进入手机防盗界面
                            boolean needPwd = sp.getBoolean("needPwd", true);
                            if (needPwd) {
                                showInputPwdDialog();
                            } else {
                                // 直接进入手机防盗页面
                                Intent intent = new Intent();
                                intent.setClass(HomeActivity.this, MobileGuardActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                            }
                        }
                        break;
                    }
                    //通讯卫士
                    case 1: {
                        Intent intent = new Intent(HomeActivity.this, ContactSafeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                    //软件管家
                    case 2: {
                        Intent intent = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }//进程管理
                    case 3: {
                        Intent intent = new Intent(HomeActivity.this, ProcessManagerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }

                    case 4: {
                        //进入手机杀毒
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, ScanVirusActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                    case 5: {
                        //进入缓存清理
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, CleanCacheActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                    case 6: {
                        //进入高级工具
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, HighToolActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                    //点击了 设置中心
                    case 7: {
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, SeettingCenterActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                }
            }
        });
        handler.sendEmptyMessageDelayed(0,1500);
    }


    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //实例化item
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            //将每个图片加入到ViewPager里
            /*ViewGroup.LayoutParams params = imageViews.get(arg1).getLayoutParams();
            params.height=ViewGroup.LayoutParams.MATCH_PARENT;
            params.width=ViewGroup.LayoutParams.MATCH_PARENT;

            imageViews.get(arg1).setLayoutParams(params);*/

            ((ViewPager) arg0).addView(imageViews.get(arg1%imageResIds.length));
            return imageViews.get(arg1%imageResIds.length);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            //将每个图片在ViewPager里释放掉
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //view 和 Object 是不是一个对象
            return arg0 == arg1;
        }


    }

    /*@Override
    protected void onStart() {
        //用一个定时器  来完成图片切换
        //Timer 与 ScheduledExecutorService 实现定时器的效果
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //通过定时器 来完成 每2秒钟切换一个图片
        //经过指定的时间后，执行所指定的任务
        //scheduleAtFixedRate(command, initialDelay, period, unit)
        //command 所要执行的任务
        //initialDelay 第一次启动时 延迟启动时间
        //period  每间隔多次时间来重新启动任务
        //unit 时间单位
        scheduledExecutorService.scheduleAtFixedRate(new ViewPagerTask(), 500, 1500, TimeUnit.MILLISECONDS);
        super.onStart();
    }*/

   /* @Override
    protected void onStop() {
        //停止图片切换
        scheduledExecutorService.shutdown();
        super.onStop();
    }*/

   /* private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            //设定viewPager当前页面
            vp.setCurrentItem(currentItem);
        }
    };*/

   /* //用来完成图片切换的任务
    private class ViewPagerTask implements Runnable {

        public void run() {
            //实现我们的操作
            //改变当前页面
            currentItem = (currentItem + 1) % imageViews.size();
            //Handler来实现图片切换
            handler.obtainMessage().sendToTarget();
        }
    }*/


    /**
     * 输入密码对话框函数
     */
    Dialog dialogInputPwd = null;

    private void showInputPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        //由布局文件得到view
        View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
        //由view得到布局中的控件
        final EditText et_enter_pwd = (EditText) view.findViewById(R.id.et_enter_pwd);
        final Button ok = (Button) view.findViewById(R.id.ok);
        final Button cancel = (Button) view.findViewById(R.id.cancel);

        //点击取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInputPwd.dismiss();
                return;
            }
        });
        //点击确定
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_enter_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, "亲，输入密码为空", Toast.LENGTH_LONG).show();
                    et_enter_pwd.setText("");
                    return;
                }
                //判断密码是否正确
                if (MD5Utils.md5Password(pwd).equals(sp.getString("password", ""))) {
                    //密码正确，取消对话框
                    dialogInputPwd.dismiss();
                    //保存数据，用户已经成功输入密码，则在未退出应用期间可以不输入密码
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("needPwd", false);
                    editor.commit();

                    // 进入手机防盗页面
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, MobileGuardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                } else {
                    Toast.makeText(HomeActivity.this, "亲，密码错误哟，好好想一想", Toast.LENGTH_LONG).show();
                    et_enter_pwd.setText("");
                    return;
                }
            }
        });
        builder.setView(view);
        dialogInputPwd = builder.show();
    }

    /**
     * 设置密码对话框函数
     */
    Dialog dialogSetPwd = null;

    private void showSetPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        //由布局文件得到view
        View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
        //由view得到布局中的控件
        final EditText et_setPwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        final EditText et_setPwdConfirm = (EditText) view.findViewById(R.id.et_setup_confirm);
        final Button ok = (Button) view.findViewById(R.id.ok);
        final Button cancel = (Button) view.findViewById(R.id.cancel);

        //点击取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetPwd.dismiss();
                return;
            }
        });
        //点击确定
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_setPwd.getText().toString().trim();
                String pwd_confirm = et_setPwdConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
                    Toast.makeText(HomeActivity.this, "亲，密码不能设置为空", Toast.LENGTH_LONG).show();
                    et_setPwd.setText("");
                    et_setPwdConfirm.setText("");
                    return;
                }
                //判断两次输入密码是否相同
                if (pwd.equals(pwd_confirm)) {
                    //两次输入密码相等,可以保存密码
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", MD5Utils.md5Password(pwd));
                    editor.commit();

                    Toast.makeText(HomeActivity.this, "设置密码成功", Toast.LENGTH_LONG).show();
                    //取消对话框
                    dialogSetPwd.dismiss();
                    //进入手机防盗页面
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, MobileGuardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                } else {
                    //两次输入密码不相等,提示用户
                    Toast.makeText(HomeActivity.this, "亲，两次密码输入不相等", Toast.LENGTH_LONG).show();
                    et_setPwd.setText("");
                    et_setPwdConfirm.setText("");
                    return;
                }
            }
        });
        builder.setView(view);
        dialogSetPwd = builder.show();
    }

    //弹出欢迎对话框 仅首次启动软件时调用
    private void showWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("欢迎使用");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage("亲,你好!很荣幸您使用我开发的361手机卫士。\n希望您在使用的同时，能提出您宝贵的意见或建议," +
                "以便我能修复bug,再次感谢您的使用");
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
        //下次启动就不会弹出欢迎对话框了
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isFirstUse", false);
        editor.commit();
    }

    /*
    按两次back键退出
     */
    long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(HomeActivity.this, "再按一次退出361手机卫士", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //保存数据，用户退出程序,下次进入手机防盗需要密码
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("needPwd", true);
                editor.commit();
                finish();
                //OffersManager.getInstance(this).onAppExit();
                overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    /**
     * 侧滑菜单点击事件
     * @param view
     */
    public void slideMenuItemClick(View view){

        switch (view.getId()){
            case R.id.slideMenu_news:
                Toast.makeText(HomeActivity.this,"点击了新闻",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_read:
                Toast.makeText(HomeActivity.this,"点击了订阅",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_ties:
                Toast.makeText(HomeActivity.this,"点击了跟帖",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_pics:
                Toast.makeText(HomeActivity.this,"点击了图片",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_ugc:
                Toast.makeText(HomeActivity.this,"点击了话题",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_vote:
                Toast.makeText(HomeActivity.this,"点击了投票",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_focus:
                Toast.makeText(HomeActivity.this,"点击了联合阅读",Toast.LENGTH_SHORT).show();
                break;

        }

    }
}
