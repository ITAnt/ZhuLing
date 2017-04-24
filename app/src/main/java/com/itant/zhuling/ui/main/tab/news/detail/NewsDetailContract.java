package com.itant.zhuling.ui.main.tab.news.detail;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

/**
 * Created by iTant on 2017/3/26.
 */

public interface NewsDetailContract {
    interface View extends IBaseView {
        void onGetNewsDetailSuc(NewsDetail newsBeen);

        void onGetNewsDetailFail(String msg);
    }

    interface Presenter extends IBasePresenter {
        void getNewsDetail(String postId);
    }
}
