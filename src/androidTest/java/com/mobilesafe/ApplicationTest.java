package com.mobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.db.BlackNumberSQLiteOpenHelper;
import com.domain.BlackNumberEntity;
import com.db.dao.BlackNumberSQLiteDao;

import java.util.List;
import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    //BlackNumberSQLiteDao dao = new BlackNumberSQLiteDao(getContext());

    public void testCreateDB() throws Exception {
        BlackNumberSQLiteOpenHelper helper = new BlackNumberSQLiteOpenHelper(
                getContext());
        helper.getWritableDatabase();
    }
    public void testDbAdd(){
        BlackNumberSQLiteDao dao = new BlackNumberSQLiteDao(getContext());
        for(int i=0;i<100;i++){
            String number = String.valueOf(123456+i);
            Random random = new Random();
            String mode = String.valueOf(random.nextInt(3)+1);
            dao.insert(number, "2");
        }

    }

    public void testDbQuery() throws Exception {
        BlackNumberSQLiteDao dao = new BlackNumberSQLiteDao(getContext());
        boolean result  = dao.find("123");
        assertEquals(true, result);
    }
    public void testUpdate() throws Exception {
        BlackNumberSQLiteDao dao = new BlackNumberSQLiteDao(getContext());
        dao.update("123456", "3");
    }

    public void testFindAll() throws Exception{
        BlackNumberSQLiteDao dao = new BlackNumberSQLiteDao(getContext());
        List<BlackNumberEntity> infos = dao.findAll();
        for(BlackNumberEntity info:infos){
            System.out.println(info.toString());
        }
    }

    public void testDelete() throws Exception {
        BlackNumberSQLiteDao dao = new BlackNumberSQLiteDao(getContext());
        for(int i=0;i<50;i++){
            dao.delete(String.valueOf(123456 + i));
        }
    }
}