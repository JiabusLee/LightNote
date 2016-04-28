package com.simple.lightnote.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simple.lightnote.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by homelink on 2016/4/28.
 */
public class ItemView extends FrameLayout {
    Context mContext;
    @Bind(R.id.view_item_tv_subtitle)
    TextView tv_title;
    @Bind(R.id.view_item_tv_title)
    TextView tv_subTitle;
    @Bind(R.id.view_item_cb)
    CheckBox cb;
    @Bind(R.id.view_item_ll)
    LinearLayout ll;

    public ItemView(Context context) {
        this(context,null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_item_linear, this, true);
        ButterKnife.bind(this);
    }

    public void setTitle(@NonNull String text){
        tv_title.setText(text);
    }

    public void setSubTitle(@NonNull String text){
        tv_subTitle.setText(text);
    }

    public void check(boolean check){
        cb.setChecked(check);
    }


    public void setCheckBoxPosition(){

    }


}
