package com.simple.lightnote.activities;

import android.os.Bundle;

import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.view.ItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.itemView_1)
    ItemView item_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        item_view.check(true);
        item_view.setTitle("测试正标题");
        item_view.setSubTitle("副标题");
    }


}
