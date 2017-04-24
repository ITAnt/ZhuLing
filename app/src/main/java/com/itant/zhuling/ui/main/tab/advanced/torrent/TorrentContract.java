package com.itant.zhuling.ui.main.tab.advanced.torrent;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

/**
 * Created by iTant on 2017/3/26.
 */

public interface TorrentContract {
    interface View extends IBaseView {
        void onGetTorrentSuc(String updateLog);
        void onGetTorrentFail(String msg);

    }

    interface Presenter extends IBasePresenter {
        void getTorrent(String key);
    }
}
