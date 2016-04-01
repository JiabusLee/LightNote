package com.simple.lightnote.activities.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.MarginLayoutParams;

import com.simple.lightnote.R;
import com.simple.lightnote.view.SwipeBackLayout;

public class BaseSwipeActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SwipeBackLayout swipeLayout=new SwipeBackLayout(this);
		MarginLayoutParams params=new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT);
		swipeLayout.setLayoutParams(params);
		swipeLayout.attachToActivity(this);
	}
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.enter, R.anim.out);
	}
}
