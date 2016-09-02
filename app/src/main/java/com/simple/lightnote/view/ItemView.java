package com.simple.lightnote.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simple.lightnote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by homelink on 2016/4/28.
 */
public class ItemView extends FrameLayout {

    Context mContext;
    @BindView(R.id.view_item_tv_title)
    TextView tv_title;
    @BindView(R.id.view_item_tv_subtitle)
    TextView tv_subTitle;
    @BindView(R.id.view_item_cb)
    CheckBox cb;
    @BindView(R.id.view_item_ll)
    LinearLayout ll;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_item_linear, this, true);
        ButterKnife.bind(this);
    }

    public ItemView setTitle(@NonNull String text) {
        tv_title.setText(text);
        return this;
    }

    public ItemView setSubTitle(@NonNull String text) {
        tv_subTitle.setText(text);
        return this;
    }

    public ItemView check(boolean check) {
        cb.setChecked(check);
        return this;
    }


    public ItemView changeCheckBoxPosition() {
        return null;
    }

    public ItemView hideSubTitle(boolean flag) {
        if (!flag)
            tv_subTitle.setVisibility(View.VISIBLE);
        else tv_subTitle.setVisibility(GONE);
        return this;
    }

    public ItemView hideCheckBox(boolean flag) {
        if (!flag)
            cb.setVisibility(View.VISIBLE);
        else cb.setVisibility(GONE);
        return this;
    }


    public void click() {
        cb.setChecked(!cb.isChecked());
    }
}
