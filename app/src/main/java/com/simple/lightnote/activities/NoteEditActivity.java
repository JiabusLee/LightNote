package com.simple.lightnote.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseSwipeActivity;
import com.simple.lightnote.utils.ToastUtils;

/**
 * 编辑页面
 * 
 * @author homelink
 * 
 */
public class NoteEditActivity extends BaseSwipeActivity {
	private Toolbar mToolbar;
	private View contentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentView = View.inflate(this, R.layout.activity_noteedit, null);
		setContentView(contentView);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.noteedit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	long first = 0l;

	@Override
	public void onBackPressed() {
		if (first <= 0) {

			Snackbar snackbar = Snackbar.make(contentView,
					"Welcome to AndroidHive", Snackbar.LENGTH_LONG);
			snackbar.setCallback(new Callback() {
				@Override
				public void onDismissed(Snackbar snackbar, int event) {
					super.onDismissed(snackbar, event);
					ToastUtils.showToast(NoteEditActivity.this, "关闭了。。。");
				}
				@Override
				public void onShown(Snackbar snackbar) {
					super.onShown(snackbar);
					
				}
			});
			snackbar.setAction("取消", new OnClickListener() {

				@Override
				public void onClick(View v) {
					first = 0;
				}
			});
			snackbar.show();

		} else {
			long temp = System.currentTimeMillis();
			if (first + 500 <= temp) {
			} else {
				super.onBackPressed();
			}
			first = 0;
		}

	}
}
