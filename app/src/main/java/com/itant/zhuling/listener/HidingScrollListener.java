package com.itant.zhuling.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.itant.zhuling.utils.UIUtils;


/*
* This class is a ScrollListener for RecyclerView that allows to show/hide
* views when list is scrolled.
* */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final float HIDE_THRESHOLD = 10;
    private static final float SHOW_THRESHOLD = 70;

    private int mToolbarOffset = 0;
    private boolean mControlsVisible = true;
    private int mToolbarHeight;
    private int mTotalScrolledDistance;

    public HidingScrollListener(Context context) {
        mToolbarHeight = UIUtils.getToolbarHeight(context);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
            if(mTotalScrolledDistance < mToolbarHeight) {
                setVisible();
            } else {
                if (mControlsVisible) {
                    if (mToolbarOffset > HIDE_THRESHOLD) {
                        setInvisible();
                    } else {
                       setVisible();
                    }
                } else {
                    if ((mToolbarHeight - mToolbarOffset) > SHOW_THRESHOLD) {
                        setVisible();
                    } else {
                        setInvisible();
                    }
                }
            }
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(mToolbarOffset);

        if((mToolbarOffset <mToolbarHeight && dy>0) || (mToolbarOffset >0 && dy<0)) {
            mToolbarOffset += dy;
        }
        if (mTotalScrolledDistance < 0) {
            mTotalScrolledDistance = 0;
        } else {
            mTotalScrolledDistance += dy;
        }
    }

    private void clipToolbarOffset() {
        if(mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if(mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    private void setVisible() {
        if(mToolbarOffset > 0) {
            onShow();
            mToolbarOffset = 0;
        }
        mControlsVisible = true;
    }

    private void setInvisible() {
        if(mToolbarOffset < mToolbarHeight) {
            onHide();
            mToolbarOffset = mToolbarHeight;
        }
        mControlsVisible = false;
    }

    public abstract void onMoved(int distance);
    public abstract void onShow();
    public abstract void onHide();
}
