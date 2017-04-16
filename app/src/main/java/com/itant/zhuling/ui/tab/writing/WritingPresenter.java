package com.itant.zhuling.ui.tab.writing;

import android.content.Context;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Jason on 2017/3/26.
 */

public class WritingPresenter implements WritingContract.Presenter {

    private Context mContext;
    private WritingContract.View mView;

    public WritingPresenter(Context context, WritingContract.View view) {
        mContext = context;
        mView = view;
    }


    @Override
    public void getWriting(int page) {
        BmobQuery<WritingBean> query = new BmobQuery<>();
        query.addWhereNotEqualTo("objectId", "");
        query.setLimit(10);
        query.setSkip(10*page);
        //执行查询方法
        query.findObjects(mContext, new FindListener<WritingBean>() {

            @Override
            public void onSuccess(List<WritingBean> list) {
                mView.onGetWritingSuc(list);
            }

            @Override
            public void onError(int i, String s) {
                mView.onGetWritingFail("获取失败");
            }
        });
    }
}
