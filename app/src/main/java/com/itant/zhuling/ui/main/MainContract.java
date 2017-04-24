package com.itant.zhuling.ui.main;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

/**
 * Created by iTant on 2017/3/26.
 */

public interface MainContract {
    interface View extends IBaseView {
        void onGetUpdateInfoSuc(UpdateInfo updateInfo);
        void onGetUpdateInfoFail(String msg);

    }

    interface Presenter extends IBasePresenter {
        void getUpdateInfo();
    }
}
