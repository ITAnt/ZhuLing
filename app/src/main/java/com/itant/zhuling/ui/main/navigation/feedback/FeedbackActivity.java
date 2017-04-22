package com.itant.zhuling.ui.main.navigation.feedback;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.itant.zhuling.R;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

/**
 * Created by Jason on 2017/4/4.
 */

public class FeedbackActivity extends BaseSwipeActivity implements FeedbackContract.View, View.OnClickListener {
    private EditText et_feedback;
    private FeedbackContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_feedback);

        // 右划删除
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        setTitle("反馈");

        initView();
        presenter = new FeedbackPresenter(this, this);
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            toolbar.setTitleTextColor(Color.WHITE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_feedback = (EditText) findViewById(R.id.et_feedback);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onSubmitSuc() {
        dismissProgressDialog();
        ToastTool.showShort(this, "我们会尽快处理");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    @Override
    public void onSubmitFail(String msg) {
        dismissProgressDialog();
        ToastTool.showShort(this, "提交失败");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                String feedback = et_feedback.getText().toString();
                if (TextUtils.isEmpty(feedback)) {
                    ToastTool.showShort(this, "请输入您的意见哦");
                    return;
                }

                // 隐藏软键盘
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                presenter.submitFeedback(feedback);
                showProgressDialog();
                break;
        }
    }
}
