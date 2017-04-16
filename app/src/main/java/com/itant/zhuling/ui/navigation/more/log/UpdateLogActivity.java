package com.itant.zhuling.ui.navigation.more.log;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

/**
 * Created by Jason on 2017/3/26.
 */

public class UpdateLogActivity extends BaseSwipeActivity implements UpdateLogContract.View {

    private TextView tv_log;
    private UpdateLogContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_log);
        // 右划删除
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        setTitle("日志");

        initView();

        presenter = new UpdateLogPresenter(this, this);
        presenter.getUpdateLogs();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            toolbar.setTitleTextColor(Color.WHITE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_log = (TextView) findViewById(R.id.tv_log);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onGetUpdateLogsSuc(UpdateLog updateLog) {

        if (updateLog == null) {
            return;
        }

        if (!TextUtils.isEmpty(updateLog.getHistoryLogs())) {
            tv_log.setText(updateLog.getHistoryLogs());
        }
    }

    @Override
    public void onGetUpdateLogFail(String msg) {

    }
}
