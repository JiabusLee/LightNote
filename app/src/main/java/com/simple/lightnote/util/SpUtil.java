package com.simple.lightnote.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by homelink on 2016/4/7.
 */
public class SpUtil {

    public static final String LIGHT_NOTE = "lightNote";

    public static SharedPreferences getInstance(Context context){
        return context.getSharedPreferences(LIGHT_NOTE, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context){
        return getInstance(context).edit();
    }


    public static  String getString(Context context,String key,String defaultValue){
        return getInstance(context).getString(key,defaultValue);
    }



}
