package com.itant.zhuling.tool;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Jason on 2017/4/9.
 */

public class PermissionTool {
    /**
     * 初始化权限
     */
    public static void initPermission(Activity activity, String[] permissions, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        boolean isGranted = true;
        for (String permission : permissions) {
            int result = ActivityCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }

        if (!isGranted) {
            // 还没有的话，去申请权限
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }
}
