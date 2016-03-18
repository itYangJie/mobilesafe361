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
        //���ݿ�ҵ�������
        dao = new BlackNumberSQLiteDao(this);

        receiver = new StopSmsReceiver();
        listener = new StopBlackNumListener();
        //�绰�����߶���
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        //ע��㲥������
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);
        registerReceiver(receiver, filter);

        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    /**
     * �绰״̬������
     */
    class StopBlackNumListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:  //������������
                    String mode = dao.findMode(incomingNumber);
                    if ("1".equals(mode) || "3".equals(mode)) {//�绰���ػ���ȫ������
                        Log.i(TAG,"�Ҷϵ绰");
                        //ע�����ݹ۲���
                        getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {
                                super.onChange(selfChange);
                                getContentResolver().delete(Uri.parse("content://call_log/calls"),"number=?", new String[]{incomingNumber});
                                //ȡ��ע��
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
     * �Ҷϵ绰
     */
    private void endCall() {
        try {
            //�������
            Class classz = BlackNumStopService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = classz.getDeclaredMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            //���ص绰
            ITelephony.Stub.asInterface(ibinder).endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ���ض��ŵĹ㲥������
     */
    class StopSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            //��������
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                //�õ����ŷ�����
                String sender = smsMessage.getOriginatingAddress();
                String result = dao.findMode(sender);
                if ("2".equals(result) || "3".equals(result)) { //�������ػ���ȫ������
                    // Log.i(TAG,"���ض���");
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
        //��������ʱȡ��ע��Ĺ㲥������
        unregisterReceiver(receiver);
        receiver = null;
        //��������ʱȡ�������绰״̬
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
    }
}
