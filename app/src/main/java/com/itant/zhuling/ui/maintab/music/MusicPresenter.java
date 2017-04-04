package com.itant.zhuling.ui.maintab.music;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itant.zhuling.utils.ObservableDecorator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Jason on 2017/3/26.
 */

public class MusicPresenter implements MusicContract.Presenter {

    private Context mContext;
    private MusicContract.View mView;
    private OkHttpClient sOkHttpClient;

    public MusicPresenter(Context context, MusicContract.View view) {
        mContext = context;
        mView = view;
    }

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (false) {
                // 没网
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (true) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 2)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

    private final Interceptor mLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            return response;
        }
    };

    private OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            synchronized (MusicPresenter.class) {
                Cache cache = new Cache(new File(mContext.getApplicationContext().getCacheDir(), "HttpCache"),
                        1024 * 1024 * 100);
                if (sOkHttpClient == null) {
                    sOkHttpClient = new OkHttpClient.Builder().cache(cache)
                            .connectTimeout(6, TimeUnit.SECONDS)
                            .readTimeout(6, TimeUnit.SECONDS)
                            .writeTimeout(6, TimeUnit.SECONDS)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(mLoggingInterceptor).build();
                }
            }
        }
        return sOkHttpClient;
    }

    @Override
    public void getRepo() {
        Observable<List<MusicBean>> observable = Observable.create(new ObservableOnSubscribe<List<MusicBean>>() {
            @Override
            //将事件发射出去,持有观察者的对象
            public void subscribe(final ObservableEmitter<List<MusicBean>> emitter) throws Exception {
                final Request request = new Request.Builder().url("http://c.m.163.com/nc/article/list/T1348649580692/0-20.html").get().build();

                OkHttpClient httpUtils = new OkHttpClient();
                httpUtils.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            //拿到结果的一瞬间触发事件，并传递数据给观察者
                            //把请求结果转化成字节数组
                            try {
                                String result = new String(response.body().bytes());

                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    Gson gson = new GsonBuilder().create();
                                    List<MusicBean> musicBeen = gson.fromJson(jsonObject.getString("T1348649580692"), new TypeToken<List<MusicBean>>() {
                                    }.getType());
                                    emitter.onNext(musicBeen);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //数据发送已经完成
                    }
                });
            }
        });

        ObservableDecorator.decorate(observable).subscribe(new Observer<List<MusicBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<MusicBean> musicBeen) {
                mView.onGetRepoSucc(musicBeen);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
}
