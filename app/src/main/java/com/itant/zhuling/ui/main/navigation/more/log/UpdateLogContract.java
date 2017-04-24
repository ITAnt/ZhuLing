package com.itant.zhuling.ui.main.navigation.more.log;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

/**
 * Created by iTant on 2017/3/26.
 */

public interface UpdateLogContract {
    interface View extends IBaseView {
        void onGetUpdateLogsSuc(UpdateLog updateLog);
        void onGetUpdateLogFail(String msg);

    }

    interface Presenter extends IBasePresenter {
        void getUpdateLogs();
    }
}
