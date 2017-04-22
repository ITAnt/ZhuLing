package com.itant.zhuling.ui.main.navigation.more.log;

import cn.bmob.v3.BmobObject;

/**
 * Created by Jason on 2017/4/16.
 * 更新接口对应的bean
 */

public class UpdateLog extends BmobObject {
    private String historyLogs;

    public String getHistoryLogs() {
        return historyLogs;
    }

    public void setHistoryLogs(String historyLogs) {
        this.historyLogs = historyLogs;
    }
}
