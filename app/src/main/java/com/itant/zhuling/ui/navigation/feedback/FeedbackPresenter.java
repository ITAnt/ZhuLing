package com.itant.zhuling.ui.navigation.feedback;

import android.content.Context;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Jason on 2017/4/16.
 */

public class FeedbackPresenter implements FeedbackContract.Presenter {
    private Context context;
    private FeedbackContract.View view;

    public FeedbackPresenter(Context context, FeedbackContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void submitFeedback(String feedback) {

        FeedbackBean p2 = new FeedbackBean();
        p2.setFeedback(feedback);
        p2.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                view.onSubmitSuc();
            }

            @Override
            public void onFailure(int i, String s) {
                view.onSubmitFail(s);
            }
        });
    }
}
