package com.itant.zhuling.ui.main.navigation.more.log;

import android.content.Context;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Jason on 2017/4/16.
 */

public class UpdateLogPresenter implements UpdateLogContract.Presenter {
    private Context context;
    private UpdateLogContract.View view;
    private static final String UPDATE_LOG_ID = "usbNAAAL";

    public UpdateLogPresenter(Context context, UpdateLogContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void getUpdateLogs() {
        BmobQuery<UpdateLog> query = new BmobQuery<>();
        query.getObject(context, UPDATE_LOG_ID, new GetListener<UpdateLog>() {

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
               view.onGetUpdateLogFail(arg1);
            }

            @Override
            public void onSuccess(UpdateLog updateLog) {
                // TODO Auto-generated method stub
                view.onGetUpdateLogsSuc(updateLog);
            }
        });
    }
}
