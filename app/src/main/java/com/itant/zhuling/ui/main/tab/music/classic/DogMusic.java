package com.itant.zhuling.ui.main.tab.music.classic;

import android.text.TextUtils;


import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.FileTool;
import com.itant.zhuling.tool.SecureTool;
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
 * 小狗音乐
 */
public class DogMusic {
    private MusicContract.View view;
    private String keyWords;
    private int page;
    private List<Music> musics;
    public DogMusic(MusicContract.View view, String keyWords, int page) {
        musics = new ArrayList<>();
        this.view = view;
        this.keyWords = keyWords;
        this.page = page;
    }

    /**
     * 获取小狗歌曲信息
     */
    public void getDogSongs() {
        int realPage = page <= 0 ? 1 : page;
        String url = "http://mobilecdn.kugou.com/api/v3/search/song?format=jsonp&keyword=" + keyWords + "&page=" + realPage + "&pagesize=20&showtype=1";
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
        JSONObject dataObject = null;
        try {
            dataObject = jsonObject.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int total = 0;
        try {
            total = dataObject.getInt("total");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (total <= 0) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        JSONArray listArray = null;
        try {
            listArray = dataObject.getJSONArray("info");
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
            music.setMusicType(0);// 音乐来源
            try {
                music.setSourceId(object.getString("hash"));// 歌曲最原始的ID===========这个不是
            } catch (JSONException e) {
                e.printStackTrace();
            }
            music.setId("dog" + music.getSourceId());// 歌曲ID
            try {
                music.setName(object.getString("filename"));// 歌名
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                music.setSinger(object.getString("singername"));// 歌手
            } catch (JSONException e) {
                e.printStackTrace();
            }
            music.setAlbum("");// 专辑

            try {
                music.setBitrate(object.getString("bitrate"));// 音质
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String fileName = music.getName() + "-" + music.getSinger() + ".mp3";

            String uniFileName = FileTool.getUniqueFileName(ZhuConstants.PATH_CLASSIC_DOG, fileName, 1);
            music.setFileName(uniFileName);// 文件名

            String hash = null;
            try {
                hash = object.getString("hash");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String hash320 = null;
            try {
                hash320 = object.getString("320hash");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(hash320)) {
                hash = hash320;
                music.setBitrate("320");// 音质
            }

            String sqhash = null;
            try {
                sqhash = object.getString("sqhash");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(sqhash)) {
                hash = sqhash;
                music.setBitrate("无损");// 音质
            }

            String key = SecureTool.getMD5String(hash + "kgcloud");
            music.setMp3Url("http://trackercdn.kugou.com/i/?key=" + key + "&cmd=4&acceptMp3=1&hash=" + hash + "&pid=1");// 下载地址

            // 文件路径
            music.setFilePath(ZhuConstants.PATH_CLASSIC_DOG + music.getFileName());
            musics.add(music);
        }
        view.onGetMusicSuc(musics);
    }
}
