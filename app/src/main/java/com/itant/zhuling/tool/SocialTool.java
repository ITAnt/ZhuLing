package com.itant.zhuling.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Jason on 2017/4/8.
 * 社会分享工具类
 */

public class SocialTool {

    /**
     * 调到应用市场评价应用
     * @param context
     */
    public static void jumpMarketRating(Context context) {
        Uri uri = Uri.parse("market://details?id="+context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 加音乐之家QQ群
     * @param context
     * @return
     */
    public static boolean joinQQGroup(Context context) {
        String key = "k7yZ68yYzEQFJ2zid_Uhy6kPG28Nq3H0";
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    /**
     * 分享APP
     * @param context
     */
    public static void shareApp(Context context) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT,"我发现了一款非常有趣的应用，你也来下载吧！它的下载地址是www.qianxueya.com");
        context.startActivity(Intent.createChooser(share, "分享竹翎"));
    }
}
