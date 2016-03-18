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
        //读取数据，取出原来存的号码
        String phoneNumSecString = sp.getString("phoneNumSec", "");
        phoneNumSec.setText(phoneNumSecString);

    }
    /**
     * 进入下一个向导界面
     * @param
     */
    @Override
    public void showNext() {
        String phoneNum =phoneNumSec.getText().toString().trim();
        if(phoneNum.isEmpty()){//没有输入安全号码，怒能进入下一个页面
            Toast.makeText(MobileGuardSet3.this, "您还没有输入安全号码,不可以进入下一个页面", Toast.LENGTH_LONG).show();
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
     * 进入上一个向导界面
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
        Toast.makeText(MobileGuardSet3.this,"请完成整个向导过程",Toast.LENGTH_LONG).show();
    }

    /**
     * 点击进入选择联系人界面
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
