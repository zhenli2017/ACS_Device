package com.thdtek.acs.terminal.ui.cameraphoto;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hwit.HwitManager;
import com.intellif.FaceTrackListener;
import com.intellif.FaceUtils;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
import com.thdtek.acs.terminal.bean.CameraPhotoEvent;
import com.thdtek.acs.terminal.bean.CameraPreviewBean;
import com.thdtek.acs.terminal.bean.HardwareStatusEvent;
import com.thdtek.acs.terminal.bean.IDBean;
import com.thdtek.acs.terminal.bean.LianFaKeBean;
import com.thdtek.acs.terminal.bean.LianFaKeFailBean;
import com.thdtek.acs.terminal.bean.PersonInsertBean;
import com.thdtek.acs.terminal.bean.UsbEvent;
import com.thdtek.acs.terminal.camera.CameraPreview2;
import com.thdtek.acs.terminal.face.FaceUtil;
import com.thdtek.acs.terminal.imp.camera.CameraPreviewCallBack;
import com.thdtek.acs.terminal.thread.CameraThread;
import com.thdtek.acs.terminal.thread.SerialThread;
import com.thdtek.acs.terminal.thread.ThreadManager;
import com.thdtek.acs.terminal.thread.UsbThread;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.BitmapUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DensityUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.util.SoundUtil;
import com.thdtek.acs.terminal.util.ToastUtil;
import com.thdtek.acs.terminal.view.RectView;
import com.thdtek.facelibrary.FaceRect;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CameraPhotoActivity extends BaseActivity implements CameraPreviewCallBack, SurfaceHolder.Callback, FaceTrackListener {

    private static final String TAG = CameraPhotoActivity.class.getSimpleName();

    //faceApi 的初始化状态,默认是初始化失败
    public long mFaceApiStatue = 1L;

    private static final int HANDLER_INIT_SDK = 0;
    //倒计时
    private static final int HANDLER_COUNT_DOWN = 2;
    //停止拍照,返回原状态
    private static final int HANDLER_STOP_PHOTO = 3;
    //清空lastEdit数据
    private static final int HANDLER_CLEAR_LAST_EDIT = 4;
    //清空lastAllMsg
    private static final int HANDLER_CLEAR_LAST_ALL_MSG = 5;
    //上传成功
    private static final int HANDLER_UPLOAD_FAIL = 6;
    //上传失败
    private static final int HANDLER_UPLOAD_SUCCESS = 7;
    //上传人员成功
    private static final int HANDLER_UPLOAD_PERSON_SUCCESS = 8;
    private static final int TEXT_SIZE_SELECT = 36;

    private static final int TEXT_SIZE_DEFAULT = 26;
    public int CURRENT_STATUS = CURRENT_STATUS_DEFAULT;
    public static final int CURRENT_STATUS_DEFAULT = 0;
    public static final int CURRENT_STATUS_PREVIEW = 1;
    public static final int CURRENT_STATUS_ID_MESSAGE = 2;
    public static final int CURRENT_STATUS_CAMERA_IMAGE = 3;
    public static final int CURRENT_STATUS_SHOW_ID_MESSAGE = 4;
    public static final int CURRENT_STATUS_INSERT_PERSON = 5;
    public static final int CURRENT_STATUS_CHECK_IMAGE = 6;
    public static final int CURRENT_STATUS_COUNT_DOWN = 7;
    public static final int CURRENT_STATUS_UPLOADING = 8;
    public static final int CURRENT_STATUS_HELP = 9;

    private static final String SERVER_IP = "server_ip";
    private static final String SERVER_PORT = "server_port";
    private static final String SERVER_SAVE = "SERVER_save";
    private static final String SERVER_CHANGE_EDIT = "SERVER_CHANGE_EDIT";

    private static final String TEST_IP = "192.168.0.24";
    private static final String TEST_PORT = "6028";
//                private static final String TEST_IP = "";
//    private static final String TEST_PORT = "";
    //拍照
    public static final String USB_CAMERA_TAKE_PHOTO = "==#";
    //返回上一步
    public static final String USB_CAMERA_PHOTO_PREVIOUS = "1#";
    //下一步
    public static final String USB_CAMERA_PHOTO_NEXT = "3#";
    //确定
    public static final String USB_CAMERA_PHOTO_COMMIT = "9#";

    private static final int EDIT_SELECT_IP = 0;
    private static final int EDIT_SELECT_PORT = 1;
    private int mCurrentEditSelect = EDIT_SELECT_IP;

    private String mLastEditIp = "";
    private String mLastEditPort = "";
    private Rect mRect = new Rect();
    /**
     * 算法1.1 句柄
     */
    public int mSdkDetectCreate = 0;
    public int mSdkExtractCreate = 0;
    public int mSdkCompareCreate = 0;
    public int mSdkPredictorCreate = 0;
    private MyHandler mHandler;
    private RectView mCircleFaceView2;
    private TextView mTv_top_msg;
    private TextView mTv_pair;
    private TextView mTv_message;
    private TextView mTv_photo;
    private ImageView mImage_camera_photo;
    private TextView mTv_bottom_msg;
    private TextView mBtn_un_commit;
    private TextView mBtn_commit;
    private CameraThread mCameraThread;
    private RelativeLayout mLine_msg;
    private TextView mTv_name;
    private TextView mTv_sex_and_n;
    private TextView mTv_birthday;
    private TextView mTv_location;
    private TextView mTv_id_number;
    private TextView mTv_time;
    private TextView mTv_sign;
    private ImageView mIv_id_image;
    private LinearLayout mLine_ip;
    private EditText mEt_ip;
    private EditText mEt_port;
    private UsbThread mUsbThread;
    private TextView mTv_number_status;
    private TextView mTv_id_status;
    private TextView mTv_input;
    private ImageView mImage_1;
    private ImageView mImage_2;
    private ImageView mImage_3;
    private AlertDialog mFailDialog;
    private AlertDialog mSuccessDialog;
    private TextView mTv_help;
    private TextView mTv_now_location;


    public static class MyHandler extends Handler {
        private WeakReference<CameraPhotoActivity> mWeakReference;

        public MyHandler(Looper looper, CameraPhotoActivity activity) {
            super(looper);
            mWeakReference = new WeakReference<CameraPhotoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CameraPhotoActivity cameraPhotoActivity = mWeakReference.get();
            if (cameraPhotoActivity == null) {
                LogUtils.d(TAG, "====== cameraPhotoActivity == null");
                return;
            }
            switch (msg.what) {
                case HANDLER_COUNT_DOWN:
                    cameraPhotoActivity.countDown(msg);
                    break;
                case HANDLER_STOP_PHOTO:
                    //返回默认状态
                    LogUtils.d(TAG, "====== 没有找到人脸,返回默认状态");
                    cameraPhotoActivity.backToDefaultStatus();
                    break;
                case Const.HANDLER_INIT_SDK_FIRST:
                    cameraPhotoActivity.initSDKFirst();
                    break;
                case Const.HANDLER_INIT_SDK:
                    cameraPhotoActivity.initSDK(msg);
                    break;
                case HANDLER_CLEAR_LAST_EDIT:
                    cameraPhotoActivity.mLastEditMsg = "";
                    ToastUtil.showToast(cameraPhotoActivity, cameraPhotoActivity.getString(R.string.input_now) + cameraPhotoActivity.mLastEditMsg);
                    break;
                case HANDLER_CLEAR_LAST_ALL_MSG:
                    cameraPhotoActivity.mLastAllMessage = "";
                    cameraPhotoActivity.mTv_input.setText(cameraPhotoActivity.getString(R.string.camera_now_input) + cameraPhotoActivity.mLastAllMessage + cameraPhotoActivity.mLastEditMsg);
                    break;
                case HANDLER_UPLOAD_FAIL:
                    try {
                        cameraPhotoActivity.mFailDialog.dismiss();
                    } catch (Exception e) {
                        LogUtils.d(TAG, "====== HANDLER_UPLOAD_FAIL = " + e.getMessage());
                    }
                    cameraPhotoActivity.backToDefaultStatus();
                    break;
                case HANDLER_UPLOAD_SUCCESS:
                    try {
                        cameraPhotoActivity.mSuccessDialog.dismiss();
                    } catch (Exception e) {
                        LogUtils.d(TAG, "====== HANDLER_UPLOAD_SUCCESS = " + e.getMessage());
                    }
                    cameraPhotoActivity.backToDefaultStatus();
                    cameraPhotoActivity.setTakePhoto((UsbEvent) msg.obj);
                    break;
                case HANDLER_UPLOAD_PERSON_SUCCESS:
                    try {
                        cameraPhotoActivity.mSuccessDialog.dismiss();
                    } catch (Exception e) {
                        LogUtils.d(TAG, "====== HANDLER_UPLOAD_SUCCESS = " + e.getMessage());
                    }
                    cameraPhotoActivity.backToDefaultStatus();
                    break;
                default:
                    break;
            }
        }
    }

    private FrameLayout mFrameLayout;
    private int mOpenDoorType;
    private CameraPreview2 mCameraPreview2;

    @Override
    public int getLayout() {
        return R.layout.activity_camera_photo;
    }

    @Override
    public void init() {
        mHandler = new MyHandler(Looper.getMainLooper(), this);
        EventBus.getDefault().register(this);
        Const.PERSON_TYPE_CAMERA_PHOTO = true;
        mUsbThread = new UsbThread();
        mUsbThread.start();
    }

    @Override
    public void initView() {
        HWUtil.hideStatusBarAndNaviBar(this);
    }

    @Override
    public void firstResume() {

        mLastEditIp = (String) SPUtils.get(this, SERVER_IP, TEST_IP);
        mLastEditPort = (String) SPUtils.get(this, SERVER_PORT, TEST_PORT);

        mFrameLayout = findViewById(R.id.framelayout);
        ImageView imageBG = findViewById(R.id.imageBG);
        ImageView image_half = findViewById(R.id.image_half);
        TextView tv_title = findViewById(R.id.tv_title);
        mImage_1 = findViewById(R.id.image_1);
        mImage_2 = findViewById(R.id.image_2);
        mImage_3 = findViewById(R.id.image_3);
        ImageView image_rect_bg = findViewById(R.id.image_rect_bg);
        ImageView image_title_bg = findViewById(R.id.image_title_bg);
        mLine_msg = findViewById(R.id.line_msg);
        mLine_ip = findViewById(R.id.line_ip);
        mEt_ip = findViewById(R.id.et_ip);
        mEt_port = findViewById(R.id.et_port);
        if (TextUtils.isEmpty(mLastEditIp) || TextUtils.isEmpty(mLastEditPort)) {
            //当前服务器ip地址和端口没有,需要输入
            mLine_ip.setVisibility(View.VISIBLE);
        }
        mEt_ip.setText(mLastEditIp);
        mEt_ip.setText(mLastEditPort);

        mTv_number_status = findViewById(R.id.tv_number_status);
        mTv_id_status = findViewById(R.id.tv_id_status);
        mTv_input = findViewById(R.id.tv_input);

        mImage_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new UsbEvent(USB_CAMERA_PHOTO_PREVIOUS));
            }
        });
        mImage_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new UsbEvent(USB_CAMERA_PHOTO_NEXT));
            }
        });
        mImage_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new UsbEvent(USB_CAMERA_PHOTO_COMMIT));
            }
        });

        mTv_name = findViewById(R.id.tv_name);
        mTv_sex_and_n = findViewById(R.id.tv_sex_and_n);
        mTv_birthday = findViewById(R.id.tv_birthday);
        mTv_location = findViewById(R.id.tv_location);
        mTv_id_number = findViewById(R.id.tv_id_number);
        mTv_time = findViewById(R.id.tv_time);
        mTv_sign = findViewById(R.id.tv_sign);
        mIv_id_image = findViewById(R.id.iv_id_image);
        mTv_now_location = findViewById(R.id.tv_now_location);

        mTv_bottom_msg = findViewById(R.id.tv_bottom_msg);
        mBtn_un_commit = findViewById(R.id.btn_un_commit);
        mBtn_commit = findViewById(R.id.btn_commit);

        mImage_camera_photo = findViewById(R.id.image_camera_photo);
        mCircleFaceView2 = findViewById(R.id.circleFaceView);
        mTv_pair = findViewById(R.id.tv_pair);
        mTv_message = findViewById(R.id.tv_message);
        mTv_photo = findViewById(R.id.tv_photo);

        mTv_top_msg = findViewById(R.id.tv_top_msg);
        mTv_top_msg.setText(R.string.camera_title_first);

        mTv_help = findViewById(R.id.tv_help);
        mTv_help.setText("服务器配置界面 : " +
                "\n*  : 表示 ." +
                "\n#* : 删除最后一个字符" +
                "\n#8 : 清空当前输入的内容" +
                "\n#0 : 切换输入框" +
                "\n#9 : 保存数据" +
                "\n## : 打开帮助界面" +
                "\n" +
                "\n人脸+身份证识别界面 : " +
                "\n1# : 上一步" +
                "\n3# : 下一步" +
                "\n9# : 确定" +
                "\n4$ : 打开补光灯" +
                "\n5# : 关闭补光灯" +
                "\n****# : 打开服务器配置界面" +
                "\n## : 打开帮助界面" +
                "\n" +
                "\n默认3秒后清空输入数据" +
                "\n左下角显示查看当前数字键和身份证模块的连接状态");

        Glide.with(this).load(R.mipmap.ic_bg).into(imageBG);
        Glide.with(this).load(R.mipmap.ic_circle_half).into(image_half);
        Glide.with(this).load(R.mipmap.ic_top_bg).into(image_title_bg);
        Glide.with(this).load(R.mipmap.ic_out_circle_bg).into(image_rect_bg);

        setSelect(0);
        //延时5s初始化摄像头
        mTv_bottom_msg.setText(getString(R.string.device_ini));
        mHandler.sendEmptyMessageDelayed(Const.HANDLER_INIT_SDK_FIRST, Const.HANDLER_DELAY_TIME_3000);
    }

    @Override
    public void resume() {

    }

    @Override
    protected void onDestroy() {
        if (mUsbThread != null) {
            mUsbThread.close();
        }
        EventBus.getDefault().unregister(this);
        closeCamera();
        if (mCameraThread != null) {
            mCameraThread.close();
        }
        Const.PERSON_TYPE_CAMERA_PHOTO = false;
        FaceUtil.getInstance().releaseAll();
        SerialThread.getInstance().close(true);
        HWUtil.showStatusBarAndNaviBar(this);
        super.onDestroy();
        System.exit(0);
    }

    private void setSelect(int index) {
        mTv_pair.setTextSize(index == 0 ? DensityUtil.px2sp(this, TEXT_SIZE_SELECT) : DensityUtil.px2sp(this, TEXT_SIZE_DEFAULT));
        mTv_photo.setTextSize(index == 1 ? DensityUtil.px2sp(this, TEXT_SIZE_SELECT) : DensityUtil.px2sp(this, TEXT_SIZE_DEFAULT));
        mTv_message.setTextSize(index == 2 ? DensityUtil.px2sp(this, TEXT_SIZE_SELECT) : DensityUtil.px2sp(this, TEXT_SIZE_DEFAULT));

        Glide.with(this).load(index == 0 ? R.mipmap.ic_face_pair_select : R.mipmap.ic_face_pair_un_select).into(mImage_1);
        Glide.with(this).load(index == 1 ? R.mipmap.ic_photo_select : R.mipmap.ic_photo_un_select).into(mImage_2);
        Glide.with(this).load(index == 2 ? R.mipmap.ic_message_select : R.mipmap.ic_message_un_select).into(mImage_3);
    }

    public void countDown(Message msg) {
        if (msg.arg1 == 0) {
            LogUtils.d(TAG, "====== 倒数完成,准备开始拍照 ======");
            CURRENT_STATUS = CURRENT_STATUS_PREVIEW;
            mTv_bottom_msg.setText(R.string.camera_take_photo);
        } else {
            LogUtils.d(TAG, "====== 准备拍照 倒数 - " + msg.arg1 + " ======");
            mTv_bottom_msg.setText(getString(R.string.camera_take_photo_count_down) + msg.arg1);
            Message message = Message.obtain();
            message.what = HANDLER_COUNT_DOWN;
            message.arg1 = msg.arg1 - 1;
            mHandler.sendMessageDelayed(message, Const.HANDLER_DELAY_TIME_1000);
        }
    }

    private CameraPhotoEvent mCameraPhotoEvent;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cameraEvent(CameraPhotoEvent cameraPhotoEvent) {
        if (cameraPhotoEvent.getType() == CameraPhotoEvent.CODE_FAIL) {
            LogUtils.d(TAG, "====== 比对失败 ======");
            CURRENT_STATUS = CURRENT_STATUS_PREVIEW;
            mImage_camera_photo.setVisibility(View.INVISIBLE);
            mHandler.removeMessages(HANDLER_STOP_PHOTO);
            mCameraPhotoEvent = null;
            backToDefaultStatus();
        } else if (cameraPhotoEvent.getType() == CameraPhotoEvent.CODE_SUCCESS) {
            LogUtils.d(TAG, "====== 比对成功 ======");
            mHandler.removeMessages(HANDLER_STOP_PHOTO);
            mCircleFaceView2.setRect(mRect, Color.RED);
            CURRENT_STATUS = CURRENT_STATUS_CAMERA_IMAGE;
            mTv_top_msg.setText(R.string.camera_msg_commit);
            mBtn_commit.setVisibility(View.VISIBLE);
            mBtn_commit.setText(R.string.camera_next);
            mBtn_un_commit.setVisibility(View.VISIBLE);
            mBtn_un_commit.setText(R.string.camera_previous);
            mTv_pair.setTextSize(DensityUtil.px2sp(this, TEXT_SIZE_DEFAULT));
            mTv_message.setTextSize(DensityUtil.px2sp(this, TEXT_SIZE_DEFAULT));
            mTv_photo.setTextSize(DensityUtil.px2sp(this, TEXT_SIZE_SELECT));
            mTv_bottom_msg.setVisibility(View.INVISIBLE);
            if (TextUtils.isEmpty(cameraPhotoEvent.getImagePath())) {
                backToDefaultStatus();
                ToastUtil.showToast(this, getString(R.string.camera_id_photo_error));
            } else {
                mImage_camera_photo.setVisibility(View.VISIBLE);
                setSelect(1);
                Glide.with(this).load(cameraPhotoEvent.getImagePath()).into(mImage_camera_photo);
                mCameraPhotoEvent = cameraPhotoEvent;
            }
        }
    }

    /**
     * 摄像头预览数据回调
     *
     * @param bytes
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (bytes == null) {
            LogUtils.d(TAG, "========= onPreviewFrame null");
            return;
        }
        if (CURRENT_STATUS == CURRENT_STATUS_PREVIEW || CURRENT_STATUS == CURRENT_STATUS_CHECK_IMAGE) {
            //当前处于人脸比对的状态,需要抓取数据
            if (Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI)) {

                FaceUtils.getInstance().IFaceRecSDK_FaceCaptureReq(FaceUtil.mSdkCaptureCreate,
                        bytes,
                        Const.CAMERA_PREVIEW_WIDTH,
                        Const.CAMERA_PREVIEW_HEIGHT);
            } else {
                if (FaceUtil.getInstance().getFaceApiCamera() == null) {
                    return;
                }
                FaceRect rect = FaceUtil.getInstance().getFaceApiCamera().MaxFaceFeatureDetectImage(bytes, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
                if (rect == null || rect.rect == null || rect.rect.width() == 0 || rect.rect.height() == 0) {
                    mCircleFaceView2.setRect(mRect, Color.RED);
                    return;
                }
                mCircleFaceView2.setRect(((FaceRect) (rect)).rect, Color.RED);
                if (CURRENT_STATUS == CURRENT_STATUS_CHECK_IMAGE) {
                    return;
                }
                SoundUtil.soundShutter(0);
                mTv_bottom_msg.setText(R.string.camera_pairing_photo);
                CURRENT_STATUS = CURRENT_STATUS_CHECK_IMAGE;
                ThreadManager.getArrayBlockingQueue(CameraThread.class.getSimpleName()).add(new CameraPreviewBean(bytes, rect, Const.PAIR_TYPE_ID));
            }
        }
    }

    @Override
    public void onTrackerListener(byte[] bytes, int i, int i1, com.intellif.FaceRect[] faceRects) {
        if (faceRects == null || faceRects.length == 0) {
            return;
        }
        mCircleFaceView2.setRect(new Rect(faceRects[0].dRectLeft, faceRects[0].dRectTop, faceRects[0].dRectRight, faceRects[0].dRectBottom), Color.RED);
        if (CURRENT_STATUS == CURRENT_STATUS_CHECK_IMAGE || CURRENT_STATUS == CURRENT_STATUS_CAMERA_IMAGE) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTv_bottom_msg.setText(R.string.camera_pairing_photo);
            }
        });
        SoundUtil.soundShutter(0);
        CURRENT_STATUS = CURRENT_STATUS_CHECK_IMAGE;
        ThreadManager.getArrayBlockingQueue(CameraThread.class.getSimpleName()).add(new CameraPreviewBean(bytes, faceRects[0], Const.PAIR_TYPE_ID));

    }

    private void initCamera() {
        SurfaceView surfaceView = new SurfaceView(this);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
        mCameraPreview2 = new CameraPreview2(0, this);
        mFrameLayout.addView(surfaceView);
    }

    private void closeCamera() {
        LogUtils.d(TAG, "关闭摄像头,清空数据");
        if (mFrameLayout != null) {
            mFrameLayout.removeAllViews();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.i(TAG, "=============== surfaceCreated ===============");

        if (mCameraPreview2 != null) {
            mCameraPreview2.startPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.i(TAG, "=============== surfaceChanged ===============");
        if (mCameraPreview2 != null) {
            mCameraPreview2.stopPreview(false);
            mCameraPreview2.setParameters(width, height);
            mCameraPreview2.startPreview(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.i(TAG, "=============== surfaceDestroyed ===============");
        if (mCameraPreview2 != null) {
            mCameraPreview2.stopPreview(true);
        }
    }

    private void backToDefaultStatus() {
        CURRENT_STATUS = CURRENT_STATUS_DEFAULT;
        mTv_top_msg.setText(R.string.camera_msg_second);
        mImage_camera_photo.setVisibility(View.INVISIBLE);
        mLine_msg.setVisibility(View.INVISIBLE);
        setSelect(0);
        mTv_bottom_msg.setText("");
        mBtn_commit.setText(R.string.camera_input_id_card);
        mBtn_commit.setVisibility(View.VISIBLE);
        mBtn_un_commit.setVisibility(View.INVISIBLE);
        mCircleFaceView2.setRect(mRect, Color.RED);
    }

    private void setIdMessage(IDBean idBean) {
        mTv_name.setText(getString(R.string.camera_name) + idBean.getName());
        mTv_sex_and_n.setText(getString(R.string.camera_sex) + idBean.getSex() + getString(R.string.camera_nation) + idBean.getNation());
        mTv_birthday.setText(getString(R.string.camera_birthday) + idBean.getBirthday());
        mTv_location.setText(getString(R.string.camera_location) + idBean.getLocal());
        mTv_id_number.setText(getString(R.string.camera_id_number) + idBean.getIdNumber());
        mTv_time.setText(getString(R.string.camera_id_time) + idBean.getValidityTime());
        mTv_sign.setText(getString(R.string.camera_sign) + idBean.getSigningOrganization());
        mTv_now_location.setText(getString(R.string.camera_now_location) + idBean.getNowLocation());
        Glide.with(this).load(idBean.getImage()).into(mIv_id_image);
    }

    private void initSDKFirst() {
        Message message = Message.obtain();
        message.what = Const.HANDLER_INIT_SDK;
        try {
            if (Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI)) {
                mTv_bottom_msg.setText(getStrings(Const.SDK_INIT_START_YUN_TIAN_LI_FEI));
                FaceUtil.getInstance().init(Const.SDK_INIT_START_YUN_TIAN_LI_FEI);
                message.arg1 = Const.SDK_INIT_START_YUN_TIAN_LI_FEI;
            } else {
                mTv_bottom_msg.setText(getStrings(Const.SDK_INIT_START_HONG_RUAN));
                FaceUtil.getInstance().init(Const.SDK_INIT_START_HONG_RUAN);
                message.arg1 = Const.SDK_INIT_START_HONG_RUAN;
            }
            mHandler.sendMessageDelayed(message, Const.HANDLER_DELAY_TIME_2000);
        } catch (Exception e) {
            LogUtils.e(TAG, "initSurfaceAndCamera error = " + e.getMessage());
            mTv_bottom_msg.setText(getString(R.string.camera_init_error) + e.getMessage());
        }
    }

    public String getStrings(int number) {
        return getString(getResources().getIdentifier("sdk_init_" + number, "string", AppUtil.getPackageName()));
    }

    private boolean mFaceSdkInitSuccess = false;

    private void initSDK(Message oldMessage) {
        LogUtils.e(TAG, "====== SDK 初始化没有结束,上次执行的是 = " + oldMessage.arg1);
        try {
            Message newMessage = Message.obtain();
            newMessage.what = Const.HANDLER_INIT_SDK;
            newMessage.arg1 = oldMessage.arg1 + 1;
            int init = FaceUtil.getInstance().init(newMessage.arg1);
            if (init != Const.SDK_INIT_END) {
                mTv_bottom_msg.setText(getStrings(newMessage.arg1));
                mHandler.sendMessageDelayed(newMessage, Const.HANDLER_DELAY_TIME_2000);
            } else {
                LogUtils.e(TAG, "====== SDK 初始化结束 = " + oldMessage.arg1);
                if (Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI)) {
                    LogUtils.e(TAG, "====== SDK 初始化结束 = IFaceRecSDK_SetCaptureCallBack");
                    FaceUtils.getInstance().IFaceRecSDK_SetCaptureCallBack(FaceUtil.mSdkCaptureCreate, this);
                }
                mCameraThread = new CameraThread();
                mCameraThread.init(false);
                mCameraThread.start();
                initCamera();
                mFaceSdkInitSuccess = true;
                mTv_bottom_msg.setText("");
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "initSDK error = " + e.getMessage());
            mTv_bottom_msg.setText(getString(R.string.camera_init_error) + e.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hardWareStatus(HardwareStatusEvent hardwareStatusEvent) {
        if (hardwareStatusEvent.getType() == HardwareStatusEvent.HARDWARE_STATUS_NUMBER) {
            mTv_number_status.setText(getString(R.string.camera_number_input) + hardwareStatusEvent.getCodeConnect() + "-" + hardwareStatusEvent.getCodeRead());
        } else if (hardwareStatusEvent.getType() == HardwareStatusEvent.HARDWARE_STATUS_ID) {
            mTv_id_status.setText(getString(R.string.camera_id_model) + hardwareStatusEvent.getCodeConnect() + "-" + hardwareStatusEvent.getCodeRead());

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void usbEvent(UsbEvent usbEvent) {
        if (mLine_ip.getVisibility() == View.VISIBLE) {
            //表示当前小键盘输入是给editText控件的
            System.out.println("============== mLine_ip " + usbEvent.getMsg());
            handleEditText(usbEvent);
        } else {
            //表示正常的逻辑
            System.out.println("============== other  " + usbEvent.getMsg());
            handleOtherMsg(usbEvent);
        }
    }


    private void handleEditText(UsbEvent usbEvent) {
        if (usbEvent.getMsg().equals(USB_CAMERA_TAKE_PHOTO)) {
            LogUtils.d(TAG, "====== 当前是服务器IP输如界面,不显示拍照 6# ======");
            return;
        }
        if (mCurrentEditSelect == EDIT_SELECT_IP) {
            mEt_ip.setFocusable(true);
            mEt_ip.setFocusableInTouchMode(true);
            mEt_ip.requestFocus();
            String msg = parseEditMsg(mLastEditIp, usbEvent.getMsg());
            if (msg.equals(SERVER_CHANGE_EDIT)) {
                mCurrentEditSelect = EDIT_SELECT_PORT;
            } else if (msg.equals(SERVER_SAVE) && checkEdit()) {
                saveEdit(mEt_ip.getText().toString(), mEt_port.getText().toString());
            } else {
                mEt_ip.setText(msg);
                mEt_ip.setSelection(msg.length());
                mLastEditIp = mEt_ip.getText().toString();
            }
        } else {
            mEt_port.setFocusable(true);
            mEt_port.setFocusableInTouchMode(true);
            mEt_port.requestFocus();
            String msg = parseEditMsg(mLastEditPort, usbEvent.getMsg());
            if (msg.equals(SERVER_CHANGE_EDIT)) {
                mCurrentEditSelect = EDIT_SELECT_IP;
            } else if (msg.equals(SERVER_SAVE) && checkEdit()) {
                saveEdit(mLastEditIp, mLastEditPort);
            } else {
                mEt_port.setText(msg);
                mEt_port.setSelection(msg.length());
                mLastEditPort = mEt_port.getText().toString();
            }
        }
        mTv_input.setText(getString(R.string.camera_now_input) + mLastAllMessage + mLastEditMsg);
    }

    private String mLastEditMsg = "";

    private String parseEditMsg(String old, String msg) {
        mTv_help.setVisibility(View.INVISIBLE);
        if (TextUtils.isEmpty(mLastEditMsg) && msg.equals("#")) {
            LogUtils.d(TAG, "====== 当前输入 # ======");
            mLastEditMsg = "#";
            //元数据返回,等待下一次输入
            mHandler.sendEmptyMessageDelayed(HANDLER_CLEAR_LAST_EDIT, Const.HANDLER_DELAY_TIME_3000);
            return old;
        }
        if (mLastEditMsg.equals("#") && msg.equals("*")) {
            LogUtils.d(TAG, "====== 后退一格 #* ======");
            mLastEditMsg = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_EDIT);
            return old.length() > 0 ? old.substring(0, old.length() - 1) : "";
        } else if (mLastEditMsg.equals("#") && msg.equals("8")) {
            LogUtils.d(TAG, "====== 清空所有 #8 ======");
            mLastEditMsg = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_EDIT);
            return "";
        } else if (mLastEditMsg.equals("#") && msg.equals("0")) {
            LogUtils.d(TAG, "====== 切换控件 #0 ======");
            mLastEditMsg = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_EDIT);
            return SERVER_CHANGE_EDIT;
        } else if (mLastEditMsg.equals("#") && msg.equals("9")) {
            LogUtils.d(TAG, "====== 保存数据 #9 ======");
            mLastEditMsg = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_EDIT);
            return SERVER_SAVE;
        } else if (msg.equals("*")) {
            LogUtils.d(TAG, "====== * 转成 . ======");
            mLastEditMsg = "";
            return old + ".";
        } else if (mLastEditMsg.equals("#") && msg.equals("#")) {
            LogUtils.d(TAG, "====== 打开帮助界面 ======");
            mLastEditMsg = "";
            mTv_help.setVisibility(View.VISIBLE);
            return old;
        }
        LogUtils.d(TAG, "====== 输入框 正常数据 ======");
        mHandler.removeMessages(HANDLER_CLEAR_LAST_EDIT);
        mLastEditMsg = "";
        return old + msg;
    }

    private boolean checkEdit() {
        return !TextUtils.isEmpty(mEt_ip.getText().toString()) && !TextUtils.isEmpty(mEt_port.getText().toString());
    }

    private void saveEdit(String ip, String port) {

        SPUtils.put(this, SERVER_IP, ip);
        SPUtils.put(this, SERVER_PORT, port);
        mLine_ip.setVisibility(View.INVISIBLE);
    }

    private String mLastAllMessage = "";

    private void handleOtherMsg(UsbEvent usbEvent) {
        if (!mFaceSdkInitSuccess) {
            LogUtils.d(TAG, "====== 初始化未完成或失败 ======");
            return;
        }
        mLastAllMessage = mLastAllMessage + usbEvent.getMsg();
        mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        mHandler.sendEmptyMessageDelayed(HANDLER_CLEAR_LAST_ALL_MSG, Const.HANDLER_DELAY_TIME_3000);
        mTv_help.setVisibility(View.INVISIBLE);
        if (mLastAllMessage.length() >= 7) {
            LogUtils.d(TAG, "====== 数据过长,清空数据 ======");
            mLastAllMessage = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        } else if (mLastAllMessage.equals("==#")) {
            LogUtils.d(TAG, "====== 拍照 ======");
            try {
                getMessage(usbEvent);
            } catch (Exception e) {
                handleCheckPersonFail(getString(R.string.camera_error_1), e.getMessage());
            }
            mLastAllMessage = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        } else if (mLastAllMessage.equals("1#")) {
            LogUtils.d(TAG, "====== 上一步 ======");
            setPrevious();
            mLastAllMessage = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        } else if (mLastAllMessage.equals("3#")) {
            LogUtils.d(TAG, "====== 下一步 ======");
            setNext();
            mLastAllMessage = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        } else if (mLastAllMessage.equals("9#")) {
            LogUtils.d(TAG, "====== 确定 ======");
            try {
                postPerson(mPersonId, mCameraPhotoEvent);
            } catch (Exception e) {
                handleCheckPersonFail(getString(R.string.camera_error_1), e.getMessage());
            }
            mLastAllMessage = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        } else if (mLastAllMessage.equals("****#")) {
            LogUtils.d(TAG, "====== 打开服务器配置界面 ======");
            setServer();
            mLastAllMessage = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        } else if (mLastAllMessage.equals("##")) {
            LogUtils.d(TAG, "====== 打开帮助界面 ======");
            mTv_help.setVisibility(View.VISIBLE);
            mLastAllMessage = "";
        } else if (mLastAllMessage.equals("4#")) {
            LogUtils.d(TAG, "====== 打开补光灯 ======");
            int i = HwitManager.HwitGetIOValue(3);
            if (i == 0) {
                HwitManager.HwitSetIOValue(3, 1);
            }
            mLastAllMessage = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        } else if (mLastAllMessage.equals("5#")) {
            LogUtils.d(TAG, "====== 关闭补光灯 ======");
            HWUtil.closeFillLight();
            mLastAllMessage = "";
            mHandler.removeMessages(HANDLER_CLEAR_LAST_ALL_MSG);
        }
        mTv_input.setText(getString(R.string.camera_now_input) + mLastAllMessage + mLastEditMsg);
    }

    private void showHelp() {

    }

    private void setServer() {
        if (mLine_ip.getVisibility() == View.VISIBLE) {
            return;
        }
        mLastEditIp = (String) SPUtils.get(this, SERVER_IP, "");
        mLastEditPort = (String) SPUtils.get(this, SERVER_PORT, "");
        mEt_ip.setText(mLastEditIp);
        mEt_port.setText(mLastEditPort);
        mLine_ip.setVisibility(View.VISIBLE);
    }

    private void setPrevious() {
        if (CURRENT_STATUS == CURRENT_STATUS_CAMERA_IMAGE) {
            //当前是显示拍照后的图片状态
            backToDefaultStatus();
        } else if (CURRENT_STATUS == CURRENT_STATUS_SHOW_ID_MESSAGE) {
            //当前是显示身份证信息的状态,上一步是显示图片信息
            setSelect(1);
            mLine_msg.setVisibility(View.INVISIBLE);
            mTv_top_msg.setText(R.string.camera_msg_three);
            mBtn_commit.setText(R.string.camera_next);
            CURRENT_STATUS = CURRENT_STATUS_CAMERA_IMAGE;
        }
    }

    private void setNext() {
        if (CURRENT_STATUS == CURRENT_STATUS_CAMERA_IMAGE) {
            //当前状态是显示拍照后的图片状态,下一步显示身份证信息
            setSelect(2);
            mLine_msg.setVisibility(View.VISIBLE);
            mTv_top_msg.setText(R.string.camera_msg_four);
            mBtn_commit.setText(R.string.camera_commmit);
            CURRENT_STATUS = CURRENT_STATUS_SHOW_ID_MESSAGE;
        }
    }

    private void setTakePhoto(UsbEvent usbEvent) {
        //不是默认状态下不能拍照
        if (CURRENT_STATUS != CURRENT_STATUS_DEFAULT || mLine_ip.getVisibility() == View.VISIBLE) {
            LogUtils.d(TAG, "====== 当前状态不允许拍照 ====== ");
            return;
        }
        LogUtils.d(TAG, "====== 检测到身份证,准备拍照 ======");
        mHandler.sendEmptyMessageDelayed(HANDLER_STOP_PHOTO, Const.HANDLER_DELAY_TIME_10000);
        setIdMessage(usbEvent.getIDBean());
        mTv_bottom_msg.setVisibility(View.VISIBLE);
        //拍照
        mBtn_commit.setVisibility(View.INVISIBLE);
        mBtn_un_commit.setVisibility(View.INVISIBLE);
        CURRENT_STATUS = CURRENT_STATUS_COUNT_DOWN;
        Message message = Message.obtain();
        message.what = HANDLER_COUNT_DOWN;
        message.arg1 = 3;
        countDown(message);
    }

    private void showProgressBar(String title, String msg) {
        mProgressDialog = new ProgressDialog(CameraPhotoActivity.this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public String getCheckSum(String path, String body) {
        //接口地址

        String key = "4200efc681e2d01dca19dea30f2bca6b";
        String checkSum = "";
        if (TextUtils.isEmpty(body)) {
            checkSum = md5(path + key);
        } else {
            checkSum = md5(path + body + key);
        }
        return checkSum;
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    private ProgressDialog mProgressDialog;

    public void getMessage(final UsbEvent usbEvent) {
        if (CURRENT_STATUS != CURRENT_STATUS_DEFAULT || mLine_ip.getVisibility() == View.VISIBLE) {
            LogUtils.d(TAG, "====== 当前状态不允许刷身份证 ====== ");
            return;
        }
        LogUtils.d(TAG, "====== 查询人员是否存在 ====== " + usbEvent.getIDBean().getIdNumber());
        CURRENT_STATUS = CURRENT_STATUS_UPLOADING;
        showProgressBar(getString(R.string.camera_check_person), "");
        String ipAddress = (String) SPUtils.get(this, SERVER_IP, TEST_IP);
        String ipPort = (String) SPUtils.get(this, SERVER_PORT, TEST_PORT);
        String path = "/personnel/id_number_query";
        String url = "http://" + ipAddress + ":" + ipPort + path;
        String body = "id_number=" + usbEvent.getIDBean().getIdNumber();
        System.out.println("body = " + body);
        String checkSum = getCheckSum(path, body);
        url = url + "?checksum=" + checkSum;
        LogUtils.d(TAG, "url = " + url);
        FormBody build = null;
        try {
            build = new FormBody.Builder()
                    .addEncoded("id_number", URLEncoder.encode(usbEvent.getIDBean().getIdNumber(), "utf-8"))
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(ipAddress) || TextUtils.isEmpty(ipPort) || build == null) {
            handleCheckPersonFail(getString(R.string.camera_less_param), "");
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(build)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "连接失败 = " + e.getMessage() + " call ");
                handleCheckPersonFail(getString(R.string.camera_connect_fail), "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.d(TAG, "请求成功");

                ResponseBody body1 = response.body();
                if (body1 == null) {
                    handleCheckPersonFail(getString(R.string.camera_data_error), "数据为空");
                    return;
                }
                String msg = body1.string();
                try {
                    LogUtils.d(TAG, "====== 请求成功 = " + msg);
                    boolean b = handleCheckPersonSuccess(usbEvent, msg);
                    if (!b) {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    LogUtils.d(TAG, "====== getMessage = " + e.getMessage());
                    handleCheckPersonFail(getString(R.string.camera_data_error), "数据处理异常");
                }
            }
        });
    }

    private void handleCheckPersonFail(final String title, final String msg) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mCameraPhotoEvent = null;
        mPersonId = -1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFailDialog = new AlertDialog.Builder(CameraPhotoActivity.this).setTitle(title).setMessage(msg).show();
                mHandler.sendEmptyMessageDelayed(HANDLER_UPLOAD_FAIL, Const.HANDLER_DELAY_TIME_1000);
            }
        });
    }

    private int mPersonId = -1;

    private boolean handleCheckPersonSuccess(final UsbEvent usbEvent, String msg) {

        if (TextUtils.isEmpty(msg)) {
            LogUtils.d(TAG, "====== handleCheckPersonSuccess 返回数据为空 return");
            handleCheckPersonFail(getString(R.string.camera_not_exist), "");
            return false;
        }
        System.out.println(" ==== " + msg);
        PersonInsertBean personInsertBean = new Gson().fromJson(msg, PersonInsertBean.class);
        if (personInsertBean == null) {
            LogUtils.d(TAG, "====== handleCheckPersonSuccess personInsertBean = null return");
            handleCheckPersonFail(getString(R.string.camera_not_exist), "");
            return false;
        }
        if (personInsertBean.getCode() != 0) {
            LogUtils.d(TAG, "====== handleCheckPersonSuccess personInsertBean.getCode() != 0 return");
            handleCheckPersonFail(getString(R.string.camera_not_exist), "");
            return false;
        }
        PersonInsertBean.DataBean data = personInsertBean.getData();
        if (data == null) {
            LogUtils.d(TAG, "====== handleCheckPersonSuccess personInsertBean.getData() == null return");
            handleCheckPersonFail(getString(R.string.camera_not_exist), "");
            return false;
        }
        PersonInsertBean.DataBean.PersonnelBean personnel = data.getPersonnel();
        if (personnel == null) {
            LogUtils.d(TAG, "====== handleCheckPersonSuccess personnel == null return");
            handleCheckPersonFail(getString(R.string.camera_not_exist), "");
            return false;
        }
        mPersonId = personnel.getPersonnel_id();
        getLianFaKeData(usbEvent);
        return true;
    }

    private void postPerson(int personId, CameraPhotoEvent cameraPhotoEvent) {
        if (CURRENT_STATUS != CURRENT_STATUS_SHOW_ID_MESSAGE) {
            LogUtils.d(TAG, "====== 当前状态不允许修改人员 ====== " + personId);
            return;
        }
        if (cameraPhotoEvent == null || mPersonId == -1 || mPersonId == 0) {
            ToastUtil.showToast(this, "缺少信息");
            LogUtils.d(TAG, "====== 缺少信息 = " + cameraPhotoEvent + " " + mPersonId);
            return;
        }
        LogUtils.d(TAG, "====== 正在修改人员信息 ====== " + personId);
        CURRENT_STATUS = CURRENT_STATUS_UPLOADING;
        showProgressBar(getString(R.string.camera_change_person_msg), "");
        String ipAddress = (String) SPUtils.get(this, SERVER_IP, TEST_IP);
        String ipPort = (String) SPUtils.get(this, SERVER_PORT, TEST_PORT);
        String path = "/personnel/change_info";
        String url = "http://" + ipAddress + ":" + ipPort + path;
        Bitmap bitmap = BitmapFactory.decodeFile(cameraPhotoEvent.getImagePath());
        if (bitmap == null) {
            LogUtils.d(TAG, "====== bitmap = null ");
            handleCheckPersonFail(getString(R.string.camera_load_fail), "");
            return;
        }
        byte[] bytes = BitmapUtil.bitmap2Byte(bitmap);
        String imageData = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll("\\\\n", "");

        String body = null;
        try {
            body = "personnel_id=" + personId + "&base_image=" + URLEncoder.encode(imageData, "utf-8") + "&cover_type=2&like_personnel_id=" + personId;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String checkSum = getCheckSum(path, body);
        url = url + "?checksum=" + checkSum;
        FormBody build = null;
        LogUtils.d(TAG, "url = " + url);
        try {
            build = new FormBody.Builder()
                    .addEncoded("personnel_id", URLEncoder.encode(personId + "", "utf-8"))
                    .addEncoded("base_image", URLEncoder.encode(Base64.encodeToString(bytes, Base64.DEFAULT), "utf-8"))
                    .addEncoded("cover_type", URLEncoder.encode("2", "utf-8"))
                    .addEncoded("like_personnel_id", URLEncoder.encode(personId + "", "utf-8"))
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(ipAddress) || TextUtils.isEmpty(ipPort) || build == null) {
            handleCheckPersonFail(getString(R.string.camera_less_param), "");
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(build)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "连接失败");
                handleCheckPersonFail(getString(R.string.camera_connect_fail), "");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.d(TAG, "请求成功");

                ResponseBody body1 = response.body();
                if (body1 == null) {
                    handleCheckPersonFail(getString(R.string.camera_data_error), "");
                    return;
                }
                String msg = body1.string();
                try {
                    LogUtils.d(TAG, "====== 请求成功 = " + msg);
                    handlePostPersonSuccess(msg);
                } catch (Exception e) {
                    LogUtils.d(TAG, "====== getMessage = " + e.getMessage());
                    handleCheckPersonFail(getString(R.string.camera_data_error), "");
                }
            }
        });
    }

    private void handlePostPersonSuccess(String msg) {
        PostPersonBean postPersonBean = new Gson().fromJson(msg, PostPersonBean.class);
        if (postPersonBean == null) {
            handleCheckPersonFail(getString(R.string.camera_change_fail), "");
            return;
        }
        if (postPersonBean.getCode() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    mSuccessDialog = new AlertDialog.Builder(CameraPhotoActivity.this).setTitle(R.string.camera_change_success).setMessage("").show();
                    mHandler.sendEmptyMessageDelayed(HANDLER_UPLOAD_PERSON_SUCCESS, Const.HANDLER_DELAY_TIME_1000);
                }
            });
        } else {
            handleCheckPersonFail(getString(R.string.camera_change_fail), "");
        }

    }

    public void getLianFaKeData(final UsbEvent usbEvent) {

        String url = "https://lfk.fedau.com/index/api/check_user";

        FormBody build = null;
        try {
            build = new FormBody.Builder()
                    .addEncoded("idcard", URLEncoder.encode(usbEvent.getIDBean().getIdNumber(), "utf-8"))
//                    .addEncoded("idcard", URLEncoder.encode("510121198402077216", "utf-8"))
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(build)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("请求失败,联发科请求");
                handleCheckPersonFail(getString(R.string.camera_lian_fail), "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.d(TAG, "====== 请求成功,联发科请求 = ");
                ResponseBody body = response.body();
                if (body == null) {
                    handleCheckPersonFail(getString(R.string.camera_lian_fail), "");
                    return;
                }
                String string = body.string();
                handleLianFaKeSuccess(usbEvent, string);
            }
        });
    }

    private void handleLianFaKeSuccess(final UsbEvent usbEvent, String msg) {
        LianFaKeBean lianFaKeBean = null;
        LogUtils.d(TAG, "handleLianFaKeSuccess = " + msg);
        try {
            lianFaKeBean = new Gson().fromJson(msg, LianFaKeBean.class);
        } catch (Exception e) {
            LogUtils.e(TAG, "handleLianFaKeSuccess = " + e.getMessage());
        }
        if (lianFaKeBean == null) {
            handleCheckPersonFail(getString(R.string.camera_lian_fail), "");
            return;
        }
        if (lianFaKeBean.getCode() == -1) {
            handleCheckPersonFail(getString(R.string.camera_lian_fail), "");
            return;
        }
        LianFaKeBean.ResultBean result = lianFaKeBean.getResult();
        if (result == null) {
            handleCheckPersonFail(getString(R.string.camera_lian_fail), "");
            return;
        }
        final List<LianFaKeBean.ResultBean.HouseBean> house = result.getHouse();
        String houseMsg = getString(R.string.camera_unkonw);
        if (house != null && house.size() > 0) {
            houseMsg = house.get(house.size() - 1).getAddress();
        }
        final String finalHouseMsg = houseMsg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                usbEvent.getIDBean().setNowLocation(finalHouseMsg);
                setIdMessage(usbEvent.getIDBean());
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                mSuccessDialog = new AlertDialog.Builder(CameraPhotoActivity.this).setTitle(R.string.camera_check_success).setMessage("").show();
                Message message = Message.obtain();
                message.what = HANDLER_UPLOAD_SUCCESS;
                message.obj = usbEvent;
                mHandler.sendMessageDelayed(message, Const.HANDLER_DELAY_TIME_1000);
            }
        });
    }


    public static class PostPersonBean {


        /**
         * msg : 成功
         * code : 0
         * data : {"personnel_id":339}
         */

        private String msg;
        private int code;
        private DataBean data;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * personnel_id : 339
             */

            private int personnel_id;

            public int getPersonnel_id() {
                return personnel_id;
            }

            public void setPersonnel_id(int personnel_id) {
                this.personnel_id = personnel_id;
            }
        }
    }
}
