package com.itant.zhuling.ui.main.tab.news;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;


/**
 * Created by iTant on 2017/3/26.
 */

public class NewsPresenter implements NewsContract.Presenter {
    private static final String URL_NEWS_PREFIX = "http://c.m.163.com/nc/article/list/T1348649580692/";
    private String urlSuffix;// 请求后缀
    private static final int SIZE_PAGE = 20;// 一页获取的数量
    private Context mContext;
    private NewsContract.View mView;

    public NewsPresenter(Context context, NewsContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void getNews(int page) {
        urlSuffix = page * SIZE_PAGE + "-20.html";
        String url = URL_NEWS_PREFIX + urlSuffix;
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Gson gson = new GsonBuilder().create();
                    List<NewsBean> musicBeen = gson.fromJson(jsonObject.getString("T1348649580692"),
                            new TypeToken<List<NewsBean>>() {}.getType());
                    // 获取数据成功
                    mView.onGetNewsSuc(musicBeen);
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.onGetNewsFail("获取失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mView.onGetNewsFail("获取失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                mView.onGetNewsFail("获取失败");
            }

            @Override
            public void onFinished() {
            }
        });
    }

    // 使用okhttp会出现请求失败的情况，似乎在请求完成之后，并没有关闭请求，网上的处理方案是：
    // 1.addHeader("Connection", "close")
    // 2.retryOnConnectionFailure(true)
    // 3.response.body().close();
    // 就本应用而言，以上三种方案均不能很好解决403禁止访问的问题，有待深究。
    /*Request request = new Request.Builder()
            .url(URL_NEWS_PREFIX + urlSuffix)
            .get()
            .addHeader("Content-Type", "application/json;charset=utf-8")
            .addHeader("Cache-Control", "max-age=60")
            .addHeader("Connection", "close")
            .build();
        ObservableDecorator.decorate(ObservableTool.getGetObFromUrl(mContext, request)).subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                Gson gson = new GsonBuilder().create();
                List<NewsBean> musicBeen = gson.fromJson(jsonObject.getString("T1348649580692"),
                        new TypeToken<List<NewsBean>>() {}.getType());
                // 获取数据成功
                mView.onGetNewsSuc(musicBeen);
            } catch (Exception e) {
                e.printStackTrace();
                mView.onGetNewsFail("获取失败");
            }
        }

        @Override
        public void onError(Throwable e) {
            mView.onGetNewsFail("获取失败");
        }

        @Override
        public void onComplete() {

        }
    });*/
}
