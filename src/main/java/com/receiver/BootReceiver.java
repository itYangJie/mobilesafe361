package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    private SharedPreferences sp;
    private TelephonyManager tm;
    @Override
    public void onReceive(Context context, Intent intent) {

        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        boolean useGuard = sp.getBoolean("useGuard", false);
        if(useGuard){
            //开启防盗保护才执行这个地方
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // 读取之前保存的SiM信息；
            String saveSim = sp.getString("bundleSim", "");
            //读取当前的sim卡信息
            String realSim = tm.getSimSerialNumber();
            //比较是否一样
            if(saveSim.equals(realSim)){
                //sim没有变更，还是同一张卡
            }else{
                // sim 已经变更 发一个短信给安全号码
                System.out.println("sim 已经变更");
                Toast.makeText(context, "sim 卡已变更", Toast.LENGTH_LONG).show();
                SmsManager.getDefault().sendTextMessage(sp.getString("phoneNumSec", ""), null, "您关联的sim卡已经变更,请注意....", null, null);
            }
        }
    }
}
