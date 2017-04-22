package com.itant.zhuling.ui.main.tab.sentence;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public interface SentenceContract {
    interface View extends IBaseView {
        void onGetSentenceSuc(List<SentenceBean> newsBeen);

        void onGetSentenceFail(String msg);
    }

    interface Presenter extends IBasePresenter {
        void getSentences(int page);
    }
}
