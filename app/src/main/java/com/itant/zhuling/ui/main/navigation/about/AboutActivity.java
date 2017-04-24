package com.itant.zhuling.ui.main.navigation.about;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.ActivityTool;
import com.itant.zhuling.tool.AppTool;
import com.itant.zhuling.tool.PreferencesTool;
import com.itant.zhuling.tool.SocialTool;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.itant.zhuling.ui.main.MainContract;
import com.itant.zhuling.ui.main.MainPresenter;
import com.itant.zhuling.ui.main.UpdateInfo;
import com.itant.zhuling.ui.main.navigation.about.contents.DonateActivity;
import com.itant.zhuling.ui.main.navigation.about.contents.HelpActivity;
import com.itant.zhuling.ui.main.navigation.about.contents.WeiboActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.lang.reflect.Field;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by iTant on 2017/4/4.
 */

public class AboutActivity extends BaseSwipeActivity implements View.OnClickListener, MainContract.View {
    private int times;
    private MainContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_about);
        // 右划关闭
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        setTitle("关于");

        initView();
        // 获取更新信息
        presenter = new MainPresenter(this, this);
    }
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("竹翎 V" + AppTool.getVersionName(this));

        // 加入我们
        findViewById(R.id.ll_join).setOnClickListener(this);
        // 检查更新
        findViewById(R.id.ll_update).setOnClickListener(this);
        // 微博
        findViewById(R.id.ll_weibo).setOnClickListener(this);
        // 捐助
        findViewById(R.id.ll_donate).setOnClickListener(this);
        // 使用帮助
        findViewById(R.id.ll_help).setOnClickListener(this);
        // 点击头像
        findViewById(R.id.iv_logo).setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_update:
                presenter.getUpdateInfo();
                break;

            case R.id.ll_join:
                // 加入
                SocialTool.joinQQGroup(this);
                break;
            case R.id.ll_weibo:
                // 微博
                ActivityTool.startActivity(this, new Intent(this, WeiboActivity.class));
                break;
            case R.id.ll_donate:
                // 捐助
                ActivityTool.startActivity(this, new Intent(this, DonateActivity.class));
                break;
            case R.id.ll_help:
                // 使用帮助
                ActivityTool.startActivity(this, new Intent(this, HelpActivity.class));
                break;

            case R.id.iv_logo:
                times++;
                if (times == 10) {
                    boolean advanced = PreferencesTool.getBoolean(this, "advanced");
                    if (!advanced) {
                        // 还没开启
                        PreferencesTool.putBoolean(this, "advanced", true);
                        ToastTool.showShortRed(this, "请重新打开竹翎查看");
                        //EventBus.getDefault().post(AppEvent.EVENT_OPEN_ADVANCED);
                    } else {
                        ToastTool.showShortRed(this, "您已开启高级功能");
                    }
                }
                break;
        }
    }

    @Override
    public void onGetUpdateInfoSuc(UpdateInfo updateInfo) {
        // 获取更新信息成功
        if (updateInfo != null) {

            ZhuConstants.musicEnable = !updateInfo.getMusicDisable();

            try {
                int serverVersionCode = Integer.parseInt(updateInfo.getVersionCode());
                if (AppTool.getVersionCode(this) >= serverVersionCode) {
                    ToastTool.showShort(this, "当前已经是最新版本啦");
                    return;
                }

                switch (updateInfo.getUpdateType()) {
                    case "1":
                        // 1强制更新，到应用市场
                        goMarketUpdate(true, updateInfo);
                        break;
                    case "2":
                        // 2强制更新，Bmob下载
                        break;
                    case "3":
                        // 3可选更新，到应用市场
                        goMarketUpdate(false, updateInfo);
                        break;
                    case "4":
                        // 4可选更新，Bmob下载
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 更新
     * @param force
     * @param updateInfo
     */
    private void goMarketUpdate(final boolean force, UpdateInfo updateInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("发现新版本")
                .setMessage("最新版本："+updateInfo.getVersionName()+"\r\n"+
                        "新版大小："+updateInfo.getPackageSizeMB()+"M"+"\r\n"
                        +updateInfo.getUpdateDesc()+ "(如市场不能更新，请到QQ群484111083下载)")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                if (force) {
                    // 对话框不能关闭
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setText("确定");
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    positiveButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            SocialTool.jumpMarket(AboutActivity.this);
                        }
                    });

                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setVisibility(View.GONE);
                } else {
                    // 对话框可以关闭
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setText("更新");
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    positiveButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            SocialTool.jumpMarket(AboutActivity.this);
                            dialog.dismiss();
                        }
                    });

                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setText("取消");
                    negativeButton.setTextColor(getResources().getColor(R.color.gray_1));
                    negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    negativeButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        // updateDialog一定要先show再findViewById(android.R.id.message)，否则会返回null
        updateDialog.show();

        // 设置内容字体大小
        TextView tv_message = (TextView)updateDialog.findViewById(android.R.id.message);
        if (tv_message != null) {
            tv_message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            // 设置内容字体颜色
            tv_message.setTextColor(getResources().getColor(R.color.txt_black));
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

    @Override
    public void onGetUpdateInfoFail(String msg) {

    }
}
