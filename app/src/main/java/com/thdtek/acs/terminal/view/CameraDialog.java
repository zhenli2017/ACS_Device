package com.thdtek.acs.terminal.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.thdtek.acs.terminal.R;

/**
 * Time:2018/6/23
 * User:lizhen
 * Description:
 */

public class CameraDialog extends Dialog {

    public CameraDialog(@NonNull Context context) {
        super(context);
    }

    public CameraDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void showDialog(View view, int x, int y) {
        setContentView(view);
        setWindow(x, y);
        //设置触摸对话框意外的地方取消对话框
        show();
    }

    public void showDialog(int layoutId, int x, int y) {
        setContentView(layoutId);
        setWindow(x, y);
        //设置触摸对话框意外的地方取消对话框
        show();
    }

    private void setWindow(int x, int y) {
        Window mWindow = getWindow(); //得到对话框
        mWindow.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        mWindow.setBackgroundDrawableResource(R.color.transparent); //设置对话框背景为透明
        WindowManager.LayoutParams wl = mWindow.getAttributes();
        //根据x，y坐标设置窗口需要显示的位置
        wl.x = x; //x小于0左移，大于0右移
        wl.y = y; //y小于0上移，大于0下移
//            wl.alpha = 0.6f; //设置透明度
//            wl.gravity = Gravity.BOTTOM; //设置重力
        mWindow.setAttributes(wl);
//        mWindow.setDimAmount(0);
    }
}
