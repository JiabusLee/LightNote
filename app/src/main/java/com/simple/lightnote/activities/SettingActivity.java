package com.simple.lightnote.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.utils.ToastUtils;
import com.simple.lightnote.view.ItemView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements EvernoteLoginFragment.ResultCallback, View.OnClickListener {
    @Bind(R.id.itemView_0)
    ItemView item_view_test;
    @Bind(R.id.itemView_1)
    ItemView item_view_bindEvernote;
    @Bind({R.id.itemView_2})
    ItemView item_view_backup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        item_view_test.check(true).setTitle("测试正标题").setSubTitle("副标题");
        item_view_bindEvernote.hideCheckBox(true).hideSubTitle(true).setTitle("绑定Evernote");
        item_view_backup.hideCheckBox(true).hideSubTitle(true).setTitle("备份与恢复");
    }


    @Override
    public void onLoginFinished(boolean successful) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EvernoteSession.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    // handle success
                } else {
                    // handle failure
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    @Override
    @OnClick({R.id.itemView_1,R.id.itemView_2})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                super.onClick(v);
            case R.id.itemView_0:
                EvernoteSession.getInstance().authenticate(SettingActivity.this);
                item_view_bindEvernote.setEnabled(false);
                break;
            case R.id.itemView_2:
                ToastUtils.showSequenceToast(this,"备份");
                break;
        }

    }
}
