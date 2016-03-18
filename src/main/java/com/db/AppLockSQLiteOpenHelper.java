package com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/8/2.
 */
public class AppLockSQLiteOpenHelper extends SQLiteOpenHelper {
    public AppLockSQLiteOpenHelper(Context context) {
        super(context,"applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库
        db.execSQL("create table applock (_id integer primary key autoincrement,packName varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
