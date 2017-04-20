package com.itant.zhuling.ui.tab.music.classic;


import android.content.Context;
import android.text.TextUtils;

import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.FileTool;
import com.itant.zhuling.tool.net.ObservableDecorator;
import com.itant.zhuling.tool.net.ObservableTool;
import com.itant.zhuling.ui.tab.music.MusicContract;
import com.itant.zhuling.ui.tab.music.bean.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Request;

/**
 * Created by 詹子聪 on 2016/11/15.
 * 熊掌音乐
 */
public class XiongMusic {

    private Context context;
    private MusicContract.View view;
    private String keyWords;
    private int page;
    private List<Music> musics;

    public XiongMusic(Context context, MusicContract.View view, String keyWords, int page) {
        musics = new ArrayList<>();
        this.context = context;
        this.view = view;
        this.keyWords = keyWords;
        this.page = page;
    }

    private int index = 0;

    /**
     * 获取熊掌歌曲信息
     */
    public void getXiongSongs() throws Exception {
        String url = "http://music.baidu.com/search/song?s=1&key=" + keyWords + "&start=" + page + "&size=20";
        Request request = new Request.Builder().url(url).get().build();
        request.header("Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)");
        ObservableDecorator.decorate(ObservableTool.getGetObFromUrl(context, request)).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String result) {
                resolveMusic(result);
            }

            @Override
            public void onError(Throwable e) {
                view.onGetMusicFail("暂无结果");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void resolveMusic(String html) {
        if (TextUtils.isEmpty(html)) {
            // 结束加载动画
            view.onGetMusicFail("暂无结果");
            return;
        }

        // 这里获得了html代码
        String advanceResult = html.replaceAll("&quot;", "\"");
        String regex = "\"sid\":\\d+";
        //String regex = "data-sid=\"\\d+";

        Matcher matcher = null;
        try {
            Pattern pattern = Pattern.compile(regex);
            matcher = pattern.matcher(advanceResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (matcher == null) {
            // 结束加载动画
            view.onGetMusicFail("暂无结果");
            return;
        }

        final List<String> ids = new ArrayList<>();
        while (matcher.find()) {
            String[] raw = matcher.group().split(":");
            if (raw != null && raw.length == 2) {
                ids.add(raw[1]);
            }
        }

        if (ids.size() <= 0) {
            // 结束加载动画
            view.onGetMusicFail("暂无结果");
            return;
        }

        for (String id : ids) {
            String infoUrl = "http://music.baidu.com/data/music/fmlink?songIds=" + id + "&type=mp3&rate=320";
            RequestParams params = new RequestParams(infoUrl);
            x.http().get(params, new org.xutils.common.Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonObject == null) {
                        return;
                    }

                    int errorCode = 0;
                    try {
                        errorCode = jsonObject.getInt("errorCode");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (errorCode != 22000) {
                        return;
                    }

                    JSONObject dataObject = null;
                    try {
                        dataObject = jsonObject.getJSONObject("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (dataObject == null) {
                        return;
                    }

                    JSONArray listArray = null;
                    try {
                        listArray = dataObject.getJSONArray("songList");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (listArray == null) {
                        return;
                    }

                    if (listArray.length() >= 1) {
                        JSONObject info = null;
                        try {
                            info = listArray.getJSONObject(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (info == null) {
                            return;
                        }

                        Music music = new Music();
                        music.setMusicType(4);// 音乐来源
                        try {
                            music.setSourceId(info.getString("queryId"));// 歌曲ID
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        music.setId("xiong" + music.getSourceId());// 歌曲ID
                        try {
                            music.setName(info.getString("songName"));// 歌名
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            music.setSinger(info.getString("artistName"));// 歌手
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            music.setAlbum(info.getString("albumName"));// 专辑
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            music.setBitrate(info.getString("rate"));// 音质
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String format = null;
                        try {
                            format = info.getString("format");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String fileName = music.getName() + "-" + music.getSinger() + "." +format;
                        String uniFileName = FileTool.getUniqueFileName(ZhuConstants.PATH_CLASSIC_XIONG, fileName, 1);
                        music.setFileName(uniFileName);// 文件名


                        try {
                            music.setMp3Url(info.getString("songLink"));// 下载地址
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 文件路径
                        music.setFilePath(ZhuConstants.PATH_CLASSIC_XIONG + music.getFileName());
                        musics.add(music);
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
                    notifyIfNeed(ids.size());
                }
            });
        }

    }

    private synchronized void notifyIfNeed(int size) {
        index++;
        if (index >= size) {
            view.onGetMusicSuc(musics);
            index = 0;
        }
    }
}
