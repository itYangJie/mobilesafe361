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
        //������ʾ��ѯ���
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * ���������ݸ�=�ı�ʱ�ص�
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
                //��̬��ѯ
                numberAddress.setText(QueryPhoneNumAdd.query(s.toString().trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * ��ѯ����
     * @param view
     */
    public void query(View view) {
        //����Ϊ�գ���ʾ�û�
        if (TextUtils.isEmpty(phoneNum.getText().toString())) {
            Toast.makeText(this, "����Ϊ�գ�ʲôҲ�鲻��", Toast.LENGTH_LONG).show();
            //����򶶶�����
            Animation am = AnimationUtils.loadAnimation(this, R.anim.shake);
            phoneNum.startAnimation(am);
            //������
            long[] pattern = {100, 200, 200, 300};
            //-1���ظ� 0ѭ���� 1
            vibrator.vibrate(pattern, -1);
            return;
        } else {
            //��ѯ���ݿⲢ���ؽ����ʾ����Ļ
            numberAddress.setText(QueryPhoneNumAdd.query(phoneNum.getText().toString().trim()));
        }
    }
}