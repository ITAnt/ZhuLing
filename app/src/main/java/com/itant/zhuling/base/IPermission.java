package com.itant.zhuling.base;

/**
 * Created by Jason on 2017/4/15.
 */

public interface IPermission {
    void onPermissionSuccess(int requestCode);
    void onPermissionFail(int requestCode);
}
