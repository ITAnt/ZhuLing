package com.itant.zhuling.ui.main.navigation.download;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.itant.library.recyclerview.CommonAdapter;
import com.itant.library.recyclerview.base.ViewHolder;
import com.itant.zhuling.R;
import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.PermissionTool;
import com.itant.zhuling.tool.SocialTool;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.tool.UriTool;
import com.itant.zhuling.ui.base.BaseSwipeActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadBean;
import zlc.season.rxdownload2.entity.DownloadEvent;
import zlc.season.rxdownload2.entity.DownloadFlag;
import zlc.season.rxdownload2.entity.DownloadRecord;

/**
 * Created by iTant on 2017/3/26.
 * 下载界面
 */

public class DownloadActivity extends BaseSwipeActivity implements View.OnClickListener {
    private LinearLayout ll_empty;
    private RxDownload mRxDownload;
    private List<Disposable> disposables;
    private List<DownloadRecord> mMusicBeans;
    private CommonAdapter<DownloadRecord> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_download);
        // 右划关闭
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        setTitle("下载");

        initView();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
        findViewById(R.id.tv_music_dir).setOnClickListener(this);

        mRxDownload = RxDownload.getInstance(this);

        RecyclerView rv_download = (RecyclerView) findViewById(R.id.rv_download);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_download.setLayoutManager(mLayoutManager);

        mMusicBeans = new ArrayList<>();
        mAdapter = new CommonAdapter<DownloadRecord>(this, R.layout.item_music_download, mMusicBeans) {
            @Override
            protected void convert(final ViewHolder viewHolder, final DownloadRecord music, final int position) {
                viewHolder.setOnClickListener(R.id.ll_more, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 点击更多
                        selectActionDialog(position);
                    }
                });
                viewHolder.setText(R.id.tv_name, music.getExtra1());
                viewHolder.setText(R.id.tv_singer, music.getExtra2());
                viewHolder.setText(R.id.tv_bitrate, music.getExtra3());
                NumberProgressBar progressBar = viewHolder.getView(R.id.number_progress_bar);
                try {
                    progressBar.setProgress((int)music.getStatus().getPercentNumber());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        rv_download.setAdapter(mAdapter);

        // 获取所有的下载任务
        mRxDownload.getTotalDownloadRecords().subscribe(new Consumer<List<DownloadRecord>>() {
            @Override
            public void accept(@NonNull List<DownloadRecord> downloadRecords) throws Exception {
                mMusicBeans.addAll(downloadRecords);
                Collections.reverse(mMusicBeans);
                mAdapter.notifyDataSetChanged();

                if (mMusicBeans.size() == 0) {
                    ll_empty.setVisibility(View.VISIBLE);
                } else {
                    ll_empty.setVisibility(View.GONE);
                }
                onProgressChanged();
            }
        });
    }

    /**
     * 监听下载进度
     */
    private void onProgressChanged() {
        // 接收事件可以在任何地方接收，不管该任务是否开始下载均可接收.
        disposables = new ArrayList<>();
        for (int i = 0, j = mMusicBeans.size(); i < j; i++) {
            final DownloadRecord record = mMusicBeans.get(i);
            if (record.getFlag() != DownloadFlag.COMPLETED) {
                // 只监听未完成的
                final int finalI = i;
                Disposable disposable =  mRxDownload.receiveDownloadStatus(record.getUrl())
                        .subscribe(new Consumer<DownloadEvent>() {
                            @Override
                            public void accept(DownloadEvent event) throws Exception {
                                //当事件为Failed时, 才会有异常信息, 其余时候为null.
                                if (event.getFlag() == DownloadFlag.FAILED) {

                                }

                                // 更新下载进度
                                record.setStatus(event.getDownloadStatus());
                                mAdapter.notifyItemChanged(finalI);
                            }
                        });
                disposables.add(disposable);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (disposables == null) {
            return;
        }

        for (Disposable disposable : disposables) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    /**
     * 点击更多，选择继续进行的操作
     */
    private AlertDialog musicActionDialog;
    private void selectActionDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DownloadRecord record = mMusicBeans.get(position);

        musicActionDialog = builder.create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_music_download_action, null);
        // 播放
        view.findViewById(R.id.ll_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }

                //利用url获取
                File[] files = mRxDownload.getRealFiles(record.getUrl());
                if (files != null) {
                    File file = files[0];
                    if (file == null || file.length() <= 0) {
                        ToastTool.showShort(getApplicationContext(), "请等待下载完成");
                        return;
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri musicUri = UriTool.getUriFromFile(DownloadActivity.this, ZhuConstants.NAME_PROVIDE, file);
                        intent.setDataAndType(musicUri, "audio/MP3");
                        //将存储图片的uri读写权限授权给相机应用
                        PermissionTool.grantUriPermission(DownloadActivity.this, intent, musicUri);
                        startActivity(intent);
                    } catch (Exception e) {
                        ToastTool.showShort(getApplicationContext(), "找不到文件或没有相关播放器");
                    }
                }
            }
        });

        // 暂停下载
        view.findViewById(R.id.ll_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }

                mRxDownload.pauseServiceDownload(record.getUrl()).subscribe();
            }
        });

        // 继续下载
        view.findViewById(R.id.ll_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }

                //再次调用下载方法并传入相同的url即可继续下载(并不行)
                //mRxDownload.serviceDownload(record.getUrl());
                DownloadBean bean = new DownloadBean();
                //bean.setSavePath(Environment.getExternalStorageDirectory() + ZhuConstants.DIRECTORY_ROOT_FILE_MUSIC);
                bean.setUrl(record.getUrl());

                bean.setSaveName(record.getSaveName());
                bean.setExtra1(record.getExtra1());
                bean.setExtra2(record.getExtra2());
                bean.setExtra3(record.getExtra3());
                // 本应用下载
                RxDownload.getInstance(DownloadActivity.this)
                        .serviceDownload(bean)   // 添加一个下载任务
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                ToastTool.showShort(DownloadActivity.this, "继续下载" + "《" + record.getExtra1() + "》");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ToastTool.showShort(DownloadActivity.this, "不支持断点续传");
                            }
                        });

                // 获取所有的下载任务
                onProgressChanged();
            }
        });

        // 仅删除记录
        view.findViewById(R.id.ll_delete_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }

                //暂停地址为url的下载并从数据库中删除记录，deleteFile为true会同时删除该url下载产生的所有文件
                mRxDownload.deleteServiceDownload(record.getUrl(), false).subscribe();
                mMusicBeans.remove(position);
                mAdapter.notifyItemRemoved(position);
                // 如果移除的不是最后一个则要刷新，不刷会闪退
                if (position != mMusicBeans.size()) {
                    mAdapter.notifyItemRangeChanged(position, mMusicBeans.size() - position);
                }

                if (mMusicBeans.size() == 0) {
                    ll_empty.setVisibility(View.VISIBLE);
                } else {
                    ll_empty.setVisibility(View.GONE);
                }
            }
        });

        // 删除记录和文件
        view.findViewById(R.id.ll_delete_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }

                //暂停地址为url的下载并从数据库中删除记录，deleteFile为true会同时删除该url下载产生的所有文件
                mRxDownload.deleteServiceDownload(record.getUrl(), true).subscribe();
                mMusicBeans.remove(position);
                mAdapter.notifyItemRemoved(position);
                // 如果移除的不是最后一个则要刷新，不刷会闪退
                if (position != mMusicBeans.size()) {
                    mAdapter.notifyItemRangeChanged(position, mMusicBeans.size() - position);
                }

                if (mMusicBeans.size() == 0) {
                    ll_empty.setVisibility(View.VISIBLE);
                } else {
                    ll_empty.setVisibility(View.GONE);
                }
            }
        });

        // 分享
        view.findViewById(R.id.ll_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicActionDialog != null) {
                    musicActionDialog.dismiss();
                }

                SocialTool.shareApp(DownloadActivity.this, "分享音乐", "我发现了一首非常好听的歌曲，你也听听吧！"
                        + "《" + record.getSaveName() + "》" + record.getUrl());
            }
        });

        musicActionDialog.setView(view);
        musicActionDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_music_dir:
                Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + ZhuConstants.DIRECTORY_ROOT_FILE_MUSIC);
                //Uri selectedUri = UriTool.getUriFromFile(this, ZhuConstants.NAME_PROVIDE, new File(Environment.getExternalStorageDirectory() + ZhuConstants.DIRECTORY_ROOT_FILE_MUSIC));

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedUri, "*/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastTool.showShort(this, "没找到相关文件浏览器");
                }

                /*Intent chooser = Intent.createChooser(intent, "选择打开方式");
                if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                    startActivity(chooser);
                } else {
                    // if you reach this place, it means there is no any file
                    // explorer app installed on your device
                    ToastTool.showShort(this, "没找到相关文件浏览器");
                }*/
                break;
        }
    }
}
