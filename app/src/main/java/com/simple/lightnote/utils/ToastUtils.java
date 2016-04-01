package com.simple.lightnote.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	static Toast toast =null;
	public static void showToast(Context context, String msg) {
		showToast(context, msg,Toast.LENGTH_SHORT);
	}
	public static void showToast(Context context, String msg,int duration) {
		Toast.makeText(context, msg, duration).show();
	}
	
	public static void showSequenceToast(Context context,String msg){

		if(toast==null)
			toast =Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		else
			toast.setText(msg);
		toast.show();
	}
	
}
