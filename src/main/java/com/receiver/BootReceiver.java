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
            //��������������ִ������ط�
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // ��ȡ֮ǰ�����SiM��Ϣ��
            String saveSim = sp.getString("bundleSim", "");
            //��ȡ��ǰ��sim����Ϣ
            String realSim = tm.getSimSerialNumber();
            //�Ƚ��Ƿ�һ��
            if(saveSim.equals(realSim)){
                //simû�б��������ͬһ�ſ�
            }else{
                // sim �Ѿ���� ��һ�����Ÿ���ȫ����
                System.out.println("sim �Ѿ����");
                Toast.makeText(context, "sim ���ѱ��", Toast.LENGTH_LONG).show();
                SmsManager.getDefault().sendTextMessage(sp.getString("phoneNumSec", ""), null, "��������sim���Ѿ����,��ע��....", null, null);
            }
        }
    }
}
