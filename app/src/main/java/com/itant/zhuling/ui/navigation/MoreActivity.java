package com.itant.zhuling.ui.navigation;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.itant.zhuling.R;
import com.itant.zhuling.tool.ActivityTool;
import com.itant.zhuling.tool.SocialTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.itant.zhuling.ui.navigation.more.WeiboActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

/**
 * Created by Jason on 2017/4/4.
 */

public class MoreActivity extends BaseSwipeActivity implements View.OnClickListener, View.OnTouchListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        // 右划删除
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        setTitle("更多");

        initView();

    }

    private LinearLayout ll_top;
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 顶部
        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        ll_top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 我们必须等view加载之后才开始动画，否则取到的宽高为0
                ll_top.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                showTopLayout(ll_top.getWidth()/2, ll_top.getHeight()/2);
            }
        });
        ll_top.setOnTouchListener(this);

        // 加入我们
        findViewById(R.id.ll_join).setOnClickListener(this);
        // 微博
        findViewById(R.id.ll_weibo).setOnClickListener(this);
        // 分享
        findViewById(R.id.ll_share).setOnClickListener(this);
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
            case R.id.ll_join:
                // 加入
                SocialTool.joinQQGroup(this);
                break;
            case R.id.ll_weibo:
                // 微博
                ActivityTool.startActivity(this, new Intent(this, WeiboActivity.class));
                break;

            case R.id.ll_share:
                // 分享
                SocialTool.shareApp(this);
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
}
