package com.itant.zhuling.ui.maintab.csdn;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.itant.zhuling.R;
import com.itant.zhuling.ui.base.BaseFragment;

/**
 * Created by Jason on 2017/3/26.
 */

public class CsdnFragment extends BaseFragment {


    @Override
    public int getLayoutId() {
        // 绑定视图
        return R.layout.fragment_csdn;
    }

    @Override
    public void initViews(View view) {

        WebView wv_csdn = (WebView) view.findViewById(R.id.wv_csdn);

        // 设置WebView属性，能够执行Javascript脚本
        WebSettings settings = wv_csdn.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);

        // 设置显示完整网页
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // 加快WebView速度
        // 提高渲染的优先级
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 把图片加载放在最后来加载渲染
        //settings.setBlockNetworkImage(true);
        // 开启H5(APPCache)缓存功能
        settings.setAppCacheEnabled(true);
        // webView数据缓存分为两种：AppCache和DOM Storage（Web Storage）。
        // 开启 DOM storage 功能
        //settings.setDomStorageEnabled(true);
        // 应用可以有数据库
        settings.setDatabaseEnabled(true);
        // 根据网络连接情况，设置缓存模式，
        // 可以读取文件缓存(manifest生效)
        settings.setAllowFileAccess(true);

        wv_csdn.setWebChromeClient(new WebChromeClient());
        wv_csdn.setWebViewClient(new WebViewClient() {
        });//希望点击链接继续在当前browser中响应，必须覆盖 WebViewClient对象。
        wv_csdn.loadUrl("http://m.blog.csdn.net/blog/index?username=ithouse");
    }
}
