package com.itant.zhuling.ui.navigation.notice;

import cn.bmob.v3.BmobObject;

/**
 * Created by Jason on 2017/4/16.
 * 更新接口对应的bean
 */

public class NoticeBean extends BmobObject {
    private String title;
    private String notice;

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
