package com.simple.lightnote.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class SimpleEditText extends EditText implements TextWatcher {

	public SimpleEditText(Context context) {
		this(context,null);
	}

	public SimpleEditText(Context context, AttributeSet attrs) {
		this(context, attrs,-1);
	}

	public SimpleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence text, int start, int lengthBefore,
			int lengthAfter) {

	}
}
