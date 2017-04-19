package com.itant.zhuling.tool.image.glide;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Jason on 2017/4/18.
 */

public class GlideTool {
    private static GlideTool instance;

    private GlideTool(Context context) {
        //缓存文件最大限制大小20M
    }

    private static synchronized void syncInit(Context context) {
        if (instance == null) {
            instance = new GlideTool(context);
        }
    }

    public static GlideTool getInstance(Context context) {
        if (instance == null) {
            syncInit(context);
        }
        return instance;
    }
}
