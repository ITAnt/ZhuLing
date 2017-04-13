package com.itant.zhuling.ui.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.tool.ActivityTool;
import com.itant.zhuling.tool.AppTool;
import com.itant.zhuling.tool.SocialTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.itant.zhuling.ui.navigation.about.DonateActivity;
import com.itant.zhuling.ui.navigation.about.WeiboActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Jason on 2017/4/4.
 */

public class AboutActivity extends BaseSwipeActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // 右划删除
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        setTitle("关于");

        initView();

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
        // 微博
        findViewById(R.id.ll_weibo).setOnClickListener(this);
        findViewById(R.id.ll_donate).setOnClickListener(this);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
            case R.id.ll_donate:
                // 捐助
                ActivityTool.startActivity(this, new Intent(this, DonateActivity.class));
                break;

        }
    }
}
