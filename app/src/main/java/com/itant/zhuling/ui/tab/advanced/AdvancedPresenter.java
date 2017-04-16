package com.itant.zhuling.ui.tab.advanced;

import android.content.Context;

import com.itant.zhuling.ui.tab.writing.WritingBean;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Jason on 2017/3/26.
 */

public class AdvancedPresenter implements AdvancedContract.Presenter {

    private Context mContext;
    private AdvancedContract.View mView;

    public AdvancedPresenter(Context context, AdvancedContract.View view) {
        mContext = context;
        mView = view;
    }


    @Override
    public void getWriting(int page) {
        BmobQuery<AdvancedBean> query = new BmobQuery<>();
        query.addWhereNotEqualTo("objectId", "");
        query.setLimit(10);
        query.setSkip(10*page);
        //执行查询方法
        query.findObjects(mContext, new FindListener<AdvancedBean>() {

            @Override
            public void onSuccess(List<AdvancedBean> list) {
                mView.onGetWritingSuc(list);
            }

            @Override
            public void onError(int i, String s) {
                mView.onGetWritingFail("获取失败");
            }
        });
    }
}
