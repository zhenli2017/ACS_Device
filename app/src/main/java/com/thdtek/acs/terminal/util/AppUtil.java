package com.thdtek.acs.terminal.util;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import com.thdtek.acs.terminal.base.MyApplication;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class AppUtil {
    private static final String TAG = AppUtil.class.getSimpleName();

    /**
     * 获取包名
     *
     * @return
     */
    public static String getPackageName() {
        String pkName = "";
        try {
            pkName = MyApplication.getContext().getPackageName();
        } catch (Exception e) {
            LogUtils.e(TAG, " ==== getPackageName 发生异常 = " + e.getMessage());
        }
        return pkName;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 打卡软键盘
     */
    public static void openKeybord(View view, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        view.setFocusable(true);
        view.requestFocus();
        view.setFocusableInTouchMode(true);
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeyBord(View view, Activity mContext) {

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 读取baseurl
     *
     * @param url
     * @return
     */
    public static String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");
        if (index != -1) {
            head = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index + 1);
        }
        return head + url;
    }


    /**
     * 使用值动画获取渐变数值,设置透明度渐变
     *
     * @param activity
     * @param start
     * @param end
     */

    public static void setWindowAlpha(final Activity activity, float start, float end) {
        final WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        ValueAnimator anim = ValueAnimator.ofFloat(start, end);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.alpha = (float) animation.getAnimatedValue();
                activity.getWindow().setAttributes(lp);
            }
        });
        anim.start();

    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    public static boolean isActivityForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 拉起app
     *
     * @param context
     */
    public static void launchApp(Context context, String packageName) {
        Intent sayHelloIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        sayHelloIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.startActivity(sayHelloIntent);
    }


    /**
     * 静默安装
     *
     * @param context
     * @param filePath
     */
    public static void installSilentWithReflection(Context context, String filePath) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Method method = packageManager.getClass().getDeclaredMethod("installPackage",
                    new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class});
            String packageName = AppUtil.getPackageName();
            LogUtils.e(TAG, " ==== 安装apk packageName = " + packageName);
            method.setAccessible(true);
            File apkFile = new File(filePath);
            Uri apkUri = Uri.fromFile(apkFile);

            method.invoke(packageManager, new Object[]{apkUri, new IPackageInstallObserver.Stub() {
                @Override
                public void packageInstalled(String pkgName, int resultCode) throws RemoteException {
                    LogUtils.i(TAG, "packageInstalled = " + pkgName + "; resultCode = " + resultCode);
                }
            }, Integer.valueOf(2), packageName});
            //PackageManager.INSTALL_REPLACE_EXISTING = 2;
            SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_VERSION_CODE, 0);
            SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_PATH, "");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getInstalledApps(Context context) {
        ArrayList<String> apps = new ArrayList<String>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            android.content.pm.PackageInfo p = packs.get(i);
            if (p.versionName == null) {
                continue;
            }
            String appname = p.applicationInfo.loadLabel(context.getPackageManager())
                    .toString();
            apps.add(appname);
            Log.d("tingxiangScreen", appname);
        }
        return apps;
    }

}
