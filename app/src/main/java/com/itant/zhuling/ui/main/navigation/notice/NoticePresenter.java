package com.itant.zhuling.ui.main.navigation.notice;

import android.content.Context;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Jason on 2017/4/16.
 */

public class NoticePresenter implements NoticeContract.Presenter {
    private Context context;
    private NoticeContract.View view;
    private static final String UPDATE_LOG_ID = "Ew3N2224";

    public NoticePresenter(Context context, NoticeContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void getNotice() {
        BmobQuery<NoticeBean> query = new BmobQuery<>();
        query.getObject(context, UPDATE_LOG_ID, new GetListener<NoticeBean>() {

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
               view.onGetNoticeFail(arg1);
            }

            @Override
            public void onSuccess(NoticeBean noticeBean) {
                // TODO Auto-generated method stub
                view.onGetNoticeSuc(noticeBean);
            }
        });
    }
}
