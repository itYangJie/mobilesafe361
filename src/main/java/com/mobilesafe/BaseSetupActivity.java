package com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	SharedPreferences sp=null;
	//1.����һ������ʶ����
		private GestureDetector detector;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			sp=getSharedPreferences("config",MODE_PRIVATE);
			//2.ʵ�����������ʶ����
			detector = new GestureDetector(this, new SimpleOnGestureListener(){

				/**
				 * �����ǵ���ָ�����滬����ʱ��ص�
				 */
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					//������X��������������
					if(Math.abs(velocityX)<200){
						Toast.makeText(getApplicationContext(), "������̫����", Toast.LENGTH_SHORT).show();
						return true;
					}
					//����б���������
					if(Math.abs((e2.getRawY() - e1.getRawY())) > 200){
						Toast.makeText(getApplicationContext(), "�����һ���", Toast.LENGTH_SHORT).show();
						return true;
					}
					if((e2.getRawX() - e1.getRawX())> 130 ){
						//��ʾ��һ��ҳ�棺�������һ���
						System.out.println("��ʾ��һ��ҳ�棺�������һ���");
						showPrevious();
						return true;
					}
					if((e1.getRawX()-e2.getRawX()) > 150 ){
						//��ʾ��һ��ҳ�棺�������󻬶�
						System.out.println("��ʾ��һ��ҳ�棺�������󻬶�");
						showNext();
						return true;
					}
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				
			});
		}
		public abstract void showNext();
		public abstract void showPrevious();
		/**
		 * ��һ���ĵ���¼�
		 * @param view
		 */
		public void next(View view){
			showNext();
		}
		/**
		 *   ��һ��
		 * @param view
		 */
		public void previous(View view){
			showPrevious();
		}
			
		//3.ʹ������ʶ����
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			detector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
}