package com.simple.lightnote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MainListView extends ListView{
	private Context mContext;
	public MainListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext=context;
	}

	public MainListView(Context context, AttributeSet attrs) {
		this(context, attrs,-1);
	}

	public MainListView(Context context) {
		this(context,null);
	}

}
