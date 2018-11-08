package com.thdtek.acs.terminal.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.anruxe.downloadlicense.HttpDownload;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.bean.ICEvent;
import com.thdtek.acs.terminal.bean.NoAliveEvent;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.camera.CameraPreview2;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.face.FaceUtil;
import com.thdtek.acs.terminal.http.upload.UploadRecord;
import com.thdtek.acs.terminal.imp.camera.CameraPreviewCallBack;
import com.thdtek.acs.terminal.imp.camera.CameraPreviewFindFaceInterface;
import com.thdtek.acs.terminal.imp.person.PersonDownLoadListener;
import com.thdtek.acs.terminal.imp.person.persondownload.PersonDownLoadImp;
import com.thdtek.acs.terminal.receiver.DownLoadReceiver;
import com.thdtek.acs.terminal.receiver.TimeReceiver;
import com.thdtek.acs.terminal.receiver.WeatherReceiver;
import com.thdtek.acs.terminal.server.AppHttpServer;
import com.thdtek.acs.terminal.socket.core.ConnectHandler;
import com.thdtek.acs.terminal.thread.ReadICOrIDThread;
import com.thdtek.acs.terminal.thread.SerialThread;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DownLoadVideo;
import com.thdtek.acs.terminal.util.DownloadApk;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.IHALUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.util.ToastUtil;
import com.thdtek.acs.terminal.util.WeakHandler;
import com.thdtek.acs.terminal.util.camera.CameraUtil;
import com.thdtek.acs.terminal.util.tts.TtsUtil;
import com.thdtek.facelibrary.FaceRect;
import com.thdtek.hal.IHALInterface;

import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Time:2018/10/10
 * User:lizhen
 * Description:
 */
public abstract class BaseCameraActivity extends BaseActivity implements SurfaceHolder.Callback,
        CameraPreviewFindFaceInterface,
        ServiceConnection,
        PersonDownLoadListener {


    //是否发送了本次匹配结束时间
    public boolean mSendOver = true;
    //没有找到人脸的桢的总数
    public long mNoFaceCount = 0L;

    public DefaultHandler mWeakHandler;
    public CloseLightHandler mWeakHandlerCloseLight;
    public CameraHandler mCameraHandler;
    //彩色摄像头最后捕获数据时间
    private long mColorLastPreviewTime;
    //黑白摄像头最后捕获数据时间
    private long mRedLastPreviewTime;
    //重复打开摄像头的次数
    private int mOpenCameraRepeatCount;

    //时间的广播
    private TimeReceiver mTimeReceiver;
    //天气的广播
    private WeatherReceiver mWeatherReceiver;
    //下载apk进度的广播
    private DownLoadReceiver mDownLoadReceiver;
    //存放照相机的view
    public FrameLayout mFrameLayout;

    //彩色摄像头
    private CameraPreview2 mCameraPreviewColor;
    //红外摄像头
    private CameraPreview2 mCameraPreviewRed;
    //红外摄像头的实时预览数据
    private byte[] mByteRed;
    //人脸框的矩形对象
    public Rect mRect = new Rect();
    //是否需要重启设备
    public boolean mCameraDeviceReboot = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        TAG = getTAG();
        initTts();
        //开启风扇
        HWUtil.openFan();
        //初始化Handler
        initHandler();

        //绑定IC卡和身份证读取服务
        bindService();
        initCameraDeviceReboot();

        //人脸比对状态回调对象
        FacePairStatus.getInstance().init(this);
        //人员下载监听对象
        PersonDownLoadImp.getInstance().setListener(this);

        //打开补光灯
        HWUtil.openFillLight();
        //打开背光等
        HWUtil.openBackLight();
        //设置cpu频率
        HWUtil.setCpuFeed(HWUtil.CPU_FEED_1608000);
        //延时5s初始化摄像头
        PersonDownLoadImp.getInstance().personDownLoadStart(getString(R.string.device_ini), Const.SDK_YUN_TIAN_LI_FEI_INIT_TIME);
        mWeakHandler.sendEmptyMessageDelayed(Const.HANDLER_INIT_CAMERA, Const.HANDLER_DELAY_TIME_3000);
        //1分钟后设置屏幕变暗,关闭补光
        mWeakHandlerCloseLight.sendEmptyMessageDelayed(Const.HANDLER_MAIN_CLOSE_BACK_AND_FILL, Const.HANDLER_DELAY_TIME_60000);
        //2分钟后开始上传未上传的通过记录
        mWeakHandler.sendEmptyMessageDelayed(Const.HANDLER_MAIN_UPLOAD_ACCESS_RECORD, Const.HANDLER_DELAY_TIME_120000);
        //30s后检查是否有升级的apk
        mWeakHandler.sendEmptyMessageDelayed(Const.HANDLER_UPDATE_APK, Const.HANDLER_DELAY_TIME_30000);
        //1分钟后检查cpu温度
        mWeakHandler.sendEmptyMessageDelayed(Const.HANDLER_HANDLE_FEN, Const.HANDLER_DELAY_TIME_60000);

        registerTimeReceiver();
        registerWeatherReceiver();
        registerDownReceiver();
        //下载license
//        initLicense();

    }

    private void initLicense() {
        if (Const.SDK.equals(Const.SDK_HONG_RUAN)) {
            LogUtils.d(TAG, "当前算法是旧算法,不检测license");
            return;
        }
        File file = new File(Const.DIR_LICENSE + File.separator + "license");
        if (file.exists()) {
            LogUtils.d(TAG, "license 文件存在,不去网络下载");
            return;
        }
        LogUtils.e(TAG, "license 文件 不 存在,去网络下载");
        final AlertDialog show = new AlertDialog.Builder(BaseCameraActivity.this).setTitle(R.string.less_license).setCancelable(true).setMessage(R.string.license_down_loading).show();
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                int licenseFileCode = HttpDownload.getInstance().getLisenseFile(Const.SDK_YUN_TIAN_LI_FEI_KEY, Const.SDK_YUN_TIAN_LI_FEI_SECRET, MyApplication.getContext(), HttpDownload.COMPARE_TYPE);
                if (licenseFileCode == 200) {
                    if (show != null) {
                        show.dismiss();
                    }
                    setResult(RESULT_OK);
                    finish();
                } else {
                    if (show != null) {
                        show.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(BaseCameraActivity.this).setTitle(R.string.license_down_fail).setCancelable(true).setMessage(R.string.license_down_fail).show();
                        }
                    });
                }
            }
        });
    }

    public void initCameraDeviceReboot() {

        mCameraDeviceReboot = (boolean) SPUtils.get(MyApplication.getContext(), Const.DEVICE_REBOOT, false);
        if (mCameraDeviceReboot) {
            SPUtils.put(MyApplication.getContext(), Const.DEVICE_REBOOT, false);
        }
    }

    /**
     * 处理IC卡事件
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void icEvent(ICEvent messageEvent) {
        ToastUtil.showToast(this, messageEvent.getMsg() + " -> " + messageEvent.getIcNumber(), ToastUtil.SHOW_LONG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i(TAG, "============== onStart ==============");
        //初始化屏幕大小
        initWindowSize();
        mOnStop = false;

    }

    @Override
    public void firstResume() {
        //连接服务器  整个项目只需要在此发起一次即可   内部已经实现心跳、重连机制
        if (Const.IS_OPEN_SOCKET_MODE) {
            //连接服务器  整个项目只需要在此发起一次即可   内部已经实现心跳、重连机制
            ConnectHandler.closeConnect();
            ConnectHandler.connect(AppSettingUtil.getConfig().getServerIp(), AppSettingUtil.getConfig().getServerPort(), true, null);
        }
    }

    @Override
    public void resume() {
        mOnStop = false;
        HWUtil.hideStatusBarAndNaviBar(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i(TAG, "============== onPause ==============");
    }

    private boolean mOnStop = false;

    @Override
    protected void onStop() {
        super.onStop();
        mOnStop = true;
        LogUtils.i(TAG, "============== onStop ==============");
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        HWUtil.showStatusBarAndNaviBar(this);
        TtsUtil.getInstance().stop();
        try {
            if (IHALUtil.getmIhalInterface() != null) {
                IHALUtil.getmIhalInterface().IReader_UnInit();
                IHALUtil.getmIhalInterface().GPIO_UnInit();
                IHALUtil.getmIhalInterface().Fan_UnInit();
                IHALUtil.getmIhalInterface().Light_UnInit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        unregisterReceiver(mTimeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownLoadReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mWeatherReceiver);
        LogUtils.i(TAG, "============== onDestroy ==============");
        if (mCameraPreviewColor != null) {
            mCameraPreviewColor.stopPreview(true);
        }
        if (mCameraPreviewRed != null) {
            mCameraPreviewRed.stopPreview(true);
        }
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacksAndMessages(null);
        }
        //1分钟后设置屏幕变暗,关闭补光
        if (mWeakHandlerCloseLight != null) {
            mWeakHandlerCloseLight.removeCallbacksAndMessages(null);
        }
        if (mFrameLayout != null) {
            mFrameLayout.removeAllViews();
        }
        if (mCameraHandler != null) {
            mCameraHandler.removeCallbacksAndMessages(null);
        }
        if (mCameraPreviewColor != null) {
            mCameraPreviewColor.stopPreview(true);
        }

        if (mCameraPreviewRed != null) {
            mCameraPreviewRed.stopPreview(true);
        }
        LogUtils.e(TAG, "onDestroy = stopService");
        Intent intent = new Intent("com.thdtek.hal.service");
        intent.setPackage("com.thdtek.hal"); //Android5.0之后需要指定共享Service所在应用的应用包名，否则会抛异常
        stopService(intent);
        LogUtils.e(TAG, "onDestroy = closeConnect");
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                ConnectHandler.closeConnect();
            }
        });
        LogUtils.e(TAG, "onDestroy = TtsUtil release");
        TtsUtil.getInstance().release();
        FacePairStatus.getInstance().unInit();
        PersonDownLoadImp.getInstance().clear();
        LogUtils.e(TAG, "onDestroy = FaceUtil releaseAll");
        FaceUtil.getInstance().releaseAll();
        LogUtils.e(TAG, "onDestroy = ReadICOrIDThread stopThread");
        ReadICOrIDThread.getInstance().stopThread();
        HWUtil.closeFillLight();
        //APP关闭必须发送关门信号,否则门会一直开关
        HWUtil.closeDoor();
        //APP退出关闭风扇
        HWUtil.closeFan();
        DownLoadVideo.getInstance().closeAll();
        LogUtils.e(TAG, "onDestroy = SerialThread close");
        SerialThread.getInstance().close(true);
        unbindService(this);
        setResult(RESULT_OK);
        super.onDestroy();
    }

    private void initWindowSize() {
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        LogUtils.d(TAG, "屏幕宽度 = " + widthPixels);
        LogUtils.d(TAG, "屏幕高度 = " + heightPixels);
    }

    private void initTts() {
        //初始化百度tts语音
        WeakHandler weakHandler = new WeakHandler(new WeakHandler.WeakHandlerCallBack() {
            @Override
            public void handleMessage(Message message) {

            }
        });
        TtsUtil.getInstance().init(weakHandler);
    }

    private void initHandler() {
        mWeakHandler = new DefaultHandler(this);
        mWeakHandlerCloseLight = new CloseLightHandler(this);
        mCameraHandler = new CameraHandler(this);
    }


    /**
     * 初始化绑定IC卡和身份证读取服务
     */
    private void bindService() {
        Intent intent = new Intent("com.thdtek.hal.service");
        intent.setPackage("com.thdtek.hal");
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IHALInterface mHALInterface = IHALInterface.Stub.asInterface(iBinder);
        IHALUtil.setIHALInterface(mHALInterface);
        LogUtils.d(TAG, "onServiceConnected IC卡和ID卡读取服务连接成功");
        //初始化读取身份证和ic卡
        try {
            ReadICOrIDThread.getInstance();
        } catch (Exception e) {
            LogUtils.e(TAG, "========== ReadICOrIDThread error = " + e.getMessage());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        IHALInterface mHALInterface = null;
        ToastUtil.showToast(this, "远程服务绑定失败,无法读取IC卡或身份证信息");
    }

    /**
     * 监听系统时间改变的广播
     */
    private void registerTimeReceiver() {
        LogUtils.d(TAG, "============== registerTimeReceiver ==============");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        mTimeReceiver = getTimeReceiver();
        if (mTimeReceiver == null) {
            LogUtils.d(TAG, "============== registerTimeReceiver 失败 ==============");
            return;
        }
        LogUtils.d(TAG, "============== registerTimeReceiver 成功 ==============");
        registerReceiver(mTimeReceiver, intentFilter);
    }


    /**
     * 天气更新的广播
     */
    private void registerWeatherReceiver() {
        LogUtils.d(TAG, "============== registerWeatherReceiver ==============");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.WEATHER);//天气

        mWeatherReceiver = getWeatherReceiver();
        if (mWeatherReceiver == null) {
            LogUtils.d(TAG, "============== registerWeatherReceiver 失败 ==============");
            return;
        }
        LogUtils.d(TAG, "============== registerWeatherReceiver 成功 ==============");
        LocalBroadcastManager.getInstance(this).registerReceiver(mWeatherReceiver, intentFilter);
    }


    /**
     * 下载apk进度的广播
     */
    private void registerDownReceiver() {
        LogUtils.d(TAG, "============== registerDownReceiver ==============");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.DOWN_RECEIVE);
        mDownLoadReceiver = getDownLoadReceiver();
        if (mWeatherReceiver == null) {
            LogUtils.d(TAG, "============== registerDownReceiver 失败 ==============");
            return;
        }
        LogUtils.d(TAG, "============== registerDownReceiver 成功 ==============");
        LocalBroadcastManager.getInstance(this).registerReceiver(mDownLoadReceiver, intentFilter);
    }

    private void initSurfaceAndCamera() {
        FaceUtil.getInstance().setFindFaceInterface(this);
        Message message = Message.obtain();
        message.what = Const.HANDLER_INIT_SDK;
        try {
            if (Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI)) {
                PersonDownLoadImp.getInstance().personDownLoadStart(getStrings(Const.SDK_INIT_START_YUN_TIAN_LI_FEI), Const.SDK_YUN_TIAN_LI_FEI_INIT_TIME);
                FaceUtil.getInstance().init(Const.SDK_INIT_START_YUN_TIAN_LI_FEI);
                message.arg1 = Const.SDK_INIT_START_YUN_TIAN_LI_FEI;
            } else {
                PersonDownLoadImp.getInstance().personDownLoadStart(getStrings(Const.SDK_INIT_START_HONG_RUAN), Const.SDK_YUN_TIAN_LI_FEI_INIT_TIME);
                FaceUtil.getInstance().init(Const.SDK_INIT_START_HONG_RUAN);
                message.arg1 = Const.SDK_INIT_START_HONG_RUAN;
            }
            mWeakHandler.sendMessageDelayed(message, Const.HANDLER_DELAY_TIME_3000);
        } catch (Exception e) {
            LogUtils.e(TAG, "initSurfaceAndCamera error = " + e.getMessage());
            message.obj = getString(R.string.sdk_init_fail);
            handleDownLoadPersonStart(message);
        }

    }

    private void initSDK(Message oldMessage) {
        LogUtils.e(TAG, "====== SDK 初始化没有结束,上次执行的是 = " + oldMessage.arg1);
        try {
            Message newMessage = Message.obtain();
            newMessage.what = Const.HANDLER_INIT_SDK;
            newMessage.arg1 = oldMessage.arg1 + 1;
            int init = FaceUtil.getInstance().init(newMessage.arg1);
            if (init != Const.SDK_INIT_END) {
                PersonDownLoadImp.getInstance().personDownLoadStart(getStrings(newMessage.arg1), Const.SDK_YUN_TIAN_LI_FEI_INIT_TIME);
                mWeakHandler.sendMessageDelayed(newMessage, Const.HANDLER_DELAY_TIME_3000);
            } else {
                LogUtils.e(TAG, "====== SDK 初始化结束 = " + oldMessage.arg1);
                initCamera();
                PersonDownLoadImp.getInstance().personDownLoadEnd(getString(R.string.device_init_finish), Const.HANDLER_DELAY_TIME_3000);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "initSDK error = " + e.getMessage());
            oldMessage.obj = getString(R.string.sdk_init_fail);
            handleDownLoadPersonStart(oldMessage);
        }

    }

    public String getStrings(int number) {

        return getString(getResources().getIdentifier("sdk_init_" + number, "string", AppUtil.getPackageName()));
    }

    /**
     * 初始化摄像头
     */
    private void initCamera() {


        SurfaceView surfaceView = new SurfaceView(this);
        initCameraSurfaceView(surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
        //最后一次有预览数据的时间,第一次默认认为有数据
        mColorLastPreviewTime = System.currentTimeMillis();
        mRedLastPreviewTime = System.currentTimeMillis();
        mCameraHandler.removeMessages(Const.HANDLER_CHECK_CAMERA);
        mCameraHandler.sendEmptyMessageDelayed(Const.HANDLER_CHECK_CAMERA, Const.HANDLER_DELAY_TIME_3000);
        if (mCameraPreviewColor == null) {
            LogUtils.i(TAG, "开始初始化 彩色 摄像头");
            mCameraPreviewColor = new CameraPreview2(0, new CameraPreviewCallBack() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    if (bytes == null || bytes.length == 0) {
                        return;
                    }
                    mColorLastPreviewTime = System.currentTimeMillis();
                    if (AppHttpServer.isTakingPictures) {
                        try {
                            AppHttpServer.mPhotoQueue.put(bytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppHttpServer.isTakingPictures = false;
                        }
                    } else {
                        if (AppSettingUtil.getConfig().getCameraDetectType() == Const.FACE_TWO_EYE_ALIVE) {
                            if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK)) {
                                FaceUtil.getInstance().handleYunTianLiFei(bytes, mByteRed);
                            } else {
                                FaceUtil.getInstance().handleHongRuan(bytes, mByteRed);
                            }
                        } else {
                            if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK)) {
                                FaceUtil.getInstance().handleYunTianLiFei(bytes);
                            } else {
                                FaceUtil.getInstance().handleHongRuan(bytes);
                            }
                        }
                    }

                }
            });
        }
        if (mCameraPreviewRed == null) {
            LogUtils.i(TAG, "开始初始化 黑白 摄像头");
            mCameraPreviewRed = new CameraPreview2(1, new CameraPreviewCallBack() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    if (bytes == null || bytes.length == 0) {
                        return;
                    }
                    mRedLastPreviewTime = System.currentTimeMillis();
                    mByteRed = bytes;
                }
            });
        }
    }

    private void closeCamera() {
        LogUtils.d(TAG, "关闭摄像头,清空数据");
        if (mCameraPreviewColor != null) {
            mCameraPreviewColor.stopPreview(true);
            mCameraPreviewColor = null;
        }
        if (mCameraPreviewRed != null) {
            mCameraPreviewRed.stopPreview(true);
            mCameraPreviewRed = null;
        }
        if (mFrameLayout != null) {
            mFrameLayout.removeAllViews();
        }
        LogUtils.d(TAG, "再次开启摄像头");
        initCamera();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.i(TAG, "=============== surfaceCreated ===============");
        if (mCameraPreviewColor != null) {
            mCameraPreviewColor.startPreview(holder);
        }
        if (mCameraPreviewRed != null) {
            mCameraPreviewRed.startPreview(null);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.i(TAG, "=============== surfaceChanged width = " + width + " height = " + height + " ===============");
        if (mCameraPreviewColor != null) {
            mCameraPreviewColor.stopPreview(false);
            mCameraPreviewColor.setParameters(width, height);
            mCameraPreviewColor.startPreview(holder);
        }
        if (mCameraPreviewRed != null) {
            mCameraPreviewRed.stopPreview(false);
            mCameraPreviewRed.setParameters(width, height);
            mCameraPreviewRed.startPreview(null);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.i(TAG, "=============== surfaceDestroyed ===============");
        if (mCameraPreviewColor != null) {
            mCameraPreviewColor.stopPreview(false);
        }

        if (mCameraPreviewRed != null) {
            mCameraPreviewRed.stopPreview(false);
        }
    }


    @Override
    public void personDownLoadStart(String msg, long delay) {
        mWeakHandler.removeMessages(Const.HANDLER_DOWN_LOAD_PERSON_END);
        mWeakHandler.removeMessages(Const.HANDLER_DOWN_LOAD_PERSON_FINISH);
        Message message = Message.obtain();
        message.what = Const.HANDLER_DOWN_LOAD_PERSON_START;
        message.arg1 = (int) delay;
        message.obj = msg;
        mWeakHandler.sendMessage(message);
        FaceTempData.getInstance().setDownLoadPersonMsg(true);
    }

    @Override
    public void personDownLoadEnd(String msg, long delay) {
        mWeakHandler.removeMessages(Const.HANDLER_DOWN_LOAD_PERSON_END);
        mWeakHandler.removeMessages(Const.HANDLER_DOWN_LOAD_PERSON_FINISH);
        Message message = Message.obtain();
        message.what = Const.HANDLER_DOWN_LOAD_PERSON_END;
        message.arg1 = (int) delay;
        message.obj = msg;
        mWeakHandler.sendMessage(message);

    }


    @Override
    public void findFaceReady() {
        handleFacePairReady();
    }

    /**
     * 准备进行识别,已经找到人脸
     */
    private void handleFacePairReady() {
        HWUtil.openFillLight();
        HWUtil.openBackLight();
        HWUtil.setCpuFeed(HWUtil.CPU_FEED_1608000);
        mWeakHandlerCloseLight.removeCallbacksAndMessages(null);
        mWeakHandlerCloseLight.sendEmptyMessageDelayed(Const.HANDLER_MAIN_CLOSE_BACK_AND_FILL, Const.HANDLER_DELAY_TIME_60000);
        handleFacePairReadyUI();
    }


    @Override
    public void findNotFace(Object rect, int color) {
        mRect.set(0, 0, 0, 0);
        if (mNoFaceCount >= Const.MAX_NO_FACE_COUNT && mSendOver) {
            LogUtils.e(TAG, "========= findNotFace 找不到人脸 =========");
            FacePairStatus.getInstance().facePairThreadContinueOnce();
            mWeakHandler.removeMessages(Const.FACE_PAIR_ING);
            mWeakHandler.removeMessages(Const.FACE_PAIR_SUCCESS);
            mWeakHandler.removeMessages(Const.FACE_PAIR_FAIL);
            mWeakHandler.removeMessages(Const.FACE_PAIR_FINISH);
            Message message = Message.obtain();
            message.what = Const.FACE_PAIR_FINISH;
            message.obj = true;
            mWeakHandler.sendMessageDelayed(message, Const.HANDLER_DELAY_TIME_500);
//                handleThisPairOver(true);
            mSendOver = false;
        }
        mNoFaceCount = mNoFaceCount > 10000 ? 0L : ++mNoFaceCount;
        handleNotFindFaceUI(mRect, color);
    }

    @Override
    public void findFace(Object rect, int color) {

        //检测到人脸
        if (rect instanceof FaceRect) {
//            mRect = new Rect(((FaceRect) (rect)).rect);
            mRect.set(((FaceRect) (rect)).rect);
        } else if (rect instanceof com.intellif.FaceRect) {
//            mRect = new Rect(((com.intellif.FaceRect) (rect)).dRectLeft,
//                    ((com.intellif.FaceRect) (rect)).dRectTop,
//                    ((com.intellif.FaceRect) (rect)).dRectRight,
//                    ((com.intellif.FaceRect) (rect)).dRectBottom);
            mRect.set(((com.intellif.FaceRect) (rect)).dRectLeft,
                    ((com.intellif.FaceRect) (rect)).dRectTop,
                    ((com.intellif.FaceRect) (rect)).dRectRight,
                    ((com.intellif.FaceRect) (rect)).dRectBottom);
        }
        mSendOver = true;
        mNoFaceCount = 0L;
        handleFindFaceUI(rect, color);
    }

    @Override
    public void facePairing() {
        //正在匹配
        mWeakHandler.removeMessages(Const.FACE_PAIR_ING);
        mWeakHandler.sendEmptyMessage(Const.FACE_PAIR_ING);

    }

    /**
     * 正在人脸匹配数据回调
     */
    private void handleFacePairing() {
        LogUtils.e(TAG, "============== 正在匹配");
        mWeakHandler.removeMessages(Const.FACE_PAIR_FINISH);
        handleFacePairingUI();
    }

    @Override
    public void facePairSuccess(PersonBean personBean, int samePeople) {
        //匹配成功
        Message message = Message.obtain();
        message.what = Const.FACE_PAIR_SUCCESS;
        message.obj = personBean;
        message.arg1 = samePeople;
        mWeakHandler.sendMessage(message);
    }


    /**
     * 人脸匹配成功数据回调
     */
    private void handleFacePairSuccess(Message message) {
        LogUtils.e(TAG, "============== 匹配成功");
        //本次识别结束
        mWeakHandler.removeMessages(Const.FACE_PAIR_FINISH);
        Message newMessage = Message.obtain();
        newMessage.what = Const.FACE_PAIR_FINISH;
        newMessage.obj = true;
        mWeakHandler.sendMessageDelayed(newMessage,
                AppSettingUtil.getConfig().getFaceFeaturePairSuccessOrFailWaitTime() >= Const.SLEEP_TIME ?
                        AppSettingUtil.getConfig().getFaceFeaturePairSuccessOrFailWaitTime() : Const.SLEEP_TIME);
        //重新捕获数据,处理同一个人的情况下连续显示
        if (FaceTempData.getInstance().isHaveIdMessage()) {
            LogUtils.d(TAG, "当前有身份证信息,不重新捕获数据");
        } else if (FaceTempData.getInstance().isHaveICMessage()) {
            LogUtils.d(TAG, "当前有IC卡信息,不重新捕获数据");
        } else {
            LogUtils.d(TAG, "main 重新捕获数据");
            CameraUtil.resetCameraVariable(true);
        }
        //是同一个人,什么也不干
        LogUtils.d(TAG, "message.arg1 = " + message.arg1 + " getSuccessViewVisible() = " + getSuccessViewVisible());
        if (message.arg1 == Const.FACE_PAIR_SAME_PEOPLE && getSuccessViewVisible()) {
            LogUtils.d(TAG, "当前是同一个人,并且View已经显示");
            return;
        }
        final PersonBean personBean = (PersonBean) message.obj;
//        LogUtils.d(TAG, "personBean="+personBean.toString());
        //本次匹配完成
        HWUtil.openDoor(personBean.getEmployee_card_id());
        //延时关门
        mWeakHandler.removeMessages(Const.HANDLER_MAIN_CLOSE_RELAY);
        mWeakHandler.sendEmptyMessageDelayed(Const.HANDLER_MAIN_CLOSE_RELAY, AppSettingUtil.getConfig().getOpenDoorContinueTime());

        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                //播放语音
                try {
                    TtsUtil.getInstance().stop();
                    TtsUtil.getInstance().speak(String.format(Locale.getDefault(),
                            AppSettingUtil.getConfig().getAppWelcomeMsg().replaceFirst("@", "%s"),
                            personBean.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, "tts = " + e.getMessage());
                }
            }
        });
        handleFacePairSuccessUI(message, personBean);
    }


    @Override
    public void facePairFail(String msg, int code) {
        //匹配失败
        Message message = Message.obtain();
        message.obj = msg;
        message.arg1 = code;
        message.what = Const.FACE_PAIR_FAIL;
        mWeakHandler.sendMessage(message);
    }

    /**
     * 人脸匹配数据失败回调
     */
    private void handleFacePairFail(Message oldMessage) {
        LogUtils.e(TAG, "============== 匹配失败");
        //1秒后本次识别结束
        mWeakHandler.removeMessages(Const.FACE_PAIR_FINISH);
        Message message = Message.obtain();
        message.what = Const.FACE_PAIR_FINISH;
        message.obj = true;
        mWeakHandler.sendMessageDelayed(message, Const.HANDLER_DELAY_TIME_1000);
        handleFacePairFailUI(oldMessage);
    }

    @Override
    public void facePairFailNoVisible() {
        handlePairFailNoVisibleUI();
        CameraUtil.resetCameraVariable(true);
    }

    @Override
    public void facePairFinish() {
        //匹配结束
        Message message = Message.obtain();
        message.what = Const.FACE_PAIR_FINISH;
        message.obj = true;
        mWeakHandler.sendMessage(message);

    }


    /**
     * 本次匹配结束回调
     */
    private void handleThisPairOver(boolean reset) {
        LogUtils.e(TAG, "============== 匹配结束");
        //继续读取身份证信息
        if (FaceTempData.getInstance().isHaveIdMessage()) {
            FaceTempData.getInstance().setHaveIdMessage(false);
        }
        //继续读取ic卡信息
        if (FaceTempData.getInstance().isHaveICMessage()) {
            FaceTempData.getInstance().setHaveICMessage(false);
        }
        //结束本次比对
        FacePairStatus.getInstance().setLastFacePairSuccessAuthorityId(Const.DEFAULT_CONITUE_AUTHORITY_ID);
        FacePairStatus.getInstance().resetFailCount();
        //重置,开始判断相机是否有人脸
        CameraUtil.resetCameraVariable(reset);
        handleThisPairOverUI();
    }

    @Override
    public void faceNotAlive(String msg) {
        Message message = Message.obtain();
        message.what = Const.FACE_PAIR_NOT_ALIVE;
        message.obj = msg;
        mWeakHandler.sendMessage(message);
    }

    public static class CloseLightHandler extends Handler {
        private WeakReference<BaseCameraActivity> mWeakReference;

        public CloseLightHandler(BaseCameraActivity activity) {
            mWeakReference = new WeakReference<BaseCameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            BaseCameraActivity baseCameraActivity = mWeakReference.get();
            if (baseCameraActivity == null) {
                LogUtils.d(TAG, "====== CloseLightHandler baseCameraActivity == null return");
                return;
            }
            switch (message.what) {
                //一分钟后关闭背光等
                case Const.HANDLER_MAIN_CLOSE_BACK_AND_FILL:
                    HWUtil.closeBackLight();
                    HWUtil.closeFillLight();
                    HWUtil.setCpuFeed(HWUtil.CPU_FEED_696000);
                    //一分钟后没有背光,关闭背光
                    baseCameraActivity.handleOneMinuteNoPeople(message);
                    break;
                default:
                    break;
            }
        }
    }

    public static class CameraHandler extends Handler {
        private WeakReference<BaseCameraActivity> mWeakReference;

        public CameraHandler(BaseCameraActivity activity) {
            mWeakReference = new WeakReference<BaseCameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseCameraActivity baseCameraActivity = mWeakReference.get();
            if (baseCameraActivity == null) {
                LogUtils.d(TAG, "====== CameraHandler baseCameraActivity == null return");
                return;
            }

            long currentTimeMillis = System.currentTimeMillis();
            if (baseCameraActivity.mOnStop) {
                baseCameraActivity.mOpenCameraRepeatCount = 0;
                baseCameraActivity.mCameraHandler.removeMessages(Const.HANDLER_CHECK_CAMERA);
                baseCameraActivity.mCameraHandler.sendEmptyMessageDelayed(Const.HANDLER_CHECK_CAMERA, Const.HANDLER_DELAY_TIME_2000);
                return;
            }
            if (currentTimeMillis - baseCameraActivity.mColorLastPreviewTime >= Const.HANDLER_DELAY_TIME_2000
                    || (currentTimeMillis - baseCameraActivity.mRedLastPreviewTime >= Const.HANDLER_DELAY_TIME_2000 && AppSettingUtil.getConfig().getCameraDetectType() == Const.FACE_TWO_EYE_ALIVE)) {
                if (baseCameraActivity.mOpenCameraRepeatCount <= Const.CAMERA_REPEAT_MAX_COUNT) {
                    baseCameraActivity.closeCamera();
                } else {
                    if (baseCameraActivity.mCameraDeviceReboot) {
                        HWUtil.reboot("摄像头打不开,重启机器");
                    } else {
                        //重启APP
                        LogUtils.e(TAG, "========== 摄像头打不开,重启 APP ==========");
                        SPUtils.put(MyApplication.getContext(), Const.DEVICE_REBOOT, true);
                        baseCameraActivity.setResult(RESULT_OK);
                        baseCameraActivity.finish();
                    }
                }
                baseCameraActivity.mOpenCameraRepeatCount++;
                //LogUtils.d(TAG, "重试次数 = " + mOpenCameraRepeatCount);
            } else {
                baseCameraActivity.mOpenCameraRepeatCount = 0;
                baseCameraActivity.mCameraHandler.removeMessages(Const.HANDLER_CHECK_CAMERA);
                baseCameraActivity.mCameraHandler.sendEmptyMessageDelayed(Const.HANDLER_CHECK_CAMERA, Const.HANDLER_DELAY_TIME_2000);
            }
        }
    }


    public static class DefaultHandler extends Handler {
        private WeakReference<BaseCameraActivity> mWeakReference;

        public DefaultHandler(BaseCameraActivity activity) {
            mWeakReference = new WeakReference<BaseCameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            BaseCameraActivity baseCameraActivity = mWeakReference.get();
            if (baseCameraActivity == null) {
                LogUtils.d(TAG, "====== DefaultHandler baseCameraActivity == null return");
                return;
            }
            switch (message.what) {

                //检查apk
                case Const.HANDLER_UPDATE_APK:
                    DownloadApk.getInstance().updateApp(MyApplication.getContext());
                    break;
                //正在匹配
                case Const.FACE_PAIR_ING:
                    baseCameraActivity.mWeakHandler.removeMessages(Const.FACE_PAIR_NOT_ALIVE_FINISH);
                    baseCameraActivity.handleFacePairing();
                    break;
                //匹配成功
                case Const.FACE_PAIR_SUCCESS:
                    baseCameraActivity.mWeakHandler.removeMessages(Const.FACE_PAIR_NOT_ALIVE_FINISH);
                    baseCameraActivity.handleFacePairSuccess(message);
                    break;
                //匹配失败
                case Const.FACE_PAIR_FAIL:
                    baseCameraActivity.mWeakHandler.removeMessages(Const.FACE_PAIR_NOT_ALIVE_FINISH);
                    baseCameraActivity.handleFacePairFail(message);
                    break;
                //匹配结束
                case Const.FACE_PAIR_FINISH:
                    baseCameraActivity.mWeakHandler.removeMessages(Const.FACE_PAIR_NOT_ALIVE_FINISH);
                    baseCameraActivity.handleThisPairOver((Boolean) message.obj);
                    break;
                //非活体
                case Const.FACE_PAIR_NOT_ALIVE:
                    baseCameraActivity.handleNotAliveUI((String) message.obj);
                    baseCameraActivity.mWeakHandler.removeMessages(Const.FACE_PAIR_NOT_ALIVE_FINISH);
                    baseCameraActivity.mWeakHandler.sendEmptyMessageDelayed(Const.FACE_PAIR_NOT_ALIVE_FINISH, Const.HANDLER_DELAY_TIME_1000);
                    break;
                //非活体检测结束
                case Const.FACE_PAIR_NOT_ALIVE_FINISH:
                    CameraUtil.resetCameraVariable(true);
                    baseCameraActivity.handleNotAliveFinishUI("");
                    break;
                //数据库中没有人员
                case Const.FACE_PAIR_DATABASE_NO_PEOPLE:
                    ToastUtil.showToast(baseCameraActivity, "请添加人员");
                    break;
                //IC卡匹配失败
                case Const.FACE_PAIR_IC_FAIL:
                    ToastUtil.showToast(baseCameraActivity, "IC 卡开门失败,请检查...");
                    break;
                //延时一秒关门
                case Const.HANDLER_MAIN_CLOSE_RELAY:
                    HWUtil.closeDoor();
                    break;
                //设置上传锁为false
                case Const.HANDLER_MAIN_UPLOAD_ACCESS_RECORD:
                    if (Const.IS_OPEN_SOCKET_MODE) {
                        UploadRecord.setUploadingFalse();
                    }
                    baseCameraActivity.mWeakHandler.removeMessages(Const.HANDLER_MAIN_UPLOAD_ACCESS_RECORD);
                    baseCameraActivity.mWeakHandler.sendEmptyMessageDelayed(Const.HANDLER_MAIN_UPLOAD_ACCESS_RECORD, Const.HANDLER_DELAY_TIME_600000);
                    break;
                //开始下载人员信息
                case Const.HANDLER_DOWN_LOAD_PERSON_START:
                    baseCameraActivity.handleDownLoadPersonStart(message);
                    baseCameraActivity.mWeakHandler.sendEmptyMessageDelayed(Const.HANDLER_DOWN_LOAD_PERSON_FINISH, message.arg1);
                    break;
                //下载人员信息结束
                case Const.HANDLER_DOWN_LOAD_PERSON_END:
                    baseCameraActivity.handleDownLoadPersonEnd(message);
                    baseCameraActivity.mWeakHandler.sendEmptyMessageDelayed(Const.HANDLER_DOWN_LOAD_PERSON_FINISH, message.arg1);
                    break;
                //下载人员信息view消失
                case Const.HANDLER_DOWN_LOAD_PERSON_FINISH:
                    FaceTempData.getInstance().setDownLoadPersonMsg(false);
                    baseCameraActivity.handleDownLoadPersonFinish();
                    break;
                //判断是否打开风扇
                case Const.HANDLER_HANDLE_FEN:
//                HWUtil.handleFan();
                    //初始化摄像头
                    break;
                case Const.HANDLER_INIT_CAMERA:
                    baseCameraActivity.initSurfaceAndCamera();
                    break;
                case Const.HANDLER_INIT_SDK:
                    baseCameraActivity.initSDK(message);
                    break;
                default:
                    break;
            }

        }
    }


    public abstract String getTAG();

    public abstract void handleOneMinuteNoPeople(Message message);

    public abstract TimeReceiver getTimeReceiver();

    public abstract WeatherReceiver getWeatherReceiver();

    public abstract DownLoadReceiver getDownLoadReceiver();

    public abstract void initCameraSurfaceView(SurfaceView surfaceView);

    public abstract boolean getSuccessViewVisible();

    public abstract void handleDownLoadPersonStart(Message msg);

    public abstract void handleDownLoadPersonEnd(Message msg);

    public abstract void handleDownLoadPersonFinish();

    public abstract void handleNotFindFaceUI(Object rect, int color);

    public abstract void handleFindFaceUI(Object rect, int color);

    public abstract void handleFacePairReadyUI();

    public abstract void handleFacePairingUI();

    public abstract void handleFacePairSuccessUI(Message message, PersonBean personBean);

    public abstract void handleFacePairFailUI(Message message);

    public abstract void handlePairFailNoVisibleUI();

    public abstract void handleThisPairOverUI();

    public abstract void handleNotAliveUI(String msg);

    public abstract void handleNotAliveFinishUI(String msg);
}
