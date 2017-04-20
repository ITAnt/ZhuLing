package com.itant.zhuling.ui.tab.music;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;
import com.itant.zhuling.ui.tab.music.bean.Music;

import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public interface MusicContract {
    interface View extends IBaseView {
        void onGetMusicSuc(List<Music> musicBeans);

        void onGetMusicFail(String msg);
    }

    interface Presenter extends IBasePresenter {
        void getMusic(int position, String keywords, int page);//0小狗 1凉窝 2企鹅 3白云 4熊掌 5龙虾 6推荐
    }
}
