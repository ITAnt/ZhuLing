package com.itant.zhuling.ui.tab.music;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public interface MusicContract {
    interface View extends IBaseView {
        void onGetMusicSuc(List<MusicBean> musicBeans);

        void onGetMusicFail(String msg);
    }

    interface Presenter extends IBasePresenter {
        void getMusic(int page);
    }
}
