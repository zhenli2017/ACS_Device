package com.thdtek.acs.terminal.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.intellif.FaceRecAttrResult;
import com.intellif.FaceTools;
import com.intellif.FaceTrackListener;
import com.intellif.FaceUtils;
import com.intellif.ImageFormat;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.CameraPreviewBean;
import com.thdtek.acs.terminal.bean.FaceAttribute;
import com.thdtek.acs.terminal.imp.camera.CameraPreviewFindFaceInterface;
import com.thdtek.acs.terminal.thread.FacePairThread9;
import com.thdtek.acs.terminal.thread.ImageSaveThread;
import com.thdtek.acs.terminal.thread.PairThreadThree;
import com.thdtek.acs.terminal.thread.PairThreadTwo;
import com.thdtek.acs.terminal.thread.ReadIcSerialThread;
import com.thdtek.acs.terminal.thread.SerialThread;
import com.thdtek.acs.terminal.thread.ThreadManager;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.camera.Bmp2YUV;
import com.thdtek.acs.terminal.util.camera.CameraUtil;
import com.thdtek.facelibrary.FaceApi;
import com.thdtek.facelibrary.FaceFeature;
import com.thdtek.facelibrary.FaceRect;

import java.io.IOException;


/**
 * Time:2018/6/20
 * User:lizhen
 * Description:
 */

public class FaceUtil implements FaceTrackListener {

    private final String TAG = FaceUtil.class.getSimpleName();
    private FaceApi mFaceApiCamera;
    private static final int THREAD_SLEEP = 500;

    private static FaceUtil mFaceUtil;
    private CameraPreviewFindFaceInterface mFindFaceInterface;
    private int mSuccessColor;
    private int mFailColor;

    private ReadIcSerialThread mReadIcSerialThread;
    private int mSdkCaptureCreate = 0;
    public static int mSdkDetectCreate = 0;
    public static int mSdkExtractCreate = 0;
    public static int mSdkCompareCreate = 0;
    public static int mSdkPredictorCreate = 0;

    private FaceUtil() {

    }

    public static FaceUtil getInstance() {
        if (mFaceUtil == null) {
            synchronized (FaceUtil.class) {
                if (mFaceUtil == null) {
                    mFaceUtil = new FaceUtil();

                }
            }
        }
        return mFaceUtil;
    }

    public void setFindFaceInterface(CameraPreviewFindFaceInterface findFaceInterface) {
        mSuccessColor = MyApplication.getContext().getResources().getColor(R.color.color_scan);
        mFailColor = Color.RED;
        mFindFaceInterface = findFaceInterface;
    }

    public int init(int initNumber) throws IOException, InterruptedException {
        LogUtils.d(TAG, "================ Face Init number = " + initNumber);
        int end = 1;
        switch (initNumber) {
            case 0:
                LogUtils.d(TAG, "====== 初始化 云天励飞 IFaceRecSDK_Init");
                int sdkInit = FaceUtils.getInstance().IFaceRecSDK_Init(Const.LICENSE_PATH, MyApplication.getContext());
                LogUtils.e(TAG, "sdkInit = " + sdkInit);
                String sdkInfo = FaceUtils.getInstance().IFaceRecSDK_GetSDKInfo();
                LogUtils.e(TAG, "sdkInfo = " + sdkInfo);
                break;
            case 1:
                LogUtils.d(TAG, "====== 初始化 IFaceRecSDK_CapturerCreate");
                mSdkCaptureCreate = FaceUtils.getInstance().IFaceRecSDK_CapturerCreate(Const.MODEL_PATH);
                FaceUtils.getInstance().IFaceRecSDK_SetCaptureCallBack(mSdkCaptureCreate, this);
                break;
            case 2:
                //人脸单图检测句柄初始化
                LogUtils.d(TAG, "====== 初始化 IFaceRecSDK_Detect_Create");
                mSdkDetectCreate = FaceUtils.getInstance().IFaceRecSDK_Detect_Create(Const.MODEL_PATH, 30);
                LogUtils.e(TAG, "SDK Detect Create = " + mSdkDetectCreate);
                break;
            case 3:
                //初始化人脸特征值提取
                LogUtils.d(TAG, "====== 初始化 IFaceRecSDK_Extract_Create");
                mSdkExtractCreate = FaceUtils.getInstance().IFaceRecSDK_Extract_Create(Const.MODEL_PATH);
                LogUtils.e(TAG, "SDK Extract Create = " + mSdkExtractCreate);
                break;
            case 4:
                //初始化人脸比对句柄
                LogUtils.d(TAG, "====== 初始化 IFaceRecSDK_Compare_Create");
                mSdkCompareCreate = FaceUtils.getInstance().IFaceRecSDK_Compare_Create(Const.MODEL_PATH);
                LogUtils.e(TAG, "SDK Compare Create = " + mSdkCompareCreate);
                break;
            case 5:
                //初始化人脸属性
                LogUtils.d(TAG, "====== 初始化 IFaceRecSDK_Predictor_Create");
                mSdkPredictorCreate = FaceUtils.getInstance().IFaceRecSDK_Predictor_Create(Const.MODEL_PATH);
                LogUtils.e(TAG, "SDK Predictor Create = " + mSdkPredictorCreate);
                break;
            case 6:
                LogUtils.d(TAG, "====== 初始化 算法1.0");
                initHongRuan(SerialThread.getInstance());
                break;
            case 7:
                LogUtils.d(TAG, "====== 初始化 facePairThread");
                FacePairThread9 facePairThread = new FacePairThread9();
                facePairThread.init(true);
                facePairThread.start();
                break;
            case 8:
                LogUtils.d(TAG, "====== 初始化 PairThreadTwo");
                PairThreadTwo pairThreadTwo = new PairThreadTwo();
                pairThreadTwo.init(false);
                pairThreadTwo.start();
                break;
            case 9:
                LogUtils.d(TAG, "====== 初始化 PairThreadThree");
                PairThreadThree pairThreadThree = new PairThreadThree();
                pairThreadThree.init(false);
                pairThreadThree.start();
                break;
            case 10:
                LogUtils.d(TAG, "====== 初始化 初始化正装照解析");
                ImageSaveThread imageSaveThread = new ImageSaveThread();
                imageSaveThread.init(false);
                imageSaveThread.start();
                break;
            case 11:
                LogUtils.d(TAG, "====== 初始化 串口读写线程");
                mReadIcSerialThread = new ReadIcSerialThread();
                mReadIcSerialThread.start();
                LogUtils.d(TAG, "====== 初始化 所有的初始化完成后需要关闭串口写出");
                SerialThread.getInstance().close(false);
                break;
            default:
                end = -1;
                break;
        }
        return end;
    }

    private long initHongRuan(SerialThread serialThread) throws InterruptedException, IOException {
        Thread.sleep(THREAD_SLEEP);
        mFaceApiCamera = new FaceApi();
        int iToken = mFaceApiCamera.GetToken();

        if (!serialThread.getFWVer()) {
            return Const.FACE_INIT_CODE_FAIL;
        }
        int signToken = serialThread.getSignToken(iToken);
        if (signToken == Const.FACE_INIT_GET_SIGN_TOKEN_FAIL) {
            return Const.FACE_INIT_CODE_FAIL;
        }
        return mFaceApiCamera.Init(signToken);
    }

    public byte[] getHongRuanImageData(byte[] imageData, boolean cameraData) {
        byte[] yuv420sp = null;
        if (cameraData) {
            yuv420sp = imageData;
        } else {
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            yuv420sp = Bmp2YUV.getYUV420sp(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1);
        }

        return yuv420sp;
    }

    public Object getHongRuanRect(Object object, byte[] imageData, boolean cameraData) {
        FaceApi faceApi = (FaceApi) object;
        byte[] yuv420sp = getHongRuanImageData(imageData, cameraData);
        FaceRect faceRect = faceApi.MaxFaceFeatureDetectImage(yuv420sp, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
        if (faceRect == null || faceRect.rect == null || faceRect.rect.width() == 0 || faceRect.rect.height() == 0) {
            return null;
        } else {
            return faceRect;
        }
    }

    public byte[] getHongRuanFaceFeature(Object object, byte[] imageData, Object faceRect, boolean cameraData) {
        FaceApi faceApi = (FaceApi) object;
        FaceRect rect = (FaceRect) faceRect;
        byte[] yuv420sp = getHongRuanImageData(imageData, cameraData);
        FaceFeature faceFeature = faceApi.ExtractMaxFaceFeatur(yuv420sp, Const.CAMERA_BITMAP_WIDTH, Const.CAMERA_BITMAP_HEIGHT, rect);
        if (faceFeature == null || faceFeature.getFeatureBytes() == null || faceFeature.getFeatureBytes().length <= 4) {
            return null;
        } else {
            return faceFeature.getFeatureBytes();
        }
    }

    public float getHongRuanPairNumber(Object object, byte[] faceFeatureOne, byte[] faceFeatureTwo) {
        FaceApi faceApi = (FaceApi) object;
        return faceApi.FacePairMatching(faceFeatureOne, faceFeatureTwo);
    }

    public Object getYunTianLiFeiRect(Object object, byte[] imageData, boolean cameraData) {
        FaceUtils faceUtils = (FaceUtils) object;
        byte[] imageClone = new byte[imageData.length];
        System.arraycopy(imageData, 0, imageClone, 0, imageClone.length);
        //云天励飞算法所有数据都是bgr格式,需要转成bgr格式
        byte[] pixelsBGR = imageClone;
        if (!cameraData) {
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageClone, 0, imageClone.length);
            pixelsBGR = FaceTools.getPixelsBGR(bitmap1);
        }
        if (pixelsBGR == null) {
            return null;
        }
        com.intellif.FaceRect[] faceRects = faceUtils.IFaceRecSDK_Face_Detect(mSdkDetectCreate, pixelsBGR, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, ImageFormat.IFACEREC_IMG_BGR);
        if (faceRects == null || faceRects.length == 0) {
            return null;
        } else {
            return faceRects[0];
        }
    }

    public byte[] getYunTianLiFeiFaceFeature(Object object, byte[] imageData, Object faceRect, boolean cameraData) {
        FaceUtils faceUtils = (FaceUtils) object;
        com.intellif.FaceRect rect = (com.intellif.FaceRect) faceRect;
        byte[] imageClone = new byte[imageData.length];
        System.arraycopy(imageData, 0, imageClone, 0, imageClone.length);

        byte[] pixelsBGR = imageClone;
        if (!cameraData) {
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageClone, 0, imageClone.length);
            pixelsBGR = FaceTools.getPixelsBGR(bitmap1);
        }
        if (pixelsBGR == null) {
            return null;
        }

        byte[] bytes = faceUtils.IFaceRecSDK_Face_Feature(mSdkExtractCreate, pixelsBGR, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, rect, ImageFormat.IFACEREC_IMG_BGR);

        if (bytes == null || bytes.length == 0) {
            return null;
        } else {
            return bytes;
        }
    }

    public FaceAttribute getYunTianLiFeiAttribute(Object object, byte[] imageData, Object faceRect, int type, boolean cameraData) {
        com.intellif.FaceRect rect = (com.intellif.FaceRect) faceRect;
        FaceUtils faceUtils = (FaceUtils) object;
        byte[] bytes = new byte[imageData.length];
        System.arraycopy(imageData, 0, bytes, 0, bytes.length);

        FaceRecAttrResult[] faceRecAttrResults = faceUtils
                .IFaceRecSDK_AttributePredict(mSdkPredictorCreate, imageData, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, rect, Const.IFACEREC_LIVE_MASK, ImageFormat.IFACEREC_IMG_BGR);
        if (type == Const.IFACEREC_QUALITY_MASK) {
            //侧脸检测
            return new FaceAttribute(faceRecAttrResults);
        } else if (type == Const.IFACEREC_LIVE_MASK) {
            //活体检测
            return new FaceAttribute(faceRecAttrResults);
        } else {
            return new FaceAttribute(faceRecAttrResults);
        }
    }

    public float getYunTianLiFeiPairNumber(Object object, byte[] faceFeatureOne, byte[] faceFeatureTwo) {
        FaceUtils faceUtils = (FaceUtils) object;
        return faceUtils.IFaceRecSDK_FeatureCompare(mSdkCompareCreate, faceFeatureOne, faceFeatureTwo);
    }


    /**
     * 释放faceApi
     *
     * @return
     */
    public void releaseAll() {
        if (mFaceApiCamera != null) {
            mFaceApiCamera.UnInit();
        }
        if (FaceUtils.getInstance() != null) {
            FaceUtils.getInstance().IFaceRecSDK_CapturerDestroy(mSdkCaptureCreate);
            //销毁特征值比对句柄
            FaceUtils.getInstance().IFaceRecSDK_Compare_Destory(mSdkCompareCreate);
            //销毁人脸特征值提取句柄
            FaceUtils.getInstance().IFaceRecSDK_Extract_Destory(mSdkExtractCreate);
            //销毁人脸单图检测句柄
            FaceUtils.getInstance().IFaceRecSDK_Detect_Destory(mSdkDetectCreate);
            //销毁人脸属性句柄
            FaceUtils.getInstance().IFaceRecSDK_Predictor_Destory(mSdkPredictorCreate);
            //销毁人脸检测
            FaceUtils.getInstance().IFaceRecSDK_UnInit();
        }
        if (mReadIcSerialThread != null) {
            mReadIcSerialThread.close();
        }
        mFaceUtil = null;
        ThreadManager.closeAll();
    }

    /**
     * 非活体检测
     *
     * @param colorByte 彩色摄像头实时预览数据
     */
    public void handleHongRuan(byte[] colorByte) {
        if (FaceTempData.getInstance().isDownLoadPersonMsg()) {
            mFindFaceInterface.findNotFace(null, Color.TRANSPARENT);
            return;
        }
        FaceRect rectColor = mFaceApiCamera.MaxFaceFeatureDetect(colorByte, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
        if (rectColor == null
                || rectColor.rect == null
                || rectColor.rect.width() == 0
                || rectColor.rect.height() == 0
//                || rectColor.rect.left * Const.CAMERA_SCALE_NUMBER + rectColor.rect.width() * Const.CAMERA_SCALE_NUMBER > Const.CAMERA_MAX_RIGHT
                ) {
            mFindFaceInterface.findNotFace(rectColor, Color.TRANSPARENT);
            return;
        }
        mFindFaceInterface.findFace(rectColor, CameraUtil.PAIR_FACE_SUCCESS_COLOR
                ? MyApplication.getContext().getResources().getColor(R.color.color_scan) : Color.RED);
        if (CameraUtil.FIND_FACE_LOCK) {
//            LogUtils.d(TAG, "刚才已经找到脸,现在是冷却时间");
            return;
        }
        if (rectColor.rect.width() != 0 && rectColor.rect.height() != 0) {
//            LogUtils.d(TAG, "检测到人脸");
            CameraUtil.FIND_FACE_LOCK = true;
        } else {
//            LogUtils.d(TAG, "没有检测到人脸,return");
            CameraUtil.FIND_FACE_LOCK = false;
            return;
        }
        mFindFaceInterface.findFaceReady();
        addToQueue(colorByte, rectColor);
    }

    /**
     * 活体检测
     *
     * @param colorByte 彩色摄像头实时预览数据
     * @param redByte   黑白摄像头实时预览数据
     */


    public void handleHongRuan(byte[] colorByte, byte[] redByte) {
        if (FaceTempData.getInstance().isDownLoadPersonMsg()) {
            mFindFaceInterface.findNotFace(null, Color.TRANSPARENT);
            return;
        }
        //判断黑白摄像头是否存在人脸,不存在人脸直接return
        FaceRect mRectRed = mFaceApiCamera.MaxFaceFeatureDetect(redByte, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
//        System.out.println("left = " + mRectRed.rect.left
//                + " top = " + mRectRed.rect.top
//                + " right = " + mRectRed.rect.right
//                + " bottom = " + mRectRed.rect.bottom
//                + " width = " + mRectRed.rect.width()
//                + " height = " + mRectRed.rect.height()
//                + " scaleNumber = " + Const.CAMERA_SCALE_NUMBER
//                + " max right = " + Const.CAMERA_MAX_RIGHT);
        if (mRectRed == null
                || mRectRed.rect == null
                || mRectRed.rect.width() == 0
                || mRectRed.rect.height() == 0
//                || mRectRed.rect.left * Const.CAMERA_SCALE_NUMBER < Const.CAMERA_MIN_LEFT
//                || mRectRed.rect.left * Const.CAMERA_SCALE_NUMBER + mRectRed.rect.width() * Const.CAMERA_SCALE_NUMBER > Const.CAMERA_MAX_RIGHT
                ) {
            mFindFaceInterface.findNotFace(mRectRed, Color.TRANSPARENT);
//            System.out.println("width = " + mRectRed.rect.width() + " height = " + mRectRed.rect.height());
//            LogUtils.e(TAG, "红外摄像头没有检测到人脸");
            return;
        }
        //回调准备找人脸
        FaceRect mRectColor = null;
        mFindFaceInterface.findFaceReady();
        if (!CameraUtil.LIVE_FACE_LOCK) {
            //判断是不是活体
            boolean liveBody = mFaceApiCamera.LiveBodyDetectEx(Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, colorByte, redByte);
//            boolean liveBody = mFaceApiCamera.LiveBodyDetect(colorByte, redByte);
            if (liveBody) {
//                LogUtils.d(TAG, "是活体,次数 = " + CameraUtil.CHECK_LIVE_COUNT);
                mRectColor = mFaceApiCamera.MaxFaceFeatureDetect(colorByte, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
                mFindFaceInterface.findFace(mRectColor, CameraUtil.PAIR_FACE_SUCCESS_COLOR ? mSuccessColor : mFailColor);
                CameraUtil.CHECK_LIVE_COUNT++;
            } else {
//                LogUtils.d(TAG, "不是活体,次/数 = " + CameraUtil.CHECK_LIVE_COUNT);
                CameraUtil.CHECK_LIVE_COUNT = 0;
                mRectColor = mFaceApiCamera.MaxFaceFeatureDetect(colorByte, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
                mFindFaceInterface.findFace(mRectColor, Color.TRANSPARENT);
                return;
            }
            if (CameraUtil.CHECK_LIVE_COUNT <= 3) {
//                LogUtils.d(TAG, "活体检测的次数小于3次,return");
                return;
            } else {
                CameraUtil.LIVE_FACE_LOCK = true;
            }
        }
        //进行最大人脸检测
        mRectColor = mFaceApiCamera.MaxFaceFeatureDetect(colorByte, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
        mFindFaceInterface.findFace(mRectColor, CameraUtil.PAIR_FACE_SUCCESS_COLOR ? mSuccessColor : mFailColor);
        if (CameraUtil.FIND_FACE_LOCK && CameraUtil.LIVE_FACE_LOCK) {
//            LogUtils.d(TAG, "正在进行人脸和活体检测,return");
            return;
        }
        if (mRectColor.rect.width() != 0 && mRectColor.rect.height() != 0) {
//            LogUtils.d(TAG, "检测到人脸");
            CameraUtil.FIND_FACE_LOCK = true;
        } else {
//            LogUtils.d(TAG, "没有检测到人脸");
            CameraUtil.resetCameraVariable(true);
            return;
        }
        addToQueue(colorByte, mRectColor);
    }

    public void handleYunTianLiFei(byte[] imageData) {
        if (FaceTempData.getInstance().isDownLoadPersonMsg()) {
            mFindFaceInterface.findNotFace(null, Color.TRANSPARENT);
            return;
        }
        FaceUtils.getInstance().IFaceRecSDK_FaceCaptureReq(mSdkCaptureCreate,
                imageData,
                Const.CAMERA_PREVIEW_WIDTH,
                Const.CAMERA_PREVIEW_HEIGHT);
    }

    public void handleYunTianLiFei(byte[] imageData, byte[] redData) {
        if (FaceTempData.getInstance().isDownLoadPersonMsg()) {
            mFindFaceInterface.findNotFace(null, Color.TRANSPARENT);
            return;
        }
        boolean liveBodyDetectEx = mFaceApiCamera.LiveBodyDetectEx(Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, imageData, redData);
        if (!liveBodyDetectEx) {
            mFindFaceInterface.findNotFace(null, Color.TRANSPARENT);
            return;
        }
        FaceUtils.getInstance().IFaceRecSDK_FaceCaptureReq(mSdkCaptureCreate,
                imageData,
                Const.CAMERA_PREVIEW_WIDTH,
                Const.CAMERA_PREVIEW_HEIGHT);
    }

    //    private void addToQueue(byte[] colorByte, Object mRectColor) {
//        try {
//
//            byte[] bytes = new byte[colorByte.length];
//            System.arraycopy(colorByte, 0, bytes, 0, colorByte.length);
////            ThreadManager.getArrayBlockingQueue(FacePairThread9.class.getSimpleName()).put(new CameraPreviewBean(bytes, mRectColor, Const.PAIR_TYPE_DEFAULT));
//            ThreadManager.clearAllQueue(FacePairThread10.class.getSimpleName());
//            ThreadManager.getArrayBlockingQueue(FacePairThread10.class.getSimpleName()).put(new CameraPreviewBean(bytes, mRectColor, Const.PAIR_TYPE_DEFAULT));
//        } catch (InterruptedException e) {
//            LogUtils.e(TAG, "mQueue 发生异常 = " + e.getMessage());
//            CameraUtil.resetCameraVariable(true);
//        }
//    }
    private void addToQueue(byte[] colorByte, Object mRectColor) {
        try {

            byte[] bytes = new byte[colorByte.length];
            System.arraycopy(colorByte, 0, bytes, 0, colorByte.length);
            if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE) {
                //人脸开门
                if ((AppSettingUtil.getConfig().getGuestOpenDoorType() == Const.OPEN_DOOR_TYPE_GUEST_ID_FACE_UN_REGISTER
                        || AppSettingUtil.getConfig().getGuestOpenDoorType() == Const.OPEN_DOOR_TYPE_GUEST_ID_FACE_REGISTER)
                        && FaceTempData.getInstance().isHaveIdMessage()) {
                    //开启了访客模式,并且检测到身份证
                    LogUtils.e(TAG, "============= 访客 : 人脸+身份证");
                    ThreadManager.getArrayBlockingQueue(FacePairThread9.class.getSimpleName()).put(new CameraPreviewBean(bytes, mRectColor, Const.PAIR_TYPE_ID));
                } else {
                    //没有开启访客模式
                    LogUtils.e(TAG, "============= 人脸");
                    ThreadManager.getArrayBlockingQueue(FacePairThread9.class.getSimpleName()).put(new CameraPreviewBean(bytes, mRectColor, Const.PAIR_TYPE_FACE));
                }
            } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_I_C) {
                //人脸 || IC卡
                LogUtils.e(TAG, "============= 人脸 || IC卡");
                ThreadManager.getArrayBlockingQueue(FacePairThread9.class.getSimpleName()).put(new CameraPreviewBean(bytes, mRectColor, Const.PAIR_TYPE_FACE));
            } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE_IC) {
                //人脸+IC卡
                if (FaceTempData.getInstance().isHaveICMessage()) {
                    LogUtils.e(TAG, "============= 人脸+IC 卡");
                    ThreadManager.getArrayBlockingQueue(FacePairThread9.class.getSimpleName()).put(new CameraPreviewBean(bytes, mRectColor, Const.PAIR_TYPE_IC));
                } else {
                    CameraUtil.resetCameraVariable(true);
                }
            } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE_ID) {
                //人脸+ID(身份证)
                if (FaceTempData.getInstance().isHaveIdMessage()) {
                    //有身份证
                    LogUtils.e(TAG, "============= 人脸+身份证");
                    ThreadManager.getArrayBlockingQueue(FacePairThread9.class.getSimpleName()).put(new CameraPreviewBean(bytes, mRectColor, Const.PAIR_TYPE_ID));
                } else {
                    CameraUtil.resetCameraVariable(true);
                }
            }
        } catch (InterruptedException e) {
            LogUtils.e(TAG, "mQueue 发生异常 = " + e.getMessage());
            CameraUtil.resetCameraVariable(true);
        }
    }

    @Override
    public void onTrackerListener(byte[] bytes, int i, int i1, com.intellif.FaceRect[] faceRects) {
        if (mFindFaceInterface == null) {
            return;
        }

        if (faceRects == null || faceRects.length == 0) {
            mFindFaceInterface.findNotFace(null, Color.TRANSPARENT);
            return;
        }
//        Log.e(TAG,"==== now FaceRect= "+faceRects[0].dRectLeft+" "+faceRects[0].dRectTop+" "+faceRects[0].dRectRight+" "+faceRects[0].dRectBottom);

        com.intellif.FaceRect maxFace = getMaxFace(faceRects);

        mFindFaceInterface.findFace(maxFace, Color.RED);
        if (CameraUtil.FIND_FACE_LOCK) {
            return;
        }
        CameraUtil.FIND_FACE_LOCK = true;
        mFindFaceInterface.findFaceReady();
        addToQueue(bytes, maxFace);
    }


    public com.intellif.FaceRect getMaxFace(com.intellif.FaceRect[] faceRects) {
        int maxIndex = 0;
        int maxHeight = faceRects[0].dRectBottom - faceRects[0].dRectTop;
        for (int i = 1; i < faceRects.length; i++) {
            int height = faceRects[i].dRectBottom - faceRects[i].dRectTop;
            if (height > maxHeight) {
                maxHeight = height;
                maxIndex = i;
            }
        }
        return faceRects[maxIndex];
    }

}
