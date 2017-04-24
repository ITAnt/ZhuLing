package com.itant.zhuling.ui.main.tab.advanced.meizhi;

import com.itant.zhuling.ui.base.IBasePresenter;
import com.itant.zhuling.ui.base.IBaseView;

import java.util.List;

/**
 * Created by iTant on 2017/3/26.
 */

public interface MeizhiContract {
    interface View extends IBaseView {
        void getMeizhiSuc(List<Meizhi> meizhis);

        void getMeizhiFail(String msg);
    }

    interface Presenter extends IBasePresenter {
        void getMeizhi(int page);
    }
}
