package com.itant.zhuling.ui.main.navigation.feedback;

import cn.bmob.v3.BmobObject;

/**
 * Created by Jason on 2017/4/17.
 */

public class FeedbackBean extends BmobObject {
    private String feedback;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
