package com.itant.zhuling.constant;

import java.net.HttpCookie;
import java.util.List;

/**
 * Created by Jason on 2017/4/9.
 */

public class ZhuConstants {
    public static final boolean DEBUG = true;// 打包的时候，设为false以节省系统资源
    public static boolean musicEnable = true;// 音乐开关，默认可以搜听音乐

    public static final String BMOB_APPLICATION_ID = "dd9f50028de2404b3bdf6356e6798327";

    /**
     * 新特性，文件提供者路径
     */
    public static final String NAME_PROVIDE = "com.itant.zhuling.fileprovider";

    /**
     * 应用外部根目录
     */
    public static final String DIRECTORY_ROOT = "/Android/data/com.itant.zhuling";

    /**
     * 文件根目录
     */
    public static final String DIRECTORY_ROOT_FILE = DIRECTORY_ROOT + "/files";

    /**
     * 图片根目录
     */
    public static final String DIRECTORY_ROOT_FILE_IMAGES = DIRECTORY_ROOT_FILE + "/images";

    /**
     * 缓存目录
     */
    public static final String DIRECTORY_ROOT_CACHE = DIRECTORY_ROOT + "/cache";

    /**
     * 头像目录
     */
    public static final String HEAD_FULL_NAME =  DIRECTORY_ROOT_FILE_IMAGES + "/head.jpeg";


    /**
     * 临时头像
     */
    public static final String HEAD_FULL_NAME_TEMP = DIRECTORY_ROOT_CACHE + "/temphead.jpeg";


    /**
     * 微信支付二维码
     */
    public static final String PAY_WECHAT = DIRECTORY_ROOT_FILE_IMAGES + "/pay_wechat.png";

    /**
     * 临时头像
     */
    public static final String MEI_ZHI_TEMP = DIRECTORY_ROOT_CACHE + "/meizhi.png";

    /**
     * 经典路径
     */
    public static String DIRECTORY_CLASSIC = DIRECTORY_ROOT_FILE + "/classic";

    /**
     * 小狗下载地址
     */
    public static String PATH_CLASSIC_DOG = DIRECTORY_CLASSIC + "/dog/";

    /**
     * 凉窝下载地址
     */
    public static String PATH_CLASSIC_KWO = DIRECTORY_CLASSIC + "/wo/";

    /**
     * 企鹅下载地址
     */
    public static String PATH_CLASSIC_QIE = DIRECTORY_CLASSIC + "/qie/";

    /**
     * 白云下载地址
     */
    public static String PATH_CLASSIC_YUN = DIRECTORY_CLASSIC + "/yun/";

    /**
     * 熊掌下载地址
     */
    public static String PATH_CLASSIC_XIONG = DIRECTORY_CLASSIC + "/xiong/";

    /**
     * 龙虾下载地址
     */
    public static String PATH_CLASSIC_XIA = DIRECTORY_CLASSIC + "/xia/";
    public static List<HttpCookie> COOKIE_CONTAINER;
    public static long TIME_XIA_MI;
}
