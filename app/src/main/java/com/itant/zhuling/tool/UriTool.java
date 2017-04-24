package com.itant.zhuling.tool;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by iTant on 2017/4/14.
 */

public class UriTool {
    /**
     * 根据文件获取URI
     * @param file
     * @return
     */
    public static Uri getUriFromFile(Context context, String providerName, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT < 24) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(context, providerName, file);
        }
        return uri;
    }
}
