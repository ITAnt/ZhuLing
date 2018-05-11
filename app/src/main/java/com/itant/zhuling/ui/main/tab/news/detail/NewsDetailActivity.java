package com.itant.zhuling.ui.main.tab.news.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itant.zhuling.R;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by iTant on 2017/3/28.
 * 由于新闻详情使用了共享动画，所以就不要用右划关闭和从右往左进的动画了
 * 直接继承AppCompatActivity，而不是BaseActivity
 */

public class NewsDetailActivity extends AppCompatActivity implements NewsDetailContract.View {
    private LinearLayout ll_scroll;
    private String bodyStr;

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
        ActivityCompat.postponeEnterTransition(this);
        ActivityCompat.startPostponedEnterTransition(this);

        String url = getIntent().getStringExtra("url_top");
        ImageView iv_top = (ImageView) findViewById(R.id.iv_top);

        // 加placeholder可以解决match_parent不全屏的bug，我们这里用override可以不加
        Glide.with(this).load(url)
                //.placeholder(R.mipmap.empty)
                //.error(R.mipmap.empty)
                .priority(Priority.HIGH)
                .override(256, 256)// 两边都使用override方法可以让过渡非常流畅
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)// 缓存所有尺寸的图片
                .dontAnimate()// 重要：加入这一句，可以修复转场动画ImageView 宽度match_parent时边上有间隙的bug
                .dontTransform()
                .into(iv_top);

        /*Glide.with(this).load(url)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)// 缓存所有尺寸的图片
                .into(iv_top);*/

        /*Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        NewsDetailActivity.this.supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(iv_top);*/

        ll_scroll = (LinearLayout) findViewById(R.id.ll_scroll);
        String postId = getIntent().getStringExtra("postId");
        if (!TextUtils.isEmpty(postId)) {
            NewsDetailPresenter presenter = new NewsDetailPresenter(this, this);
            presenter.getNewsDetail(postId);
        }

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 沉浸式状态栏下面这三句话让顶部出现返回按钮
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // 给页面设置工具栏
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null) {
            // 设置隐藏图片时候ToolBar的颜色
            //collapsingToolbar.setContentScrimColor(Color.parseColor("#11B7F3"));
            // 设置工具栏标题
            collapsingToolbar.setTitle("新闻详情");
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

    @Override
    public void onGetNewsDetailSuc(NewsDetail newsBeen) {
        List<NewsDetail.ImgBean> images = newsBeen.getImg();
        bodyStr = newsBeen.getBody();

        if (images != null && images.size() > 0) {
            // 替换内容里的图片占位符为真正的图片网址
            for (int i = 0, j = images.size(); i < j; i++) {
                String imagePlaceHolder = images.get(i).getRef();// 图片占位符
                int index = bodyStr.indexOf(imagePlaceHolder);// 图片的位置
                String text = bodyStr.substring(0, index);
                if (!TextUtils.isEmpty(text)) {
                    TextView textView = new TextView(this);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    textView.setLayoutParams(params);
                    textView.setText(Html.fromHtml(text));
                    ll_scroll.addView(textView);
                }

                ImageView imageView = new ImageView(this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                String pixels = images.get(i).getPixel();
                String[] wh = pixels.split("\\*");
                if (wh.length == 2) {
                    try {
                        params.width = Integer.parseInt(wh[0]);
                        params.height = Integer.parseInt(wh[1]);
                    } catch (Exception e) {
                        params.width = MATCH_PARENT;
                        params.height = WRAP_CONTENT;
                    }
                } else {
                    params.width = MATCH_PARENT;
                    params.height = WRAP_CONTENT;
                }
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(this)
                        .load(images.get(i).getSrc())
                        .placeholder(R.mipmap.empty)
                        .error(R.mipmap.empty)
                        .into(imageView);

                ll_scroll.addView(imageView);

                if (index+imagePlaceHolder.length() < bodyStr.length()-1) {
                    if (i == j-1) {
                        bodyStr = bodyStr.substring(index+imagePlaceHolder.length());
                        TextView textView = new TextView(this);
                        textView.setText(Html.fromHtml(bodyStr));
                        ll_scroll.addView(textView);
                        break;
                    } else {
                        bodyStr = bodyStr.substring(index+imagePlaceHolder.length());
                    }
                } else {
                    break;
                }
            }
        } else {
            // 没有图片
            TextView textView = new TextView(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setText(Html.fromHtml(bodyStr));
            ll_scroll.addView(textView);
        }
    }

    @Override
    public void onGetNewsDetailFail(String msg) {

    }

    public void onResume() {
        super.onResume();
        //统计时长
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
