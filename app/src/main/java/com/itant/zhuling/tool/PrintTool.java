package com.itant.zhuling.tool;

import android.util.Log;

import com.itant.zhuling.constant.ZhuConstants;

/**
 * Created by iTant on 2017/4/24.
 */

public class PrintTool {
    public static void i(String tag, String msg) {
        if (ZhuConstants.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (ZhuConstants.DEBUG) {
            Log.e(tag, msg);
        }
    }
}
