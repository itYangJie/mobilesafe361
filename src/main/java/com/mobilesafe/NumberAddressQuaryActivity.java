package com.mobilesafe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.db.dao.QueryPhoneNumAdd;


public class NumberAddressQuaryActivity extends Activity {

    private EditText phoneNum=null;
    private TextView numberAddress=null;
    private Vibrator vibrator = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_number_address_quary);

        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        phoneNum=(EditText)findViewById(R.id.phoneNumber);
        numberAddress=(TextView)findViewById(R.id.phoneNumberAddress);
        numberAddress.setTextColor(Color.RED);
        //智能显示查询结果
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * 当输入内容改=改变时回调
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)){
                    numberAddress.setText("");
                    return;
                }
                //动态查询
                numberAddress.setText(QueryPhoneNumAdd.query(s.toString().trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 查询号码
     * @param view
     */
    public void query(View view) {
        //输入为空，提示用户
        if (TextUtils.isEmpty(phoneNum.getText().toString())) {
            Toast.makeText(this, "输入为空，什么也查不到", Toast.LENGTH_LONG).show();
            //输入框抖动提醒
            Animation am = AnimationUtils.loadAnimation(this, R.anim.shake);
            phoneNum.startAnimation(am);
            //震动提醒
            long[] pattern = {100, 200, 200, 300};
            //-1不重复 0循环振动 1
            vibrator.vibrate(pattern, -1);
            return;
        } else {
            //查询数据库并返回结果显示在屏幕
            numberAddress.setText(QueryPhoneNumAdd.query(phoneNum.getText().toString().trim()));
        }
    }
}