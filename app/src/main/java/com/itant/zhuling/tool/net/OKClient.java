package com.itant.zhuling.tool.net;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by iTant on 2017/4/18.
 */

public class OKClient {
    private static OKClient instance;
    private OkHttpClient client;
    private OkHttpClient.Builder builder;

    private OKClient(Context context) {
        //缓存文件最大限制大小20M
        builder = new OkHttpClient.Builder()
                .cache(new Cache(context.getCacheDir(), 1024*1024*20))
                .writeTimeout(15, TimeUnit.SECONDS) // 设置写入超时时间
                .readTimeout(15, TimeUnit.SECONDS)  // 设置读取数据超时时间
                //.followRedirects(true)
                .retryOnConnectionFailure(true);    // 设置是否进行连接失败重试
        client = builder.build();
    }

    private static synchronized void syncInit(Context context) {
        if (instance == null) {
            instance = new OKClient(context);
        }
    }

    public static OKClient getInstance(Context context) {
        if (context == null) {
            return null;
        }

        if (instance == null) {
            syncInit(context);
        }
        return instance;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public OkHttpClient.Builder getBuilder() {
        return builder;
    }
}
