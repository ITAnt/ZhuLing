package com.itant.zhuling.widget.leaf;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itant.zhuling.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * 翎羽飘落
 */
public class FloatLeafLayout extends RelativeLayout {
    private static final String TAG = FloatLeafLayout.class.getSimpleName();
    private Drawable mLeafDrawable;     // 树叶种类的数组
    private Interpolator mInterpolator; // 补间器种类的数组
    private int mWidthSize;             // view宽度
    private int mHeightSize;            // view高度
    private ArrayList<AnimatorSet> mAnimatorSets;// 动画合集

    private int tempX;
    private int tempY;
    private int tempRotation;

    public FloatLeafLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        // 叶子
        mLeafDrawable = getResources().getDrawable(R.mipmap.ling);

        // 插值器
        mInterpolator = new AccelerateDecelerateInterpolator();
        //AccelerateInterpolator：动画从开始到结束，变化率是一个加速的过程。
        //DecelerateInterpolator：动画从开始到结束，变化率是一个减速的过程。
        //CycleInterpolator：动画从开始到结束，变化率是循环给定次数的正弦曲线。
        //AccelerateDecelerateInterpolator：动画从开始到结束，变化率是先加速后减速的过程。
        //LinearInterpolator：动画从开始到结束，变化率是线性变化。

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidthSize = measure(widthMeasureSpec), mHeightSize = measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = dip2px(getContext(), 300);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 落叶飘零
     */
    public void floatLing() {
        final Random random = new Random();
        int startX =  random.nextInt(mWidthSize-2);
        int startY = 1;

        final ImageView mLeaf = new ImageView(getContext());
        mLeaf.setImageDrawable(mLeafDrawable);
        // 设置落叶起点，添加到布局
        ViewCompat.setX(mLeaf, startX);
        ViewCompat.setY(mLeaf, startY);
        addView(mLeaf);

        // 设置树叶移动时的动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mLeaf, "alpha", 1, 1);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLeaf, "scaleX", 0.2f, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLeaf, "scaleY", 0.2f, 1);
        tempRotation = random.nextInt(360) + 360;
        ObjectAnimator rotate = ObjectAnimator.ofFloat(mLeaf, "rotation", 0f, tempRotation);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alpha, scaleX, scaleY, rotate);
        set.setDuration(700);

        // 树叶落下的起点，必须在顶部，即假定Y坐标为1，X坐标为0到mWidthSize-2的范围
        final PointF pointF0 = new PointF(startX, startY);
        // 树叶落下经过的第二个点，横坐标任意，纵坐标不能大于 mHeightSize/2
        final PointF pointF1 = new PointF(random.nextInt(mWidthSize-2), random.nextInt(mHeightSize/2));
        // 树叶落下经过的第三个点，横坐标任意，纵坐标不能大于 mHeightSize/2
        final PointF pointF2 = new PointF(random.nextInt(mWidthSize-2), random.nextInt(mHeightSize/2));

        // 临时点
        tempX = random.nextInt(mWidthSize-2);
        tempY = random.nextInt(mHeightSize/2);
        // 树叶落下经过的第四个点，横坐标任意，纵坐标不能大于 mHeightSize/2
        final PointF pointF3 = new PointF(tempX, tempY);

        // 通过自定义的贝塞尔估值器算出途经的点的x，y坐标
        final BazierTypeEvaluator bazierTypeEvaluator = new BazierTypeEvaluator(pointF1, pointF2);
        // 设置值动画
        ValueAnimator bazierAnimator = ValueAnimator.ofObject(bazierTypeEvaluator, pointF0, pointF3);
        bazierAnimator.setTarget(mLeaf);
        bazierAnimator.addUpdateListener(new BazierUpdateListener(mLeaf));
        bazierAnimator.setDuration(700);

        // 将以上动画添加到动画集合
        AnimatorSet allSet = new AnimatorSet();
        //allSet.play(set).before(bazierAnimator);
        allSet.play(bazierAnimator).with(set);
        // 随机设置一个补间器
        allSet.setInterpolator(mInterpolator);
        allSet.addListener(new AnimatorEndListener(mLeaf));
        allSet.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 大小不变，旋转还是要的
                ObjectAnimator rotate = ObjectAnimator.ofFloat(mLeaf, "rotation", tempRotation, 720);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(rotate);
                set.setDuration(1300);

                // 树叶落下经过的第七个点，横坐标任意，纵坐标不能大于 mHeightSize/2
                final PointF pointF4 = new PointF(tempX, tempY);
                // 树叶落下经过的第八个点，横坐标任意，纵坐标不能大于 mHeightSize/2
                final PointF pointF5 = new PointF(random.nextInt(mWidthSize-2), random.nextInt(mHeightSize/2));
                // 树叶落下经过的第九个点，横坐标任意，纵坐标不能大于 mHeightSize/2
                final PointF pointF6 = new PointF(random.nextInt(mWidthSize-2), random.nextInt(mHeightSize/2));
                // 树叶落下的终点
                final PointF pointF7 = new PointF(mWidthSize*11/20, mHeightSize*15/30);


                // 通过自定义的贝塞尔估值器算出途经的点的想x，y坐标
                final BazierTypeEvaluator bazierTypeEvaluator2 = new BazierTypeEvaluator(pointF5, pointF6);
                // 设置值动画
                ValueAnimator bazierAnimator2 = ValueAnimator.ofObject(bazierTypeEvaluator2, pointF4, pointF7);
                bazierAnimator2.setTarget(mLeaf);
                bazierAnimator2.addUpdateListener(new BazierUpdateListener(mLeaf));
                bazierAnimator2.setDuration(1300);

                // 将以上动画添加到动画集合
                AnimatorSet allSet2 = new AnimatorSet();
                allSet2.play(bazierAnimator2).with(set);
                // 随机设置一个补间器
                allSet2.setInterpolator(mInterpolator);
                allSet2.addListener(new AnimatorEndListener(mLeaf));
                allSet2.start();

            }
        }, 700);

        // 将动画添加进列表，方便移除
        if (mAnimatorSets == null) {
            mAnimatorSets = new ArrayList<>();
        }
        mAnimatorSets.add(allSet);
    }

    // 值动画更新监听
    private class BazierUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        View target;

        public BazierUpdateListener(View target) {
            BazierUpdateListener.this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // 获取坐标，设置落叶的位置
            final PointF pointF = (PointF) animation.getAnimatedValue();
            ViewCompat.setX(target, pointF.x);
            ViewCompat.setY(target, pointF.y);
            ViewCompat.setAlpha(target, 1);
            //ViewCompat.setAlpha(target, 1 - animation.getAnimatedFraction());
        }
    }

    // 动画更新适配器，用于动画停止的时候移除翎羽，我们这里不需要移除
    private class AnimatorEndListener extends AnimatorListenerAdapter {

        View target;

        public AnimatorEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //removeView(target);
            Log.e(TAG, "child:" + getChildCount());
        }
    }

    // 销毁的时候做清理工作
    public void onDestroy() {
        if (mAnimatorSets == null) return;
        for (int i = 0; i < mAnimatorSets.size(); i++) {
            mAnimatorSets.get(i).cancel();
        }
        mAnimatorSets.clear();
    }
}
