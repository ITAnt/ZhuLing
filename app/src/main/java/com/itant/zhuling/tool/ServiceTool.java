package com.itant.zhuling.tool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.itant.zhuling.application.ZhuManager;
import com.itant.zhuling.service.PlayService;

/**
 * Created by Jason on 2017/4/25.
 */

public class ServiceTool {
    /**
     * 启动音乐Service
     */
    public static void startMusicService(final Context context) {
        // 先start一个service
        context.startService(new Intent(context, PlayService.class));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // start service之后，bind该service，然后将得到的service对象传递给管理者类，这样就可以在其他界
                // 面得到该service的对象，控制service进行一系列操作
                Intent intent = new Intent(context, PlayService.class);
                connection = new MusicServiceConnection();
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
        }, 1000);
    }

    private static MusicServiceConnection connection;
    /**
     * service连接
     */
    private static class MusicServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获得了音乐service对象
            final PlayService playService = ((PlayService.MusicBinder) service).getService();
            ZhuManager.getInstance().setMusicService(playService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
}
