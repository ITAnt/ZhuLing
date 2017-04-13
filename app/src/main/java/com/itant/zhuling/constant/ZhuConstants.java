package com.itant.zhuling.constant;

/**
 * Created by Jason on 2017/4/9.
 */

public class ZhuConstants {
    public static final boolean DEBUG = true;//打包的时候，设为false以节省系统资源

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
     * 头像目录
     */
    public static final String HEAD_FULL_NAME =  DIRECTORY_ROOT_FILE_IMAGES + "/head.jpeg";
    public static final String HEAD_FULL_NAME_TEMP = DIRECTORY_ROOT_FILE_IMAGES + "/temphead.jpeg";

    public static final String PAY_WECHAT = DIRECTORY_ROOT_FILE_IMAGES + "/pay_wechat.png";

    /**
     * 新特性，文件提供者路径
     */
    public static final String NAME_PROVIDE = "com.itant.zhuling.fileprovider";
}
