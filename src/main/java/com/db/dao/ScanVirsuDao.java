package com.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/8/9.
 */
public class ScanVirsuDao {

    public static boolean isVirsu(String md5){
        boolean result  = false;
        String path = "/data/data/com.mobilesafe/files/antivirus.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }
}
