package com.itant.zhuling.ui.main;

import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

import com.itant.zhuling.R;
import com.itant.zhuling.tool.UITool;
import com.itant.zhuling.ui.base.BaseFragment;

/**
 * Created by Jason on 2017/4/21.
 * 正在播放的界面
 */

public class PlayingFragment extends BaseFragment {
    private LinearLayout ll_content;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_playing;
    }

    @Override
    public void initViews(View view) {
        ll_content = (LinearLayout) view.findViewById(R.id.ll_content);

        initSystemBar();
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
}
