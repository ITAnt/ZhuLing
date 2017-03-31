package com.itant.zhuling.event;

/**
 * Created by Jason on 2017/3/28.
 */

public class ToolBarMoveEvent {
    private int distance;

    public ToolBarMoveEvent(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }


}
