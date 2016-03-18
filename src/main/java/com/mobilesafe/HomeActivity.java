package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ui.SlideMenu;
import com.utils.MD5Utils;

import java.util.ArrayList;


public class HomeActivity extends Activity  {
    private GridView gv_home_item = null;
    private SharedPreferences sp = null;
    private ViewPager vp = null;
    private TextView home_text  = null;
    private LinearLayout ll_home  = null;
    private SlideMenu slideMenu = null;
    private ImageView btn_back = null;
    private ArrayList<ImageView> imageViews;
    private static String[] names = {
            "�ֻ�����", "ͨѶ��ʿ", "�������",
            "���̹���", "�ֻ�ɱ��",
            "��������", "�߼�����", "��������"

    };



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            vp.setCurrentItem(vp.getCurrentItem()+1);
            handler.sendEmptyMessageDelayed(0,1500);
        }
    };
    private static int[] ids = {
            R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.taskmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings

    };
    private static int[] imageResIds = new int[]{R.drawable.smile,
            R.drawable.b,R.drawable.d,R.drawable.f,R.drawable.h,
            R.drawable.g
    };
    private String[] picDes = {"΢Ц�������","�����ķ羰�������","������ʵ���������г�����Ŭ��ǰ���ͻ�ˤ��"
    ,"�ں����ǵ�û�м���","�������һ�����õ��ӽ�","��������������������;ѽ��"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        gv_home_item = (GridView) findViewById(R.id.gv_home_item);
        vp = (ViewPager) findViewById(R.id.vp);

        home_text = (TextView)findViewById(R.id.home_text);
        ll_home  =(LinearLayout)findViewById(R.id.ll_home);

        slideMenu = (SlideMenu)findViewById(R.id.slideMenu);
        btn_back = (ImageView)findViewById(R.id.btn_back);

        //����򿪻��߹رղ໬�˵�
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideMenu.switchMenu();
            }
        });
        //������ͼƬ
        imageViews = new ArrayList<ImageView>();
        for (int i = 0; i < imageResIds.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageResIds[i]);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageViews.add(imageView);
        }



        //��viewPager�������
        vp.setAdapter(new MyAdapter());


        //��viewPager���ü�����
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                home_text.setText(picDes[position%imageResIds.length]);
                for(int i=0;i<imageResIds.length;i++){
                    if(i==position%imageResIds.length){
                        ll_home.getChildAt(i).setEnabled(true);
                    }else {
                        ll_home.getChildAt(i).setEnabled(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for(int i=0;i<imageResIds.length;i++){
            View view = new View(this);
            LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(25,25);
            if(i==0){
                params.leftMargin=20;

            }
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.selector_dot);
            ll_home.addView(view);
        }
        vp.setCurrentItem(Integer.MAX_VALUE/2-(Integer.MAX_VALUE/2)%(imageResIds.length));
        home_text.setText(picDes[0]);

        for(int i=0;i<imageResIds.length;i++){
            if(i==0){
                ll_home.getChildAt(i).setEnabled(true);
            }else {
                ll_home.getChildAt(i).setEnabled(false);
            }
        }
        //λGridView����adapter
        gv_home_item.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return names.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = getLayoutInflater().inflate(R.layout.home_item, null);

                TextView tx_item = (TextView) view.findViewById(R.id.tv_item);
                ImageView lv_item = (ImageView) view.findViewById(R.id.iv_item);

                lv_item.setImageResource(ids[position]);
                tx_item.setText(names[position]);
                return view;
            }
        });


      /*  //Ϊ��ҳ���ó��ֵĶ���Ч��
        ScaleAnimation am = new ScaleAnimation(0.1f, 2.0f, 0.1f, 2.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(600);
        am.setRepeatCount(1);*/
        AlphaAnimation am = new AlphaAnimation(0.3f,1.0f);
        am.setDuration(800);

        //��һ����������ʱ������ӭ�Ի���
        boolean isFirstUse = sp.getBoolean("isFirstUse", true);
        if (isFirstUse) {
            showWelcomeDialog();
        }

        //ΪGridView���ü�����
        gv_home_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //����� �ֻ�����
                    case 0: {
                        //�ж��Ƿ����ù�����
                        if (TextUtils.isEmpty(sp.getString("password", ""))) {
                            //δ���ù����룬������������Ի���
                            showSetPwdDialog();

                        } else { //���ù����룬������������Ի���

                            //�ж��û��Ƿ���δ�˳�Ӧ���ڼ��ٴν����ֻ���������
                            boolean needPwd = sp.getBoolean("needPwd", true);
                            if (needPwd) {
                                showInputPwdDialog();
                            } else {
                                // ֱ�ӽ����ֻ�����ҳ��
                                Intent intent = new Intent();
                                intent.setClass(HomeActivity.this, MobileGuardActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                            }
                        }
                        break;
                    }
                    //ͨѶ��ʿ
                    case 1: {
                        Intent intent = new Intent(HomeActivity.this, ContactSafeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                    //����ܼ�
                    case 2: {
                        Intent intent = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }//���̹���
                    case 3: {
                        Intent intent = new Intent(HomeActivity.this, ProcessManagerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }

                    case 4: {
                        //�����ֻ�ɱ��
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, ScanVirusActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                    case 5: {
                        //���뻺������
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, CleanCacheActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                    case 6: {
                        //����߼�����
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, HighToolActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                    //����� ��������
                    case 7: {
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, SeettingCenterActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                        break;
                    }
                }
            }
        });
        handler.sendEmptyMessageDelayed(0,1500);
    }


    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //ʵ����item
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            //��ÿ��ͼƬ���뵽ViewPager��
            /*ViewGroup.LayoutParams params = imageViews.get(arg1).getLayoutParams();
            params.height=ViewGroup.LayoutParams.MATCH_PARENT;
            params.width=ViewGroup.LayoutParams.MATCH_PARENT;

            imageViews.get(arg1).setLayoutParams(params);*/

            ((ViewPager) arg0).addView(imageViews.get(arg1%imageResIds.length));
            return imageViews.get(arg1%imageResIds.length);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            //��ÿ��ͼƬ��ViewPager���ͷŵ�
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //view �� Object �ǲ���һ������
            return arg0 == arg1;
        }


    }

    /*@Override
    protected void onStart() {
        //��һ����ʱ��  �����ͼƬ�л�
        //Timer �� ScheduledExecutorService ʵ�ֶ�ʱ����Ч��
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //ͨ����ʱ�� ����� ÿ2�����л�һ��ͼƬ
        //����ָ����ʱ���ִ����ָ��������
        //scheduleAtFixedRate(command, initialDelay, period, unit)
        //command ��Ҫִ�е�����
        //initialDelay ��һ������ʱ �ӳ�����ʱ��
        //period  ÿ������ʱ����������������
        //unit ʱ�䵥λ
        scheduledExecutorService.scheduleAtFixedRate(new ViewPagerTask(), 500, 1500, TimeUnit.MILLISECONDS);
        super.onStart();
    }*/

   /* @Override
    protected void onStop() {
        //ֹͣͼƬ�л�
        scheduledExecutorService.shutdown();
        super.onStop();
    }*/

   /* private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            //�趨viewPager��ǰҳ��
            vp.setCurrentItem(currentItem);
        }
    };*/

   /* //�������ͼƬ�л�������
    private class ViewPagerTask implements Runnable {

        public void run() {
            //ʵ�����ǵĲ���
            //�ı䵱ǰҳ��
            currentItem = (currentItem + 1) % imageViews.size();
            //Handler��ʵ��ͼƬ�л�
            handler.obtainMessage().sendToTarget();
        }
    }*/


    /**
     * ��������Ի�����
     */
    Dialog dialogInputPwd = null;

    private void showInputPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        //�ɲ����ļ��õ�view
        View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
        //��view�õ������еĿؼ�
        final EditText et_enter_pwd = (EditText) view.findViewById(R.id.et_enter_pwd);
        final Button ok = (Button) view.findViewById(R.id.ok);
        final Button cancel = (Button) view.findViewById(R.id.cancel);

        //���ȡ��
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInputPwd.dismiss();
                return;
            }
        });
        //���ȷ��
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_enter_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, "�ף���������Ϊ��", Toast.LENGTH_LONG).show();
                    et_enter_pwd.setText("");
                    return;
                }
                //�ж������Ƿ���ȷ
                if (MD5Utils.md5Password(pwd).equals(sp.getString("password", ""))) {
                    //������ȷ��ȡ���Ի���
                    dialogInputPwd.dismiss();
                    //�������ݣ��û��Ѿ��ɹ��������룬����δ�˳�Ӧ���ڼ���Բ���������
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("needPwd", false);
                    editor.commit();

                    // �����ֻ�����ҳ��
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, MobileGuardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                } else {
                    Toast.makeText(HomeActivity.this, "�ף��������Ӵ���ú���һ��", Toast.LENGTH_LONG).show();
                    et_enter_pwd.setText("");
                    return;
                }
            }
        });
        builder.setView(view);
        dialogInputPwd = builder.show();
    }

    /**
     * ��������Ի�����
     */
    Dialog dialogSetPwd = null;

    private void showSetPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        //�ɲ����ļ��õ�view
        View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
        //��view�õ������еĿؼ�
        final EditText et_setPwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        final EditText et_setPwdConfirm = (EditText) view.findViewById(R.id.et_setup_confirm);
        final Button ok = (Button) view.findViewById(R.id.ok);
        final Button cancel = (Button) view.findViewById(R.id.cancel);

        //���ȡ��
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetPwd.dismiss();
                return;
            }
        });
        //���ȷ��
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_setPwd.getText().toString().trim();
                String pwd_confirm = et_setPwdConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
                    Toast.makeText(HomeActivity.this, "�ף����벻������Ϊ��", Toast.LENGTH_LONG).show();
                    et_setPwd.setText("");
                    et_setPwdConfirm.setText("");
                    return;
                }
                //�ж��������������Ƿ���ͬ
                if (pwd.equals(pwd_confirm)) {
                    //���������������,���Ա�������
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", MD5Utils.md5Password(pwd));
                    editor.commit();

                    Toast.makeText(HomeActivity.this, "��������ɹ�", Toast.LENGTH_LONG).show();
                    //ȡ���Ի���
                    dialogSetPwd.dismiss();
                    //�����ֻ�����ҳ��
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, MobileGuardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
                } else {
                    //�����������벻���,��ʾ�û�
                    Toast.makeText(HomeActivity.this, "�ף������������벻���", Toast.LENGTH_LONG).show();
                    et_setPwd.setText("");
                    et_setPwdConfirm.setText("");
                    return;
                }
            }
        });
        builder.setView(view);
        dialogSetPwd = builder.show();
    }

    //������ӭ�Ի��� ���״��������ʱ����
    private void showWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("��ӭʹ��");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage("��,���!��������ʹ���ҿ�����361�ֻ���ʿ��\nϣ������ʹ�õ�ͬʱ���������������������," +
                "�Ա������޸�bug,�ٴθ�л����ʹ��");
        builder.setPositiveButton("�õ�", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
        //�´������Ͳ��ᵯ����ӭ�Ի�����
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isFirstUse", false);
        editor.commit();
    }

    /*
    ������back���˳�
     */
    long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()���ۺ�ʱ���ã��϶�����2000
            {
                Toast.makeText(HomeActivity.this, "�ٰ�һ���˳�361�ֻ���ʿ", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //�������ݣ��û��˳�����,�´ν����ֻ�������Ҫ����
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("needPwd", true);
                editor.commit();
                finish();
                //OffersManager.getInstance(this).onAppExit();
                overridePendingTransition(R.anim.homeitem_in, R.anim.homeitem_out);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    /**
     * �໬�˵�����¼�
     * @param view
     */
    public void slideMenuItemClick(View view){

        switch (view.getId()){
            case R.id.slideMenu_news:
                Toast.makeText(HomeActivity.this,"���������",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_read:
                Toast.makeText(HomeActivity.this,"����˶���",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_ties:
                Toast.makeText(HomeActivity.this,"����˸���",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_pics:
                Toast.makeText(HomeActivity.this,"�����ͼƬ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_ugc:
                Toast.makeText(HomeActivity.this,"����˻���",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_vote:
                Toast.makeText(HomeActivity.this,"�����ͶƱ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.slideMenu_focus:
                Toast.makeText(HomeActivity.this,"����������Ķ�",Toast.LENGTH_SHORT).show();
                break;

        }

    }
}
