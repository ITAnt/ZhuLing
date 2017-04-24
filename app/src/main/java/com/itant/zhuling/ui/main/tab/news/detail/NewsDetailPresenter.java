package com.itant.zhuling.ui.main.tab.news.detail;

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

public class NewsDetailPresenter implements NewsDetailContract.Presenter {
    private static final String URL_NEWS_PREFIX = "http://c.m.163.com/nc/article/";
    private String urlSuffix;// 请求后缀
    private Context mContext;
    private NewsDetailContract.View mView;

    public NewsDetailPresenter(Context context, NewsDetailContract.View view) {
        mContext = context;
        mView = view;
    }


    @Override
    public void getNewsDetail(final String postId) {
        urlSuffix = postId + "/full.html";
        String url = URL_NEWS_PREFIX + urlSuffix;
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                NewsDetail newsDetail = new NewsDetail();
                try {
                    JSONObject resultObject = new JSONObject(result).getJSONObject(postId);
                    newsDetail.setSource(resultObject.getString("source"));
                    newsDetail.setPtime("ptime");
                    newsDetail.setShareLink("shareLink");
                    newsDetail.setBody(resultObject.getString("body"));

                    String imgArray = resultObject.getString("img");
                    Gson gson = new GsonBuilder().create();
                    List<NewsDetail.ImgBean> images = gson.fromJson(imgArray, new TypeToken<List<NewsDetail.ImgBean>>(){}.getType());
                    newsDetail.setImg(images);

                    mView.onGetNewsDetailSuc(newsDetail);
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.onGetNewsDetailFail("获取失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mView.onGetNewsDetailFail("获取失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                mView.onGetNewsDetailFail("获取失败");
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
