package com.itant.zhuling.tool;

import android.util.Log;

import com.itant.zhuling.constant.ZhuConstants;

/**
 * Created by Jason on 2017/4/9.
 */

public class LogTool {
    private static final String TAG = "zhuling";

    public static void i(String message) {
        if (ZhuConstants.DEBUG) {
            Log.i(TAG, message);
        }
    }

    public static void e(String message) {
        if (ZhuConstants.DEBUG) {
            Log.e(TAG, message);
        }
    }
}
