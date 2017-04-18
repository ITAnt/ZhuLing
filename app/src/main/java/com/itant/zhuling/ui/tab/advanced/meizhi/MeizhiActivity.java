package com.itant.zhuling.ui.tab.advanced.meizhi;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.itant.library.recyclerview.CommonAdapter;
import com.itant.library.recyclerview.base.ViewHolder;
import com.itant.zhuling.R;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.ui.base.BaseActivity;
import com.itant.zhuling.widget.recyclerview.SpacesItemDecoration2;
import com.itant.zhuling.widget.recyclerview.bottomlistener.OnRcvScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public class MeizhiActivity extends BaseActivity implements MeizhiContract.View, SwipeRefreshLayout.OnRefreshListener {
    private static final int START_PAGE = 1;

    private MeizhiContract.Presenter presenter;
    private RecyclerView rv_meizhi;
    private int page = START_PAGE;

    private SwipeRefreshLayout swipe_refresh_layout;
    private LinearLayout ll_empty;
    private StaggeredGridLayoutManager mLayoutManager;
    private int mLastVisibleItem;
    private List<Meizhi> mMeizhis;
    private CommonAdapter<Meizhi> adapter;

    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meizhi);

        setTitle("妹纸");

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        initView();

        presenter = new MeizhiPresenter(this, this);
        presenter.getMeizhi(page);
    }



    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);// 瀑布流
        rv_meizhi.setLayoutManager(mLayoutManager);
        SpacesItemDecoration2 decoration = new SpacesItemDecoration2(16);
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
            protected void convert(ViewHolder viewHolder, Meizhi item, final int position) {

                final ImageView iv_meizhi = viewHolder.getView(R.id.iv_meizhi);
                // ====图片错乱、重复、缓存
                //Glide.with(MeizhiActivity.this).load(item.getUrl()).into(iv_meizhi);

                Glide.with(MeizhiActivity.this).load(item.getUrl()).asBitmap().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        int imageWidth = resource.getWidth();
                        int imageHeight = resource.getHeight();
                        // 减去间距
                        int height = (screenWidth - 16 * 3) / 2 * imageHeight / imageWidth;
                        ViewGroup.LayoutParams para = iv_meizhi.getLayoutParams();
                        para.height = height;
                        para.width = screenWidth;
                        iv_meizhi.setImageBitmap(resource);
                    }
                });
            }
        };
        rv_meizhi.setAdapter(adapter);


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
        }

        if (meizhis != null && meizhis.size() > 0) {
            int start = mMeizhis.size();
            mMeizhis.addAll(meizhis);
            adapter.notifyItemRangeChanged(start, mMeizhis.size());
        } else {
            if (page > START_PAGE) {
                ToastTool.showShort(MeizhiActivity.this, "没有更多的数据啦");
                page--;
            }
        }

        if (mMeizhis.size() > 0) {
            // 有数据
            ll_empty.setVisibility(View.GONE);
            rv_meizhi.setVisibility(View.VISIBLE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
            rv_meizhi.setVisibility(View.GONE);
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
            ll_empty.setVisibility(View.VISIBLE);
            rv_meizhi.setVisibility(View.GONE);
        } else {
            // 加载更多失败，页数回滚
            ll_empty.setVisibility(View.GONE);
            rv_meizhi.setVisibility(View.VISIBLE);
            page--;
        }
    }

    @Override
    public void onRefresh() {
        // 下拉刷新
        page = START_PAGE;
        presenter.getMeizhi(page);
    }
}
