package com.itant.zhuling.ui.main.tab.advanced.torrent;

import android.content.Context;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by iTant on 2017/4/16.
 */

public class TorrentPresenter implements TorrentContract.Presenter {
    private Context context;
    private TorrentContract.View view;

    public TorrentPresenter(Context context, TorrentContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void getTorrent(String key) {
        String url = "http://cn.btbit.net/list/" + key + "/1-0-0.html";
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                view.onGetTorrentSuc(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                view.onGetTorrentFail("获取失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                view.onGetTorrentFail("获取失败");
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
