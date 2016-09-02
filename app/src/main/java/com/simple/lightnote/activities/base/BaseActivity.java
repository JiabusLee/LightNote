package com.simple.lightnote.activities.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.simple.lightnote.R;
import com.simple.lightnote.util.ActivityManagerUtil;

public abstract class BaseActivity extends AppCompatActivity implements
        OnClickListener {
    public ActivityManagerUtil activityManagerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManagerUtil = ActivityManagerUtil.getInstance();
        activityManagerUtil.pushOneActivity(this);
    }

    @Override
    public void onClick(View v) {
    }


    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityManagerUtil.popOneActivity(this);
    }
}
