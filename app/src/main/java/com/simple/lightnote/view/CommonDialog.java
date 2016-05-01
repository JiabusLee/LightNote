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
        this(context, -1);
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, R.style.commonDialog);
        this.mContext = context;
    }

    @Override
    public void show() {
        super.show();
      /*  ObjectAnimator mAnimatorAlpha = ObjectAnimator.ofFloat(getWindow().getDecorView(), "alpha", 0.6f, 1f);

        AnimatorSet set = new AnimatorSet();
//        mAnimatorAlpha.setDuration(200);

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(getWindow().getDecorView(), "scaleX", 0.6f, 1f);

//        mAnimatorScaleX.setDuration(200);

        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(getWindow().getDecorView(), "scaleY",  0.6f, 1f);
//        mAnimatorScaleY.setDuration(200);

        set.playTogether(mAnimatorAlpha, mAnimatorScaleX, mAnimatorScaleY);
        set.setDuration(300);
        set.start();*/
    }

    @Override
    public void dismiss() {
        super.dismiss();
       /* ObjectAnimator mAnimatorAlpha = ObjectAnimator.ofFloat(getWindow().getDecorView(), "alpha", 1f, 0f);

        AnimatorSet set = new AnimatorSet();
        mAnimatorAlpha.setDuration(1000);

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(getWindow().getDecorView(), "scaleX", 1.0f, 0.0f);

        mAnimatorScaleX.setDuration(1000);

        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(getWindow().getDecorView(), "scaleY", 1.0f, 0.0f);
        mAnimatorScaleY.setDuration(1000);

        set.playTogether(mAnimatorAlpha, mAnimatorScaleX, mAnimatorScaleY);
        set.setDuration(500);
        set.start();*/
    }

/*    @Override
    public void hide() {
        super.hide();
    }*/
}
