package com.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SelectContactActivity extends Activity {
    //联系人集合,用来封装联系人对象
    ArrayList<ContactPerson> contacts=null;

    private ListView list_contacts=null;
    private ProgressBar pb = null;
    private TextView tv = null;
    private MyBaseAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_contact);

        list_contacts=(ListView)findViewById(R.id.list_contacts);
        pb = (ProgressBar)findViewById(R.id.pb);
        tv = (TextView)findViewById(R.id.tv);

        pb.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                contacts= getContacts();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyBaseAdapter();
                        list_contacts.setAdapter(adapter);
                        pb.setVisibility(View.INVISIBLE);
                        tv.setVisibility(View.INVISIBLE);
                    }
                });

            }
        }).start();

        list_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNumber = contacts.get(position).phoneNumber;
                Intent intent = new Intent();
                intent.putExtra("phoneNum", phoneNumber);
                setResult(1, intent);
                //当前页面关闭掉
                finish();
            }
        });
    }

    /**
     * 读取数据库，查询联系人
     * @return
     */
    private ArrayList<ContactPerson> getContacts() {

        ArrayList<ContactPerson> list =new ArrayList<ContactPerson>();
        ContentResolver resolver = getContentResolver();
        // 得到一个内容解析器
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");

        Cursor cursor =resolver.query(uri, new String[]{"contact_id"}, null, null, null);

        while (cursor.moveToNext()){
            String contact_id = cursor.getString(0);
            if(contact_id!=null){
                //具体的某一个联系人
                ContactPerson person = new ContactPerson();
                Cursor dataCursor = resolver.query(uriData, new String[] {
                                "data1", "mimetype" }, "contact_id=?",
                        new String[] { contact_id }, null);
                while (dataCursor.moveToNext()) {
                    String data1 = dataCursor.getString(0);
                    String mimetype = dataCursor.getString(1);

                    if("vnd.android.cursor.item/name".equals(mimetype)){
                        //联系人的姓名
                        person.name=data1;
                    }else if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                        //联系人的电话号码
                        person.phoneNumber=data1;
                    }
                }
                list.add(person);
                dataCursor.close();
            }
        }
        cursor.close();
        return list;
    }

    /**
     * 联系人对象内部类
     */
    class ContactPerson {
         String name;
         String phoneNumber;
    }

    class MyBaseAdapter extends BaseAdapter{

            @Override
            public int getCount() {
                return contacts.size();
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
                View view;
                //复用convertView,节省资源
                if(convertView!=null){
                    view=convertView;
                }else {
                    view=getLayoutInflater().inflate(R.layout.contacts_item,null);
                }
                //得到控件
                TextView contactName = (TextView)view.findViewById(R.id.contact_name);
                TextView contactNumber = (TextView)view.findViewById(R.id.contact_number);

                ContactPerson  person = contacts.get(position);
                contactName.setText(person.name);
                contactNumber.setText(person.phoneNumber);

                return view;
            }
    }

}
