package com.itant.zhuling.ui.main.tab.advanced.meizhi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.itant.zhuling.R;
import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.tool.UriTool;
import com.itant.zhuling.tool.image.BitmapTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;

import java.io.File;
import java.io.FileNotFoundException;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by 89003530 on 2017/4/19.
 */

public class MeizhiDetailActivity extends BaseSwipeActivity implements View.OnClickListener {
    private PhotoView pv_meizhi;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meizhi_detail);

        pv_meizhi = (PhotoView) findViewById(R.id.pv_meizhi);

        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            findViewById(R.id.tv_save).setOnClickListener(this);

            Glide.with(MeizhiDetailActivity.this)
                    .load(url)
                    .asBitmap()
                    .placeholder(R.mipmap.empty)
                    .error(R.mipmap.empty)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)// 缓存所有尺寸的图片
                    //.thumbnail( 0.1f )//先加载原图大小的十分之一
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            bitmap = resource;
                            pv_meizhi.setImageBitmap(resource);
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                // 保存图片到本地
                if (bitmap == null) {
                    ToastTool.showShort(this, "保存失败");
                    break;
                }

                String fullPath = Environment.getExternalStorageDirectory() + ZhuConstants.MEI_ZHI_TEMP;
                if (BitmapTool.saveBitmap(fullPath, bitmap)) {
                    ToastTool.showShort(this, "保存成功");
                    insertIntoAlbum(fullPath, "妹纸") ;
                } else {
                    ToastTool.showShort(this, "保存失败");
                }
                break;
        }
    }



    /**
     * 插入到相册
     * @param picName
     */
    private void insertIntoAlbum(String fullPath, String picName) {
        File file = new File(fullPath);
        Uri uri = UriTool.getUriFromFile(this, ZhuConstants.NAME_PROVIDE, file);

        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), fullPath, picName, null);
            // 通知相册更新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            // 删除临时的
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
