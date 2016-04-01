package com.simple.lightnote.activity;

import android.os.Bundle;

import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseSwipeActivity;

public class OverScrollViewActivity extends BaseSwipeActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_overscrollview);
	}
}
