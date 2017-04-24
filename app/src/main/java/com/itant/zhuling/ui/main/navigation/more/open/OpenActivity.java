package com.itant.zhuling.ui.main.navigation.more.open;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dl7.tag.TagLayout;
import com.itant.zhuling.R;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

/**
 * Created by iTant on 2017/3/26.
 * 开源
 */

public class OpenActivity extends BaseSwipeActivity {
    private final String[] opens = {"rxandroid", "nineoldandroids", "daimajia", "smarttablayout",
            "contextmenu", "eventbus", "smooth-app-bar-layout", "circleimageview",
            "recyclerview-animators", "swipeback", "calligraphy", "CommonAdapter",
            "TagLayout", "PhotoView", "xUtils", "umeng"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_more_open);
        // 右划关闭
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        setTitle("开源");

        initView();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            toolbar.setTitleTextColor(Color.WHITE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TagLayout tl_open = (TagLayout) findViewById(R.id.tl_open);
        for (String open : opens) {
            tl_open.addTag(open);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
