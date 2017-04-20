package com.itant.zhuling.ui.tab.music;

import android.content.Context;

import com.itant.zhuling.ui.tab.music.bean.Music;
import com.itant.zhuling.ui.tab.music.classic.DogMusic;
import com.itant.zhuling.ui.tab.music.classic.KmeMusic;
import com.itant.zhuling.ui.tab.music.classic.QieMusic;
import com.itant.zhuling.ui.tab.music.classic.XiaMusic;
import com.itant.zhuling.ui.tab.music.classic.XiongMusic;
import com.itant.zhuling.ui.tab.music.classic.YunMusic;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Jason on 2017/3/26.
 */

public class MusicPresenter implements MusicContract.Presenter {

    private Context mContext;
    private MusicContract.View mView;

    public MusicPresenter(Context context, MusicContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void getMusic(int position, String keywords, int page) {
        try {
            switch (position) {
                //0关 1虾 2鹅 3云 4我 5熊 6狗 7推荐
                case 0:
                    break;
                case 1:
                    new XiaMusic(mView, keywords, page).getXiaSongs();
                    break;
                case 2:
                    new QieMusic(mView, keywords, page).getQieSongs();
                    break;
                case 3:
                    new YunMusic(mView, keywords, page).getYunSongs();
                    break;
                case 4:
                    new KmeMusic(mView, keywords, page).getWoSongs();
                    break;
                case 5:
                    new XiongMusic(mContext, mView, keywords, page).getXiongSongs();
                    break;
                case 6:
                    new DogMusic(mView, keywords, page).getDogSongs();
                    break;
                case 7:
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
        query.setSkip(10*page);
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
