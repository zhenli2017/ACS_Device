package com.thdtek.acs.terminal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DateUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.Date;
import java.util.Locale;

/**
 * Time:2018/7/16
 * User:lizhen
 * Description:
 */

public class WeatherReceiver extends BroadcastReceiver {

    private final String TAG = WeatherReceiver.class.getSimpleName();

    private TextView mTextView;
    private ImageView mImageView;
    private View mView;

    public WeatherReceiver(TextView textView, ImageView imageView, View view) {
        mTextView = textView;
        mImageView = imageView;
        mView = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String code = intent.getStringExtra(Const.WEATHER_CODE);
        String type = intent.getStringExtra(Const.WEATHER_TYPE);
        int deviceOnLine = intent.getIntExtra(Const.DEVICE_ON_LINE, Const.VIEW_STATUS_OFF_LINE);
        if (deviceOnLine == Const.VIEW_STATUS_ON_LINE) {
            mView.setSelected(true);
            mView.setBackgroundColor(context.getResources().getColor(R.color.color_scan));
        } else if (deviceOnLine == Const.VIEW_STATUS_OFF_LINE) {
            mView.setSelected(false);
            mView.setBackgroundColor(context.getResources().getColor(R.color.red));
        } else if (deviceOnLine == Const.VIEW_STATUS_NOT_ALIVE) {
            mView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }

        if (!TextUtils.isEmpty(code)) {
            mTextView.setText(code);
        }
        if (!TextUtils.isEmpty(type)) {

            int currentHour = DateUtil.getCurrentHour();
            int mipmap = -1;
            if (currentHour >= 7 && currentHour <= 19) {
                //白天
                try {
                    String s = String.format(Locale.getDefault(), "d%02d", Integer.parseInt(type));
                    mipmap = context.getResources().getIdentifier(s, "mipmap", AppUtil.getPackageName());
                } catch (Exception e) {
                    mipmap = context.getResources().getIdentifier("d00", "mipmap", AppUtil.getPackageName());
                }
            } else {
                //晚上
                try {
                    String s = String.format(Locale.getDefault(), "n%02d", Integer.parseInt(type));
                    mipmap = context.getResources().getIdentifier(s, "mipmap", AppUtil.getPackageName());
                } catch (Exception e) {
                    mipmap = context.getResources().getIdentifier("d00", "mipmap", AppUtil.getPackageName());
                }
            }
            mImageView.setImageResource(mipmap);
        }


    }
}
