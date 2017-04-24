package com.itant.zhuling.ui.main.tab.advanced.meizhi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.itant.library.recyclerview.CommonAdapter;
import com.itant.library.recyclerview.base.ViewHolder;
import com.itant.zhuling.R;
import com.itant.zhuling.tool.ActivityTool;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.ui.base.BaseActivity;
import com.itant.zhuling.widget.recyclerview.DividerItemDecoration;
import com.itant.zhuling.widget.recyclerview.bottomlistener.OnRcvScrollListener;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * Created by iTant on 2017/3/26.
 */

public class MeizhiActivity extends BaseActivity implements MeizhiContract.View, SwipeRefreshLayout.OnRefreshListener {
    private static final int START_PAGE = 1;                // 分页起始页
    private static final int SPAN_COUNT = 2;                // 列数
    private static final int SPACE_LINES = SPAN_COUNT + 1;  // 空隙列数
    private static final int SPACE_WIDTH_DP = 6;            // 空隙宽度

    private MeizhiContract.Presenter presenter;
    private RecyclerView rv_meizhi;
    private int page = START_PAGE;

    private SwipeRefreshLayout swipe_refresh_layout;
    private LinearLayout ll_empty;
    private StaggeredGridLayoutManager mLayoutManager;
    private int dividerWidth;
    private List<Meizhi> mMeizhis;
    private CommonAdapter<Meizhi> adapter;

    private int screenWidth;
    private int screenHeight;
    private Bitmap emptyBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meizhi);

        setTitle("妹纸");

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        dividerWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SPACE_WIDTH_DP, getResources().getDisplayMetrics());
        emptyBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.empty);

        initView();

        presenter = new MeizhiPresenter(this, this);
        presenter.getMeizhi(page);
    }



    public void initView() {
        swipe_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipe_refresh_layout.setOnRefreshListener(this);

        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
        rv_meizhi = (RecyclerView) findViewById(R.id.rv_meizhi);
        rv_meizhi.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);// 瀑布流
        rv_meizhi.setLayoutManager(mLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(dividerWidth);
        rv_meizhi.addItemDecoration(decoration);
        rv_meizhi.addOnScrollListener(new OnRcvScrollListener(){
            @Override
            public void onBottom() {
                super.onBottom();
                // 如果到底部了，而且不是正在加载状态，就变为正在加载状态，并及时去加载数据
                if (!swipe_refresh_layout.isRefreshing()) {
                    swipe_refresh_layout.setRefreshing(true);
                    // 加载更多
                    page++;
                    presenter.getMeizhi(page);
                }
            }
        });

        mMeizhis = new ArrayList<>();
        adapter = new CommonAdapter<Meizhi>(this, R.layout.item_meizhi, mMeizhis) {
            @Override
            protected void convert(final ViewHolder viewHolder, final Meizhi item, final int position) {
                // 解决图片错乱、重复、缓存
                final ImageView iv_meizhi = viewHolder.getView(R.id.iv_meizhi);
                iv_meizhi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳到详情界面，共享元素动画
                        Intent intent = new Intent(MeizhiActivity.this, MeizhiDetailActivity.class);
                        intent.putExtra("url", item.getUrl());

                        ActivityTool.startActivity(MeizhiActivity.this, intent);
                    }
                });

                if (!TextUtils.isEmpty(item.getUrl())) {
                    final String tag = (String) iv_meizhi.getTag(R.id.iv_meizhi);
                    final String uri = item.getUrl();

                    if (!uri.equals(tag)) {
                        // 设置默认图片
                        int imageWidth = emptyBitmap.getWidth();
                        int imageHeight = emptyBitmap.getHeight();
                        // 减去间距
                        int height = (screenWidth - dividerWidth * SPACE_LINES) / SPAN_COUNT * imageHeight / imageWidth;
                        ViewGroup.LayoutParams para = iv_meizhi.getLayoutParams();
                        para.height = height;
                        para.width = (screenWidth - dividerWidth * SPACE_LINES) / SPAN_COUNT;
                        iv_meizhi.setImageBitmap(emptyBitmap);
                    }

                    iv_meizhi.setTag(R.id.iv_meizhi, item.getUrl());

                    Glide.with(MeizhiActivity.this)
                            .load(item.getUrl())
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)// 缓存所有尺寸的图片
                            .thumbnail( 0.1f )//先加载原图大小的十分之一
                            .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            int imageWidth = resource.getWidth();
                            int imageHeight = resource.getHeight();
                            // 减去间距
                            int height = (screenWidth - dividerWidth * SPACE_LINES) / SPAN_COUNT * imageHeight / imageWidth;
                            ViewGroup.LayoutParams para = iv_meizhi.getLayoutParams();
                            para.height = height;
                            para.width = (screenWidth - dividerWidth * SPACE_LINES) / SPAN_COUNT;
                            iv_meizhi.setImageBitmap(resource);
                        }
                    });
                } else {
                    // 设置默认图片
                    int imageWidth = emptyBitmap.getWidth();
                    int imageHeight = emptyBitmap.getHeight();
                    // 减去间距
                    int height = (screenWidth - dividerWidth * SPACE_LINES) / SPAN_COUNT * imageHeight / imageWidth;
                    ViewGroup.LayoutParams para = iv_meizhi.getLayoutParams();
                    para.height = height;
                    para.width = (screenWidth - dividerWidth * SPACE_LINES) / SPAN_COUNT;
                    iv_meizhi.setImageBitmap(emptyBitmap);
                }
            }
        };

        AnimationAdapter animationAdapter = new SlideInBottomAnimationAdapter(adapter);
        animationAdapter.setFirstOnly(false);// 不只第一次有动画
        animationAdapter.setDuration(1200);
        animationAdapter.setInterpolator(new OvershootInterpolator(0.5f));

        // AnimationAdapter效果可以叠加
        //AnimationAdapter scale = new ScaleInAnimationAdapter(animationAdapter);
        animationAdapter.setFirstOnly(true);// 只有第一次有动画
        //animationAdapter.setDuration(800);

        rv_meizhi.setAdapter(animationAdapter);
        //rv_meizhi.setAdapter(adapter);


        // 刚进来开始加载
        swipe_refresh_layout.setRefreshing(true);
    }


    @Override
    public void getMeizhiSuc(List<Meizhi> meizhis) {

        // 获取到数据了
        int preSize = mMeizhis.size();
        if (page == START_PAGE) {
            // 是刷新操作，或者是第一次进来，要清空
            mMeizhis.clear();
            // 在item太短的情况下，不执行这步操作会闪退。
            adapter.notifyItemRangeRemoved(0, preSize);
            //adapter.notifyDataSetChanged();
            ToastTool.showShort(MeizhiActivity.this, "刷新成功");
        }

        if (meizhis != null && meizhis.size() > 0) {
            int start = mMeizhis.size();
            mMeizhis.addAll(meizhis);
            adapter.notifyItemRangeInserted(start, mMeizhis.size());
        } else {
            if (page > START_PAGE) {
                ToastTool.showShort(MeizhiActivity.this, "没有更多的数据啦");
                page--;
            }
        }

        if (mMeizhis.size() > 0) {
            // 有数据
            ll_empty.setVisibility(View.GONE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
        }

        // 刷新|加载的动作完成了
        swipe_refresh_layout.setRefreshing(false);
    }

    @Override
    public void getMeizhiFail(String msg) {
        // 刷新|加载的动作完成了
        swipe_refresh_layout.setRefreshing(false);
        ToastTool.showShort(this, "获取妹纸失败");

        // 第一页的数据拉取失败
        if (page < START_PAGE) {
            page = START_PAGE;
        }
        if (page == START_PAGE) {
            int preSize = mMeizhis.size();
            // 是刷新操作，或者是第一次进来，要清空
            mMeizhis.clear();
            // 在item太短的情况下，不执行这步操作会闪退。
            adapter.notifyItemRangeRemoved(0, preSize);
            //adapter.notifyDataSetChanged();
        } else {
            // 加载更多失败，页数回滚
            page--;
        }

        if (mMeizhis.size() > 0) {
            // 有数据
            ll_empty.setVisibility(View.GONE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        // 下拉刷新
        page = START_PAGE;
        presenter.getMeizhi(page);
    }
}
