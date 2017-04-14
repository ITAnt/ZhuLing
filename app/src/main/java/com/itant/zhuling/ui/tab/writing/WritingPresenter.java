package com.itant.zhuling.ui.tab.writing;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itant.zhuling.error.NetError;
import com.itant.zhuling.tool.ObservableDecorator;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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
        Observable<List<WritingBean>> observable = Observable.create(new ObservableOnSubscribe<List<WritingBean>>() {
            @Override
            //将事件发射出去,持有观察者的对象
            public void subscribe(final ObservableEmitter<List<WritingBean>> emitter) throws Exception {
                final Request request = new Request.Builder().url("http://c.m.163.com/nc/article/list/T1348649580692/0-20.html").get().build();

                OkHttpClient httpUtils = new OkHttpClient();
                httpUtils.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        emitter.onError(new NetError());
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            //拿到结果的一瞬间触发事件，并传递数据给观察者
                            //把请求结果转化成字节数组
                            try {
                                String result = new String(response.body().bytes());
                                JSONObject jsonObject = new JSONObject(result);
                                Gson gson = new GsonBuilder().create();
                                List<WritingBean> musicBeen = gson.fromJson(jsonObject.getString("T1348649580692"),
                                        new TypeToken<List<WritingBean>>() {}.getType());
                                // 获取数据成功
                                emitter.onNext(musicBeen);

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

        ObservableDecorator.decorate(observable).subscribe(new Observer<List<WritingBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<WritingBean> musicBeen) {
                mView.onGetWritingSuc(musicBeen);
            }

            @Override
            public void onError(Throwable e) {
                mView.onGetWritingFail("获取失败");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
