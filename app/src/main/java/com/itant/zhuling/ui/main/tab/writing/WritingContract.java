package com.itant.zhuling.ui.main.tab.writing;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

import java.util.List;

/**
 * Created by iTant on 2017/3/26.
 */

public interface WritingContract {
    interface View extends IBaseView {
        void onGetWritingSuc(List<WritingBean> newsBeen);

        void onGetWritingFail(String msg);
    }

    interface Presenter extends IBasePresenter {
        void getWriting(int page);
    }
}
