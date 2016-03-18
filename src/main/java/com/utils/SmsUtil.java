package com.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import com.db.dao.BlackNumberSQLiteDao;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2015/8/4.
 */

public class SmsUtil {

    /**
     * ���ݶ��ŵĻص��ӿ�
     */
    public interface BackUpCallBack {
        /**
         * ��ʼ���ݵ�ʱ�����ý��ȵ����ֵ
         *
         * @param max
         *            �ܽ���
         */
        public void beforeBackup(int max);

        /**
         * ���ݹ����У����ӽ���
         *
         * @param progress
         *            ��ǰ����
         */
        public void onSmsBackup(int progress);
    }

    public static void backupSms(Context context,BackUpCallBack backUpCallBack ) throws Exception{
        File file = new File(Environment.getExternalStorageDirectory(),"sms.xml");
        OutputStream fos = new FileOutputStream(file);
        //xml���л���
        XmlSerializer serializer = Xml.newSerializer();
        //�õ����ݽ�����
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        //��ѯ���ݿ⣬�õ����ż�¼
        Cursor cursor = resolver.query(uri, new String[]{ "body", "address",
                "type", "date"}, null, null, null);
        int max = cursor.getCount();
        //�ܹ��ļ�¼����  �ص�
        backUpCallBack.beforeBackup(max);

        //������� ��ʼ��
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);

        serializer.startTag(null, "smss");
        serializer.attribute(null, "count", String.valueOf(max));

        int progress=0;
        while (cursor.moveToNext()){
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);

            serializer.startTag(null, "sms");

            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.endTag(null, "sms");
            progress++;
            backUpCallBack.onSmsBackup(progress);
        }
        cursor.close();
        serializer.endTag(null, "smss");
        serializer.endDocument();
        fos.close();
    }

    /**
     * ���ŵĻ�ԭ
     * @param context
     * @param backUpCallBack
     * @throws Exception
     */
    public static void restoreSms(Context context,BackUpCallBack backUpCallBack ) throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(), "sms.xml");
        FileInputStream fis = new FileInputStream(file);
        //�õ�xml��pull������
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(fis, "utf-8");	// ָ��������, �ͱ���

        //�õ����ݽ�����
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");

        int eventType= pullParser.getEventType();


        ContentValues values = null;
        int progress=0;
        while(eventType != XmlPullParser.END_DOCUMENT) {	// ���û�е���β��, ����ѭ��
            String tagName = pullParser.getName();	// �ڵ�����
            switch (eventType) {

                case XmlPullParser.START_TAG:
                    if("smss".equals(tagName)) {
                        //�õ����ŵ�����
                        String count = pullParser.getAttributeValue(null, "count");
                        backUpCallBack.beforeBackup(Integer.valueOf(count));
                        //System.out.println("��������"+count);
                    } else if("sms".equals(tagName)) {
                        values = new ContentValues();
                        //System.out.println(address);
                    } else if("address".equals(tagName)) {
                        values.put("address",pullParser.getText());
                        //System.out.println(address);
                    } else if("type".equals(tagName)) {
                        values.put("type",pullParser.getText());
                        //System.out.println(type);
                    }else if("body".equals(tagName)) {
                        values.put("body",pullParser.getText());
                        //System.out.println(address);
                    } else if("date".equals(tagName)) {
                        values.put("date",pullParser.getText());
                       // System.out.println(date);
                    }

                    break;
                case XmlPullParser.END_TAG:
                    if("sms".equals(tagName)) {

                         /*   ContentValues values = new ContentValues();
                        values.put("body", body);
                        values.put("date", date);
                        values.put("type", type);
                        values.put("address", address);
                        //System.out.println("hello");;*/
                        System.out.println( values.toString());
                        resolver.insert(uri, values);
                        progress++;
                        backUpCallBack.onSmsBackup(progress);
                    }
                    break;
                default:
                    break;
            }
            eventType = pullParser.next();		// ȡ��һ���¼�����
        }

    }
}
