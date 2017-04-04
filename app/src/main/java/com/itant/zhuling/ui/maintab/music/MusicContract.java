package com.itant.zhuling.ui.maintab.music;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public interface MusicContract {
    interface View extends IBaseView {
        void onGetRepoSucc(List<MusicBean> musicBeans);

        void onGetRepoFail();
    }

    interface Presenter extends IBasePresenter {
        void getRepo();
    }
}
