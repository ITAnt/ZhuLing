package com.itant.zhuling.ui.main;

import cn.bmob.v3.BmobObject;

/**
 * Created by iTant on 2017/4/16.
 * 更新接口对应的bean
 */

public class UpdateInfo extends BmobObject {
    private String versionCode;
    private String versionName;
    private String packageSizeMB;
    private String updateDesc;
    private Boolean appDisable;
    private Boolean musicDisable;
    /**
     * 更新类型：1强制更新，到应用市场 2强制更新，Bmob下载 3可选更新，到应用市场 4可选更新，Bmob下载
     */
    private String updateType;
    private String downloadUrl;

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPackageSizeMB() {
        return packageSizeMB;
    }

    public void setPackageSizeMB(String packageSizeMB) {
        this.packageSizeMB = packageSizeMB;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Boolean getAppDisable() {
        return appDisable;
    }

    public void setAppDisable(Boolean appDisable) {
        this.appDisable = appDisable;
    }

    public Boolean getMusicDisable() {
        return musicDisable;
    }

    public void setMusicDisable(Boolean musicDisable) {
        this.musicDisable = musicDisable;
    }
}
