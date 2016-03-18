package com.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.db.dao.BlackNumberSQLiteDao;

import java.lang.reflect.Method;

public class BlackNumStopService extends Service {

    private static final String TAG ="BlackNumStopService" ;
    private StopSmsReceiver receiver = null;
    private BlackNumberSQLiteDao dao = null;
    private TelephonyManager tm = null;
    private StopBlackNumListener listener = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //数据库业务类对象
        dao = new BlackNumberSQLiteDao(this);

        receiver = new StopSmsReceiver();
        listener = new StopBlackNumListener();
        //电话管理者对象
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        //注册广播接受者
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);
        registerReceiver(receiver, filter);

        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    /**
     * 电话状态监听类
     */
    class StopBlackNumListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:  //来电铃声响起
                    String mode = dao.findMode(incomingNumber);
                    if ("1".equals(mode) || "3".equals(mode)) {//电话拦截或者全部拦截
                        Log.i(TAG,"挂断电话");
                        //注册内容观察者
                        getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {
                                super.onChange(selfChange);
                                getContentResolver().delete(Uri.parse("content://call_log/calls"),"number=?", new String[]{incomingNumber});
                                //取消注册
                                getContentResolver().unregisterContentObserver(this);
                            }
                        });
                        endCall();
                    }
                    break;
            }
        }
    }
    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            //反射机制
            Class classz = BlackNumStopService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = classz.getDeclaredMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            //拦截电话
            ITelephony.Stub.asInterface(ibinder).endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拦截短信的广播接收者
     */
    class StopSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            //遍历短信
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                //得到短信发件人
                String sender = smsMessage.getOriginatingAddress();
                String result = dao.findMode(sender);
                if ("2".equals(result) || "3".equals(result)) { //短信拦截或者全部拦截
                    // Log.i(TAG,"拦截短信");
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //服务销毁时取消注册的广播接收者
        unregisterReceiver(receiver);
        receiver = null;
        //服务销毁时取消监听电话状态
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
    }
}
