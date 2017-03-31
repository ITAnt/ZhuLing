package com.itant.zhuling.ui.fragment.news;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itant.zhuling.R;
import com.itant.zhuling.event.AppEvent;
import com.itant.zhuling.event.ToolBarMoveEvent;
import com.itant.zhuling.listener.HidingScrollListener;
import com.itant.zhuling.ui.base.BaseFragment;
import com.itant.zhuling.ui.fragment.news.detail.NewsDetailActivity;
import com.itant.zhuling.utils.UIUtils;
import com.itant.library.recyclerview.CommonAdapter;
import com.itant.library.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public class NewsFragment extends BaseFragment implements NewsContract.View {

    private NewsContract.Presenter mPresenter;

    private RecyclerView rv_repo;
    private List<NewsBean> mMusicBeen;
    private CommonAdapter<NewsBean> mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public int getLayoutId() {
        // 绑定视图
        return R.layout.fragment_news;
    }

    @Override
    public void initViews(View view) {

        rv_repo = (RecyclerView) view.findViewById(R.id.rv_repo);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        //设置下拉出现小圆圈是否是缩放出现，出现的位置，最大的下拉位置
        mSwipeRefreshLayout.setProgressViewOffset(true, 150, 500);

        //设置下拉圆圈的大小，两个值 LARGE， DEFAULT
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);

        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // 通过 setEnabled(false) 禁用下拉刷新
        mSwipeRefreshLayout.setEnabled(true);

        // 设定下拉圆圈的背景
        mSwipeRefreshLayout.setProgressBackgroundColor(R.color.red);


        // 为了上拉隐藏ToolBar而计算
        int paddingTop = UIUtils.getToolbarHeight(getActivity()) + UIUtils.getTabsHeight(getActivity());
        //rv_repo.setPadding(rv_repo.getPaddingLeft(), paddingTop, rv_repo.getPaddingRight(), rv_repo.getPaddingBottom());

        rv_repo.setHasFixedSize(true);
        rv_repo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_repo.setItemAnimator(new DefaultItemAnimator());

        rv_repo.addOnScrollListener(new HidingScrollListener(getActivity()) {

            // 监听加载更多
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                // 加载更多
//                if (!mIsAllLoaded && visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE&& lastVisibleItemPosition >= totalItemCount - 1) {
//                    mNewsListPresenter.loadMore();
//                    mNewsListAdapter.showFooter();
//                    rv_repo.scrollToPosition(mNewsListAdapter.getItemCount() - 1);
//                }
            }

            // 以下3个方法监听滑动以隐藏或显示ToolBar
            @Override
            public void onMoved(int distance) {
                EventBus.getDefault().post(new ToolBarMoveEvent(distance));
            }

            @Override
            public void onShow() {
                EventBus.getDefault().post(AppEvent.EVENT_SHOW_TOOL_BAR);
            }

            @Override
            public void onHide() {
                EventBus.getDefault().post(AppEvent.EVENT_HIDE_TOOL_BAR);
            }
        });


        mMusicBeen = new ArrayList<>();
        mAdapter = new CommonAdapter<NewsBean>(getActivity(), R.layout.item_news, mMusicBeen) {
            @Override
            protected void convert(final ViewHolder viewHolder, final NewsBean item, int position) {

                viewHolder.setText(R.id.news_summary_title_tv, item.getTitle());
                viewHolder.setText(R.id.news_summary_digest_tv, item.getDigest());
                viewHolder.setText(R.id.news_summary_ptime_tv, item.getPtime());

                Glide.with(getActivity()).load(item.getImgsrc()).asBitmap() // gif格式有时会导致整体图片不显示，貌似有冲突
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.white)
                        .error(R.drawable.ic_launcher_inner)
                        .into((ImageView) viewHolder.getView(R.id.news_summary_photo_iv));

                viewHolder.setOnClickListener(R.id.news_summary_photo_iv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 共享元素动画
                        Intent i = new Intent(getActivity(), NewsDetailActivity.class);
                        i.putExtra("url_top", item.getImgsrc());
                        View sharedView = viewHolder.getView(R.id.news_summary_photo_iv);
                        String transitionName = getString(R.string.transition_name_news_to_detail);

                        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);
                        startActivity(i, transitionActivityOptions.toBundle());
                    }
                });
            }
        };

        rv_repo.setAdapter(mAdapter);

        mPresenter = new NewsPresenter(getActivity(), this);
        mPresenter.getRepo();
    }

    @Override
    public void onGetRepoSucc(List<NewsBean> repoBeens) {
        if (repoBeens != null && repoBeens.size() > 0) {
            // 获取到数据了
            mMusicBeen.addAll(repoBeens);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetRepoFail() {

    }

    public void setRefresh(boolean enable) {
        //让刷新状态随TooBar位置而改变
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);

            if (!enable) {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
    }
}
