package com.itant.zhuling.ui.main.navigation.notice;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

/**
 * Created by iTant on 2017/3/26.
 */

public class NoticeActivity extends BaseSwipeActivity implements NoticeContract.View, SwipeRefreshLayout.OnRefreshListener {
    private LinearLayout ll_empty;

    private SwipeRefreshLayout swipe_refresh_layout;
    private TextView tv_notice;
    private TextView tv_title;

    private NoticeContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_notice);
        // 右划关闭
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        setTitle("通知");

        initView();

        presenter = new NoticePresenter(this, this);
        presenter.getNotice();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            toolbar.setTitleTextColor(Color.WHITE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_title = (TextView) findViewById(R.id.tv_title);

        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);

        swipe_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipe_refresh_layout.setOnRefreshListener(this);
        // 刚进来开始加载
        swipe_refresh_layout.setRefreshing(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onGetNoticeSuc(NoticeBean noticeBean) {
        swipe_refresh_layout.setRefreshing(false);


        if (noticeBean != null && !TextUtils.isEmpty(noticeBean.getNotice())) {
            tv_notice.setText(noticeBean.getNotice());
            tv_title.setText(noticeBean.getTitle());
            ll_empty.setVisibility(View.GONE);
            tv_title.setVisibility(View.VISIBLE);
            tv_notice.setVisibility(View.VISIBLE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.GONE);
            tv_notice.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetNoticeFail(String msg) {
        swipe_refresh_layout.setRefreshing(false);
        ToastTool.showShort(this, "获取更新日志失败");
        ll_empty.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.GONE);
        tv_notice.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        presenter.getNotice();
    }
}
