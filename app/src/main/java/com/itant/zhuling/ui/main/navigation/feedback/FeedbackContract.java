package com.itant.zhuling.ui.main.navigation.feedback;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

/**
 * Created by Jason on 2017/3/26.
 */

public interface FeedbackContract {
    interface View extends IBaseView {
        void onSubmitSuc();
        void onSubmitFail(String msg);

    }

    interface Presenter extends IBasePresenter {
        void submitFeedback(String feedback);
    }
}
