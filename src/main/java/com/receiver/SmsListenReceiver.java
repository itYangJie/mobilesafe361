package com.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.mobilesafe.R;
import com.service.LocationService;

import java.util.Objects;

public class SmsListenReceiver extends BroadcastReceiver {
    private SharedPreferences sp = null;
    private DevicePolicyManager dpm = null;

    //�����յ������ǵ��ø÷���
    @Override
    public void onReceive(Context context, Intent intent) {

        //�õ�����
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        //����ÿһ������
        for (Object obj : objs) {
            //һ������
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
            //�õ������˺������������
            String number = message.getOriginatingAddress();
            String content = message.getMessageBody();
            String safeNumber = sp.getString("phoneNumSec", "");
            //�ж���������Ƿ�Ϊ��ȫ����
            if (number.contains(safeNumber)) {
                if ("#*location*#".equals(content)) {
                    //������̨����λ
                    Intent data = new Intent();
                    data.setClass(context, LocationService.class);
                    context.startService(data);

                    String location = sp.getString("locationString", "");
                    if ("".equals(location)) {
                        SmsManager.getDefault().sendTextMessage(sp.getString("phoneNumSec", ""), null,
                                "���ڶ�λ,���ٴη���....", null, null);
                    } else {
                        SmsManager.getDefault().sendTextMessage(sp.getString("phoneNumSec", ""), null,
                                location, null, null);
                    }
                    //������㲥��ֹ��
                    abortBroadcast();
                } else if ("#*alarm*#".equals(content)) {
                    //�õ�MediaPlayer����
                    MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
                    //���ò��Ų���
                    mp.setLooping(true); //ѭ������
                    mp.setVolume(1.0f, 1.0f);//�������
                    //��ʼ���ű�������
                    mp.start();
                    abortBroadcast();
                } else if ("#*wipedata*#".equals(content)) {
                    //ʵ����dpm
                    dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    //Զ���������
                    ComponentName componentName = new ComponentName(context, MyAdmin.class);
                    //�ж��Ƿ��Ѿ������豸����Ȩ��
                    if (dpm.isAdminActive(componentName)) {
                        //���Sdcard�ϵ�����
                        //dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                        //�ָ���������
                        //dpm.wipeData(0);
                    } else {
                        return;
                    }
                    abortBroadcast();
                } else if ("#*lockscreen*#".equals(content)) {
                    //ʵ����dpm
                    dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    //Զ������
                    ComponentName componentName = new ComponentName(context, MyAdmin.class);
                    //�ж��Ƿ��Ѿ������豸����Ȩ��
                    if (dpm.isAdminActive(componentName)) {
                        dpm.lockNow();
                        dpm.resetPassword(sp.getString("password", ""), 0);
                    } else {
                        return;
                    }
                    abortBroadcast();
                }
            }
        }
    }
}
