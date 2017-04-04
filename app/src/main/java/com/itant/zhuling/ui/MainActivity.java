package com.itant.zhuling.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.itant.zhuling.R;
import com.itant.zhuling.ui.maintab.csdn.CsdnFragment;
import com.itant.zhuling.ui.maintab.github.GithubFragment;
import com.itant.zhuling.ui.maintab.music.MusicFragment;
import com.itant.zhuling.ui.maintab.news.NewsFragment;
import com.itant.zhuling.ui.maintab.weibo.WeiboFragment;
import com.itant.zhuling.ui.navigation.SettingActivity;
import com.itant.zhuling.utils.ToastUtils;
import com.itant.zhuling.utils.smarttab.v4.FragmentPagerItemAdapter;
import com.itant.zhuling.utils.smarttab.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMenuItemClickListener, OnMenuItemLongClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppThemeGreen);即可实现换主题颜色

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 系统默认生成的代码-----------------------------------------------------------开始
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // 系统默认生成的代码-------------------------------------------------------------------结束

        // 初始化顶部界面
        initView();

        // 初始化右侧弹出菜单
        initMenuFragment();
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
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.drawable.icn_1);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icn_2);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.icn_3));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.drawable.icn_4);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.drawable.icn_5);

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
                .add("资讯", MusicFragment.class)
                .add("悦听", NewsFragment.class)
                .add("妹纸", NewsFragment.class)
                .add("博客", CsdnFragment.class)
                .add("微博", WeiboFragment.class)
                .add("开源", GithubFragment.class)
                .create());


        vp_main.setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理ToolBar上的控件点击事件
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
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
                ToastUtils.showShort(this, "我是自定义的Toast哦");
                break;

            case R.id.nav_setting:
                // 打开设置界面
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.nav_comment:
                // 跳转到应用市场，评论应用
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.nav_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "我发现了一款非常有趣的应用，你也来下载吧！它的下载地址是www.qianxueya.com");
                startActivity(Intent.createChooser(share, "分享竹翎"));
                break;

            case R.id.nav_feedback:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
