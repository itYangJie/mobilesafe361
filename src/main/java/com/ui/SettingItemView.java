package com.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilesafe.R;

/**�Զ�����Ͽؼ� ����������
 * Created by Administrator on 2015/7/26.
 */
public class SettingItemView extends RelativeLayout {
    private TextView title=null;
    private TextView des=null;
    private CheckBox state=null;

    private  String desc_on;
    private String desc_off;
    private String desc_on_color;

    //get�����Ա���ȡ���ռ����ò���
    public CheckBox getState() {
        return state;
    }
    public TextView getDes() {
        return des;
    }
    public TextView getTitle() {
        return title;
    }

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.setting_item_view,this);
        title=(TextView)this.findViewById(R.id.tv_setCenterTitle);
        des=(TextView)this.findViewById(R.id.tv_setCenterDes);
        state=(CheckBox)this.findViewById(R.id.cb_status);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        //�õ��Զ�������ֵ
        String titleString = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "titleString");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_off");

        title.setText(titleString);
        setChecked(false);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public boolean isChecked(){
        return state.isChecked();
    }

    public void setChecked(boolean flag){
        if(flag) {
            des.setText(desc_on);
            des.setTextColor(Color.RED);
            state.setChecked(flag);
        }else{
            des.setText(desc_off);
            des.setTextColor(Color.GRAY);
            state.setChecked(flag);
        }
    }
}
