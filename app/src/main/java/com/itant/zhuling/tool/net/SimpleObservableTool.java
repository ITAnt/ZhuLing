package com.itant.zhuling.tool.net;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by iTant on 2017/4/18.
 */

public class SimpleObservableTool {
    /**
     * 发送get请求
     * @param context
     * @return
     */
    public static Observable<String> getGetObFromUrl(final Context context, final String url) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            //将事件发射出去,持有观察者的对象
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                emitter.onNext(new OkHttpClient().newCall(request).execute().body().string());
            }
        });
        return observable;
    }
}
