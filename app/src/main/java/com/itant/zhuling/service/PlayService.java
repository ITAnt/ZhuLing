package com.itant.zhuling.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.itant.zhuling.application.ZhuManager;
import com.itant.zhuling.listener.PlayStateChangeListener;
import com.itant.zhuling.ui.main.tab.music.bean.Music;

import java.io.IOException;

/**
 * Created by iTant on 2017/4/22.
 */

public class PlayService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private AudioManager mAudioManager;
    private MediaPlayer mPlayer = new MediaPlayer();
    private PlayStateChangeListener mPlayStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mPlayer.setOnCompletionListener(this);
        //Notifier.init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
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

    /**
     * 开始播放
     * @param music
     */
    public void play(Music music) {
        if (music == null) {
            return;
        }
        ZhuManager.getInstance().setPlayingMusic(music);
        ZhuManager.getInstance().setMusicPlaying(true);
        // 正在播放
        if (mPlayStateListener != null) {
            mPlayStateListener.onChange(true);
        }

        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getMp3Url());
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(mPreparedListener);
            //Notifier.showPlay(music);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        mPlayer.stop();
        ZhuManager.getInstance().setMusicPlaying(false);
        if (mPlayStateListener != null) {
            mPlayStateListener.onChange(false);
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
        ZhuManager.getInstance().setMusicPlaying(false);
        // 播放完了
        if (mPlayStateListener != null) {
            mPlayStateListener.onChange(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ZhuManager.getInstance().setMusicService(null);
    }

    public PlayStateChangeListener getPlayStateListener() {
        return mPlayStateListener;
    }

    public void setPlayStateListener(PlayStateChangeListener mPlayStateListener) {
        this.mPlayStateListener = mPlayStateListener;
    }
}
