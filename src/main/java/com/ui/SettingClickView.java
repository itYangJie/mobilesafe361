package com.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilesafe.R;

/**自定义组合控件 起设置作用
 * Created by Administrator on 2015/8/1.
 */
public class SettingClickView extends RelativeLayout {
    private TextView title=null;
    private TextView des=null;



    //get方法以便能取到空间设置参数
    public TextView getDes() {
        return des;
    }
    public TextView getTitle() {
        return title;
    }

    public SettingClickView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.setting_click_view,this);
        title=(TextView)this.findViewById(R.id.tv_setCenterTitle);
        des=(TextView)this.findViewById(R.id.tv_setCenterDes);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        //得到自定义属性值
        String titleString = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "titleString");
        title.setText(titleString);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setDesc(String descString){
        des.setText(descString);
    }



}
