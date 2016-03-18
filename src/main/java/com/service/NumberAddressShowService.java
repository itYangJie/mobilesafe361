package com.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.db.dao.QueryPhoneNumAdd;
import com.mobilesafe.R;

public class NumberAddressShowService extends Service {
    private OutgoingCallReceiver receiver = null;
    private TelephonyManager tm = null;
    private MyPhoneStateListener listener = null;
    private WindowManager wm = null;
    private View view = null;
    private WindowManager.LayoutParams params=null;
    private SharedPreferences sp = null;
    //双击居中显示
    long[] times =new long[2];

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sp=getSharedPreferences("config",MODE_PRIVATE);
        receiver = new OutgoingCallReceiver();
        listener=new MyPhoneStateListener();
        //实例化窗体
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        //用代码注册广播接受者注册
        IntentFilter intentFilter = new IntentFilter();//意图过滤器
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        intentFilter.setPriority(1000);
        registerReceiver(receiver, intentFilter);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //注册监听器
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

     class MyPhoneStateListener extends PhoneStateListener {
         //电话状态改变时触发该方法
         @Override
         public void onCallStateChanged(int state, String incomingNumber) {
             super.onCallStateChanged(state, incomingNumber);
             switch (state){
                 case TelephonyManager.CALL_STATE_RINGING ://铃声响起
                     String address = QueryPhoneNumAdd.query(incomingNumber);
                     //Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                     showMyToast(address);
                     break;
                 case TelephonyManager.CALL_STATE_IDLE ://空暇状态时取消土司显示
                     if(view!=null) {
                         wm.removeViewImmediate(view);
                     }
                     break;
                 case TelephonyManager.CALL_STATE_OFFHOOK ://挂断电话时
                     break;
             }

         }
     }

    /**
     * 显示自定义土司归属地显示框
     * @param s
     */
    private void showMyToast(String address) {
        view = View.inflate(this, R.layout.address_show, null);
        TextView textview  = (TextView) view.findViewById(R.id.tv_address);
        //用户可以移动归属地显示框
        view.setOnTouchListener(new View.OnTouchListener() {
            // 定义手指的初始化位置,结束位置
            int startX,startY,endX,endY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //得到触摸位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动中的位置
                        endX = (int) event.getRawX();
                        endY = (int) event.getRawY();
                        //偏移量
                        int dx = endX-startX;
                        int dy = endY-startY;
                        params.x+=dx;
                        params.y+=dy;
                        // 考虑边界问题
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > (wm.getDefaultDisplay().getWidth() - view
                                .getWidth())) {
                            params.x = (wm.getDefaultDisplay().getWidth() - view
                                    .getWidth());
                        }
                        if (params.y > (wm.getDefaultDisplay().getHeight() - view
                                .getHeight())) {
                            params.y = (wm.getDefaultDisplay().getHeight() - view
                                    .getHeight());
                        }

                        //在窗体中更新显示位置
                        wm.updateViewLayout(view,params);
                        //刷新起始位置
                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        //记录位置,以便下次直接显示到指定的位置
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastX", params.x);
                        editor.putInt("lastY", params.y);
                        editor.commit();
                        break;
                }
                return true;
            }
        });
        //显示框双击居中
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(times, 1, times, 0, times.length - 1);
                times[times.length - 1] = SystemClock.uptimeMillis();
                if (times[times.length - 1] - times[0] <= 500) {//双击居中
                    params.x = (wm.getDefaultDisplay().getWidth() - view.getWidth()) / 2;
                    wm.updateViewLayout(view, params);
                    //保存位置
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("lastX", params.x);
                    editor.commit();
                }
            }
        });
        //"半透明","活力橙","卫士蓝","金属灰","苹果绿"
        int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
                ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        view.setBackgroundResource(ids[sp.getInt("which", 0)]);
        textview.setText(address);
        //窗体的参数就设置好了
        params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //得到用户上次移动到的位置
        params.x = sp.getInt("lastX", 0);
        params.y = sp.getInt("lastY", 0);

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        // android系统里面具有电话优先级的一种窗体类型，记得添加权限。
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        wm.addView(view, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //service销毁时取消注册广播接受者
        unregisterReceiver(receiver);
        receiver = null;
        //取消监听
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;
    }

    /**
     * 接收外拨电话广播的广播接收者内部类
     */
    class OutgoingCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //查询数据库得到归属地信息并显示出来
            String address = QueryPhoneNumAdd.query(getResultData());
            System.out.println(address);
            //Toast.makeText(context, address,Toast.LENGTH_LONG).show();
            showMyToast(address);
        }
    }
}
