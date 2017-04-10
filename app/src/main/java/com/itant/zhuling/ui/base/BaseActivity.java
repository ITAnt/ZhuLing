package com.itant.zhuling.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.itant.zhuling.R;

/**
 * Created by Jason on 2017/3/26.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // 点击了返回按钮
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}