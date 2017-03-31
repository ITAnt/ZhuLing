package com.itant.zhuling.ui.fragment.music;

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
import com.itant.zhuling.utils.UIUtils;
import com.itant.library.recyclerview.CommonAdapter;
import com.itant.library.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2017/3/26.
 */

public class MusicFragment extends BaseFragment implements MusicContract.View {

    private MusicContract.Presenter mPresenter;

    private RecyclerView rv_repo;
    private List<MusicBean> mMusicBeen;
    private CommonAdapter<MusicBean> mAdapter;

    @Override
    public int getLayoutId() {
        // 绑定视图
        return R.layout.fragment_music;
    }

    @Override
    public void initViews(View view) {
        rv_repo = (RecyclerView) view.findViewById(R.id.rv_repo);

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
        mAdapter = new CommonAdapter<MusicBean>(getActivity(), R.layout.item_news, mMusicBeen) {
            @Override
            protected void convert(final ViewHolder viewHolder, final MusicBean item, int position) {

                viewHolder.setText(R.id.news_summary_title_tv, item.getTitle());
                viewHolder.setText(R.id.news_summary_digest_tv, item.getDigest());
                viewHolder.setText(R.id.news_summary_ptime_tv, item.getPtime());

                Glide.with(getActivity()).load(item.getImgsrc()).asBitmap() // gif格式有时会导致整体图片不显示，貌似有冲突
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.white)
                        .error(R.drawable.ic_launcher_inner)
                        .into((ImageView) viewHolder.getView(R.id.news_summary_photo_iv));
            }
        };

        rv_repo.setAdapter(mAdapter);

        mPresenter = new MusicPresenter(getActivity(), this);
        mPresenter.getRepo();
    }

    @Override
    public void onGetRepoSucc(List<MusicBean> repoBeens) {
        if (repoBeens != null && repoBeens.size() > 0) {
            // 获取到数据了
            mMusicBeen.addAll(repoBeens);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetRepoFail() {

    }
}
