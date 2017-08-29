package com.itant.zhuling.ui.main.tab.music;

import android.content.Context;
import android.text.TextUtils;

import com.itant.zhuling.event.music.MusicType;
import com.itant.zhuling.ui.main.tab.music.bean.Music;
import com.itant.zhuling.ui.main.tab.music.classic.DogMusic;
import com.itant.zhuling.ui.main.tab.music.classic.KmeMusic;
import com.itant.zhuling.ui.main.tab.music.classic.QieMusic;
import com.itant.zhuling.ui.main.tab.music.classic.XiaMusic;
import com.itant.zhuling.ui.main.tab.music.classic.XiongMusic;
import com.itant.zhuling.ui.main.tab.music.classic.YunMusic;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by iTant on 2017/3/26.
 */
public class MusicPresenter implements MusicContract.Presenter {
    private Context mContext;
    private MusicContract.View mView;

    public MusicPresenter(Context context, MusicContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void getMusic(int position, String words, int page) {
        String keywords = words;
        if (position != MusicType.MUSIC_TYPE_JIAN) {
            keywords = words.replaceAll(" ", "");
            if (TextUtils.isEmpty(keywords)) {
                mView.onGetMusicFail("关键字不能为空");
                return;
            }
        }

        try {
            switch (position) {
                case MusicType.MUSIC_TYPE_CLOSE:
                    break;
                case MusicType.MUSIC_TYPE_XIA:
                    new XiaMusic(mView, keywords, page).getXiaSongs();
                    break;
                case MusicType.MUSIC_TYPE_QIE:
                    new QieMusic(mView, keywords, page).getQieSongs();
                    break;
                case MusicType.MUSIC_TYPE_YUN:
                    new YunMusic(mView, keywords, page).getYunSongs();
                    break;
                case MusicType.MUSIC_TYPE_KU:
                    new KmeMusic(mView, keywords, page).getWoSongs();
                    break;
                case MusicType.MUSIC_TYPE_XIONG:
                    new XiongMusic(mContext, mView, keywords, page).getXiongSongs();
                    break;
                case MusicType.MUSIC_TYPE_GOU:
                    new DogMusic(mView, keywords, page).getDogSongs();
                    break;
                case MusicType.MUSIC_TYPE_JIAN:
                    getRecommendMusic(page);
                    break;
            }
        } catch (Exception e) {
            mView.onGetMusicFail("获取失败");
        }
    }

    /**
     * 获取推荐列表
     * @param page
     */
    private void getRecommendMusic(int page) {
        BmobQuery<Music> query = new BmobQuery<>();
        query.addWhereNotEqualTo("objectId", "");
        query.setLimit(10);
        query.setSkip(10 * page);
        //执行查询方法
        query.findObjects(mContext, new FindListener<Music>() {
            @Override
            public void onSuccess(List<Music> list) {
                mView.onGetMusicSuc(list);
            }

            @Override
            public void onError(int i, String s) {
                mView.onGetMusicFail("获取失败");
            }
        });
    }
}
