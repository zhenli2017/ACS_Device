package com.thdtek.acs.terminal.ui.cameraphoto;

import android.hardware.Camera;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intellif.FaceUtils;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
import com.thdtek.acs.terminal.bean.ConfigBean;
import com.thdtek.acs.terminal.camera.CameraPreview2;
import com.thdtek.acs.terminal.face.FaceUtil;
import com.thdtek.acs.terminal.imp.camera.CameraPreviewCallBack;
import com.thdtek.acs.terminal.thread.SerialThread;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DensityUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.view.CameraSurfaceView;
import com.thdtek.facelibrary.FaceApi;

public class CameraPhotoActivity extends BaseActivity implements CameraPreviewCallBack {

    private static final String TAG = CameraPhotoActivity.class.getSimpleName();

    //faceApi 的初始化状态,默认是初始化失败
    public long mFaceApiStatue = 1L;
    private FaceApi mFaceApi;

    private static final int HANDLER_INIT_SDK = 0;
    private static final int TEXT_SIZE_SELECT = 33;
    private static final int TEXT_SIZE_DEFAULT = 26;

    private int CURRENT_STATUS = CURRENT_STATUS_PAIR;
    private static final int CURRENT_STATUS_PAIR = 1;
    private static final int CURRENT_STATUS_ID_MESSAGE = 2;
    private static final int CURRENT_STATUS_INSERT_PERSON = 3;

    /**
     * 算法1.1 句柄
     */
    public int mSdkDetectCreate = 0;
    public int mSdkExtractCreate = 0;
    public int mSdkCompareCreate = 0;
    public int mSdkPredictorCreate = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_INIT_SDK:
                    initSDK();
                    break;
                default:
                    break;
            }

            return false;
        }
    });
    private CameraSurfaceView mCameraSurfaceView;
    private int mOpenDoorType;

    @Override
    public int getLayout() {
        return R.layout.activity_camera_photo;
    }

    @Override
    public void init() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void firstResume() {
        mCameraSurfaceView = findViewById(R.id.framelayout);
        ImageView imageBG = findViewById(R.id.imageBG);
        ImageView image_half = findViewById(R.id.image_half);
        TextView tv_title = findViewById(R.id.tv_title);
        ImageView image_1 = findViewById(R.id.image_1);
        ImageView image_2 = findViewById(R.id.image_2);
        ImageView image_3 = findViewById(R.id.image_3);
        ImageView image_rect_bg = findViewById(R.id.image_rect_bg);
        ImageView image_title_bg = findViewById(R.id.image_title_bg);

        TextView tv_pair = findViewById(R.id.tv_pair);
        TextView tv_message = findViewById(R.id.tv_message);
        TextView tv_photo = findViewById(R.id.tv_photo);

        TextView tv_top_msg = findViewById(R.id.tv_top_msg);
        tv_top_msg.setText("请将身份证放入读卡区,并将头像正对识别区域...");
        tv_pair.setTextSize(DensityUtil.px2sp(this, TEXT_SIZE_SELECT));
//        tv_top_msg.setText("请确认身份证信息是否准确...");
//        tv_top_msg.setText("请确认图片信息是否清晰准确...");

        Glide.with(this).load(R.mipmap.ic_bg).into(imageBG);
        Glide.with(this).load(R.mipmap.ic_circle_half).into(image_half);
        Glide.with(this).load(R.mipmap.ic_face_pair_select).into(image_1);
        Glide.with(this).load(R.mipmap.ic_message_un_select).into(image_2);
        Glide.with(this).load(R.mipmap.ic_photo_un_select).into(image_3);
        Glide.with(this).load(R.mipmap.ic_top_bg).into(image_title_bg);
        Glide.with(this).load(R.mipmap.ic_out_circle_bg).into(image_rect_bg);
        mCameraSurfaceView.setPreviewCallBack(this);
        //3秒后初始化SDK
        mHandler.sendEmptyMessageDelayed(HANDLER_INIT_SDK, Const.HANDLER_DELAY_TIME_3000);

    }

    @Override
    public void resume() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 摄像头预览数据回调
     *
     * @param bytes
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (CURRENT_STATUS == CURRENT_STATUS_PAIR) {
            //当前处于人脸比对的状态,需要抓取数据
            if (Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI)) {
                FaceUtil.getInstance().handleYunTianLiFei(bytes);
            } else {
                FaceUtil.getInstance().handleHongRuan(bytes);
            }
        }
    }

    private void initSDK() {
        if (Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI)) {
            LogUtils.d(TAG, "====== 初始化 算法1.1 ======");
            initYunTianLiFei();
            if (mFaceApiStatue == 0) {
                LogUtils.d(TAG, "====== 初始化 算法1.1 成功 ======");
            } else {
                LogUtils.d(TAG, "====== 初始化 算法1.1 失败 ======");
            }
        } else {
            LogUtils.d(TAG, "====== 初始化 算法1.0 ======");
            if (mFaceApiStatue == 0) {
                LogUtils.d(TAG, "====== 初始化 算法1.0 成功 ======");
                mFaceApi = initFaceApi();
            } else {
                LogUtils.d(TAG, "====== 初始化 算法1.0 失败 ======");
            }
        }
    }

    private FaceApi initFaceApi() {
        LogUtils.e(TAG, "=========== " + TAG + " 开始初始化 虹软 算法 ===========");
        FaceApi faceApi = new FaceApi();

        try {
            int faceToken = faceApi.GetToken();
            LogUtils.e(TAG, "face token = " + faceToken);
            int signToken = SerialThread.getInstance().getSignToken(faceToken);
            LogUtils.e(TAG, "signToken = " + signToken);
            mFaceApiStatue = faceApi.Init(signToken);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "=========== " + TAG + " 初始化异常 = " + e.getMessage());
        }
        if (mFaceApiStatue != 0) {
            LogUtils.e(TAG, "=========== " + TAG + " 初始化发生异常关闭 =========== " + mFaceApiStatue);
            return null;
        }
        LogUtils.e(TAG, "=========== " + TAG + " 初始化成功 =========== " + mFaceApiStatue);
        return faceApi;
    }

    private FaceUtils initYunTianLiFei() {
        FaceUtils faceUtils = FaceUtils.getInstance();
        mSdkDetectCreate = FaceUtil.mSdkDetectCreate;
        LogUtils.e(TAG, "SDK Detect Create = " + mSdkDetectCreate);
        //初始化人脸特征值提取
        mSdkExtractCreate = FaceUtil.mSdkExtractCreate;
        LogUtils.e(TAG, "SDK Extract Create = " + mSdkExtractCreate);
        //初始化人脸比对句柄
        mSdkCompareCreate = FaceUtil.mSdkCompareCreate;
        LogUtils.e(TAG, "SDK Compare Create = " + mSdkCompareCreate);
        //初始化人脸属性
        mSdkPredictorCreate = FaceUtil.mSdkPredictorCreate;
        LogUtils.e(TAG, "SDK Predictor Create = " + mSdkPredictorCreate);
        mFaceApiStatue = mSdkDetectCreate != 0 && mSdkExtractCreate != 0 && mSdkCompareCreate != 0 && mSdkPredictorCreate != 0 ? 0 : 1L;
        return faceUtils;
    }


    /**
     * 获取人脸范围
     *
     * @return
     */
    public Object getRect() {
        return null;
    }

    /**
     * 获取人脸特征值
     *
     * @return
     */
    public byte[] getFaceFeature() {
        return null;
    }

    /**
     * 获取人脸比对结果
     *
     * @return
     */
    public float getFacePair() {
        return 0;
    }


}
