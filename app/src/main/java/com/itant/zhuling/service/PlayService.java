package com.itant.zhuling.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.itant.zhuling.application.Notifier;
import com.itant.zhuling.application.ZhuManager;
import com.itant.zhuling.ui.main.tab.music.bean.Music;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jason on 2017/4/22.
 */

public class PlayService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private List<Music> mMusicList;
    private Handler mHandler = new Handler();
    private AudioManager mAudioManager;
    private MediaPlayer mPlayer = new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicList = ZhuManager.getMusicList();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mPlayer.setOnCompletionListener(this);
        Notifier.init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public static void startCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public class MusicBinder extends Binder {
        /**
         * 提供一个公开的方法，在被bind的时候，让调用者可以拥有当前service的对象，进行相应的操作
         */
        public PlayService getService() {
            return PlayService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                /*case Actions.ACTION_MEDIA_PLAY_PAUSE:
                    playPause();
                    break;*/
            }
        }

        /**
         * START_NOT_STICKY：“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，
         * 服务被异常kill掉，系统不会自动重启该服务。
         * 参考：https://zhidao.baidu.com/question/2138913632394538588.html
         */
        return START_NOT_STICKY;
    }

    public void play(Music music) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getMp3Url());
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(mPreparedListener);
            /*isPreparing = true;

            if (mListener != null) {
                mListener.onChange(music);
            }*/
            //Notifier.showPlay(music);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mPlayer.start();
            //Notifier.showPlay(mPlayingMusic);
            mAudioManager.requestAudioFocus(PlayService.this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            //registerReceiver(mNoisyReceiver, mNoisyFilter);
        }
    };

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ZhuManager.setMusicService(null);
    }
}
