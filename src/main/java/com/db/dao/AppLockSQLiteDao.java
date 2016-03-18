package com.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.db.AppLockSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * ���������ݿ�ҵ����
 * Created by Administrator on 2015/8/2.
 */
public class AppLockSQLiteDao {
    private AppLockSQLiteOpenHelper helper = null;

    public AppLockSQLiteDao(Context context) {
        helper = new AppLockSQLiteOpenHelper(context);
    }

    /**
     * ����һ��������Ӧ�ü�¼
     */
    public void insert(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packName", packName);
        db.insert("applock", null, values);
        db.close();
    }

    /**
     * ɾ��һ����¼
     */
    public void delete(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock", "packName=?", new String[]{packName});
        db.close();
    }


    /**
     * ��ѯ��¼�Ƿ����
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
     * ��ѯȫ���İ���
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
