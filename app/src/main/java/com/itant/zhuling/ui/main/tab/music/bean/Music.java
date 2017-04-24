package com.itant.zhuling.ui.main.tab.music.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import cn.bmob.v3.BmobObject;

/**
 * Created by iTant on 2016/11/13.
 */
@Table(name = "music")
public class Music extends BmobObject {

    @Column(name = "id", isId = true)
    private String id;// 歌曲的ID

    private String sourceId;// 歌曲最原始的ID

    @Column(name = "fileName")
    private String fileName;// 本地文件名，包括后缀名

    @Column(name = "name")
    private String name;// 歌曲名字

    @Column(name = "singer")
    private String singer;// 歌手

    @Column(name = "album")
    private String album;// 专辑

    @Column(name = "size")
    private String size;// 文件大小

    @Column(name = "bitrate")
    private String bitrate;// 比特率

    @Column(name = "mp3Url")
    private String mp3Url;// 歌曲下载地址

    @Column(name = "imageUrl")
    private String imageUrl;// 封面图片地址

    @Column(name = "progress")
    private Integer progress;// 下载进度

    @Column(name = "filePath")
    private String filePath;// 本地文件路径

    @Column(name = "musicTime")
    private String musicTime;// 音乐时长

    @Column(name = "musicType")
    private Integer musicType;// 0小狗 1凉窝 2企鹅 3白云 4熊掌 5龙虾

    @Column(name = "flacUrl")
    private String flacUrl;// flac地址

    @Column(name = "apeUrl")
    private String apeUrl;// ape地址

    @Column(name = "sqUrl")
    private String sqUrl;// SQ地址

    @Column(name = "hqUrl")
    private String hqUrl;// HQ地址
    @Column(name = "lqUrl")
    private String lqUrl;// LQ地址
    @Column(name = "mvUrl")
    private String mvUrl;// MV地址

    private boolean isPlaying;// 是否为正在播放

    /*@Column(name = "engine")
    private String engine;// 引擎*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public void setMp3Url(String mp3Url) {
        this.mp3Url = mp3Url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getMusicTime() {
        return musicTime;
    }

    public void setMusicTime(String musicTime) {
        this.musicTime = musicTime;
    }

    public String getFlacUrl() {
        return flacUrl;
    }

    public void setFlacUrl(String flacUrl) {
        this.flacUrl = flacUrl;
    }

    public String getApeUrl() {
        return apeUrl;
    }

    public void setApeUrl(String apeUrl) {
        this.apeUrl = apeUrl;
    }

    public String getSqUrl() {
        return sqUrl;
    }

    public void setSqUrl(String sqUrl) {
        this.sqUrl = sqUrl;
    }

    public String getHqUrl() {
        return hqUrl;
    }

    public void setHqUrl(String hqUrl) {
        this.hqUrl = hqUrl;
    }

    public String getLqUrl() {
        return lqUrl;
    }

    public void setLqUrl(String lqUrl) {
        this.lqUrl = lqUrl;
    }

    public String getMvUrl() {
        return mvUrl;
    }

    public void setMvUrl(String mvUrl) {
        this.mvUrl = mvUrl;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getMusicType() {
        return musicType;
    }

    public void setMusicType(Integer musicType) {
        this.musicType = musicType;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
