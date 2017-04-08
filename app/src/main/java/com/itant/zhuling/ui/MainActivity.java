package com.itant.zhuling.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.itant.zhuling.R;
import com.itant.zhuling.tools.PreferencesTool;
import com.itant.zhuling.tools.ToastTool;
import com.itant.zhuling.tools.UITool;
import com.itant.zhuling.ui.navigation.SettingActivity;
import com.itant.zhuling.ui.tab.csdn.CsdnFragment;
import com.itant.zhuling.ui.tab.github.GithubFragment;
import com.itant.zhuling.ui.tab.music.MusicFragment;
import com.itant.zhuling.ui.tab.news.NewsFragment;
import com.itant.zhuling.widgets.smarttab.v4.FragmentPagerItemAdapter;
import com.itant.zhuling.widgets.smarttab.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    private AppBarLayout abl_toolbar_container;
    private Toolbar tb_main;
    private int toolBarHeight;

    // 权限
    private static final int REQUEST_PERMISSION = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppThemeGreen);即可实现换主题颜色

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 系统默认生成的代码-----------------------------------------------------------开始
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
                        // 已经藏住了一半了，提示用户
                        fragment.setSearchVisible(false);
                    } else {
                        fragment.setSearchVisible(true);
                    }
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tb_main, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // 系统默认生成的代码-------------------------------------------------------------------结束

        // 初始化顶部界面
        initView();

        // 初始化右侧弹出菜单
        initMenuFragment();

        // 申请权限
        initPermission();
    }

    /**
     * 初始化权限
     */
    private void initPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            boolean isGranted = true;
            for (String permission : PERMISSIONS) {
                int result = ActivityCompat.checkSelfPermission(this, permission);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }

            if (!isGranted) {
                // 还没有的话，去申请权限
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
            }
        }
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
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.icn_close);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.mipmap.icn_1);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.icn_2);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.mipmap.icn_3));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.mipmap.icn_4);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.mipmap.icn_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        // 单击右侧菜单
        switch (clickedView.getId()) {

        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        // 长按右侧菜单

    }

    /**************************************** 右侧菜单结束*************************************************/

    private ViewPager vp_main;
    private SmartTabLayout stl_main;
    private FragmentPagerItemAdapter adapter;

    private void initView() {


        fragmentManager = getSupportFragmentManager();


        vp_main = (ViewPager) findViewById(R.id.vp_main);
        stl_main = (SmartTabLayout) findViewById(R.id.stl_main);


        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("资讯", NewsFragment.class)
                .add("悦听", MusicFragment.class)
                .add("妹纸", NewsFragment.class)
                .add("开源", GithubFragment.class)
                .add("博客", CsdnFragment.class)
                //.add("微博", WeiboFragment.class)
                .create());


        vp_main.setAdapter(adapter);
        vp_main.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    // 切换到音乐栏的时候，搜索按钮才可见
                    menu_search.setVisible(true);
                } else {
                    menu_search.setVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        stl_main.setViewPager(vp_main);
    }


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
        } else {
            super.onBackPressed();
        }
    }

    private MenuItem menu_search;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // 找到ToolBar的搜索控件
        menu_search = menu.findItem(R.id.menu_search);
        menu_search.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理ToolBar上的控件点击事件
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_search:
                // 点击搜索，如果文字不为空则弹出右侧菜单，目前只能搜音乐===
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    // 弹出右侧菜单
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }

        // return true应该就是展示空间不够而隐藏的控件

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 处理左侧导航栏的点击事件
        switch (item.getItemId()) {

            case R.id.nav_download:
                break;
            case R.id.nav_about:
                break;

            case R.id.nav_update:
                ToastTool.showShort(this, "已是最新版本啦");
                break;

            case R.id.nav_more:
                // 打开设置界面
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.nav_comment:
                // 跳转到应用市场，评论应用
                Uri uri = Uri.parse("market://details?id="+getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            /*case R.id.nav_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT,"我发现了一款非常有趣的应用，你也来下载吧！它的下载地址是www.qianxueya.com");
                startActivity(Intent.createChooser(share, "分享竹翎"));
                break;*/

            case R.id.nav_feedback:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = result == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    break;
                }
            }

            if (!granted) {
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
            } else {
                // 已经赋予过权限了
                boolean hasGranted = PreferencesTool.getBoolean(this, "hasGranted");
                if (!hasGranted) {
                    // 第一次被赋予权限
                    PreferencesTool.putBoolean(this, "hasGranted", true);

                    ToastTool.showLong(this, "若不能正常加载请退出重新打开");
                    // 重新启动Activity
                    recreate();
                }
            }
        }
    }
}
