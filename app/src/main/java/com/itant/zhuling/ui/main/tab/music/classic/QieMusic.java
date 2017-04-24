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
 * Created by iTant on 2016/11/15.
 * 企鹅音乐
 */
public class QieMusic {
    private MusicContract.View view;
    private List<Music> musics;
    private String keyWords;
    private int page;
    public QieMusic(MusicContract.View view, String keyWords, int page) {
        musics = new ArrayList<>();
        this.view = view;
        this.keyWords = keyWords;
        this.page = page;
    }

    /**
     * 获取企鹅歌曲信息
     */
    public void getQieSongs() {
        String url = "http://soso.music.qq.com/fcgi-bin/music_search_new_platform?t=0&n=20&g_tk=157256710&loginUin=584586119&hostUin=0&format=jsonp&inCharset=GB2312&outCharset=utf-8&notice=0&platform=newframe&jsonpCallback=jsnp_callback&needNewCode=0&w=" + keyWords + "&p=" + page + "&catZhida=1&remoteplace=sizer.newclient.song_all&searchid=11040987310239770213&clallback=jsnp_callback&lossless=0";
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                resolveMusic(result);
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

    private void resolveMusic(String result) {
        if (TextUtils.isEmpty(result)) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        String json = result.substring(result.indexOf("{"), result.lastIndexOf(")"));
        if (TextUtils.isEmpty(json)) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        JSONObject dataObject = null;
        try {
            dataObject = jsonObject.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (dataObject == null) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        JSONObject songObject = null;
        try {
            songObject = dataObject.getJSONObject("song");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (songObject == null) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        String totalNum = null;
        try {
            totalNum = songObject.getString("totalnum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.equals(totalNum, "0")) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        JSONArray listArray = null;
        try {
            listArray = songObject.getJSONArray("list");
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
            String isWeiYun = null;
            try {
                isWeiYun = object.getString("isweiyun");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (TextUtils.equals(isWeiYun, "1")) {
                continue;
            }

            String f = null;
            try {
                f = object.getString("f");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(f)) {
                continue;
            }

            Music music = new Music();// 音乐来源
            music.setMusicType(2);
            music.setSourceId(String.valueOf(System.currentTimeMillis()));// 歌曲ID
            music.setId("qie" + music.getSourceId());
            if (f.contains("@@")) {
                music.setBitrate("128");
                String[] infos = f.split("@@");
                if (infos.length < 1) {
                    continue;
                }
                // 音乐的唯一ID
                music.setSourceId(infos[0].trim());// 歌曲ID
                music.setId("qie"+music.getSourceId());// 歌曲ID
                music.setName(infos[1].trim());// 歌名
                String singer1 = null;
                try {
                    singer1 = object.getString("fsinger");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String singer2 = null;
                try {
                    singer2 = object.getString("fsinger2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(singer2)) {
                    singer1 = singer1 + "、" + singer2;
                }
                music.setSinger(singer1);// 歌手
                music.setAlbum("");// 专辑
                music.setMp3Url(infos[infos.length-4]);// 下载地址

                String fileName = music.getName() + "-" + music.getSinger() + ".m4a";
                String uniFileName = FileTool.getUniqueFileName(ZhuConstants.PATH_CLASSIC_QIE, fileName, 1);
                music.setFileName(uniFileName);// 文件名
            } else {
                String[] infos = f.split("\\|");
                if (infos == null) {
                    continue;
                }
                music.setSourceId(infos[0].trim());// 歌曲ID
                music.setId("qie"+music.getSourceId());// 歌曲ID
                try {
                    music.setName(object.getString("fsong"));// 歌名
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String singer1 = null;
                try {
                    singer1 = object.getString("fsinger");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String singer2 = null;
                try {
                    singer2 = object.getString("fsinger2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(singer2)) {
                    singer1 = singer1 + "、" + singer2;
                }
                music.setSinger(singer1);// 歌手
                music.setBitrate("128");
                try {
                    music.setAlbum(infos[5]);// 专辑
                    // 低音质下载地址
                    music.setMp3Url("http://tsmusic128.tc.qq.com/" + (Integer.parseInt(music.getSourceId()) + 30000000) + ".mp3");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (f.contains("320000|0|")) {
                    // 高音质下载地址
                    music.setBitrate("320");
                    music.setMp3Url("http://vsrc.music.tc.qq.com/M800" + infos[infos.length-5] + ".mp3");

                    if (!f.contains("320000|0|0|")) {
                        // 无损音质下载地址
                        music.setBitrate("无损");
                        music.setMp3Url("http://vsrc.music.tc.qq.com/F000" + infos[infos.length-5] + ".flac");
                    }
                }

                String suffix = music.getMp3Url().substring(music.getMp3Url().lastIndexOf("."), music.getMp3Url().length());

                String fileName = music.getName() + "-" + music.getSinger() + suffix;
                String uniFileName = FileTool.getUniqueFileName(ZhuConstants.PATH_CLASSIC_QIE, fileName, 1);
                music.setFileName(uniFileName);// 文件名

                // 音乐相册
                try {
                    music.setImageUrl("http://imgcache.qq.com/music/photo/album/" + (Integer.parseInt(infos[4]) % 100) + "/albumpic_" + infos[4] + "_0.jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            music.setFilePath(ZhuConstants.PATH_CLASSIC_QIE + music.getFileName());
            musics.add(music);
        }
        view.onGetMusicSuc(musics);
    }
}
