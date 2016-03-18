package com.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.db.AppLockSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁数据库业务类
 * Created by Administrator on 2015/8/2.
 */
public class AppLockSQLiteDao {
    private AppLockSQLiteOpenHelper helper = null;

    public AppLockSQLiteDao(Context context) {
        helper = new AppLockSQLiteOpenHelper(context);
    }

    /**
     * 插入一个程序锁应用记录
     */
    public void insert(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packName", packName);
        db.insert("applock", null, values);
        db.close();
    }

    /**
     * 删除一个记录
     */
    public void delete(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock", "packName=?", new String[]{packName});
        db.close();
    }


    /**
     * 查询记录是否存在
     *
     * @param number
     * @return
     */
    public boolean find(String packName){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from applock where packName=?", new String[]{packName});
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部的包名
     * @param packname
     * @return
     */
    public List<String> findAll(){
        List<String> protectPacknames = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packName"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            protectPacknames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return protectPacknames;
    }
}
