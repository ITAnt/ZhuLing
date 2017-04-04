package com.itant.zhuling.ui.maintab.news;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public interface NewsContract {
    interface View extends IBaseView {
        void onGetRepoSucc(List<NewsBean> newsBeen);

        void onGetRepoFail();
    }

    interface Presenter extends IBasePresenter {
        void getRepo();
    }
}
