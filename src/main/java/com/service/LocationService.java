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
        // 设置参数细化：
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
        // criteria.setAltitudeRequired(false);//不要求海拔信息
        // criteria.setBearingRequired(false);//不要求方位信息
        // criteria.setCostAllowed(true);//是否允许付费
        // criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求

        //得到最佳方式定位位置
        String provider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(provider,0,0,listener);



    }


    class MyListener implements LocationListener{


        @Override
        public void onLocationChanged(Location location) {
            //得到经度
            String longitude =""+location.getLongitude();
            //得到纬度
            String latitude = ""+location.getLatitude();
            //得到精确度
            String accurary =""+location.getAccuracy();

// 把标准的GPS坐标转换成火星坐标
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
            //将得到的信息保存起来
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
        // 取消监听位置服务
        lm.removeUpdates(listener);
        listener = null;
    }
}
