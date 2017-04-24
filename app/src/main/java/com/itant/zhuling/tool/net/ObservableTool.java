package com.itant.zhuling.tool.net;

import android.content.Context;

import com.itant.zhuling.error.NetError;
import com.itant.zhuling.tool.PrintTool;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by iTant on 2017/4/18.
 */

public class ObservableTool {

    /**
     * 发送同步get请求
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
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    String result = new String(response.body().bytes());
                    emitter.onNext(result);

                    // 打印响应header信息
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        PrintTool.i("response header", responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(new NetError());
                } finally {
                    if (response != null) {
                        response.body().close();
                    }
                }
            }
        });
        return observable;
    }
}
