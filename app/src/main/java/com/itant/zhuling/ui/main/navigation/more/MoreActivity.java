package com.itant.zhuling.ui.main.navigation.more;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.tool.ActivityTool;
import com.itant.zhuling.tool.FileTool;
import com.itant.zhuling.tool.PreferencesTool;
import com.itant.zhuling.tool.SocialTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.itant.zhuling.ui.main.navigation.more.open.OpenActivity;
import com.itant.zhuling.ui.main.navigation.more.log.UpdateLogActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.text.DecimalFormat;

/**
 * Created by Jason on 2017/4/4.
 */

public class MoreActivity extends BaseSwipeActivity implements View.OnClickListener, View.OnTouchListener, CompoundButton.OnCheckedChangeListener {

    private DecimalFormat format;

    private TextView tv_cache;
    private LinearLayout ll_top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_more);
        // 右划删除
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        setTitle("更多");

        initView();
    }



    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 顶部
        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        /*ll_top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 我们必须等view加载之后才开始动画，否则取到的宽高为0
                ll_top.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                showTopLayout(ll_top.getWidth()/2, ll_top.getHeight()/2);
            }
        });*/
        ll_top.setOnTouchListener(this);

        // 分享
        findViewById(R.id.ll_share).setOnClickListener(this);
        // 相关开源
        findViewById(R.id.ll_open_source).setOnClickListener(this);
        // 清理缓存
        findViewById(R.id.ll_clean).setOnClickListener(this);
        // 更新日志
        findViewById(R.id.ll_update_logs).setOnClickListener(this);
        // 夜间模式
        Switch switch_night = (Switch) findViewById(R.id.switch_night);
        boolean isNight = PreferencesTool.getBoolean(this, "night");
        switch_night.setChecked(isNight);
        //switch_night.setOnCheckedChangeListener(this);

        format = new DecimalFormat("######0.00");//保留两位小数
        tv_cache = (TextView) findViewById(R.id.tv_cache);
        tv_cache.setOnClickListener(this);
        // 刚好，我们指定的缓存目录和安卓getExternalCacheDir()是同一个地方，如果我们在Constants里指定的缓存目录
        // 是别的目录，则可能还要把那个目录算进去（如果是需要删除的缓存的话）。这里我们算一次就行了。
        long externalCacheSize = FileTool.calculateCacheSize(getExternalCacheDir());
        long internalCacheSize = FileTool.calculateCacheSize(getCacheDir());
        double totalCacheSize = ((double)(externalCacheSize + internalCacheSize)) / 1024 / 1024;
        tv_cache.setText(format.format(totalCacheSize) + "MB");
    }

    /**
     * 显示头部layout
     * @param centerX
     * @param centerY
     */
    private void showTopLayout(int centerX, int centerY) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    ll_top,
                    centerX,
                    centerY,
                    0,
                    (float) Math.hypot(ll_top.getWidth(), ll_top.getHeight()));

            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(2000);
            animator.start();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_share:
                // 分享
                SocialTool.shareApp(this);
                break;

            case R.id.ll_open_source:
                // 开源
                ActivityTool.startActivity(this, new Intent(this, OpenActivity.class));
                break;

            case R.id.ll_update_logs:
                ActivityTool.startActivity(this, new Intent(this, UpdateLogActivity.class));
                break;

            case R.id.tv_cache:
                alertClean();
                break;

            case R.id.ll_clean:
                alertClean();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.ll_top:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showTopLayout((int)event.getX(), (int)event.getY());
                }
                break;
        }
        //return true MotionEvent.ACTION_UP才能正常响应
        return true;
    }

    /**
     * 弹出对话框询问是否要清除缓存
     */
    private void alertClean() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("确定清除应用缓存？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileTool.deletePacketsByDirectory(getExternalCacheDir());
                        FileTool.deletePacketsByDirectory(getCacheDir());
                        tv_cache.setText("0.00MB");
                        dialog.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_night:
                if (isChecked) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    recreate();
                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    recreate();
                }
                PreferencesTool.putBoolean(this, "night", isChecked);
                break;
        }
    }
}
