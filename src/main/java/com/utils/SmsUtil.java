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
     * 备份短信的回调接口
     */
    public interface BackUpCallBack {
        /**
         * 开始备份的时候，设置进度的最大值
         *
         * @param max
         *            总进度
         */
        public void beforeBackup(int max);

        /**
         * 备份过程中，增加进度
         *
         * @param progress
         *            当前进度
         */
        public void onSmsBackup(int progress);
    }

    public static void backupSms(Context context,BackUpCallBack backUpCallBack ) throws Exception{
        File file = new File(Environment.getExternalStorageDirectory(),"sms.xml");
        OutputStream fos = new FileOutputStream(file);
        //xml序列化器
        XmlSerializer serializer = Xml.newSerializer();
        //得到内容解析者
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        //查询数据库，得到短信记录
        Cursor cursor = resolver.query(uri, new String[]{ "body", "address",
                "type", "date"}, null, null, null);
        int max = cursor.getCount();
        //总共的记录条数  回调
        backUpCallBack.beforeBackup(max);

        //设置输出 初始化
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
     * 短信的还原
     * @param context
     * @param backUpCallBack
     * @throws Exception
     */
    public static void restoreSms(Context context,BackUpCallBack backUpCallBack ) throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(), "sms.xml");
        FileInputStream fis = new FileInputStream(file);
        //得到xml的pull解析器
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(fis, "utf-8");	// 指定解析流, 和编码

        //得到内容解析者
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");

        int eventType= pullParser.getEventType();


        ContentValues values = null;
        int progress=0;
        while(eventType != XmlPullParser.END_DOCUMENT) {	// 如果没有到结尾处, 继续循环
            String tagName = pullParser.getName();	// 节点名称
            switch (eventType) {

                case XmlPullParser.START_TAG:
                    if("smss".equals(tagName)) {
                        //得到短信的条数
                        String count = pullParser.getAttributeValue(null, "count");
                        backUpCallBack.beforeBackup(Integer.valueOf(count));
                        //System.out.println("短信条数"+count);
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
            eventType = pullParser.next();		// 取下一个事件类型
        }

    }
}
