package com.itant.zhuling.event.music;

/**
 * Created by Jason on 2017/4/22.
 */

public class MusicEvent {
    public static final int MUSIC_EVENT_PLAY        = 1;// 用户点击搜索结果的item，要播放音乐
    public static final int MUSIC_EVENT_DOWNLOAD    = 2;// 用户点击下载，将打开浏览器进行下载
    public static final int MUSIC_EVENT_COPY        = 3;// 用户点击复制下载链接
    public static final int MUSIC_EVENT_SHARE       = 4;// 用户点击分享下载链接
}
