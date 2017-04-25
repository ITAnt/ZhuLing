package com.itant.zhuling.ui.main.tab.music;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.itant.library.recyclerview.CommonAdapter;
import com.itant.library.recyclerview.base.ViewHolder;
import com.itant.zhuling.R;
import com.itant.zhuling.application.ZhuManager;
import com.itant.zhuling.event.music.MusicEvent;
import com.itant.zhuling.event.music.MusicType;
import com.itant.zhuling.listener.NetStateOnClickListener;
import com.itant.zhuling.tool.SocialTool;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.ui.base.BaseFragment;
import com.itant.zhuling.ui.main.MainActivity;
import com.itant.zhuling.ui.main.tab.music.bean.Music;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iTant on 2017/3/26.
 */

public class MusicFragment extends BaseFragment implements MusicContract.View, SwipeRefreshLayout.OnRefreshListener {
    private int page;// 分页页码
    private static final int START_PAGE = 0;

    private MusicContract.Presenter mPresenter;

    private RecyclerView rv_music;
    private List<Music> mMusicBeans;
    private CommonAdapter<Music> mAdapter;

    private SwipeRefreshLayout swipe_refresh_layout;
    private LinearLayoutManager mLayoutManager;
    private int mLastVisibleItem;
    private LinearLayout ll_empty;

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
                    mPresenter.getMusic(position, keywords, page);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        mMusicBeans = new ArrayList<>();
        mAdapter = new CommonAdapter<Music>(getActivity(), R.layout.item_music_search, mMusicBeans) {
            @Override
            protected void convert(final ViewHolder viewHolder, final Music music, final int position) {
                if (music.isPlaying()) {
                    viewHolder.setBackgroundColor(R.id.ll_music, getResources().getColor(R.color.gray_slight));
                } else {
                    viewHolder.setBackgroundColor(R.id.ll_music, getResources().getColor(R.color.colorBg));
                }
                viewHolder.setText(R.id.tv_name, music.getName());
                viewHolder.setText(R.id.tv_singer, music.getSinger() + (TextUtils.isEmpty(music.getAlbum()) ? "" : " 《" + music.getAlbum() + "》"));
                viewHolder.setText(R.id.tv_bitrate, music.getBitrate());
                viewHolder.setOnClickListener(R.id.ll_music, new NetStateOnClickListener(getActivity()) {
                    @Override
                    protected void onContinueAction() {
                        super.onContinueAction();
                        // 不管流量，继续
                        goPlayMusic(position);
                    }
                });
                viewHolder.setOnClickListener(R.id.ll_more, new NetStateOnClickListener(getActivity()){
                    @Override
                    protected void onContinueAction() {
                        super.onContinueAction();
                        // 弹出对话框
                        selectActionDialog(position);
                    }
                });
            }
        };

        rv_music.setAdapter(mAdapter);

        mPresenter = new MusicPresenter(getActivity(), this);
        mPresenter.getMusic(position, "推荐", page);
    }

    // 当前正在播放的音乐位置
    private int lastPlayPosition = -1;
    /**
     * 开始播放音乐
     */
    private void goPlayMusic(int position) {
        // 上次播放的音乐恢复正常状态
        if (lastPlayPosition != -1) {
            mMusicBeans.get(lastPlayPosition).setPlaying(false);
            mAdapter.notifyItemChanged(lastPlayPosition);
        }

        // 当前正在播放的音乐为高亮状态
        Music music = mMusicBeans.get(position);
        music.setPlaying(true);
        lastPlayPosition = position;
        mAdapter.notifyItemChanged(lastPlayPosition);

        MainActivity activity = (MainActivity) getActivity();
        switch (MusicFragment.this.position) {
            case MusicType.MUSIC_TYPE_GOU:
                // 小狗的下载链接要另外获取
                onMusicAction(MusicType.MUSIC_TYPE_GOU, activity, music, MusicEvent.MUSIC_EVENT_PLAY);
                break;

            case MusicType.MUSIC_TYPE_KU:
                onMusicAction(MusicType.MUSIC_TYPE_KU, activity, music, MusicEvent.MUSIC_EVENT_PLAY);
                break;
            default:
                // 点击进入播放音乐界面
                ZhuManager.getInstance().getMusicService().play(music);
                activity.showPlayingFragment();
                break;
        }
    }

    /**
     * 获取真正下载地址
     * @param activity
     * @param music
     */
    private void onMusicAction(final int type, final MainActivity activity, final Music music, final int action) {
        org.xutils.http.RequestParams params = new org.xutils.http.RequestParams(music.getMp3Url());
        params.setCancelFast(true);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                switch (type) {
                    case MusicType.MUSIC_TYPE_GOU:

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject == null) {
                            ToastTool.showShort(getActivity(), "没有相应的下载地址");
                            return;
                        }

                        String url = null;
                        try {
                            url = jsonObject.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(url)) {
                            music.setMp3Url(url);
                            ToastTool.showShort(getActivity(), "没有找到下载地址");
                            return;
                        }

                        if (action == MusicEvent.MUSIC_EVENT_PLAY) {
                            // 点击进入播放音乐界面
                            ZhuManager.getInstance().getMusicService().play(music);
                            activity.showPlayingFragment();
                        }
                        break;

                    case MusicType.MUSIC_TYPE_KU:
                        music.setMp3Url(result.trim().replaceAll(" ", ""));
                        if (action == MusicEvent.MUSIC_EVENT_PLAY) {
                            // 点击进入播放音乐界面
                            ZhuManager.getInstance().getMusicService().play(music);
                            activity.showPlayingFragment();
                        }
                        break;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastTool.showShort(getActivity(), "这首歌不能下载了");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 点击更多，选择继续进行的操作
     */
    private AlertDialog musicActionDialog;
    private void selectActionDialog(final int position) {
        final Music music = mMusicBeans.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // setMessage会把item覆盖掉

        musicActionDialog = builder.create();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_music_action, null);
        // 播放
        view.findViewById(R.id.ll_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPlayMusic(position);
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }
            }
        });

        // 复制
        view.findViewById(R.id.ll_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("music", music.getMp3Url());
                manager.setPrimaryClip(clipData);
                ToastTool.showShort(getActivity(), "已复制到剪贴板");

                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }
            }
        });

        // 下载
        view.findViewById(R.id.ll_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialTool.downloadMusic(getActivity(), music.getMp3Url());
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }
            }
        });

        // 分享
        view.findViewById(R.id.ll_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialTool.shareApp(getActivity(), "分享音乐", "我发现了一首非常好听的歌曲，你也听听吧！"
                        + "《" + music.getName() + "》" + music.getMp3Url());
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }
            }
        });

        musicActionDialog.setView(view);
        musicActionDialog.show();
    }

    @Override
    public void onRefresh() {
        // 下拉刷新
        page = START_PAGE;
        mPresenter.getMusic(position, keywords, page);
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

    private int position = MusicType.MUSIC_TYPE_JIAN;// 默认是推荐
    private String keywords;
    /**
     * @param position //0关 1虾 2鹅 3云 4酷 5熊 6狗 7推荐
     * @param keywords
     */
    public void getMusic(int position, String keywords) {
        if (mPresenter == null) {
            return;
        }

        this.position = position;
        this.keywords = keywords;
        swipe_refresh_layout.setRefreshing(true);
        // 每次点击都是去拉第一页
        int preSize = mMusicBeans.size();
        // 是刷新操作，或者是第一次进来，要清空
        mMusicBeans.clear();
        // 在item太短的情况下，不执行这步操作会闪退。
        mAdapter.notifyItemRangeRemoved(0, preSize);
        //mAdapter.notifyDataSetChanged();
        mPresenter.getMusic(position, keywords, START_PAGE);
    }

    @Override
    public void onGetMusicSuc(List<Music> beans) {
        int preSize = mMusicBeans.size();
        if (page == START_PAGE) {
            // 是刷新操作，或者是第一次进来，要清空
            mMusicBeans.clear();
            // 在item太短的情况下，不执行这步操作会闪退。
            mAdapter.notifyItemRangeRemoved(0, preSize);// 用这一句会造成item重叠可能与item动画有关，这是recyclerview的一个bug
            //mAdapter.notifyDataSetChanged();
            ToastTool.showShort(getActivity(), "刷新成功");
        }

        if (beans != null && beans.size() > 0) {
            // 获取到数据了
            int start = mMusicBeans.size();
            mMusicBeans.addAll(beans);
            mAdapter.notifyItemRangeInserted(start, mMusicBeans.size());
        } else {
            if (page > START_PAGE) {
                ToastTool.showShort(getActivity(), "没有更多的数据啦");
                page--;
            }
        }

        if (mMusicBeans.size() > 0) {
            // 有数据
            ll_empty.setVisibility(View.GONE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
        }

        // 刷新|加载的动作完成了
        swipe_refresh_layout.setRefreshing(false);

        lastPlayPosition = -1;
    }

    @Override
    public void onGetMusicFail(String msg) {
        // 第一页的数据拉取失败
        if (page < START_PAGE) {
            page = START_PAGE;
        }
        if (page == START_PAGE) {
            int preSize = mMusicBeans.size();
            // 是刷新操作，或者是第一次进来，要清空
            mMusicBeans.clear();
            // 在item太短的情况下，不执行这步操作会闪退。
            mAdapter.notifyItemRangeRemoved(0, preSize);
            //mAdapter.notifyDataSetChanged();
        } else {
            // 加载更多失败，页数回滚
            page--;
        }

        if (mMusicBeans.size() > 0) {
            // 有数据
            ll_empty.setVisibility(View.GONE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
        }

        // 刷新|加载的动作完成了
        swipe_refresh_layout.setRefreshing(false);

        lastPlayPosition = -1;
    }
}
