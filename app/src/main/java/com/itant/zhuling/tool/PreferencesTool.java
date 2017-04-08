package com.itant.zhuling.tool;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jason on 2017/4/6.
 */

public class PreferencesTool {
    private static final String NAME = "setting";

    public static String getString(String key) {

        return "";
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(key, value).commit();
    }
}
