package com.itant.zhuling.ui.main.tab.news;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public interface NewsContract {
    interface View extends IBaseView {
        void onGetNewsSuc(List<NewsBean> newsBeen);

        void onGetNewsFail(String msg);
    }

    interface Presenter extends IBasePresenter {
        void getNews(int page);
    }
}
