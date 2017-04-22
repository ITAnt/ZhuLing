package com.itant.zhuling.ui.main.tab.music.classic;

import android.text.TextUtils;


import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.FileTool;
import com.itant.zhuling.ui.main.tab.music.MusicContract;
import com.itant.zhuling.ui.main.tab.music.bean.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 詹子聪 on 2016/11/15.
 * 凉窝音乐
 */
public class KmeMusic {

    private MusicContract.View view;
    private String keyWords;
    private int page;
    private List<Music> musics;
    public KmeMusic(MusicContract.View view, String keyWords, int page) {
        musics = new ArrayList<>();
        this.view = view;
        this.keyWords = keyWords;
        this.page = page;
    }

    /**
     * 获取凉窝歌曲信息
     */
    public void getWoSongs() {
        String url = "http://search.kuwo.cn/r.s?all=" + keyWords + "&ft=music&itemset=web_2013&client=kt&pn=" + page + "&rn=20&rformat=json&encoding=utf8";
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String rawResult) {
                try {
                    resolveMusic(rawResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    view.onGetMusicFail("解析出错");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                view.onGetMusicFail("暂无结果");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                view.onGetMusicFail("暂无结果");
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void resolveMusic(String rawResult) {
        if (TextUtils.isEmpty(rawResult)) {
            // 结束加载动画
            view.onGetMusicFail("暂无结果");
            return;
        }

        String result = rawResult.replaceAll("'", "\"");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String total = null;
        try {
            total = jsonObject.getString("TOTAL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(total) || Integer.parseInt(total) <= 0) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        JSONArray listArray = null;
        try {
            listArray = jsonObject.getJSONArray("abslist");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listArray == null) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        for (int i = 0; i < listArray.length(); i++) {
            JSONObject object = null;
            try {
                object = new JSONObject(listArray.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Music music = new Music();
            music.setMusicType(1);// 音乐来源
            music.setSinger("未知");// 歌手
            String songId = null;
            try {
                songId = object.getString("MUSICRID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            music.setSourceId(songId);// 歌曲ID
            music.setId("kwo" + songId);// 歌曲ID
            try {
                music.setName(object.getString("SONGNAME"));// 歌名
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                music.setSinger(object.getString("ARTIST"));// 歌手
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                music.setAlbum(object.getString("ALBUM"));// 专辑
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String formats = null;
            try {
                formats = object.getString("FORMATS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String bitrate = "128";
            String mp3Url = "";
            String extension = ".mp3";
            if (formats.contains("MP3128")) {
                mp3Url = "http://antiserver.kuwo.cn/anti.s?response=url&type=convert_url&br=128kmp3&format=mp3&rid=" + songId;
                bitrate = "128";
                extension = ".mp3";
            }
            if (formats.contains("MP3192")) {
                mp3Url = "http://antiserver.kuwo.cn/anti.s?response=url&type=convert_url&br=192kmp3&format=mp3&rid=" + songId;
                bitrate = "192";
                extension = ".mp3";
            }
            if (formats.contains("MP3H")) {
                mp3Url = "http://antiserver.kuwo.cn/anti.s?response=url&type=convert_url&br=320kmp3&format=mp3&rid=" + songId;
                //mp3Url = "http://antiserver.kuwo.cn/anti.s?response=url&type=convert_url&br=192kmp3&format=mp3&rid=" + songId;
                bitrate = "320";
                extension = ".mp3";
            }
            if (formats.contains("AL")) {
                mp3Url = "http://antiserver.kuwo.cn/anti.s?response=url&type=convert_url&br=2000kflac&format=ape&rid=" + songId;
                bitrate = "无损";
                extension = ".mp3";
            }
            /*if (text1.Contains("MP4")) {
                item.MvUrl = "http://antiserver.kuwo.cn/anti.s?response=url&type=convert_url&format=mp4&rid=" + item.SongId;
            }
            if (text1.Contains("MV")) {
                item.MvUrl = "http://antiserver.kuwo.cn/anti.s?response=url&type=convert_url&format=mkv&rid=" + item.SongId;
            }*/
            music.setBitrate(bitrate);// 音质
            String fileName = music.getName() + "-" + music.getSinger() + extension;
            String uniFileName = FileTool.getUniqueFileName(ZhuConstants.PATH_CLASSIC_KWO, fileName, 1);
            music.setFileName(uniFileName);// 文件名
            music.setMp3Url(mp3Url);// 下载地址
            // 文件路径
            music.setFilePath(ZhuConstants.PATH_CLASSIC_KWO + music.getFileName());
            musics.add(music);
        }

        view.onGetMusicSuc(musics);
    }
}
