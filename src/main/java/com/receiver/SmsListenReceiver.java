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

    //当接收到短信是调用该方法
    @Override
    public void onReceive(Context context, Intent intent) {

        //得到短信
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        //遍历每一条短信
        for (Object obj : objs) {
            //一条短信
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
            //得到发件人号码与短信内容
            String number = message.getOriginatingAddress();
            String content = message.getMessageBody();
            String safeNumber = sp.getString("phoneNumSec", "");
            //判断来电号码是否为安全号码
            if (number.contains(safeNumber)) {
                if ("#*location*#".equals(content)) {
                    //启动后台服务定位
                    Intent data = new Intent();
                    data.setClass(context, LocationService.class);
                    context.startService(data);

                    String location = sp.getString("locationString", "");
                    if ("".equals(location)) {
                        SmsManager.getDefault().sendTextMessage(sp.getString("phoneNumSec", ""), null,
                                "正在定位,请再次发送....", null, null);
                    } else {
                        SmsManager.getDefault().sendTextMessage(sp.getString("phoneNumSec", ""), null,
                                location, null, null);
                    }
                    //把这个广播终止掉
                    abortBroadcast();
                } else if ("#*alarm*#".equals(content)) {
                    //得到MediaPlayer对象
                    MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
                    //设置播放参数
                    mp.setLooping(true); //循环播放
                    mp.setVolume(1.0f, 1.0f);//声音最大
                    //开始播放报警音乐
                    mp.start();
                    abortBroadcast();
                } else if ("#*wipedata*#".equals(content)) {
                    //实例化dpm
                    dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    //远程清除数据
                    ComponentName componentName = new ComponentName(context, MyAdmin.class);
                    //判断是否已经开启设备管理权限
                    if (dpm.isAdminActive(componentName)) {
                        //清除Sdcard上的数据
                        //dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                        //恢复出厂设置
                        //dpm.wipeData(0);
                    } else {
                        return;
                    }
                    abortBroadcast();
                } else if ("#*lockscreen*#".equals(content)) {
                    //实例化dpm
                    dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    //远程锁屏
                    ComponentName componentName = new ComponentName(context, MyAdmin.class);
                    //判断是否已经开启设备管理权限
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
