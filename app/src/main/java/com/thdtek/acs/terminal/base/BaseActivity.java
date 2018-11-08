package com.thdtek.acs.terminal.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.util.LogUtils;

/**
 * Time:2018/6/20
 * User:lizhen
 * Description:
 */

public abstract class BaseActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    private boolean mIsFirstResume = false;
    public static String TAG = BaseActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        mToolbar = findViewById(R.id.toolBar);
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        init();
        initView();
    }


    public abstract int getLayout();

    public abstract void init();

    public abstract void initView();

    public abstract void firstResume();

    public abstract void resume();

    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsFirstResume) {
            LogUtils.i(TAG, "============== firstResume ==============");
            firstResume();
            mIsFirstResume = true;
        }
        LogUtils.i(TAG, "============== onResume ==============");
        resume();
    }
}
