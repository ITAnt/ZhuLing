package com.itant.zhuling.ui.navigation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.itant.zhuling.R;
import com.itant.zhuling.ui.base.BaseActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

/**
 * Created by Jason on 2017/4/4.
 */

public class MoreActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // 左划删除
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        setTitle("设置");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
