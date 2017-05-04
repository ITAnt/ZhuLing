package com.itant.zhuling.ui.main.tab.advanced.torrent;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.tool.UITool;
import com.itant.zhuling.ui.base.BaseActivity;

/**
 * Created by iTant on 2017/3/26.
 */

public class TorrentActivity extends BaseActivity implements TorrentContract.View, View.OnClickListener {
    private EditText et_search;
    private ImageView iv_search;

    private TextView tv_log;
    private TorrentContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_torrent);
        setTitle("磁链");

        initView();

        presenter = new TorrentPresenter(this, this);
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            toolbar.setTitleTextColor(Color.WHITE);
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tv_log = (TextView) findViewById(R.id.tv_log);

        et_search = (EditText) findViewById(R.id.et_search);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onGetTorrentSuc(String updateLog) {
        if (!TextUtils.isEmpty(updateLog)) {
            tv_log.setVisibility(View.VISIBLE);
            tv_log.setText(updateLog);
        } else {
            tv_log.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetTorrentFail(String msg) {
        ToastTool.showShort(this, "获取失败");
        tv_log.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                String key = et_search.getText().toString();
                if (TextUtils.isEmpty(key)) {
                    ToastTool.showShort(this, "关键字不能为空");
                    return;
                }
                presenter.getTorrent(key);
                break;
        }
    }

    /*点击其他地方隐藏软键盘*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (UITool.isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    et_search.clearFocus();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
}
