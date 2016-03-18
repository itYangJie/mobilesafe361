package com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.domain.BlackNumberEntity;
import com.db.dao.BlackNumberSQLiteDao;

import java.util.List;


public class ContactSafeActivity extends Activity {

    private ListView list_blackNum = null;
    private BlackNumberSQLiteDao dao = null;
    private List<BlackNumberEntity> list =null;
    private RelativeLayout rl_blackNum = null;
    private MyBaseAdapter adapter=null;
    private ImageView img_404=null;
    private TextView tv_404=null;
    private ProgressBar pb = null;
    private TextView tv = null;
    private Dialog dialogUpdate; //�޸�����ģʽ�ǵ����ĶԻ���
    private Button btn_updateBOk,btn_updateBCancel;
    private CheckBox cbupdate_phone,cbupdate_sms;
    private TextView tv_blackNum;
    private int offset=0;
    //private View view404 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_safe);
        pb = (ProgressBar)findViewById(R.id.pb);
        tv = (TextView)findViewById(R.id.tv);
        dao = new BlackNumberSQLiteDao(this);
        rl_blackNum = (RelativeLayout)findViewById(R.id.rl_blackNum);
        list_blackNum=(ListView)findViewById(R.id.list_blackNum);
        //�����ݿ����޺����������ǽ�����ʾ��img tv
        img_404 = (ImageView)findViewById(R.id.img_404);
        tv_404 = (TextView)findViewById(R.id.tv_404);

        queryPart(offset, 20);

        // listviewע��һ�������¼��ļ�������
        list_blackNum.setOnScrollListener(new AbsListView.OnScrollListener() {
            // ��������״̬�����仯��ʱ��
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// ����״̬
                        // �жϵ�ǰlistview������λ��
                        // ��ȡ���һ���ɼ���Ŀ�ڼ��������λ�á�
                        int lastposition = list_blackNum.getLastVisiblePosition();

                        // ����������20��item λ�ô�0��ʼ�� ���һ����Ŀ��λ�� 19
                        if (lastposition == (list.size() - 1)) {
                            System.out.println("�б��ƶ��������һ��λ�ã����ظ�������ݡ�����");
                            offset += 20 ;  //�ڼ���20��;
                            queryPart(offset,20);
                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// ��ָ��������
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// ���Ի���״̬
                        break;
                }
            }
            // ������ʱ����õķ�����
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        list_blackNum.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new  AlertDialog.Builder(ContactSafeActivity.this);
                //�������ļ�����Ϊview����
                View dialogView =View .inflate(ContactSafeActivity.this,R.layout.dialog_updateblacknum,null);

                tv_blackNum=(TextView)dialogView.findViewById(R.id.tv_blackNum);
                btn_updateBOk=(Button)dialogView.findViewById(R.id.ok);
                btn_updateBCancel=(Button)dialogView.findViewById(R.id.cancel);
                cbupdate_phone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
                cbupdate_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);
                //��ʾ��ǰ����
                tv_blackNum.setText(list.get(position).getNumber());
                //����ģʽ 1.�绰���� 2.�������� 3.ȫ������
                switch (Integer.valueOf(list.get(position).getMode())){
                    case 1:
                        cbupdate_phone.setChecked(true);
                        cbupdate_sms.setChecked(false);
                        break;
                    case 2:
                        cbupdate_sms.setChecked(true);
                        cbupdate_phone.setChecked(false);
                        break;
                    case 3:
                        cbupdate_phone.setChecked(true);
                        cbupdate_sms.setChecked(true);
                        break;
                }
                //ȡ���޸�����ģʽ
                btn_updateBCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUpdate.dismiss();
                    }
                });
                //�޸�����ģʽ
                btn_updateBOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //û��ѡ������ģʽ
                        if((!cbupdate_phone.isChecked())&&(!cbupdate_sms.isChecked())){
                            Toast.makeText(ContactSafeActivity.this,"��ѡ������ģʽ",Toast.LENGTH_SHORT).show();
                            return;
                        }  //����ģʽ 1.�绰���� 2.�������� 3.ȫ������
                        if((cbupdate_phone.isChecked())&&(!cbupdate_sms.isChecked())){
                            //����ģʽ 1
                            dao.update(list.get(position).getNumber(), "1");
                            //����ui
                            list.get(position).setMode("1");
                            adapter.notifyDataSetChanged();
                        }else
                        if((!cbupdate_phone.isChecked())&&(cbupdate_sms.isChecked())){
                            //����ģʽ 2
                            dao.update(list.get(position).getNumber(),"2");
                            //����ui
                            list.get(position).setMode("2");
                            adapter.notifyDataSetChanged();
                        }else
                        if((cbupdate_phone.isChecked())&&(cbupdate_sms.isChecked())){
                            //����ģʽ 3
                            dao.update(list.get(position).getNumber(),"3");
                            //����ui
                            list.get(position).setMode("3");
                            adapter.notifyDataSetChanged();
                        }
                        dialogUpdate.dismiss();
                    }
                });
                builder.setView(dialogView);
                dialogUpdate = builder.show();
                return true;
            }
        });

    }

    /**
     * �õ�20����¼
     * @param offset
     * @param i
     * @return
     */
    private void queryPart( final int offset, final int i) {
        pb.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
      new Thread(){
          @Override
          public void run() {
              //ģ���������м������ݺ�ʱ����
             /* try {
                  Thread.sleep(1000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }*/
              List<BlackNumberEntity> queryList= dao.findPart(offset, i);
              if (list == null) {//��һ�μ���
                  list = queryList;
                  if(list.size()==0||list==null){// ���ݿ���û����Ϣ
                      img_404.setVisibility(View.VISIBLE);
                      tv_404.setVisibility(View.VISIBLE);
                  }
              } else { // ԭ���Ѿ����ع������ˡ�
                  if(queryList==null||queryList.size()==0){//���ص�����
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Toast.makeText(ContactSafeActivity.this,"�Բ����Ѿ�������",Toast.LENGTH_SHORT).show();
                          }
                      });

                  }else {
                      list.addAll(queryList);
                  }
              }
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      pb.setVisibility(View.INVISIBLE);
                      tv.setVisibility(View.INVISIBLE);
                      if (adapter == null) {
                          adapter = new MyBaseAdapter();
                          list_blackNum.setAdapter(adapter);
                      }else{
                          adapter.notifyDataSetChanged();
                      }
                  }
              });

          }
      }.start();
    }

    /**
     * ��Ӻ�����
     * @param view
     */
    private EditText et_addBlack;
    private Button btn_addBOk,btn_addBCancel;
    private Dialog addBlackDialog;
    private CheckBox cb_phone,cb_sms;
    public void addBlackNumber(final View view){
        AlertDialog.Builder builder = new  AlertDialog.Builder(ContactSafeActivity.this);
        //�������ļ�����Ϊview����
        View dialogView =View .inflate(ContactSafeActivity.this,R.layout.dialog_addblacknum,null);

        et_addBlack=(EditText)dialogView.findViewById(R.id.et_blackNum);
        btn_addBOk=(Button)dialogView.findViewById(R.id.ok);
        btn_addBCancel=(Button)dialogView.findViewById(R.id.cancel);
        cb_phone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
        cb_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);

        //ȡ�����
        btn_addBCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBlackDialog.dismiss();
            }
        });
        //���
        btn_addBOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String blacknumber = et_addBlack.getText().toString().trim();
                if(TextUtils.isEmpty(blacknumber)){
                    Toast.makeText(getApplicationContext(), "���������벻��Ϊ��", 0).show();
                    return;
                }
                String mode ;
                if(cb_phone.isChecked()&&cb_sms.isChecked()){
                    //ȫ������
                    mode = "3";
                }else if(cb_phone.isChecked()){
                    //�绰����
                    mode = "1";
                }else if(cb_sms.isChecked()){
                    //��������
                    mode = "2";
                }else{
                    Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", 0).show();
                    return;
                }
                //���ݱ��ӵ����ݿ�
                dao.insert(blacknumber, mode);
                //����listview������������ݡ�
                BlackNumberEntity info = new BlackNumberEntity();
                info.setMode(mode);
                info.setNumber(blacknumber);
                list.add(0, info);
                //���ݿ���������
                if(list.size()==1){
                    /*rl_blackNum.removeView(img_404);
                    rl_blackNum.removeView(tv_404);*/
                    img_404.setVisibility(View.INVISIBLE);
                    tv_404.setVisibility(View.INVISIBLE);
                }
                //֪ͨlistview�������������ݸ����ˡ�
                adapter.notifyDataSetChanged();
                addBlackDialog.dismiss();
                Toast.makeText(ContactSafeActivity.this,"��ӳɹ�",Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(dialogView);

        addBlackDialog = builder.show();

    }

    /**
     * �Զ���BaseAdapter
     */
  /*  Dialog dialogUpdate; //�޸�����ģʽ�ǵ����ĶԻ���
    Button btn_updateBOk,btn_updateBCancel;
    CheckBox cbupdate_phone,cbupdate_sms;
    TextView tv_blackNum;*/
    class MyBaseAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if(convertView==null) {
                view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
            }else{
                view=convertView;
            }
            //�õ��ؼ�
            final TextView number = (TextView)view.findViewById(R.id.tv_black_number);
            TextView mode = (TextView) view.findViewById(R.id.tv_block_mode);
            //��ɾ�����ܵ�ͼ��
            ImageView iv_delete = (ImageView)view.findViewById(R.id.iv_delete);
            //����������Ƴ�������
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactSafeActivity.this);
                    builder.setTitle("����");
                    builder.setMessage("ȷ��Ҫ�Ƴ���������");

                    builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//ȷ��ɾ��
                            //ɾ�����ݿ������
                            dao.delete(list.get(position).getNumber());
                            //���½��档
                            list.remove(position);
                            //֪ͨlistview��������������
                            adapter.notifyDataSetChanged();
                            //���ݿ���û��������
                            if(list.size()==0){
                                img_404.setVisibility(View.VISIBLE);
                                tv_404.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    builder.setNegativeButton("ȡ��", null);
                    builder.show();
                }
            });
            number.setText(list.get(position).getNumber());
            String modeString = list.get(position).getMode();
            if("1".equals(modeString)){
                mode.setText("�绰����");
                mode.setTextColor(Color.BLUE);
            }else if("2".equals(modeString)){
                mode.setText("��������");
                mode.setTextColor(Color.GRAY);
            }else{
                mode.setText("ȫ������");
                mode.setTextColor(Color.RED);
            }
           /* //����ǿ����޸ĺ���������ģʽ
           view.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   AlertDialog.Builder builder = new  AlertDialog.Builder(ContactSafeActivity.this);
                   //�������ļ�����Ϊview����
                   View dialogView =View .inflate(ContactSafeActivity.this,R.layout.dialog_updateblacknum,null);

                   tv_blackNum=(TextView)dialogView.findViewById(R.id.tv_blackNum);
                   btn_updateBOk=(Button)dialogView.findViewById(R.id.ok);
                   btn_updateBCancel=(Button)dialogView.findViewById(R.id.cancel);
                   cbupdate_phone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
                   cbupdate_sms = (CheckBox) dialogView.findViewById(R.id.cb_sms);
                   //��ʾ��ǰ����
                   tv_blackNum.setText(list.get(position).getNumber());
                   //����ģʽ 1.�绰���� 2.�������� 3.ȫ������
                   switch (Integer.valueOf(list.get(position).getMode())){
                       case 1:
                           cbupdate_phone.setChecked(true);
                           cbupdate_sms.setChecked(false);
                           break;
                       case 2:
                           cbupdate_sms.setChecked(true);
                           cbupdate_phone.setChecked(false);
                           break;
                       case 3:
                           cbupdate_phone.setChecked(true);
                           cbupdate_sms.setChecked(true);
                           break;
                   }
                   //ȡ���޸�����ģʽ
                   btn_updateBCancel.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialogUpdate.dismiss();
                       }
                   });
                   //�޸�����ģʽ
                   btn_updateBOk.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           //û��ѡ������ģʽ
                           if((!cbupdate_phone.isChecked())&&(!cbupdate_sms.isChecked())){
                               Toast.makeText(ContactSafeActivity.this,"��ѡ������ģʽ",Toast.LENGTH_SHORT).show();
                               return;
                           }  //����ģʽ 1.�绰���� 2.�������� 3.ȫ������
                           if((cbupdate_phone.isChecked())&&(!cbupdate_sms.isChecked())){
                               //����ģʽ 1
                               dao.update(list.get(position).getNumber(), "1");
                               //����ui
                               list.get(position).setMode("1");
                               adapter.notifyDataSetChanged();
                           }else
                           if((!cbupdate_phone.isChecked())&&(cbupdate_sms.isChecked())){
                               //����ģʽ 2
                               dao.update(list.get(position).getNumber(),"2");
                               //����ui
                               list.get(position).setMode("2");
                               adapter.notifyDataSetChanged();
                           }else
                           if((cbupdate_phone.isChecked())&&(cbupdate_sms.isChecked())){
                               //����ģʽ 3
                               dao.update(list.get(position).getNumber(),"3");
                               //����ui
                               list.get(position).setMode("3");
                               adapter.notifyDataSetChanged();
                           }
                           dialogUpdate.dismiss();
                       }
                   });
                   builder.setView(dialogView);
                   dialogUpdate = builder.show();
                   return true;
               }
           });*/
            return view;
        }
    }
}
