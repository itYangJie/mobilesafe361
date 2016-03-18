package com.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import java.security.Provider;

public class LocationService extends Service {
   SharedPreferences sp =null;
    LocationManager lm=null;

    MyListener listener=null;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sp=getSharedPreferences("config",MODE_PRIVATE);
        lm= (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new MyListener();
        Criteria criteria=new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // ���ò���ϸ����
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);//����Ϊ��󾫶�
        // criteria.setAltitudeRequired(false);//��Ҫ�󺣰���Ϣ
        // criteria.setBearingRequired(false);//��Ҫ��λ��Ϣ
        // criteria.setCostAllowed(true);//�Ƿ�������
        // criteria.setPowerRequirement(Criteria.POWER_LOW);//�Ե�����Ҫ��

        //�õ���ѷ�ʽ��λλ��
        String provider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(provider,0,0,listener);



    }


    class MyListener implements LocationListener{


        @Override
        public void onLocationChanged(Location location) {
            //�õ�����
            String longitude =""+location.getLongitude();
            //�õ�γ��
            String latitude = ""+location.getLatitude();
            //�õ���ȷ��
            String accurary =""+location.getAccuracy();

// �ѱ�׼��GPS����ת���ɻ�������
//			InputStream is;
//			try {
//				is = getAssets().open("axisoffset.dat");
//				ModifyOffset offset = ModifyOffset.getInstance(is);
//				PointDouble double1 = offset.s2c(new PointDouble(location
//						.getLongitude(), location.getLatitude()));
//				longitude = offset.X;
//				latitude = offset.Y ;
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
            //���õ�����Ϣ��������
            String locationString = "jd:"+longitude+"\n"+"wd:"+latitude+"\n"+"ad:"+accurary;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("locationString",locationString);
            editor.commit();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // ȡ������λ�÷���
        lm.removeUpdates(listener);
        listener = null;
    }
}
