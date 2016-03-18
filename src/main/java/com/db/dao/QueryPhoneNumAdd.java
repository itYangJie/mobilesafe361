package com.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/7/31.
 */
public class QueryPhoneNumAdd {
    //���ݿ�·��
    private static String path = "data/data/com.mobilesafe/files/address.db";

    public static String query(String phoneNum) {

        //�õ����ݿ�
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        // �ֻ����� 13 14 15 16 18
        // �ֻ������������ʽ
        if (phoneNum.matches("^1[34568]\\d{9}$")) {
            //��ѯ�����ʼ��
            String location=phoneNum;
            // �ֻ�����
            Cursor cursor = db
                    .rawQuery(
                            "select location from data2 where id = (select outkey from data1 where id = ?)",
                            new String[]{phoneNum.substring(0, 7)});

            while (cursor.moveToNext()) {
                  location = cursor.getString(0);
            }
            cursor.close();
            return location;
        }
        return phoneNum;
    }
}
