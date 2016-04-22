package com.simple.lightnote.view;

import android.app.Dialog;
import android.content.Context;

import com.simple.lightnote.R;

/**
 * Created by homelink on 2016/4/22.
 */
public class CommonDialog extends Dialog {
    private Context mContext;
    public CommonDialog(Context context) {
        this(context,-1);
    }

    public CommonDialog(Context context, int themeResId) {
        super(context,R.style.commonDialog);
        this.mContext=context;
    }


}
