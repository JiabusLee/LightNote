package com.simple.lightnote.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class ScreenUtils {

	private ScreenUtils() {
		throw new AssertionError();
	}

	public static float dpToPx(Context context, float dp) {
		if (context == null) {
			return -1;
		}
		return dp * context.getResources().getDisplayMetrics().density;
		
	}

	public static float pxToDp(Context context, float px) {
		if (context == null) {
			return -1;
		}
		return px / context.getResources().getDisplayMetrics().density;
	}

	public static int dpToPxInt(Context context, float dp) {
		return (int) (dpToPx(context, dp) + 0.5f);
	}

	public static int pxToDpCeilInt(Context context, float px) {
		return (int) (pxToDp(context, px) + 0.5f);
	}
	
	public static int[] getScreenMetrics(Context context){
		int screenMetrics[]=new int[2];
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		screenMetrics[0]=displayMetrics.widthPixels;
		screenMetrics[1]=displayMetrics.heightPixels;
		return screenMetrics;
	}
	public static boolean inRangeOfView(View view, MotionEvent ev) {
		int[] location = new int[2];
		if (view == null) {
			return false;
		}
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		return !(ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y
				|| ev.getY() > (y + view.getHeight()));
	}
}
