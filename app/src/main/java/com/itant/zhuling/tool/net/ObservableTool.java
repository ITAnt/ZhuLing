package com.itant.zhuling.tool.net;

import android.content.Context;

import com.itant.zhuling.error.NetError;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jason on 2017/4/18.
 */

public class ObservableTool {

    /**
     * 发送get请求
     * @param context
     * @return
     */
    public static Observable<String> getGetObFromUrl(final Context context, final Request request) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            //将事件发射出去,持有观察者的对象
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                OKClient instance = OKClient.getInstance(context);
                if (instance == null) {
                    return;
                }

                OkHttpClient client = instance.getClient();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        emitter.onError(new NetError());
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            //拿到结果的一瞬间触发事件，并传递数据给观察者
                            try {
                                // 获取数据成功
                                String result = new String(response.body().bytes());
                                emitter.onNext(result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                emitter.onError(new NetError());
                            }
                        } else {
                            emitter.onError(new NetError());
                        }
                    }
                });
            }
        });
        return observable;
    }
}
