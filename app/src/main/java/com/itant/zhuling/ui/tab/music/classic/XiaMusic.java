package com.itant.zhuling.ui.tab.music.classic;

import android.text.TextUtils;

import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.FileTool;
import com.itant.zhuling.tool.StringTool;
import com.itant.zhuling.ui.tab.music.MusicContract;
import com.itant.zhuling.ui.tab.music.bean.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.x;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 詹子聪 on 2016/11/15.
 * 龙虾音乐
 */
public class XiaMusic {

    private MusicContract.View view;
    private String keyWords;
    private int page;
    private List<Music> musics;
    public XiaMusic(MusicContract.View view, String keyWords, int page) {
        musics = new ArrayList<>();
        this.view = view;
        this.keyWords = keyWords;
        this.page = page;
    }

    /**
     * 获取龙虾歌曲信息
     */
    public void getXiaSongs() {
        RequestParams loginParams = new RequestParams("https://login.xiami.com/member/login");
        loginParams.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;*/*");
        loginParams.setHeader("Referer", "http://www.xiami.com");
        loginParams.setHeader("Connection", "Keep-Alive");
        loginParams.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)");
        x.http().get(loginParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                DbCookieStore instance = DbCookieStore.INSTANCE;
                List<HttpCookie> cookieContainer = instance.getCookies();
                ZhuConstants.COOKIE_CONTAINER = cookieContainer;
                for (HttpCookie cookie : cookieContainer) {
                    String name = cookie.getName();
                    String value = cookie.getValue();
                    if ("JSESSIONID".equals(name)) {
                        // 将cookie保存下来
                        //Constants.COOKIE_XIA = value;// cookie保存到内存
                        break;
                    }
                }
                initVip();
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

    /**
     * 初始化VIP
     */
    private void initVip() {
        RequestParams vipParams = new RequestParams("https://login.xiami.com/member/login?" +
                ZhuConstants.COOKIE_CONTAINER.get(0).getName() + "=" +
                ZhuConstants.COOKIE_CONTAINER.get(0).getValue() + "&done=http%253A%252F%252Fwww.xiami.com%252F&type=&email=iloveb44%40163.com&password=a23187&autologin=1&submit=%E7%99%BB+%E5%BD%95");
        ZhuConstants.TIME_XIA_MI = System.currentTimeMillis() / 10000;
        x.http().get(vipParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                getSongInfos();
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

    /**
     * 获取歌曲信息
     *
     */
    private void getSongInfos() {
        String url = "http://www.xiami.com/web/search-songs/page/" + page + "?spm=0.0.0.0.82mhoN&key=" + keyWords + "&_xiamitoken=abchdjah6264817";
        RequestParams params = new RequestParams(url);
        params.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)");
        params.setHeader("Connection", "Keep-Alive");
        // 设置cookie
        //params.setHeader("Cookie", "JSESSIONID="+cookie);
        for (HttpCookie cookie : ZhuConstants.COOKIE_CONTAINER) {
            String name = cookie.getName();
            String value = cookie.getValue();
            params.setHeader(name, value);
        }

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String html) {
                resolveHtml(html);
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

    private void resolveHtml(String html) {
        if (TextUtils.isEmpty(html)) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        // 这里获得了html代码
        JSONArray listArray = null;
        try {
            listArray = new JSONArray(html);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listArray == null || listArray.length() == 0) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < listArray.length(); i++) {
            JSONObject idObj = null;
            try {
                idObj = new JSONObject(listArray.get(i).toString());
                builder.append(idObj.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            builder.append(",");
        }

        String songListUrl = "http://www.xiami.com/song/playlist/id/" + builder.toString() + "/type/0/cat/json";
        RequestParams params = new RequestParams(songListUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    resolveMusic(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    view.onGetMusicFail("暂无结果");
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

    private void resolveMusic(String result) {

        if (TextUtils.isEmpty(result)) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            view.onGetMusicFail("暂无结果");
            return;
        }

        boolean status = false;
        try {
            status = jsonObject.getBoolean("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!status) {
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
            // 结束加载动画
            view.onGetMusicFail("暂无结果");
            return;
        }

        JSONArray listArray = null;
        try {
            listArray = dataObject.getJSONArray("trackList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listArray == null) {
            // 结束加载动画
            view.onGetMusicFail("暂无结果");
            return;
        }

        if (listArray.length() >= 1) {

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject info = null;
                try {
                    info = new JSONObject(listArray.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Music music = new Music();
                music.setMusicType(5);// 音乐来源
                try {
                    music.setSourceId(info.getString("songId"));// 歌曲最原始的ID
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    music.setMusicTime(info.getString("length"));// 音乐时长
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                music.setId("xia" + music.getSourceId());// 歌曲ID
                try {
                    music.setName(info.getString("songName"));// 歌名
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    music.setSinger(info.getString("singers"));// 歌手
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    music.setAlbum(info.getString("album_name"));// 专辑
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                music.setBitrate("128");// 音质
                String format = ".mp3";
                try {
                    music.setMp3Url(info.getString("location"));// 下载地址
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String fileName = music.getName() + "-" + music.getSinger() + format;
                String uniFileName = FileTool.getUniqueFileName(ZhuConstants.PATH_CLASSIC_XIA, fileName, 1);
                music.setFileName(uniFileName);// 文件名


                // 文件路径
                music.setFilePath(ZhuConstants.PATH_CLASSIC_XIA + music.getFileName());
                musics.add(music);
            }

        }
        updateHQUrl();
    }

    /**
     * 更新龙虾高音质地址
     */
    private int i = 0;

    private void updateHQUrl() {
        for (final Music music : musics) {
            String url = "http://www.xiami.com/song/gethqsong/sid/" + music.getSourceId();

            final RequestParams mp3Params = new RequestParams(url);
            mp3Params.setHeader("Referer", "http://img.xiami.net/static/swf/seiya/1.5/player.swf?v=" + ZhuConstants.TIME_XIA_MI);// ==========拼上playertime
            mp3Params.setHeader("Accept", "*/*");
            mp3Params.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)");
            //mp3Params.setHeader("Accept-Encoding", "gzip,deflate,sdch,");
            //mp3Params.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            //mp3Params.setHeader("Content-Type", "text/html; charset=utf-8");
            mp3Params.setHeader("Connection", "Keep-Alive");
            mp3Params.setHeader("Charset", "UTF-8");
            // 设置cookie
            //params.setHeader("Cookie", "JSESSIONID="+cookie);
            for (HttpCookie cookie : ZhuConstants.COOKIE_CONTAINER) {
                String name = cookie.getName();
                String value = cookie.getValue();
                mp3Params.setHeader(name, value);
            }

            x.http().get(mp3Params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject resultObj = new JSONObject(result);

                        String location = resultObj.getString("location");
                        if (TextUtils.isEmpty(location)) {
                            return;
                        }

                        String mp3Url = StringTool.getXiaMp3Url(location);
                        if (TextUtils.isEmpty(mp3Url)) {
                            return;
                        }

                        music.setMp3Url(mp3Url);
                        if (mp3Url.contains("m6")) {
                            music.setBitrate("320");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                    notifyIfNeed(musics.size());
                }
            });
        }
    }

    private synchronized void notifyIfNeed(int size) {
        i++;
        if (i >= size) {
            view.onGetMusicSuc(musics);
            i = 0;
        }
    }
}
