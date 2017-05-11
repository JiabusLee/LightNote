package com.simple.lightnote.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.ImageView;

import com.simple.lightnote.activities.base.BaseActivity;

/**
 * Created by HERO on 2017/4/13.
 */

public class CodeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static void start(BaseActivity activity) {
        activity.startActivity(new Intent(activity,CodeActivity.class));
    }



    private void startActivity(View view, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView imageView = new ImageView(this);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, imageView, "transition_animation_news_photos");
            startActivity(intent, options.toBundle());
        } else {
/*            ActivityOptionsCompat.makeCustomAnimation(this,
                    R.anim.slide_bottom_in, R.anim.slide_bottom_out);
            这个我感觉没什么用处，类似于
            overridePendingTransition(R.anim.slide_bottom_in, android.R.anim.fade_out);*/

/*            ActivityOptionsCompat.makeThumbnailScaleUpAnimation(source, thumbnail, startX, startY)
            这个方法可以用于4.x上，是将一个小块的Bitmpat进行拉伸的动画。*/

            //让新的Activity从一个小的范围扩大到全屏
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
    }

}
