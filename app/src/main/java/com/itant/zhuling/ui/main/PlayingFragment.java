package com.itant.zhuling.ui.main;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.application.ZhuManager;
import com.itant.zhuling.listener.PlayStateChangeListener;
import com.itant.zhuling.tool.UITool;
import com.itant.zhuling.ui.base.BaseFragment;
import com.itant.zhuling.ui.main.tab.music.bean.Music;

/**
 * Created by iTant on 2017/4/21.
 * 正在播放的界面
 */

public class PlayingFragment extends BaseFragment implements View.OnClickListener, PlayStateChangeListener {
    private LinearLayout ll_content;
    private ImageView iv_play;
    private TextView tv_title;
    private TextView tv_singer;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_playing;
    }

    @Override
    public void initViews(View view) {
        ll_content = (LinearLayout) view.findViewById(R.id.ll_content);

        initSystemBar();

        initView(view);
    }

    private void initView(View view) {
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        iv_play = (ImageView) view.findViewById(R.id.iv_play);
        iv_play.setOnClickListener(this);

        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_singer = (TextView) view.findViewById(R.id.tv_singer);
    }

    /**
     * 沉浸式状态栏
     */
    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = UITool.getSystemBarHeight(getActivity().getApplicationContext());
            ll_content.setPadding(0, top, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                // 退出正在播放
                ((MainActivity)getActivity()).hidePlayingFragment();
                break;

            case R.id.iv_play:
                // 播放或暂停
                if (ZhuManager.getInstance().isMusicPlaying()) {
                    ZhuManager.getInstance().getMusicService().stop();
                } else {
                    ZhuManager.getInstance().getMusicService().play(ZhuManager.getInstance().getPlayingMusic());
                }
                break;
        }
    }

    @Override
    public void onChange(boolean isPlaying) {
        onShowPlaying();
    }

    @Override
    public void onResume() {
        super.onResume();
        onShowPlaying();
    }

    /**
     * 播放界面出现时调用
     */
    public void onShowPlaying() {
        if (ZhuManager.getInstance().isMusicPlaying()) {
            iv_play.setImageResource(R.drawable.play_btn_pause_selector);
        } else {
            iv_play.setImageResource(R.drawable.play_btn_play_selector);
        }

        Music music = ZhuManager.getInstance().getPlayingMusic();
        if (music == null) {
            return;
        }
        tv_title.setText(music.getName());
        tv_singer.setText(music.getSinger());
    }
}
