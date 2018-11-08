package com.thdtek.acs.terminal.util;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class ToastUtil {

    public static final int SHOW_LONG = Toast.LENGTH_LONG;
    public static final int SHOW_SHORT = Toast.LENGTH_SHORT;

    @IntDef(value = {
            SHOW_SHORT,
            SHOW_LONG
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayOptions {
    }

    private static Toast mToast;

    public static void showToast(Context context, String content) {
        showToast(context, content, ToastUtil.SHOW_SHORT);
    }

    public static void showToast(Context context, int content) {
        showToast(context, context.getResources().getString(content), ToastUtil.SHOW_SHORT);
    }

    public static void showToast(@NonNull Context context, @NonNull String content, @DisplayOptions int shotOrLong) {
        if (mToast != null) {
            mToast.setText(content);
        } else {
            mToast = Toast.makeText(context.getApplicationContext(), content, shotOrLong);
        }
        mToast.show();
    }
}
