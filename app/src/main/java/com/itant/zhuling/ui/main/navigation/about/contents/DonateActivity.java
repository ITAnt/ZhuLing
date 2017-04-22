package com.itant.zhuling.ui.main.navigation.about.contents;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.itant.zhuling.R;
import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.FileTool;
import com.itant.zhuling.tool.PreferencesTool;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.tool.UriTool;
import com.itant.zhuling.tool.image.BitmapTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Jason on 2017/3/26.
 */

public class DonateActivity extends BaseSwipeActivity implements View.OnLongClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_about_donate);
        // 右划删除
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        setTitle("捐助");

        initView();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.iv_money).setOnLongClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onLongClick(View v) {


        FileTool.initDirectory(Environment.getExternalStorageDirectory() + ZhuConstants.DIRECTORY_ROOT_FILE_IMAGES);

        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.nav_about_pay_wechat);
        if (srcBitmap == null) {
            ToastTool.showShort(this, "出错啦");
            return true;
        }

        String fullPath = Environment.getExternalStorageDirectory() + ZhuConstants.PAY_WECHAT;
        File f = new File(fullPath);
        if (f.exists()) {
            f.delete();
        }
        if (BitmapTool.saveBitmap(fullPath, srcBitmap)) {
            insertIntoAlbum(fullPath, "nav_about_pay_wechatpay_wechat.png");
            ToastTool.showShort(this, "保存成功");
        } else {
            ToastTool.showShort(this, "保存失败");
        }

        return true;
    }

    /**
     * 插入到相册
     * @param picName
     */
    private void insertIntoAlbum(String fullPath, String picName) {
        // 保证相册只存一张我们的图片
        String lastUrl = PreferencesTool.getString(this, "pay_wechat_url");
        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), Uri.parse(lastUrl), null);
        if (cursor != null && cursor.moveToNext()) {
            return;
        }

        File file = new File(fullPath);
        Uri uri = UriTool.getUriFromFile(this, ZhuConstants.NAME_PROVIDE, file);

        try {
            String url = MediaStore.Images.Media.insertImage(getContentResolver(), fullPath, picName, null);
            PreferencesTool.putString(this, "pay_wechat_url", url);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 通知相册更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }
}
