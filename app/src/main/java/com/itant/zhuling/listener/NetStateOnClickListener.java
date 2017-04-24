package com.itant.zhuling.listener;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.application.ZhuManager;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.tool.net.NetworkTool;

import java.lang.reflect.Field;

/**
 * Created by iTant on 2017/4/23.
 * 响应点击事件之前要提示是否使用移动流量继续
 */

public class NetStateOnClickListener implements View.OnClickListener {
    private Activity mActivity;

    public NetStateOnClickListener(Activity activity) {
        if (activity == null) {
            throw new NullPointerException("NetStateOnClickListener should own an activity to show dialog");
        }
        mActivity = activity;
    }

    @Override
    public void onClick(View v) {
        if (!NetworkTool.isNetworkConnected(ZhuManager.getInstance().getContext())) {
            ToastTool.showShort(ZhuManager.getInstance().getContext(), "请检查网络");
            return;
        }

        if (!NetworkTool.isWiFiConnected(ZhuManager.getInstance().getContext())) {
            alertWiFiNotConnected();
        } else {
            onContinueAction();
        }
    }

    /**
     * 我是土豪，不管是不是使用流量，继续进行
     */
    protected void onContinueAction() {

    }

    /**
     * 我的流量宝贵，不要继续进行操作了
     */
    protected void onCancelAction() {

    }

    /**
     * 提醒用户，WiFi没有连接，是否继续。由于自带对话框的字体太粗糙了，我们这里自定义一下。
     */
    private void alertWiFiNotConnected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setMessage("WiFi网络没有连接，确定要继续进行吗？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onContinueAction();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCancelAction();
                    }
                });

        final AlertDialog updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setCanceledOnTouchOutside(false);
        // 一定要先show再findViewById(android.R.id.message)，否则会返回null
        updateDialog.show();
        // 对话框可以关闭
        Button positiveButton = updateDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setText("确定");
        positiveButton.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

        Button negativeButton = updateDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setText("取消");
        negativeButton.setTextColor(mActivity.getResources().getColor(R.color.gray_1));
        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

        // 设置内容字体大小
        TextView tv_message = (TextView)updateDialog.findViewById(android.R.id.message);
        if (tv_message != null) {
            tv_message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            // 设置内容字体颜色
            tv_message.setTextColor(mActivity.getResources().getColor(R.color.txt_black));
        }

        // 标题是粗体
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(updateDialog);

            Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);

            TextView tv_title = (TextView) mTitleView.get(alertController);
            if (tv_title != null) {
                tv_title.setTypeface(tv_title.getTypeface(), Typeface.BOLD);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
