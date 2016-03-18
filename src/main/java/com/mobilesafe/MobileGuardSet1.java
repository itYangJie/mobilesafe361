package com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


public class MobileGuardSet1 extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mobile_guard_set1);

    }
    /**
     * 进入下一个向导界面
     * @param
     */
    @Override
    public void showNext() {
        Intent intent = new Intent();
        intent.setClass(this, MobileGuardSet2.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    @Override
    public void showPrevious() {

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(MobileGuardSet1.this,"请完成整个向导过程",Toast.LENGTH_LONG).show();
    }
}
