package com.itant.zhuling.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;

import com.itant.zhuling.R;
import com.liuguangqiang.swipeback.SwipeBackActivity;

/**
 * Created by Jason on 2017/3/26.
 */

public class BaseSwipeActivity extends SwipeBackActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("请稍等...");
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

    public void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
