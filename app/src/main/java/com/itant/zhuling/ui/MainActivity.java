package com.itant.zhuling.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
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
import com.itant.zhuling.constant.ZhuConstants;
import com.itant.zhuling.tool.ActivityTool;
import com.itant.zhuling.tool.BitmapTool;
import com.itant.zhuling.tool.PermissionTool;
import com.itant.zhuling.tool.PreferencesTool;
import com.itant.zhuling.tool.SocialTool;
import com.itant.zhuling.tool.ToastTool;
import com.itant.zhuling.tool.UITool;
import com.itant.zhuling.tool.UriTool;
import com.itant.zhuling.ui.navigation.AboutActivity;
import com.itant.zhuling.ui.navigation.MoreActivity;
import com.itant.zhuling.ui.tab.csdn.CsdnFragment;
import com.itant.zhuling.ui.tab.github.GithubFragment;
import com.itant.zhuling.ui.tab.music.MusicFragment;
import com.itant.zhuling.ui.tab.news.NewsFragment;
import com.itant.zhuling.widget.smarttab.v4.FragmentPagerItemAdapter;
import com.itant.zhuling.widget.smarttab.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMenuItemClickListener, OnMenuItemLongClickListener, View.OnClickListener {

    private AppBarLayout abl_toolbar_container;
    private Toolbar tb_main;
    private int toolBarHeight;

    // 权限
    private static final int REQUEST_NECESSARY_PERMISSIONS = 1;
    private static String[] PERMISSIONS_NECESSARY = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA
    };
    private CircleImageView civ_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppThemeGreen);+reCreate()即可实现换主题颜色

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
                        // AppBar被已经藏住了一半了，提示用户下拉有惊喜（下拉就可以看到搜索按钮）
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

        initNavigationView();

        // 系统默认生成的代码-------------------------------------------------------------------结束

        // 初始化顶部界面
        initView();

        // 初始化右侧弹出菜单
        initMenuFragment();

        // 申请权限
        PermissionTool.initPermission(this, PERMISSIONS_NECESSARY, REQUEST_NECESSARY_PERMISSIONS);
    }

    private void initNavigationView() {

        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        View header = nav_view.getHeaderView(0);
        //View view=nav_view.inflateHeaderView(R.layout.nav_header_main);
        // 监听头像点击事件，实现换头像
        civ_head = (CircleImageView) header.findViewById(R.id.civ_head);
        // 如果本地有头像，就设置进去
        setHeadImage();
        civ_head.setOnClickListener(this);

        /**
         * start of code configuration for color of text of your Navigation Drawer / Menu based on state
         */
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


        // FOR NAVIGATION VIEW ITEM ICON COLOR
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
                //.add("微博", WeiboActivity.class)
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

        if (System.currentTimeMillis() - lastBackMillis < 2000) {
            super.onBackPressed();
        } else {
            ToastTool.showShort(this, "再按一次退出");
            lastBackMillis = System.currentTimeMillis();
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

        // return true应该就是展示那些由于空间不够而隐藏了的控件
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
                // 打开关于界面
                ActivityTool.startActivity(this, new Intent(this, AboutActivity.class));
                break;

            case R.id.nav_more:
                // 打开更多界面
                ActivityTool.startActivity(this, new Intent(this, MoreActivity.class));
                break;

            case R.id.nav_comment:
                // 跳转到应用市场，评论应用
                SocialTool.jumpMarketRating(this);
                break;

            case R.id.nav_feedback:
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

        switch (requestCode) {
            case REQUEST_NECESSARY_PERMISSIONS:
                onNecessaryPermissionResult(grantResults);
                break;
        }

    }

    /**
     * 查看基本的权限是否被赋予
     * @param grantResults
     */
    private void onNecessaryPermissionResult(int[] grantResults) {
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_head:
                // 选择头像
                CharSequence[] items = {"拍照", "相册"};
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("请选择头像来源")// setMessage会把item覆盖掉
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
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
                        }).create();
                dialog.show();
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

        // 先检查目录是否存在
        File headDir = new File(Environment.getExternalStorageDirectory(), ZhuConstants.DIRECTORY_ROOT_FILE_IMAGES);
        if (!headDir.exists()) {
            headDir.mkdirs();
        }
        /*File tempHeadDir = new File(Environment.getExternalStorageDirectory(), ZhuConstants.DIRECTORY_HEAD_TEMP);
        if (!tempHeadDir.exists()) {
            tempHeadDir.mkdirs();
        }*/
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
}
