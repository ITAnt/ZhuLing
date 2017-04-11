package com.itant.zhuling.ui.navigation;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.itant.zhuling.R;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Jason on 2017/4/4.
 */

public class AboutActivity extends BaseSwipeActivity implements View.OnTouchListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // 右划删除
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        setTitle("关于");

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
        /*ll_top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 我们必须等view加载之后才开始动画，否则取到的宽高为0
                ll_top.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                showTopLayout(ll_top.getWidth()/2, ll_top.getHeight()/2);
            }
        });*/
        ll_top.setOnTouchListener(this);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
}
