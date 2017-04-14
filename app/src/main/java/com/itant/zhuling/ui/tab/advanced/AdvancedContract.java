package com.itant.zhuling.ui.tab.advanced;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public interface AdvancedContract {
    interface View extends IBaseView {
        void onGetWritingSuc(List<AdvancedBean> newsBeen);

        void onGetWritingFail(String msg);
    }

    interface Presenter extends IBasePresenter {
        void getWriting(int page);
    }
}
