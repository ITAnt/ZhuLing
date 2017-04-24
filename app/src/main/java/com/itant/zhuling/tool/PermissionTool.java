package com.itant.zhuling.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.itant.zhuling.base.IPermission;

import java.util.List;

/**
 * Created by iTant on 2017/4/9.
 */

public class PermissionTool {
    /**
     * 初始化权限
     */
    public static void initPermission(Activity activity, String[] permissions, int requestCode) {
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

    public static void onActivityPermissionResult(IPermission permission, int requestCode, @NonNull int[] grantResults) {
        boolean granted = true;
        for (int result : grantResults) {
            granted = result == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                break;
            }
        }

        if (granted) {
            permission.onPermissionSuccess(requestCode);
        } else {
            permission.onPermissionFail(requestCode);
        }
    }

    /**
     * 赋予读写URI对应文件的权限
     * @param intent
     * @param uri
     */
    public static void grantUriPermission(Context context, Intent intent, Uri uri) {
        if (Build.VERSION.SDK_INT < 24) {
            return;
        }

        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        //将存储图片的uri读写权限授权给剪裁工具应用
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }
}
