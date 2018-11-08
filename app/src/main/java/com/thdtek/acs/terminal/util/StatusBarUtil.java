
package com.thdtek.acs.terminal.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtil {
    /**
     * 修改状态蓝颜色,full判断内容是否嵌入状态栏
     *
     * @param activity
     */
    public static void setStatusBar(Activity activity, int color, boolean statusBarHide,boolean navigationHide) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //[4.4],[5.0]
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //[5.0]+
//            //添加Flag把状态栏设为可绘制模式
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            if (full) {
//                //如果为全透明模式，取消设置Window半透明的Flag
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                //设置状态栏为透明
//                window.setStatusBarColor(Color.TRANSPARENT);
//                //设置window的状态栏不可见
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            } else {
//                //如果为半透明模式，添加设置Window半透明的Flag
//                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                //设置系统状态栏处于可见状态
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (statusBarHide) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            if (navigationHide) {
                View decorView = window.getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                 decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
