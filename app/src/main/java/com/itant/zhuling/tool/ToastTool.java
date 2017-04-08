package com.itant.zhuling.tool;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itant.zhuling.R;

/**
 * Created by Jason on 2017/3/26.
 */

public class ToastTool {

    private static long mShortMillis;
    private static String mLastShortText;

    private static long mLongMillis;
    private static String mLastLongText;

    /**
     * 短时间Toast
     * @param context
     * @param text
     */
    public static void showShort(Context context, String text) {

        if (TextUtils.equals(text, mLastShortText) && System.currentTimeMillis()-mShortMillis < 3000) {
            // 防止频繁Toast
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.widget_toast, null);
        TextView tv_toast = (TextView) view.findViewById(R.id.tv_toast);
        tv_toast.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();

        mShortMillis = System.currentTimeMillis();
        mLastShortText = text;
    }

    /**
     * 长时间Toast
     * @param context
     * @param text
     */
    public static void showLong(Context context, String text) {

        if (TextUtils.equals(text, mLastLongText) && System.currentTimeMillis()-mLongMillis < 5000) {
            // 防止频繁Toast
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.widget_toast, null);
        TextView tv_toast = (TextView) view.findViewById(R.id.tv_toast);
        tv_toast.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();

        mLongMillis = System.currentTimeMillis();
        mLastLongText = text;
    }
}
