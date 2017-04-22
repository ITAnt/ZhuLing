package com.itant.zhuling.application;

import android.content.Context;

import com.itant.zhuling.service.PlayService;
import com.itant.zhuling.ui.main.tab.music.bean.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2017/4/22.
 * 应用管理者，主要维护一些全局的对象，如音乐service
 */

public class ZhuManager {

    /*private static ZhuManager manager;
    private PlayService playService;

    public static ZhuManager getInstance() {
        if (manager == null) {
            syncInitManager();
        }
        return manager;
    }

    private static synchronized void syncInitManager() {
        if (manager == null) {
            manager = new ZhuManager();
        }
    }

    public PlayService getMusicService() {
        return playService;
    }

    public void setMusicService(PlayService playService) {
        this.playService = playService;
    }*/
    private static Context mContext;
    private PlayService playService;
    // 本地歌曲列表
    private final List<Music> mMusicList = new ArrayList<>();

    public static List<Music> getMusicList() {
        return getInstance().mMusicList;
    }


    private static class SingletonHolder {
        private static ZhuManager manager = new ZhuManager();
    }

    private static ZhuManager getInstance() {
        return SingletonHolder.manager;
    }

    public static void init(Context context) {
        getInstance().onInit(context);
    }

    private void onInit(Context context) {
        // 单例需要这样做，防止内存泄露
        mContext = context.getApplicationContext();
    }

    public static PlayService getMusicService() {
        return getInstance().playService;
    }

    public static void setMusicService(PlayService service) {
        getInstance().playService = service;
    }

    public static Context getContext() {
        return getInstance().mContext;
    }
}
