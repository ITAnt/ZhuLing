package com.itant.zhuling.ui.tab.music;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itant.library.recyclerview.CommonAdapter;
import com.itant.library.recyclerview.base.ViewHolder;
import com.itant.zhuling.R;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.ui.base.BaseFragment;
import com.itant.zhuling.ui.tab.news.detail.NewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * Created by Jason on 2017/3/26.
 */

public class MusicFragment extends BaseFragment implements MusicContract.View, SwipeRefreshLayout.OnRefreshListener {

    private int page;// 分页页码

    private MusicContract.Presenter mPresenter;

    private RecyclerView rv_music;
    private List<MusicBean> mMusicBeans;
    private CommonAdapter<MusicBean> mAdapter;

    private SwipeRefreshLayout swipe_refresh_layout;
    private LinearLayoutManager mLayoutManager;
    private int mLastVisibleItem;
    private LinearLayout ll_empty;

    private static final int START_PAGE = 0;

    @Override
    public int getLayoutId() {
        // 绑定视图
        return R.layout.fragment_music;
    }

    @Override
    public void initViews(View view) {
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);

        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        //设置下拉出现小圆圈是否是缩放出现，出现的位置，最大的下拉位置
        //swipe_refresh_layout.setProgressViewOffset(true, 150, 500);
        //设置下拉圆圈的大小，两个值 LARGE， DEFAULT
        swipe_refresh_layout.setSize(SwipeRefreshLayout.DEFAULT);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        swipe_refresh_layout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // 通过 setEnabled(false) 禁用下拉刷新
        swipe_refresh_layout.setEnabled(true);
        // 设定下拉圆圈的背景
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.white);
        // 第一次进来，为加载数据的状态
        swipe_refresh_layout.setRefreshing(true);
        swipe_refresh_layout.setOnRefreshListener(this);

        rv_music = (RecyclerView) view.findViewById(R.id.rv_music);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_music.setLayoutManager(mLayoutManager);
        //rv_music.setHasFixedSize(true);如果加了这一句，又运用了开源动画库的话，那么第一次加载RecyclerView没有内容，也没有动画
        // 在这里，无论是上拉加载更多还是下拉刷新我们用的都是SwipeRefreshLayout的加载动画，我们也很想集成强大的XRecyclerView和
        // LoadMoreRecyclerView，可是很遗憾，我两种都尝试过了，会和我们的AppBarLayout以及SwipeRefreshLayout有冲突，同学们可以
        // 尝试一下，我没有成功，或许是我的用法不对吧。
        rv_music.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        mLastVisibleItem + 1 == mAdapter.getItemCount() &&
                        !swipe_refresh_layout.isRefreshing()) {
                    // 如果到底部了，而且不是正在加载状态，就变为正在加载状态，并及时去加载数据
                    swipe_refresh_layout.setRefreshing(true);
                    // 加载更多
                    page++;
                    mPresenter.getMusic(page);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });


        mMusicBeans = new ArrayList<>();
        mAdapter = new CommonAdapter<MusicBean>(getActivity(), R.layout.item_news, mMusicBeans) {
            @Override
            protected void convert(final ViewHolder viewHolder, final MusicBean item, int position) {

                viewHolder.setText(R.id.news_summary_title_tv, item.getTitle());
                viewHolder.setText(R.id.news_summary_digest_tv, item.getDigest());
                viewHolder.setText(R.id.news_summary_ptime_tv, item.getPtime());

                // gif格式有时会导致整体图片不显示，貌似有冲突
                Glide.with(getActivity()).load(item.getImgsrc()).asBitmap()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.white)
                        .error(R.mipmap.ic_launcher_inner)
                        .into((ImageView) viewHolder.getView(R.id.news_summary_photo_iv));

                viewHolder.setOnClickListener(R.id.news_summary_photo_iv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 共享元素动画
                        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                        intent.putExtra("url_top", item.getImgsrc());

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            // 可以实现共享动画
                            View sharedView = viewHolder.getView(R.id.news_summary_photo_iv);
                            String transitionName = getString(R.string.transition_name_news_to_detail);
                            ActivityOptions transitionActivityOptions = null;
                            transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);
                            startActivity(intent, transitionActivityOptions.toBundle());
                        } else {
                            startActivity(intent);
                        }
                    }
                });
            }
        };


        AnimationAdapter animationAdapter = new SlideInBottomAnimationAdapter(mAdapter);
        animationAdapter.setFirstOnly(true);// 只有第一次有动画
        animationAdapter.setDuration(1000);
        animationAdapter.setInterpolator(new OvershootInterpolator(0.5f));

        // AnimationAdapter效果可以叠加
        //AnimationAdapter scale = new ScaleInAnimationAdapter(animationAdapter);
        //animationAdapter.setFirstOnly(false);// 不只第一次有动画
        //animationAdapter.setDuration(800);

        //rv_music.setAdapter(animationAdapter);
        // 我们已经在MultiItemTypeAdapter使用了item的动画，这里就不使用炫酷的Adapter动画了
        rv_music.setAdapter(mAdapter);

        mPresenter = new MusicPresenter(getActivity(), this);
        mPresenter.getMusic(page);
    }

    @Override
    public void onGetMusicSuc(List<MusicBean> beans) {
        int preSize = mMusicBeans.size();
        if (page == START_PAGE) {
            // 是刷新操作，或者是第一次进来，要清空
            mMusicBeans.clear();
            // 在item太短的情况下，不执行这步操作会闪退。
            mAdapter.notifyItemRangeRemoved(0, preSize);
        }

        if (beans != null && beans.size() > 0) {
            // 获取到数据了
            int start = mMusicBeans.size();
            mMusicBeans.addAll(beans);
            mAdapter.notifyItemRangeChanged(start, mMusicBeans.size());
        } else {
            if (page > START_PAGE) {
                ToastTool.showShort(getActivity(), "没有更多的数据啦");
                page--;
            }
        }

        if (mMusicBeans.size() > 0) {
            // 有数据
            ll_empty.setVisibility(View.GONE);
            rv_music.setVisibility(View.VISIBLE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
            rv_music.setVisibility(View.GONE);
        }

        // 刷新|加载的动作完成了
        swipe_refresh_layout.setRefreshing(false);
    }

    @Override
    public void onGetMusicFail(String msg) {
        // 刷新|加载的动作完成了
        swipe_refresh_layout.setRefreshing(false);


        // 第一页的数据拉取失败
        if (page < START_PAGE) {
            page = START_PAGE;
        }
        if (page == START_PAGE) {
            ll_empty.setVisibility(View.VISIBLE);
            rv_music.setVisibility(View.GONE);
        } else {
            // 加载更多失败，页数回滚
            ll_empty.setVisibility(View.GONE);
            rv_music.setVisibility(View.VISIBLE);
            page--;
        }
    }

    @Override
    public void onRefresh() {
        // 下拉刷新
        page = START_PAGE;
        mPresenter.getMusic(page);
    }

    // 搜索按钮是否可见
    private boolean isSearchVisible;

    public void setSearchVisible(boolean searchVisible) {
        isSearchVisible = searchVisible;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isSearchVisible) {
            // 当前是音乐栏并且搜索按钮不可见，则提示用户下拉
            ToastTool.showShort(getActivity(), "下拉有惊喜哦");
        }
    }
}
