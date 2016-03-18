package com.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.db.BlackNumberSQLiteOpenHelper;
import com.domain.BlackNumberEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * �������洢���ݿ�ҵ����
 * Created by Administrator on 2015/8/2.
 */
public class BlackNumberSQLiteDao {
    private BlackNumberSQLiteOpenHelper helper = null;

    public BlackNumberSQLiteDao(Context context) {
        helper = new BlackNumberSQLiteOpenHelper(context);
    }

    /**
     * insert into blacknumber(number,mode) values(133432,1)
     * ���һ���������绰  ����ģʽ 1.�绰���� 2.�������� 3.ȫ������
     */
    public void insert(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        db.insert("blacknumber", null, values);
        db.close();
    }

    /**
     * delete from blacknumber where number=133432
     * ɾ��һ���������绰
     */
    public void delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }

    /**
     * update blacknumber set mode=2 where number=1233432
     * �޸�һ���������绰����ģʽ
     */
    public void update(String number, String newmode) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", newmode);
        db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
    }

    /**
     * ��ѯ�������������Ƿ����
     *
     * @param number
     * @return
     */
    public boolean find(String number){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }


    /**
     * ��ѯ���������ģʽ
     *
     * @param number
     * @return
     */
    public String findMode(String number){
        String result = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select mode from blacknumber where number=?", new String[]{number});
        if(cursor.moveToNext()){
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * ��ѯ���к�����
     */
    public List<BlackNumberEntity> findAll() {
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            List<BlackNumberEntity> list = new ArrayList<BlackNumberEntity>();
            Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
            while (cursor.moveToNext()) {
                BlackNumberEntity info = new BlackNumberEntity();
                String number = cursor.getString(0);
                String mode = cursor.getString(1);
                info.setMode(mode);
                info.setNumber(number);
                list.add(info);
            }
            cursor.close();
            db.close();
            return list;
        }catch (Exception e){
            return null;
        }

    }


    /**
     * ��ѯ���к�����
     */
    public List<BlackNumberEntity> findPart(int offset,int limit) {
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            List<BlackNumberEntity> list = new ArrayList<BlackNumberEntity>();
            Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc  limit ? offset ? ",
                    new String[]{String.valueOf(limit),String.valueOf(offset)});
            while (cursor.moveToNext()) {
                BlackNumberEntity info = new BlackNumberEntity();
                String number = cursor.getString(0);
                String mode = cursor.getString(1);
                info.setMode(mode);
                info.setNumber(number);
                list.add(info);
            }
            cursor.close();
            db.close();
            return list;
        }catch (Exception e){
            return null;
        }

    }
}
