package com.itant.zhuling.ui.main.navigation.notice;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

/**
 * Created by iTant on 2017/3/26.
 */

public interface NoticeContract {
    interface View extends IBaseView {
        void onGetNoticeSuc(NoticeBean noticeBean);
        void onGetNoticeFail(String msg);

    }

    interface Presenter extends IBasePresenter {
        void getNotice();
    }
}
