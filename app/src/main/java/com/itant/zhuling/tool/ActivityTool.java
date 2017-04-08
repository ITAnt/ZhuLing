package com.itant.zhuling.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.itant.zhuling.R;

/**
 * Created by Jason on 2017/4/8.
 */

public class ActivityTool {

    /**
     * Activity从右边往左边进，配合BaseActivity.finish()方法实现从左边往右边销毁
     * @param activity
     * @param intent
     */
    public static void startActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
    }

    /**
     * Activity从右边往左边进，配合BaseActivity.finish()方法实现从左边往右边销毁
     * @param activity
     * @param intent
     */
    @SuppressLint("NewApi")
    public static void startActivity(Activity activity, Intent intent, Bundle bundle) {
        activity.startActivity(intent, bundle);
        activity.overridePendingTransition(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
    }
}
