package com.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/7/31.
 */
public class QueryPhoneNumAdd {
    //数据库路径
    private static String path = "data/data/com.mobilesafe/files/address.db";

    public static String query(String phoneNum) {

        //得到数据库
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        // 手机号码 13 14 15 16 18
        // 手机号码的正则表达式
        if (phoneNum.matches("^1[34568]\\d{9}$")) {
            //查询结果初始化
            String location=phoneNum;
            // 手机号码
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
