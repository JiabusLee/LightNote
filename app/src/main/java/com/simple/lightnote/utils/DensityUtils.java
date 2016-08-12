package com.simple.lightnote.utils;

import android.content.Context;


public final class DensityUtils {

    private static float density = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;

    private DensityUtils() {
    }

    public static float getDensity(Context context) {
        if (density <= 0F) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) (dpValue * getDensity(context) + 0.5F);
    }

    public static int px2dip(Context context, int pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5F);
    }

}
