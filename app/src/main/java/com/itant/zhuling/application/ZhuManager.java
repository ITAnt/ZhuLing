package com.itant.zhuling.application;

import android.content.Context;

import com.itant.zhuling.service.PlayService;
import com.itant.zhuling.ui.main.tab.music.bean.Music;

/**
 * Created by Jason on 2017/4/22.
 * 应用管理者，主要维护一些全局的对象，如音乐service
 */

public class ZhuManager {
    private static Context mContext;
    // 当前正在播放的音乐
    private Music mPlayingMusic;
    // 是否有音乐正在播放
    private boolean isMusicPlaying;
    private static ZhuManager mManager;
    private PlayService playService;

    public static ZhuManager getInstance() {
        if (mManager == null) {
            syncInitManager();
        }
        return mManager;
    }

    private static synchronized void syncInitManager() {
        if (mManager == null) {
            mManager = new ZhuManager();
        }
    }

    public PlayService getMusicService() {
        return playService;
    }

    public void setMusicService(PlayService playService) {
        this.playService = playService;
    }

    public void onInit(Context context) {
        // 单例需要这样做，防止内存泄露
        mContext = context.getApplicationContext();
    }

    public Context getContext() {
        return getInstance().mContext;
    }

    public Music getmPlayingMusic() {
        return mPlayingMusic;
    }

    public void setmPlayingMusic(Music mPlayingMusic) {
        this.mPlayingMusic = mPlayingMusic;
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public void setMusicPlaying(boolean musicPlaying) {
        isMusicPlaying = musicPlaying;
    }
}
