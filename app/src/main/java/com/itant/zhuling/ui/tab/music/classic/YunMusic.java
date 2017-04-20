package com.itant.zhuling.ui.tab.music.classic;


import android.text.TextUtils;
import android.util.Base64;

import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.FileTool;
import com.itant.zhuling.ui.tab.music.MusicContract;
import com.itant.zhuling.ui.tab.music.bean.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 詹子聪 on 2016/11/15.
 * 白云音乐
 */
public class YunMusic {

    private MusicContract.View view;
    private List<Music> musics;
    private String keyWords;
    private int page;
    public YunMusic(MusicContract.View view, String keyWords, int page) {
        musics = new ArrayList<>();
        this.view = view;
        this.keyWords = keyWords;
        this.page = page;
    }

    /**
     * 获取白云歌曲信息
     */
    public void getYunSongs() {
        RequestParams params = new RequestParams("http://music.163.com/api/search/pc");
        params.addBodyParameter("offset", String.valueOf(page));
        params.addBodyParameter("total", "true");
        params.addBodyParameter("limit", "20");
        params.addBodyParameter("type", "1");
        params.addBodyParameter("s", keyWords);
        params.addHeader("Cookie", "os=pc;MUSIC_U=5339640232");
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                if (TextUtils.isEmpty(result)) {
                    // 结束加载动画
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
                    // 结束加载动画
                    view.onGetMusicFail("暂无结果");
                    return;
                }

                JSONObject resultObject = null;
                try {
                    resultObject = jsonObject.getJSONObject("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (resultObject == null) {
                    // 结束加载动画
                    view.onGetMusicFail("暂无结果");
                    return;
                }


                JSONArray listArray = null;
                try {
                    listArray = resultObject.getJSONArray("songs");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listArray == null) {
                    // 结束加载动画
                    view.onGetMusicFail("暂无结果");
                    return;
                }

                for (int j = 0; j < listArray.length(); j++) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(listArray.get(j).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (object == null) {
                        continue;
                    }

                    Music music = new Music();
                    music.setMusicType(3);// 音乐来源
                    try {
                        music.setSourceId(object.getString("id"));// 歌曲ID
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    music.setId("yun" + music.getSourceId());// 歌曲ID
                    music.setSinger("未知");// 歌手

                    try {
                        JSONArray singers = object.getJSONArray("artists");
                        if (singers != null && singers.length() > 0) {
                            Object singerObj = singers.get(0);
                            if (singerObj != null) {
                                JSONObject singer = new JSONObject(singerObj.toString());
                                if (singer != null) {
                                    music.setSinger(singer.getString("name"));// 歌手
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        music.setName(object.getString("name"));// 歌名
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    music.setAlbum("未知");
                    try {
                        music.setAlbum(object.getJSONObject("album").getString("name"));// 专辑
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    music.setBitrate("128");// 音质
                    String encryptId = "";
                    String dfsId = "";
                    String extension = "";

                    JSONObject lObject = null;
                    try {
                        lObject = object.getJSONObject("lMusic");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (lObject != null) {
                        try {
                            dfsId = lObject.getString("dfsId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            extension = lObject.getString("extension");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        music.setBitrate("128");// 音质
                    }

                    JSONObject mObject = null;
                    try {
                        mObject = object.getJSONObject("mMusic");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mObject != null) {
                        try {
                            dfsId = mObject.getString("dfsId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            extension = mObject.getString("extension");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        music.setBitrate("160");// 音质
                    }


                    JSONObject bObject = null;
                    try {
                        bObject = object.getJSONObject("bMusic");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (bObject != null) {
                        try {
                            dfsId = bObject.getString("dfsId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            extension = bObject.getString("extension");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        music.setBitrate("一般");// 音质
                    }

                    JSONObject hObject = null;
                    try {
                        hObject = object.getJSONObject("hMusic");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (hObject != null) {
                        try {
                            dfsId = hObject.getString("dfsId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            extension = hObject.getString("extension");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        music.setBitrate("320");// 音质
                    }


                    String url = "";
                    try {
                        String key = "3go8&$8*3*3h0k(2)2";
                        byte[] keyBytes = key.getBytes();
                        byte[] searchBytes = dfsId.getBytes();
                        for (int i = 0; i < searchBytes.length; ++i) {
                            searchBytes[i] ^= keyBytes[i % keyBytes.length];
                        }
                        MessageDigest mdInst = null;
                        mdInst = MessageDigest.getInstance("MD5");
                        mdInst.update(searchBytes);
                        String params = Base64.encodeToString(mdInst.digest(), Base64.NO_WRAP);
                        params = params.replace("+", "-");
                        params = params.replace("/", "_");
                        url = "http://m2.music.126.net/" + params + "/" + dfsId + "." + extension;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    music.setMp3Url(url);// 下载地址

                    String fileName = music.getName() + "-" + music.getSinger() + "-" + music.getSourceId() + ".mp3";
                    String uniFileName = FileTool.getUniqueFileName(ZhuConstants.PATH_CLASSIC_YUN, fileName, 1);
                    music.setFileName(uniFileName);// 文件名

                    // 文件路径
                    music.setFilePath(ZhuConstants.PATH_CLASSIC_YUN + music.getFileName());
                    musics.add(music);
                }

                // 更新搜索结果
                view.onGetMusicSuc(musics);
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
}
