package com.itant.zhuling.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BitmapTool {

    /**
     * 根据uri获取bitmap
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = getBitmapFromStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            LogTool.e(e.toString());
            return null;
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromBytes(byte[] bytes) {

        InputStream inputStream = new ByteArrayInputStream(bytes);

        // 为位图设置100K的缓存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTempStorage = new byte[100 * 1024];
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // 设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        options.inPurgeable = true;

        int len = bytes.length;
        if (len < 1024 * 100) {
            options.inSampleSize = 1;
        } else if (len < 1024 * 1024) {
            options.inSampleSize = 2;
        } else {
            //options.inSampleSize = 4;
            double times = Math.floor(((double)len) / (1024 * 1024));
            options.inSampleSize = (int)times * 2;
        }


        options.inSampleSize = 4;
        options.inInputShareable  = true;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        return bitmap;
    }

    public static Bitmap getBitmapFromStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        // 为位图设置100K的缓存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTempStorage = new byte[100 * 1024];
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // 设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        options.inPurgeable = true;

        int len = -1;
        try {
            len = inputStream.available();
        } catch (Exception e) {

        }

        if (len != -1) {
            if (len < 1024 * 100) {
                options.inSampleSize = 1;
            } else if (len < 1024 * 1024) {
                options.inSampleSize = 2;
            } else {
                options.inSampleSize = 4;
                double times = Math.floor(((double)len) / (1024 * 1024));
                options.inSampleSize = (int)times * 2;
            }

            options.inSampleSize = 4;
            options.inInputShareable  = true;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            return bitmap;
        }


        return null;
    }
}
