package com.itant.zhuling.ui.main.tab.news.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.itant.zhuling.R;

/**
 * Created by 子聪 on 2017/3/28.
 * 由于新闻详情使用了共享动画，所以就不要用右划删除和从右往左进的动画了
 * 直接继承AppCompatActivity，而不是BaseActivity
 */

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 普通Activity动画。还有一些是共享元素的动画
            Shared Elements Transition 共享元素转换
            它的作用就是共享两个acitivity中共同的元素，在Android 5.0下支持如下效果：
            changeBounds -  改变目标视图的布局边界
            changeClipBounds - 裁剪目标视图边界
            changeTransform - 改变目标视图的缩放比例和旋转角度
            changeImageTransform - 改变目标图片的大小和缩放比例

            // 退出
            getWindow().setExitTransition(new Fade());
            // 第一次进入
            getWindow().setEnterTransition(new Slide());
            // 再次进入时使用
            getWindow().setReenterTransition(new Explode());
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        String url = getIntent().getStringExtra("url_top");
        ImageView iv_top = (ImageView) findViewById(R.id.iv_top);
        Glide.with(this).load(url).into(iv_top);


        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //沉浸式状态栏下面这三句话让顶部出现返回按钮
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //给页面设置工具栏
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null) {
            //设置隐藏图片时候ToolBar的颜色
            //collapsingToolbar.setContentScrimColor(Color.parseColor("#11B7F3"));
            //设置工具栏标题
            collapsingToolbar.setTitle("洋葱科技");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // 点击了返回按钮
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
