package com.itant.zhuling.widget.leaf;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * 类名： BazierTypeEvaluator
 * 作者: oubowu
 * 时间： 2015/12/2 14:14
 * 功能：贝塞尔曲线估值器
 * svn版本号:$$Rev$$
 * 更新时间:$$Date$$
 * 更新人:$$Author$$
 * 更新描述:
 */
public class BazierTypeEvaluator implements TypeEvaluator<PointF> {
    /**
     * 三次方贝塞尔曲线
     * B(t)=P0*(1-t)^3+3*P1*t*(1-t)^2+3*P2*t^2*(1-t)+P3*t^3,t∈[0,1]
     * P0,是我们的起点,
     * P3是终点,
     * P1,P2是途径的两个点
     * 而t则是我们的一个因子,取值范围是0-1
     */
    private PointF pointF1;
    private PointF pointF2;

    public BazierTypeEvaluator(PointF pointF1, PointF pointF2) {
        this.pointF1 = pointF1;
        this.pointF2 = pointF2;
    }

    @Override
    public PointF evaluate(float t, PointF startValue, PointF endValue) {
        PointF pointF = new PointF();
        pointF.x = (float) (startValue.x * Math.pow(1 - t, 3) + 3 * pointF1.x * t * Math.pow(1 - t, 2) + 3 * pointF2.x * Math.pow(t, 2) * (1 - t) + endValue.x * Math.pow(t, 3));
        pointF.y = (float) (startValue.y * Math.pow(1 - t, 3) + 3 * pointF1.y * t * Math.pow(1 - t, 2) + 3 * pointF2.y * Math.pow(t, 2) * (1 - t) + endValue.y * Math.pow(t, 3));
        return pointF;
    }
}