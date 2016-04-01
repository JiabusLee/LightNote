package com.simple.lightnote.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseSwipeActivity;
import com.simple.lightnote.util.NoteUtil;

public class NoteDetailActivity extends BaseSwipeActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notedetails);
		String s_notedetail = getIntent().getStringExtra("notedetails");
		TextView notedetails=(TextView) findViewById(R.id.notedetails_tv);
		String text=NoteUtil.formatText(s_notedetail);
	}
}
