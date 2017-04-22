package com.itant.zhuling.ui.main.tab.advanced.meizhi;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itant.zhuling.tool.net.ObservableDecorator;
import com.itant.zhuling.tool.net.ObservableTool;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Request;


/**
 * Created by Jason on 2017/3/26.
 */

public class MeizhiPresenter implements MeizhiContract.Presenter {

    private Context mContext;
    private MeizhiContract.View mView;
    // 每页20个数据
    private static final String BASE_URL_MEI_ZHI = "http://gank.io/api/data/福利/20/";

    public MeizhiPresenter(Context context, MeizhiContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void getMeizhi(int page) {
        Request request = new Request.Builder().url(BASE_URL_MEI_ZHI + page).get().build();
        ObservableDecorator.decorate(ObservableTool.getGetObFromUrl(mContext, request)).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String result) {
                try {
                    // 妹纸获取成功
                    JSONObject jsonObject = new JSONObject(result);
                    Gson gson = new GsonBuilder().create();
                    List<Meizhi> meizhis = gson.fromJson(jsonObject.getString("results"), new TypeToken<List<Meizhi>>(){}.getType());

                    mView.getMeizhiSuc(meizhis);
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.getMeizhiFail(e.toString());
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.getMeizhiFail(e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
