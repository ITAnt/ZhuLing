package com.itant.zhuling.ui.navigation.about;

import android.content.Context;

import com.itant.zhuling.ui.main.MainContract;
import com.itant.zhuling.ui.main.UpdateInfo;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Jason on 2017/4/16.
 */

public class UpdatePresenter implements MainContract.Presenter {
    private Context context;
    private MainContract.View view;
    private static final String UPDATE_ID = "vVeqLLLl";

    public UpdatePresenter(Context context, MainContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void getUpdateInfo() {
        BmobQuery<UpdateInfo> query = new BmobQuery<>();
        query.getObject(context, UPDATE_ID, new GetListener<UpdateInfo>() {

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
               view.onGetUpdateInfoFail(arg1);
            }

            @Override
            public void onSuccess(UpdateInfo updateInfo) {
                // TODO Auto-generated method stub
                view.onGetUpdateInfoSuc(updateInfo);
            }
        });
    }
}
