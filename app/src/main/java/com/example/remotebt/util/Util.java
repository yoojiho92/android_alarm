package com.example.remotebt.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Util {
    public static void save_pref(Context context, String key, String value){
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String get_pref(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }
}
