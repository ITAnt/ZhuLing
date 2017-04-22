package com.itant.zhuling.ui;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.itant.zhuling.R;
import com.itant.zhuling.application.ZhuManager;
import com.itant.zhuling.service.PlayService;
import com.itant.zhuling.ui.main.MainActivity;
import com.itant.zhuling.widget.leaf.FloatLeafLayout;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView iv_launcher_inner;
    private TextView tv_app_name;
    boolean isShowingRubberEffect = false;

    private FloatLeafLayout fll_ling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoomin, 0);
        setContentView(R.layout.activity_welcome);

        if (ZhuManager.getMusicService() == null) {
            // 音乐service挂了，重新启动
            startMusicService();
            initView();
            initAnimation();
        } else {
            // 音乐service还在，直接去首页
            startMainActivity();
        }
    }

    /**
     * 欢迎动画等
     */
    private void initView() {
        iv_launcher_inner = (ImageView) findViewById(R.id.iv_launcher_inner);
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        // 应用自定义字体

        fll_ling = (FloatLeafLayout) findViewById(R.id.fll_ling);
    }

    private void initAnimation() {
        startLogoInner1();
        startLogoOuterAndAppName();

        startLing();
    }

    private void startLing() {
        fll_ling.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 我们必须等view加载之后才开始动画，否则取到的宽高为0
                fll_ling.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                fll_ling.floatLing();
            }
        });
    }

    private void startLogoInner1() {
        iv_launcher_inner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 我们必须等view加载之后才开始动画，否则取到的宽高为0
                iv_launcher_inner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Animation animation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.anim_top_in);
                iv_launcher_inner.startAnimation(animation);
            }
        });
    }

    private void startLogoOuterAndAppName() {
        tv_app_name.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 我们必须等view加载之后才开始动画，否则取到的宽高为0
                tv_app_name.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(1000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float fraction = animation.getAnimatedFraction();
                        if (fraction >= 0.8 && !isShowingRubberEffect) {
                            isShowingRubberEffect = true;
                            startShowAppName();
                            finishActivity();
                        } else if (fraction >= 0.95) {
                            valueAnimator.cancel();
                            startLogoInner2();
                        }
                    }
                });
                valueAnimator.start();
            }
        });
    }

    private void startShowAppName() {
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(tv_app_name);
    }

    private void startLogoInner2() {
        YoYo.with(Techniques.Bounce).duration(1000).playOn(iv_launcher_inner);
    }

    private void finishActivity() {
        Observable.timer(1800, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        startMainActivity();
                    }
                });
    }

    private void startMainActivity() {
        Intent intent = new Intent();
        intent.setClass(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, android.R.anim.fade_out);
        finish();
    }

    /**
     * 启动音乐Service
     */
    private void startMusicService() {
        // 先start一个service
        startService(new Intent(this, PlayService.class));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // start service之后，bind该service，然后将得到的service对象传递给管理者类，这样就可以在其他界
                // 面得到该service的对象，控制service进行一系列操作
                Intent intent = new Intent(WelcomeActivity.this, PlayService.class);
                connection = new MusicServiceConnection();
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
        }, 1000);
    }

    private MusicServiceConnection connection;
    /**
     * service连接
     */
    private class MusicServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获得了音乐service对象
            final PlayService playService = ((PlayService.MusicBinder) service).getService();
            ZhuManager.setMusicService(playService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (fll_ling != null) {
            fll_ling.onDestroy();
        }

        if (connection != null) {
            unbindService(connection);
        }
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
