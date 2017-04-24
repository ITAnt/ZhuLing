package com.itant.zhuling.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by iTant on 2017/4/9.
 */

public class ZhuApplication extends MultiDexApplication {
    public static DbManager db;

    @Override
    public void onCreate() {
        super.onCreate();

        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // 堆分析专业进程，这里不能写代码
            return;
        }
        LeakCanary.install(this);*/

        ZhuManager.getInstance().onInit(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 方法数超标
        MultiDex.install(this);

        x.Ext.init(this);
        // 是否输出debug日志, 开启debug会影响性能.
        //x.Ext.setDebug(BuildConfig.DEBUG);
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("music.db")
                // 不设置dbDir时, 默认存储在app的私有目录.
                //.setDbDir(new File("/sdcard"))
                // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
                .setDbVersion(2)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                    }
                });
        db = x.getDb(daoConfig);
    }
}
