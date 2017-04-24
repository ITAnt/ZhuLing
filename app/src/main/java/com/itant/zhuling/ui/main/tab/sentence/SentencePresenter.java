package com.itant.zhuling.ui.main.tab.sentence;

import android.content.Context;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by iTant on 2017/3/26.
 */

public class SentencePresenter implements SentenceContract.Presenter {
    private Context mContext;
    private SentenceContract.View mView;

    public SentencePresenter(Context context, SentenceContract.View view) {
        mContext = context;
        mView = view;
    }


    @Override
    public void getSentences(int page) {
        BmobQuery<SentenceBean> query = new BmobQuery<>();
        query.addWhereNotEqualTo("objectId", "");
        query.setLimit(10);
        query.setSkip(10*page);
        //执行查询方法
        query.findObjects(mContext, new FindListener<SentenceBean>() {
            @Override
            public void onSuccess(List<SentenceBean> list) {
                mView.onGetSentenceSuc(list);
            }

            @Override
            public void onError(int i, String s) {
                mView.onGetSentenceFail("获取失败");
            }
        });
    }
}
