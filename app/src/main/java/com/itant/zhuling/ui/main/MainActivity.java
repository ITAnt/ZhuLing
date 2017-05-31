package com.itant.zhuling.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itant.zhuling.R;
import com.itant.zhuling.adapter.HeadAdapter;
import com.itant.zhuling.application.ZhuManager;
import com.itant.zhuling.base.IPermission;
import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.event.music.MusicType;
import com.itant.zhuling.service.PlayService;
import com.itant.zhuling.tool.ActivityTool;
import com.itant.zhuling.tool.AppTool;
import com.itant.zhuling.tool.FileTool;
import com.itant.zhuling.tool.PermissionTool;
import com.itant.zhuling.tool.ServiceTool;
import com.itant.zhuling.tool.SocialTool;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.tool.UITool;
import com.itant.zhuling.tool.UriTool;
import com.itant.zhuling.tool.image.BitmapTool;
import com.itant.zhuling.ui.main.navigation.about.AboutActivity;
import com.itant.zhuling.ui.main.navigation.download.DownloadActivity;
import com.itant.zhuling.ui.main.navigation.feedback.FeedbackActivity;
import com.itant.zhuling.ui.main.navigation.more.MoreActivity;
import com.itant.zhuling.ui.main.navigation.notice.NoticeActivity;
import com.itant.zhuling.ui.main.tab.advanced.AdvancedFragment;
import com.itant.zhuling.ui.main.tab.music.MusicFragment;
import com.itant.zhuling.ui.main.tab.news.NewsFragment;
import com.itant.zhuling.ui.main.tab.sentence.SentenceFragment;
import com.itant.zhuling.ui.main.tab.writing.WritingFragment;
import com.itant.zhuling.widget.smarttab.v4.FragmentPagerItemAdapter;
import com.itant.zhuling.widget.smarttab.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zlc.season.rxdownload2.RxDownload;

import static com.itant.zhuling.constant.ZhuConstants.DIRECTORY_ROOT_FILE_MUSIC;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMenuItemClickListener, OnMenuItemLongClickListener, View.OnClickListener,
        MainContract.View, IPermission, SmartTabLayout.OnTabClickListener {

    private MainContract.Presenter presenter;

    private PlayingFragment mPlayFragment;
    private AppBarLayout abl_toolbar_container;
    private Toolbar tb_main;
    private int toolBarHeight;
    private LinearLayout ll_search;
    private EditText et_search;
    private ImageView iv_search;
    private ImageView iv_current_music;// 当前音乐标识

    // 权限
    private static final int REQUEST_NECESSARY_PERMISSIONS = 1;
    private static String[] PERMISSIONS_NECESSARY = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE
    };
    private CircleImageView civ_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppThemeGreen);+reCreate()即可实现换主题颜色
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, ZhuConstants.BMOB_APPLICATION_ID);
        initAppBarLayout();     // 顶部
        initNavigationView();   // 左侧
        initView();             // Tab和ViewPager
        initSearchBar();        // 初始化搜索栏
        initMenuFragment();     // 初始化右侧弹出菜单
        initUpdateInfo();       // 获取更新信息
    }

    private void initUpdateInfo() {
        // 获取更新信息
        presenter = new MainPresenter(this, this);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            // 初始化必要的目录
            initDirectory();
            // 直接获取更新信息
            presenter.getUpdateInfo();
        } else {
            // 有一些6.0以上的手机是不需要动态申请权限的，比如nubia，所以我们先检查是否有权限
            // 申请权限
            PermissionTool.initPermission(this, PERMISSIONS_NECESSARY, REQUEST_NECESSARY_PERMISSIONS);
        }

        // 初始化必要的目录
        try {
            initDirectory();
        } catch (Exception e) {
            e.printStackTrace();
            onPermissionFail(REQUEST_NECESSARY_PERMISSIONS);
        }

        // 获取更新信息
        presenter.getUpdateInfo();
    }

    /**
     * 顶部
     */
    private void initAppBarLayout() {
        tb_main = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(tb_main);
        toolBarHeight = UITool.getToolbarHeight(this);

        abl_toolbar_container = (AppBarLayout) findViewById(R.id.abl_toolbar_container);
        abl_toolbar_container.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (adapter != null && adapter.getCount() > 1) {
                    MusicFragment fragment = ((MusicFragment) adapter.getPage(1));
                    if (fragment == null) {
                        return;
                    }
                    if (Math.abs(verticalOffset) > toolBarHeight / 2) {
                        // AppBar被已经藏住了一半了，提示用户下拉有惊喜（下拉就可以看到搜索按钮）
                        fragment.setSearchVisible(false);
                    } else {
                        fragment.setSearchVisible(true);
                    }
                }
            }
        });
    }

    /**
     * 左侧导航
     */
    private void initNavigationView() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tb_main, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        View header = nav_view.getHeaderView(0);
        //View view=nav_view.inflateHeaderView(R.layout.nav_header_main);
        // 监听头像点击事件，实现换头像
        civ_head = (CircleImageView) header.findViewById(R.id.civ_head);
        // 如果本地有头像，就设置进去
        setHeadImage();
        civ_head.setOnClickListener(this);

        // 字体颜色
        int[][] state = new int[][] {
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] color = new int[] {
                Color.parseColor("#4caf50"),
                Color.parseColor("#4caf50"),
                Color.parseColor("#4caf50"),
                Color.parseColor("#4caf50")
        };
        ColorStateList colorStateList1 = new ColorStateList(state, color);

        // 图标颜色
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                Color.parseColor("#4caf50"),
                Color.parseColor("#4caf50"),
                Color.parseColor("#4caf50"),
                Color.parseColor("#4caf50")
        };
        ColorStateList colorStateList2 = new ColorStateList(states, colors);
        nav_view.setItemTextColor(colorStateList1);
        nav_view.setItemIconTintList(colorStateList2);
    }

    /******************************************** 右侧菜单开始 **********************************/
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.music_close);

        MenuObject xia = new MenuObject("小龙虾");
        xia.setResource(R.mipmap.music_xia);

        MenuObject qie = new MenuObject("鹅鹅鹅");
        qie.setResource(R.mipmap.music_qie);

        MenuObject yun = new MenuObject("风中云");
        yun.setResource(R.mipmap.music_yun);

        MenuObject wo = new MenuObject("酷不酷");
        wo.setResource(R.mipmap.music_wo);

        MenuObject xiong = new MenuObject("咆哮熊");
        xiong.setResource(R.mipmap.music_xiong);

        MenuObject gou = new MenuObject("道格狗");
        gou.setResource(R.mipmap.music_gou);

        MenuObject recommend = new MenuObject("特推荐");
        recommend.setResource(R.mipmap.music_recommend);

        menuObjects.add(close);
        menuObjects.add(xia);
        menuObjects.add(qie);
        menuObjects.add(yun);
        menuObjects.add(wo);
        menuObjects.add(xiong);
        menuObjects.add(gou);
        menuObjects.add(recommend);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        // 单击右侧菜单
        if (position == MusicType.MUSIC_TYPE_CLOSE) {
            return;
        }
        switch (position) {
            case MusicType.MUSIC_TYPE_XIA:
                iv_current_music.setImageResource(R.mipmap.music_xia_white);
                break;
            case MusicType.MUSIC_TYPE_QIE:
                //iv_current_music.setImageResource(R.mipmap.music_qie_white);
                ToastTool.showShort(this, "企鹅不听话了");
                return;
            case MusicType.MUSIC_TYPE_YUN:
                iv_current_music.setImageResource(R.mipmap.music_yun_white);
                break;
            case MusicType.MUSIC_TYPE_KU:
                iv_current_music.setImageResource(R.mipmap.music_wo_white);
                break;
            case MusicType.MUSIC_TYPE_XIONG:
                iv_current_music.setImageResource(R.mipmap.music_xiong_white);
                break;
            case MusicType.MUSIC_TYPE_GOU:
                iv_current_music.setImageResource(R.mipmap.music_gou_white);
                break;
            case MusicType.MUSIC_TYPE_JIAN:
                iv_current_music.setImageResource(R.mipmap.music_recommend_white);
                break;
        }

        if (adapter != null && adapter.getCount() > 1) {
            MusicFragment fragment = ((MusicFragment) adapter.getPage(1));
            if (fragment == null) {
                return;
            }

            fragment.getMusic(position, et_search.getText().toString());
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        // 长按右侧菜单

    }
    /**************************************** 右侧菜单结束*************************************************/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
        if (!ZhuManager.getInstance().isMusicPlaying()) {
            // 音乐不播放的话，杀掉进程，完全退出
            stopService(new Intent(this, PlayService.class));
        }
    }

    private SmartTabLayout stl_main;
    private FragmentPagerItemAdapter adapter;

    private void initView() {
        fragmentManager = getSupportFragmentManager();

        ViewPager vp_main = (ViewPager) findViewById(R.id.vp_main);
        stl_main = (SmartTabLayout) findViewById(R.id.stl_main);
        stl_main.setOnTabClickListener(this);

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(this)
                .add("资讯", NewsFragment.class)
                .add("悦听", MusicFragment.class)
                .add("美句", SentenceFragment.class)
                .add("书法", WritingFragment.class)
                //.add("开源", GithubFragment.class)
                //.add("博客", CsdnFragment.class)
                .add("高级", AdvancedFragment.class);

        /*if (PreferencesTool.getBoolean(this, "advanced")) {
            creator.add("高级", AdvancedFragment.class);
        }*/

        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), creator.create());

        vp_main.setAdapter(adapter);
        vp_main.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //currentTabPosition = position;

                if (position == 1) {
                    // 切换到音乐栏的时候，搜索按钮才可见
                    ll_search.setVisibility(View.VISIBLE);
                } else {
                    ll_search.setVisibility(View.GONE);
                }

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (position == 6) {
                        // 切换到高级模式了
                        if (Build.VERSION.SDK_INT >= 21) {
                            tb_main.setBackgroundColor(getResources().getColor(R.color.color_primary_red));
                            stl_main.setBackgroundColor(getResources().getColor(R.color.color_primary_red));
                            getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary_red_dark));
                        }
                    } else {
                        tb_main.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        stl_main.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                        //getWindow().setNavigationBarColor(getResources().getColor(R.color.color_primary_red));
                    }
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        stl_main.setViewPager(vp_main);
    }

    /**
     * 搜索栏
     */
    private void initSearchBar() {
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        et_search = (EditText) findViewById(R.id.et_search);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_current_music = (ImageView) findViewById(R.id.iv_current_music);
        iv_search.setOnClickListener(this);

        // 点击回车则搜索(onSearchClicked方法有隐藏键盘)
        et_search.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    // 搜索，防止执行两次。执行两次会导致右侧菜单添加两次，造成应用闪退
                    onSearchClicked();
                }
                return false;
            }
        });
    }

    /**
     * 点击软键盘的搜索
     */
    private void onSearchClicked() {
        // 收起软键盘并搜索
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); //强制隐藏键盘
        // 让EditText失去焦点
        et_search.clearFocus();
        preSearch();
    }

    private void preSearch() {
        String keyWords = et_search.getText().toString().replaceAll(" ", "");
        if (TextUtils.isEmpty(keyWords)) {
            ToastTool.showShort(this, "关键字不能为空");
            return;
        }

        if (!ZhuConstants.musicEnable) {
            ToastTool.showShort(this, "敬请期待");
            return;
        }

        if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
            // 弹出右侧菜单
            mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
        }
    }

    private long lastBackMillis;// 上一次点击返回的时间
    @Override
    public void onBackPressed() {

        // 销毁右侧菜单
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        }

        // 是否关闭抽屉
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }

        if (System.currentTimeMillis() - lastBackMillis < 2000) {
            super.onBackPressed();
        } else {
            ToastTool.showShort(this, "再按一次退出");
            lastBackMillis = System.currentTimeMillis();
        }
    }

    //private MenuItem menu_search;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 这里可以设置ToolBar的menu
        getMenuInflater().inflate(R.menu.main, menu);

        // 找到ToolBar的搜索控件
        //menu_search = menu.findItem(R.id.menu_search);
        //menu_search.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理ToolBar上的控件点击事件
        int id = item.getItemId();

        // return true应该就是展示那些由于空间不够而隐藏了的控件
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 处理左侧导航栏的点击事件
        switch (item.getItemId()) {
            case R.id.nav_notice:
                // 打开通知界面
                ActivityTool.startActivity(this, new Intent(this, NoticeActivity.class));
                break;

            case R.id.nav_play:
                // 打开正在播放
                showPlayingFragment();
                break;

            case R.id.nav_download:
                // 打开下载界面
                ActivityTool.startActivity(this, new Intent(this, DownloadActivity.class));
                break;

            case R.id.nav_about:
                // 打开关于界面
                ActivityTool.startActivity(this, new Intent(this, AboutActivity.class));
                break;

            case R.id.nav_more:
                // 打开更多界面
                ActivityTool.startActivity(this, new Intent(this, MoreActivity.class));
                break;

            case R.id.nav_comment:
                // 跳转到应用市场，评论应用
                SocialTool.jumpMarket(this);
                break;

            case R.id.nav_feedback:
                // 打开反馈界面
                ActivityTool.startActivity(this, new Intent(this, FeedbackActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionTool.onActivityPermissionResult(this, requestCode, grantResults);
    }

    @Override
    public void onPermissionSuccess(int requestCode) {
        ToastTool.showLong(this, "若不能正常加载请退出重新打开");
        // 重新启动Activity
        recreate();
    }

    @Override
    public void onPermissionFail(int requestCode) {

        if (requestCode == REQUEST_NECESSARY_PERMISSIONS) {
            // 没有赋予权限
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("权限被拒绝")
                    .setMessage("很抱歉，您拒绝了应用正常运行所需的权限，应用将退出。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            // 退出
                            System.exit(0);
                        }
                    }).create();
            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    /**
     * 初始化必须使用到的目录
     */
    private void initDirectory() {
        // 头像目录
        FileTool.initDirectory(Environment.getExternalStorageDirectory() + ZhuConstants.DIRECTORY_ROOT_FILE_IMAGES);
        // 缓存目录
        FileTool.initDirectory(Environment.getExternalStorageDirectory() + ZhuConstants.DIRECTORY_ROOT_CACHE);
        // 下载目录
        FileTool.initDirectory(Environment.getExternalStorageDirectory() + DIRECTORY_ROOT_FILE_MUSIC);
        RxDownload.getInstance(this)
                //.retrofit(myRetrofit)             //若需要自己的retrofit客户端,可在这里指定
                .defaultSavePath(Environment.getExternalStorageDirectory() + DIRECTORY_ROOT_FILE_MUSIC) //设置默认的下载路径
                .maxThread(3)                       //设置最大线程
                .maxRetryCount(3)                   //设置下载失败重试次数
                .maxDownloadNumber(5);              //Service同时下载数量
    }

    private AlertDialog headDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_head:
                // 选择头像
                AlertDialog.Builder builder = new AlertDialog.Builder(this);// setMessage会把item覆盖掉

                List<String> items = new ArrayList<>();
                items.add("拍照");
                items.add("相册");
                HeadAdapter adapter = new HeadAdapter(this, items);
                adapter.setClickListener(new HeadAdapter.OnHeadItemClickListener() {
                    @Override
                    public void onHeadItemClick(int position) {
                        if (headDialog != null) {
                            headDialog.dismiss();
                        }
                        switch (position) {
                            case 0:
                                // 拍照获取
                                pickImage(FROM_CAMERA);
                                break;
                            case 1:
                                // 相册选择
                                pickImage(FROM_ALBUMS);
                                break;
                        }
                    }
                });

                headDialog = builder.create();
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_head, null);

                ListView lv_head = (ListView) view.findViewById(R.id.lv_head);
                lv_head.setAdapter(adapter);

                headDialog.setView(view);
                headDialog.show();
                break;

            case R.id.iv_search:
                // 点击搜索，如果文字不为空则弹出右侧菜单，目前只能搜音乐
                preSearch();
                break;
        }
    }

    // 权限
    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_ALBUMS = 1;
    private static final int REQUEST_CODE_IMAGE_EDITED = 2;//裁剪相片
    private final int FROM_CAMERA = 1;// 拍照方式
    private final int FROM_ALBUMS = 2;// 相册方式
    /**
     * 根据不同方式选择图片设置ImageView
     */
    private void pickImage(int type) {
        File tempHeadFile = new File(Environment.getExternalStorageDirectory(), ZhuConstants.HEAD_FULL_NAME_TEMP);
        if (tempHeadFile.exists()) {
            // 删除缓存
            tempHeadFile.delete();
        }

        switch (type) {
            case FROM_ALBUMS:
                // 选择本地图片
                Intent intent = new Intent();
                intent.setType("image/*");//可选择图片视频
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //使用以上这种模式，并添加以上两句
                startActivityForResult(intent, REQUEST_CODE_ALBUMS);
                break;

            case FROM_CAMERA:
                // 拍照
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 在这里就设置输出路径，输出的图片大小会很大，所以和从相册选择一样，我们要编辑一下
                File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ZhuConstants.HEAD_FULL_NAME_TEMP);
                Uri tempUri = UriTool.getUriFromFile(this, ZhuConstants.NAME_PROVIDE, tempFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);

                //将存储图片的uri读写权限授权给相机应用
                PermissionTool.grantUriPermission(this, cameraIntent, tempUri);
                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            /*// 直接保存，不经压缩
            if(data.getData()==null){
                bitmap = (Bitmap)data.getExtras().get("data");
            }else{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            }*/
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    // 编辑一下拍到的图片，而不是直接保存，否则图片会很大
                    editCameraImage();
                    break;

                case REQUEST_CODE_ALBUMS:
                    // 编辑一下从本地选择的图片，以减少大小
                    editAlbumsImage(data);
                    break;

                case REQUEST_CODE_IMAGE_EDITED:
                    // 由于也编辑的时候设置了MediaStore.EXTRA_OUTPUT，会直接保存到我们的文件夹里，直接设置头像即可
                    setHeadImage();
                    break;
            }
        }
    }

    /**
     * 设置编辑头像的公用参数
     */
    private void setIntentParams(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    /**
     * 编辑从相册选择的图片，以压缩图片大小（仍然可以保持高质量），压缩速度也还不错
     * @param data
     */
    private void editAlbumsImage(Intent data) {
        Uri tempUri = data.getData();
        if (tempUri == null) {
            return;
        }

        // 编辑头像的数据来源于选择的图片
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(tempUri, "image/*");

        // 这里是个大坑！！！！！！！输出文件的URI必须是Uri.fromFile获取得到的，不管sdk版本为多少，这估计是谷歌开发者的一个bug
        File realFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ZhuConstants.HEAD_FULL_NAME);
        Uri realUri = Uri.fromFile(realFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri);

        //将存储图片的uri读写权限授权给剪裁工具应用，数据来源和出处都要授权
        PermissionTool.grantUriPermission(this, intent, tempUri);
        PermissionTool.grantUriPermission(this, intent, realUri);

        setIntentParams(intent);

        startActivityForResult(intent, REQUEST_CODE_IMAGE_EDITED);
    }

    /**
     * 编辑拍照的图片
     * 直接输出的图片大小会很大，所以和从相册选择一样，我们要编辑一下
     */
    private void editCameraImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");

        // 编辑头像的数据来源于temphead.jpeg缓存文件
        File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ZhuConstants.HEAD_FULL_NAME_TEMP);
        Uri tempUri = UriTool.getUriFromFile(this, ZhuConstants.NAME_PROVIDE, tempFile);
        intent.setDataAndType(tempUri, "image/*");

        // 这里是个大坑！！！！！！！不管sdk版本为多少，输出文件的URI必须是Uri.fromFile获取得到的，这估计是谷歌开发者的一个bug
        File realFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ZhuConstants.HEAD_FULL_NAME);
        Uri realUri = Uri.fromFile(realFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri);

        //将存储图片的uri读写权限授权给剪裁工具应用，数据来源和出处都要授权
        PermissionTool.grantUriPermission(this, intent, tempUri);
        PermissionTool.grantUriPermission(this, intent, realUri);

        setIntentParams(intent);

        startActivityForResult(intent, REQUEST_CODE_IMAGE_EDITED);
    }

    /**
     * 设置头像
     */
    private void setHeadImage() {
        File headFile  = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ZhuConstants.HEAD_FULL_NAME);
        if (!headFile.exists()) {
            return;
        }

        // bitmap不能直接设给ImageView，否则容易造成内存溢出,要经过压缩处理
        InputStream headStream = null;
        try {
            headStream = new FileInputStream(headFile);
            Bitmap compressedBitmap = BitmapTool.getBitmapFromStream(headStream);
            if (compressedBitmap != null) {
                civ_head.setImageBitmap(compressedBitmap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (headStream != null) {
                try {
                    headStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // 自定义字体需要
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onGetUpdateInfoSuc(UpdateInfo updateInfo) {
        // 获取更新信息成功
        if (updateInfo != null) {

            if (updateInfo.getAppDisable()) {
                // APP不能用了
                showAppDisableDialog();
                return;
            }

            ZhuConstants.musicEnable = !updateInfo.getMusicDisable();

            try {
                int serverVersionCode = Integer.parseInt(updateInfo.getVersionCode());
                if (AppTool.getVersionCode(this) >= serverVersionCode) {
                    return;
                }

                switch (updateInfo.getUpdateType()) {
                    case "1":
                        // 1强制更新，到应用市场
                        goMarketUpdate(true, updateInfo);
                        break;
                    case "2":
                        // 2强制更新，Bmob下载
                        break;
                    case "3":
                        // 3可选更新，到应用市场
                        goMarketUpdate(false, updateInfo);
                        break;
                    case "4":
                        // 4可选更新，Bmob下载
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * APP不能用了
     */
    private void showAppDisableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("应用下架")
                .setMessage("很抱歉，由于种种原因，竹翎迫于压力，暂停开放，开放日期未定，谢谢竹子们一直以来的支持。")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface updateDialog, int which) {
                        MainActivity.this.finish();
                    }
                });

        AlertDialog disableDialog = builder.create();
        disableDialog.setCancelable(false);
        disableDialog.setCanceledOnTouchOutside(false);
        disableDialog.show();
        Button positiveButton = disableDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        // 设置内容字体大小
        TextView tv_message = (TextView)disableDialog.getWindow().findViewById(android.R.id.message);
        tv_message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        // 设置内容字体颜色
        tv_message.setTextColor(getResources().getColor(R.color.gray_3));

        // 标题是粗体
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(disableDialog);

            Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);

            TextView tv_title = (TextView) mTitleView.get(alertController);
            if (tv_title != null) {
                tv_title.setTypeface(tv_title.getTypeface(), Typeface.BOLD);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新
     * @param force
     * @param updateInfo
     */
    private void goMarketUpdate(final boolean force, UpdateInfo updateInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("发现新版本")
                .setMessage("最新版本："+updateInfo.getVersionName() + "\r\n" + "新版大小："+updateInfo.getPackageSizeMB()+"M\r\n" + updateInfo.getUpdateDesc())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                if (force) {
                    // 对话框不能关闭
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setText("确定");
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    positiveButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            SocialTool.jumpMarket(MainActivity.this);
                        }
                    });

                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setVisibility(View.GONE);
                } else {
                    // 对话框可以关闭
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setText("更新");
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    positiveButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            SocialTool.jumpMarket(MainActivity.this);
                            dialog.dismiss();
                        }
                    });

                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setText("取消");
                    negativeButton.setTextColor(getResources().getColor(R.color.gray_1));
                    negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    negativeButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        // updateDialog一定要先show再findViewById(android.R.id.message)，否则会返回null
        updateDialog.show();

        // 设置内容字体大小
        TextView tv_message = (TextView)updateDialog.findViewById(android.R.id.message);
        if (tv_message != null) {
            tv_message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            // 设置内容字体颜色
            tv_message.setTextColor(getResources().getColor(R.color.txt_black));
        }

        // 标题是粗体
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(updateDialog);

            Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);

            TextView tv_title = (TextView) mTitleView.get(alertController);
            if (tv_title != null) {
                tv_title.setTypeface(tv_title.getTypeface(), Typeface.BOLD);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetUpdateInfoFail(String msg) {

    }

    /*点击其他地方隐藏软键盘*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (UITool.isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    et_search.clearFocus();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private int clickNewsTimes;
    private long lastClickNews;
    @Override
    public void onTabClicked(int position) {
        // 点击Tab
        switch (position) {
            case 0:
                if (System.currentTimeMillis() - lastClickNews > 2000) {
                    // 第一次点击
                    clickNewsTimes = 1;
                    lastClickNews = System.currentTimeMillis();
                } else {
                    clickNewsTimes++;
                }

                if (clickNewsTimes == 2 && adapter != null && adapter.getCount() > 0) {
                    // 双击资讯栏，资讯滚回顶端
                    NewsFragment fragment = ((NewsFragment) adapter.getPage(0));
                    if (fragment == null) {
                        return;
                    }

                    fragment.scrollToTop();
                }
                break;
        }
    }

    private boolean isPlayFragmentShow = false;

    /**
     * 点击进入播放音乐界面
     */
    public void showPlayingFragment() {
        if (ZhuManager.getInstance().getMusicService() == null) {
            ToastTool.showShort(this, "请稍候");
            ServiceTool.startMusicService(this);
            return;
        }
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayingFragment();
            // 播放界面监听音乐播放状态
            ZhuManager.getInstance().getMusicService().setPlayStateListener(mPlayFragment);
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
            mPlayFragment.onShowPlaying();
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;

        if (Build.VERSION.SDK_INT >= 21) {
            // 进入音乐播放界面，让状态栏变透明
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }
    }

    // 当前Tab位置，如果是高级模式，那么隐藏的时候，状态栏变红，其他变绿
    //private int currentTabPosition;
    public void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;

        // 退出音乐播放界面，让状态栏变回原来的颜色
        /*if (Build.VERSION.SDK_INT >= 21) {
            if (currentTabPosition == 6) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary_red_dark));
            } else {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }*/
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
