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
    //˫��������ʾ
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
        //ʵ��������
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        //�ô���ע��㲥������ע��
        IntentFilter intentFilter = new IntentFilter();//��ͼ������
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        intentFilter.setPriority(1000);
        registerReceiver(receiver, intentFilter);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //ע�������
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

     class MyPhoneStateListener extends PhoneStateListener {
         //�绰״̬�ı�ʱ�����÷���
         @Override
         public void onCallStateChanged(int state, String incomingNumber) {
             super.onCallStateChanged(state, incomingNumber);
             switch (state){
                 case TelephonyManager.CALL_STATE_RINGING ://��������
                     String address = QueryPhoneNumAdd.query(incomingNumber);
                     //Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                     showMyToast(address);
                     break;
                 case TelephonyManager.CALL_STATE_IDLE ://��Ͼ״̬ʱȡ����˾��ʾ
                     if(view!=null) {
                         wm.removeViewImmediate(view);
                     }
                     break;
                 case TelephonyManager.CALL_STATE_OFFHOOK ://�Ҷϵ绰ʱ
                     break;
             }

         }
     }

    /**
     * ��ʾ�Զ�����˾��������ʾ��
     * @param s
     */
    private void showMyToast(String address) {
        view = View.inflate(this, R.layout.address_show, null);
        TextView textview  = (TextView) view.findViewById(R.id.tv_address);
        //�û������ƶ���������ʾ��
        view.setOnTouchListener(new View.OnTouchListener() {
            // ������ָ�ĳ�ʼ��λ��,����λ��
            int startX,startY,endX,endY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //�õ�����λ��
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //�ƶ��е�λ��
                        endX = (int) event.getRawX();
                        endY = (int) event.getRawY();
                        //ƫ����
                        int dx = endX-startX;
                        int dy = endY-startY;
                        params.x+=dx;
                        params.y+=dy;
                        // ���Ǳ߽�����
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

                        //�ڴ����и�����ʾλ��
                        wm.updateViewLayout(view,params);
                        //ˢ����ʼλ��
                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        //��¼λ��,�Ա��´�ֱ����ʾ��ָ����λ��
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastX", params.x);
                        editor.putInt("lastY", params.y);
                        editor.commit();
                        break;
                }
                return true;
            }
        });
        //��ʾ��˫������
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(times, 1, times, 0, times.length - 1);
                times[times.length - 1] = SystemClock.uptimeMillis();
                if (times[times.length - 1] - times[0] <= 500) {//˫������
                    params.x = (wm.getDefaultDisplay().getWidth() - view.getWidth()) / 2;
                    wm.updateViewLayout(view, params);
                    //����λ��
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("lastX", params.x);
                    editor.commit();
                }
            }
        });
        //"��͸��","������","��ʿ��","������","ƻ����"
        int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
                ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        view.setBackgroundResource(ids[sp.getInt("which", 0)]);
        textview.setText(address);
        //����Ĳ��������ú���
        params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //�õ��û��ϴ��ƶ�����λ��
        params.x = sp.getInt("lastX", 0);
        params.y = sp.getInt("lastY", 0);

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        // androidϵͳ������е绰���ȼ���һ�ִ������ͣ��ǵ����Ȩ�ޡ�
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        wm.addView(view, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //service����ʱȡ��ע��㲥������
        unregisterReceiver(receiver);
        receiver = null;
        //ȡ������
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;
    }

    /**
     * �����Ⲧ�绰�㲥�Ĺ㲥�������ڲ���
     */
    class OutgoingCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //��ѯ���ݿ�õ���������Ϣ����ʾ����
            String address = QueryPhoneNumAdd.query(getResultData());
            System.out.println(address);
            //Toast.makeText(context, address,Toast.LENGTH_LONG).show();
            showMyToast(address);
        }
    }
}
