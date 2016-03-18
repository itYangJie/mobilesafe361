package com.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


public class MobileGuardSet3 extends BaseSetupActivity {
    private static final int SELECT_CONTACT =0 ;
    private EditText phoneNumSec=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mobile_guard_set3);

        phoneNumSec=(EditText)findViewById(R.id.phoneNumSec);
        //��ȡ���ݣ�ȡ��ԭ����ĺ���
        String phoneNumSecString = sp.getString("phoneNumSec", "");
        phoneNumSec.setText(phoneNumSecString);

    }
    /**
     * ������һ���򵼽���
     * @param
     */
    @Override
    public void showNext() {
        String phoneNum =phoneNumSec.getText().toString().trim();
        if(phoneNum.isEmpty()){//û�����밲ȫ���룬ŭ�ܽ�����һ��ҳ��
            Toast.makeText(MobileGuardSet3.this, "����û�����밲ȫ����,�����Խ�����һ��ҳ��", Toast.LENGTH_LONG).show();
            return;
        }else{
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("phoneNumSec",phoneNum);
            editor.commit();
        }
        Intent intent = new Intent();
        intent.setClass(this, MobileGuardSet4.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
    /**
     * ������һ���򵼽���
     * @param
     */
    @Override
    public void showPrevious() {
        Intent intent = new Intent();
        intent.setClass(this, MobileGuardSet2.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(MobileGuardSet3.this,"����������򵼹���",Toast.LENGTH_LONG).show();
    }

    /**
     * �������ѡ����ϵ�˽���
     * @param view
     */
    public void selsectContact(View view){
        Intent intent = new Intent();
        intent.setClass(MobileGuardSet3.this,SelectContactActivity.class);
        startActivityForResult(intent, SELECT_CONTACT);

        //overridePendingTransition(R.anim.next_in,R.anim.next_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_CONTACT && resultCode == 1){
            String phone = data.getStringExtra("phoneNum").replace("-","");
            phoneNumSec.setText(phone);
        }
    }
}
