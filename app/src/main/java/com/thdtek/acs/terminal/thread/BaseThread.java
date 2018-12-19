package com.thdtek.acs.terminal.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.LongSparseArray;

import com.intellif.FaceRecAttrResult;
import com.intellif.FaceTools;
import com.intellif.FaceUtils;
import com.intellif.ImageFormat;
import com.thdtek.acs.terminal.bean.CameraPreviewBean;
import com.thdtek.acs.terminal.bean.FaceAttribute;
import com.thdtek.acs.terminal.bean.FaceFeatureHexBean;
import com.thdtek.acs.terminal.bean.IDBean;
import com.thdtek.acs.terminal.bean.ImageSaveBean;
import com.thdtek.acs.terminal.bean.NowPicFeatureHexBean;
import com.thdtek.acs.terminal.bean.PairBean;
import com.thdtek.acs.terminal.bean.PairSuccessOtherBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.FaceFeatureDao;
import com.thdtek.acs.terminal.dao.NowPicFeatureDao;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.face.FaceUtil;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AuthorityUtil;
import com.thdtek.acs.terminal.util.BitmapUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.camera.Bmp2YUV;
import com.thdtek.acs.terminal.util.camera.CameraUtil;
import com.thdtek.facelibrary.FaceApi;
import com.thdtek.facelibrary.FaceFeature;
import com.thdtek.facelibrary.FaceRect;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Time:2018/9/25
 * User:lizhen
 * Description:
 */

public abstract class BaseThread extends Thread {

    private String TAG = BaseThread.class.getSimpleName();

    public LongSparseArray<NowPicFeatureHexBean> mNowPicMap;
    public LongSparseArray<FaceFeatureHexBean> mFaceMap;
    public Vector<PersonBean> mPeopleList;

    public ArrayBlockingQueue<Object> mQueue;
    //当前选用的算法
    private int mCurrentFaceAlgorithm = FACE_ALGORITHM_HONGRUAN;
    private static final int FACE_ALGORITHM_HONGRUAN = 1;
    private static final int FACE_ALGORITHM_YUNTIANLIFEI = 2;
    //faceApi 的初始化状态,默认是初始化失败
    public long mFaceApiStatue = 1L;
    //循环条件
    public boolean mLoop = true;
    //临时终止条件
    public volatile boolean mContinueOnce = false;
    public int mSdkDetectCreate = 0;
    public int mSdkExtractCreate = 0;
    public int mSdkCompareCreate = 0;
    public int mSdkPredictorCreate = 0;

    public void init(boolean initDataBase) {

        mPeopleList = PersonDao.getPeopleList(initDataBase);
        mNowPicMap = NowPicFeatureDao.getMap(initDataBase);
        mFaceMap = FaceFeatureDao.getMap(initDataBase);
        ThreadManager.addThread(this);


    }

    public abstract String getTag();

    public long getFaceApiStatus() {
        return mFaceApiStatue;
    }

    public void continueOnce() {
        LogUtils.d(TAG, "================= " + TAG + " continueOnce ==============");
        mQueue.clear();
        mContinueOnce = true;
    }

    public void close() {

        mLoop = false;
        mContinueOnce = true;
        interrupt();
        ThreadManager.removeThread(getTag());

    }

    @Override
    public void run() {
        super.run();
        TAG = getTag();
        mQueue = ThreadManager.getArrayBlockingQueue(TAG);
        if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK)) {
            runYunTianLiFei();
        } else {
            runHongRuan();
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

    private FaceUtils initFaceUtils() {
        LogUtils.e(TAG, "=========== " + TAG + " 开始初始化 云天励飞算法 ==========");
        FaceUtils faceUtils = FaceUtils.getInstance();
//        int sdkInit = faceUtils.IFaceRecSDK_Init(Const.LICENSE_PATH, MyApplication.getContext());
//        LogUtils.e(TAG, "SDK INIT = " + sdkInit);
//        String sdkInfo = faceUtils.IFaceRecSDK_GetSDKInfo();
//        LogUtils.e(TAG, "SDK Info = " + sdkInfo);
        //人脸单图检测句柄初始化
//        mSdkDetectCreate = faceUtils.IFaceRecSDK_Detect_Create(Const.MODEL_PATH, 48);
//        LogUtils.e(TAG, "SDK Detect Create = " + mSdkDetectCreate);
//        //初始化人脸特征值提取
//        mSdkExtractCreate = faceUtils.IFaceRecSDK_Extract_Create(Const.MODEL_PATH);
//        LogUtils.e(TAG, "SDK Extract Create = " + mSdkExtractCreate);
//        //初始化人脸比对句柄
//        mSdkCompareCreate = faceUtils.IFaceRecSDK_Compare_Create(Const.MODEL_PATH);
//        LogUtils.e(TAG, "SDK Compare Create = " + mSdkCompareCreate);
//        //初始化人脸属性
//        mSdkPredictorCreate = faceUtils.IFaceRecSDK_Predictor_Create(Const.MODEL_PATH);
//        LogUtils.e(TAG, "SDK Predictor Create = " + mSdkPredictorCreate);

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


        return faceUtils;
    }


//    private FaceUtils initFaceUtils() {
//        LogUtils.e(TAG, "=========== " + TAG + " 开始初始化 云天励飞算法 ==========");
//        FaceUtils faceUtils = new FaceUtils();
//        int sdkInit = faceUtils.IFaceRecSDK_Init(Const.LICENSE_PATH, MyApplication.getContext());
//        LogUtils.e(TAG, "SDK INIT = " + sdkInit);
//        String sdkInfo = faceUtils.IFaceRecSDK_GetSDKInfo();
//        LogUtils.e(TAG, "SDK Info = " + sdkInfo);
//        //人脸单图检测句柄初始化
//        mSdkDetectCreate = faceUtils.IFaceRecSDK_Detect_Create(Const.MODEL_PATH, 0);
//        LogUtils.e(TAG, "SDK Detect Create = " + mSdkDetectCreate);
//        //初始化人脸特征值提取
//        mSdkExtractCreate = faceUtils.IFaceRecSDK_Extract_Create(Const.MODEL_PATH);
//        LogUtils.e(TAG, "SDK Extract Create = " + mSdkExtractCreate);
//        //初始化人脸比对句柄
//        mSdkCompareCreate = faceUtils.IFaceRecSDK_Compare_Create(Const.MODEL_PATH);
//        LogUtils.e(TAG, "SDK Compare Create = " + mSdkCompareCreate);
//        //初始化人脸属性
//        mSdkPredictorCreate = faceUtils.IFaceRecSDK_Predictor_Create(Const.MODEL_PATH);
//        LogUtils.e(TAG, "SDK Predictor Create = " + mSdkPredictorCreate);
//        return faceUtils;
//    }


    private void runYunTianLiFei() {
        mCurrentFaceAlgorithm = FACE_ALGORITHM_YUNTIANLIFEI;
        FaceUtils faceUtils = initFaceUtils();
        while (mLoop) {
            try {
                if (TAG.equals(PairThreadTwo.class.getSimpleName()) || TAG.equals(PairThreadThree.class.getSimpleName())) {
                    LogUtils.d(TAG, "====== " + TAG + " 准备接受 特征值 ======");
                    Object take = mQueue.take();
                    if (take instanceof PairBean) {
                        handleData(faceUtils, take);
                    }
                } else {
                    LogUtils.d(TAG, "======  " + TAG + "准备接受相机捕获数据 ====== " + mQueue.size());
                    Object take = mQueue.take();
                    mContinueOnce = false;
                    if (take instanceof CameraPreviewBean) {
                        LogUtils.d(TAG, "======  " + TAG + "收到相机捕获数据 ====== "+mQueue.size());
                        CameraPreviewBean bean = (CameraPreviewBean) take;
                        handleData(faceUtils, bean.getData(), bean.getRect(), bean.getType());
                    } else if (take instanceof ImageSaveBean) {
                        ImageSaveBean bean = (ImageSaveBean) take;
                        handleData(faceUtils, bean);
                    }

                }

            } catch (InterruptedException e) {
                LogUtils.e(TAG, "====== " + TAG + " InterruptedException = " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                handleFail(Const.OPEN_DOOR_TYPE_FACE, "====== " + TAG + " 匹配时发生异常 = " + e.getMessage(), Const.FACE_PAIR_ERROR_CODE_EXCEPTION);
                setSynchronousQueueError(e.getMessage());
                SystemClock.sleep(2000);
                CameraUtil.resetCameraVariable(true);
            }
        }

//        //销毁特征值比对句柄
//        faceUtils.IFaceRecSDK_Compare_Destory(mSdkCompareCreate);
//        //销毁人脸特征值提取句柄
//        faceUtils.IFaceRecSDK_Extract_Destory(mSdkExtractCreate);
//        //销毁人脸单图检测句柄
//        faceUtils.IFaceRecSDK_Detect_Destory(mSdkDetectCreate);
//        //销毁人脸属性句柄
//        faceUtils.IFaceRecSDK_Predictor_Destory(mSdkPredictorCreate);
//        //销毁人脸检测
//        faceUtils.IFaceRecSDK_UnInit();
        LogUtils.e(TAG, "====== " + TAG + " 完结撒花,特征值比对线程关闭 ======");
    }


    private void runHongRuan() {
        mCurrentFaceAlgorithm = FACE_ALGORITHM_HONGRUAN;
        FaceApi faceApi = initFaceApi();
        if (faceApi == null) {
            LogUtils.e(TAG, "=========== " + TAG + " 初始化失败,完结撒花 ===========");
            return;
        }

        while (mLoop) {
            try {
                if (TAG.equals(PairThreadTwo.class.getSimpleName()) || TAG.equals(PairThreadThree.class.getSimpleName())) {
                    LogUtils.d(TAG, "====== " + TAG + " 准备接受 特征值 ======");
                    Object take = mQueue.take();
                    if (take instanceof PairBean) {
                        handleData(faceApi, take);
                    }
                } else {
                    LogUtils.d(TAG, "====== " + TAG + " 准备接受相机捕获数据 ====== " + mQueue.size());
                    Object take = mQueue.take();
                    mContinueOnce = false;
                    if (take instanceof CameraPreviewBean) {
                        LogUtils.d(TAG, "====== " + TAG + " 收到相机捕获数据 ====== "+mQueue.size());
                        CameraPreviewBean bean = (CameraPreviewBean) take;
                        handleData(faceApi, bean.getData(), bean.getRect(), bean.getType());
                    } else if (take instanceof ImageSaveBean) {
                        ImageSaveBean bean = (ImageSaveBean) take;
                        handleData(faceApi, bean);
                    }

                }
            } catch (InterruptedException e) {
                LogUtils.e(TAG, "====== " + TAG + " InterruptedException = " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                handleFail(Const.OPEN_DOOR_TYPE_FACE, "====== " + TAG + " 匹配时发生异常 = " + e.getMessage(), Const.FACE_PAIR_ERROR_CODE_EXCEPTION);
                setSynchronousQueueError(e.getMessage());
                SystemClock.sleep(2000);
                CameraUtil.resetCameraVariable(true);
            }
        }
        faceApi.UnInit();
        LogUtils.e(TAG, "====== " + TAG + " 完结撒花,特征值比对线程关闭 ======");
    }


    public Object getFaceRect(Object object, byte[] imageData, boolean cameraData, int width, int height) {
        //默认的bitmap数据是RGB格式的数据,imageData也是RGB模式数据,虹软需要转成yuv格式,云天励飞不用
        if (mCurrentFaceAlgorithm == FACE_ALGORITHM_HONGRUAN) {
            FaceApi faceApi = (FaceApi) object;
            byte[] yuv420sp = getHongRuanImageData(imageData, cameraData);
            FaceRect faceRect = faceApi.MaxFaceFeatureDetectImage(yuv420sp, width, height);
            if (faceRect == null || faceRect.rect == null || faceRect.rect.width() == 0 || faceRect.rect.height() == 0) {
                return null;
            } else {
                return faceRect;
            }
        } else if (mCurrentFaceAlgorithm == FACE_ALGORITHM_YUNTIANLIFEI) {
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
            com.intellif.FaceRect[] faceRects = faceUtils.IFaceRecSDK_Face_Detect(mSdkDetectCreate, pixelsBGR, width, height, ImageFormat.IFACEREC_IMG_BGR);
            if (faceRects == null || faceRects.length == 0) {
                return null;
            } else {
                return getMaxFace(faceRects);
            }

        } else {
            return null;
        }
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

    public synchronized byte[] getFaceFeature(Object object, byte[] imageData, Object faceRect, boolean cameraData, int width, int height) {
        if (mCurrentFaceAlgorithm == FACE_ALGORITHM_HONGRUAN) {
            FaceApi faceApi = (FaceApi) object;
            FaceRect rect = (FaceRect) faceRect;
            byte[] yuv420sp = getHongRuanImageData(imageData, cameraData);
            FaceFeature faceFeature = faceApi.ExtractMaxFaceFeatur(yuv420sp, width, height, rect);
            if (faceFeature == null || faceFeature.getFeatureBytes() == null || faceFeature.getFeatureBytes().length <= 4) {
                return null;
            } else {
                return faceFeature.getFeatureBytes();
            }
        } else if (mCurrentFaceAlgorithm == FACE_ALGORITHM_YUNTIANLIFEI) {
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
            byte[] bytes = faceUtils.IFaceRecSDK_Face_Feature(mSdkExtractCreate, pixelsBGR, width, height, rect, ImageFormat.IFACEREC_IMG_BGR);

            if (bytes == null || bytes.length == 0) {
                return null;
            } else {
                return bytes;
            }
        } else {
            return null;
        }
    }

    public FaceAttribute getFaceAttribute(Object object, byte[] imageData, Object faceRect, int type, boolean cameraData) {

        if (mCurrentFaceAlgorithm == FACE_ALGORITHM_HONGRUAN) {
            FaceApi faceApi = (FaceApi) object;
            FaceRect rect = (FaceRect) faceRect;
            return null;
        } else if (mCurrentFaceAlgorithm == FACE_ALGORITHM_YUNTIANLIFEI) {
            com.intellif.FaceRect rect = (com.intellif.FaceRect) faceRect;
            FaceUtils faceUtils = (FaceUtils) object;
            byte[] bytes = new byte[imageData.length];
            System.arraycopy(imageData, 0, bytes, 0, bytes.length);

            FaceRecAttrResult[] faceRecAttrResults = faceUtils
                    .IFaceRecSDK_AttributePredict(mSdkPredictorCreate, imageData, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, rect, type, ImageFormat.IFACEREC_IMG_BGR);
            if (type == Const.IFACEREC_QUALITY_MASK) {
                //侧脸检测
                return new FaceAttribute(faceRecAttrResults);
            } else if (type == Const.IFACEREC_LIVE_MASK) {
                //活体检测
                return new FaceAttribute(faceRecAttrResults);
            } else {
                return new FaceAttribute(faceRecAttrResults);
            }

        } else {
            return null;
        }
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

    public float getPairNumber(Object object, byte[] faceFeatureOne, byte[] faceFeatureTwo) {
        if (mCurrentFaceAlgorithm == FACE_ALGORITHM_HONGRUAN) {
            FaceApi faceApi = (FaceApi) object;
            return faceApi.FacePairMatching(faceFeatureOne, faceFeatureTwo);
        } else if (mCurrentFaceAlgorithm == FACE_ALGORITHM_YUNTIANLIFEI) {
            FaceUtils faceUtils = (FaceUtils) object;
            return faceUtils.IFaceRecSDK_FeatureCompare(mSdkCompareCreate, faceFeatureOne, faceFeatureTwo);
        } else {
            return 0f;
        }
    }

    public Rect getRect(Object faceRect) {
        if (mCurrentFaceAlgorithm == FACE_ALGORITHM_HONGRUAN) {
            FaceRect rect = (FaceRect) faceRect;
            return new Rect(rect.rect);
        } else if (mCurrentFaceAlgorithm == FACE_ALGORITHM_YUNTIANLIFEI) {
            com.intellif.FaceRect rect = (com.intellif.FaceRect) faceRect;
            return new Rect(rect.dRectLeft, rect.dRectTop, rect.dRectRight, rect.dRectBottom);
        } else {
            return null;
        }
    }


    public HashMap<Float, PersonBean> getPairMap(Object faceApi, int index, byte[] updateFaceFeature) {
        //获取特征值正常
        HashMap<Float, PersonBean> map = new HashMap<>();
        int size = mPeopleList.size();
        int count = 0;
        for (int i = index; i < size; i = i + 3) {
//        for (int i = 0; i < size; i ++) {

            count++;
            PersonBean peopleBean = mPeopleList.get(i);
            NowPicFeatureHexBean nowPicFeatureHexBean = mNowPicMap.get(peopleBean.getAuth_id());
            if (nowPicFeatureHexBean == null) {
                continue;
            }
            //第一次学习特征值比对
            byte[] oneByte = nowPicFeatureHexBean.getNowPicOneByte();
            if (oneByte == null) {
                continue;
            }
            float oneFaceFeature = getPairNumber(faceApi, updateFaceFeature, oneByte);
            float twoFaceFeature = 0f;
            float threeFaceFeature = 0f;

            //第二次学习特征值比对
            if (oneFaceFeature <= 1 && oneFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
                byte[] twoByte = nowPicFeatureHexBean.getNowPicTwoByte();

                if (twoByte == null) {
                    map.put(oneFaceFeature, peopleBean);
                } else {
                    twoFaceFeature = getPairNumber(faceApi, updateFaceFeature, twoByte);
                    //第三次学习特征值比对
                    if (twoFaceFeature <= 1 && twoFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
                        byte[] threeByte = nowPicFeatureHexBean.getNowPicThreeByte();
                        if (threeByte == null) {
                            map.put(Math.max(oneFaceFeature, twoFaceFeature), peopleBean);
                        } else {
                            threeFaceFeature = getPairNumber(faceApi, updateFaceFeature, threeByte);
                            if (threeFaceFeature <= 1 && threeFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
                                float max = Math.max(threeFaceFeature, Math.max(oneFaceFeature, twoFaceFeature));
                                map.put(max, peopleBean);
                            }
                        }
                    }
                }
            }

//            LogUtils.d(TAG, "特征值长度 = " + oneByte.length
//                    + "\t名字 = " + peopleBean.getName()
//                    + "\t第一次学习值比对 = "
//                    + oneFaceFeature
//                    + "\t第二次学习值比对 = "
//                    + twoFaceFeature
//                    + "\t第三次学习值比对 = "
//                    + threeFaceFeature
//                    + " \t当前设置阈值 = "
//                    + AppSettingUtil.getConfig().getFaceFeaturePairNumber());

        }
        LogUtils.d(TAG, TAG + " ========== 学习照 比对总数 = " + count + " index = " + index);
        return map;
    }

    public HashMap<Float, PersonBean> getPairOfficialMap(Object faceApi, int index, byte[] updateFaceFeature, HashMap<Float, PersonBean> map) {
        //通过照中没有人匹配，开始找正装照
        LogUtils.d(TAG, "======== 通过照中没有人匹配，开始找正装照 ======== " + AppSettingUtil.getConfig().getFaceFeaturePairNumber());
        int size = mPeopleList.size();
        int count = 0;
//        for (int i = index; i < size; i = i + 3) {
        for (int i = 0; i < size; i++) {
            count++;
            if (mPeopleList.size() <= i) {
                continue;
            }
            PersonBean peopleBean = mPeopleList.get(i);
            FaceFeatureHexBean faceFeatureHexBean = mFaceMap.get(peopleBean.getAuth_id());
            if (faceFeatureHexBean == null) {
                continue;
            }
            byte[] faceFeatureByte = faceFeatureHexBean.getFaceFeatureByte();
            float v = getPairNumber(faceApi, updateFaceFeature, faceFeatureByte);
            //特征值匹配
//            LogUtils.d(TAG, "特征值长度 = " + faceFeatureByte.length + "\t名字 = " + peopleBean.getName() + "\t正装照片特征值匹配 = " + v + " \t当前设置阈值 = " + AppSettingUtil.getConfig().getFaceFeaturePairNumber());
            if (v <= 1 && v >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
                map.put(v, peopleBean);
            }
        }
        LogUtils.d(TAG, TAG + "  ========== 正装照 比对总数 = " + count + " index = " + index);
        return map;
    }

    public Bitmap getFaceImageDate(IDBean idMessage) {

        if (idMessage == null) {
            LogUtils.d(TAG, "人脸+身份证 : 获取身份证图片失败");
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(idMessage.getImage());
        if (bitmap == null) {
            LogUtils.d(TAG, "人脸+身份证 : 获取身份证图片失败");
            return null;
        }
//        return bitmap;
        return BitmapUtil.getFull640Bitmap(bitmap);
//        return Bmp2YUV.getYUV420sp(Const.CAMERA_BITMAP_WIDTH, Const.CAMERA_BITMAP_HEIGHT, backBitmap);
    }

    public void handleData(Object object, byte[] imageData, Object faceRect, String type) {

    }

    public void handleData(Object object, ImageSaveBean bean) {

    }

    public void handleData(Object object, Object pairBean) {

    }

    public boolean checkContinue() {
        if (mContinueOnce) {
            LogUtils.d(TAG, "========== " + TAG + " checkContinue true,跳过本次匹配 ==========");
            mContinueOnce = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPersonAccess(PersonBean personBean) {
        boolean time = AuthorityUtil.checkTimeInTime(
                personBean.getStart_ts(),
                personBean.getEnd_ts(),
                System.currentTimeMillis(),
                AuthorityUtil.TIME_TYPE_HOUR
        );
        boolean count = AuthorityUtil.checkCount(personBean.getCount());
        boolean checkWeekly = AuthorityUtil.checkWeekly(personBean.getWeekly());
        LogUtils.d(TAG, "time = " + time + " count = " + count + " checkWeekly = " + checkWeekly);
        return time && count && checkWeekly;
    }

    public void setSynchronousQueueError(String msg) {

    }

    public void handlePairing() {
        FacePairStatus.getInstance().pairIng();
    }

    public void handleFail(int type, String msg, int code) {
        LogUtils.d(TAG, msg);
        FacePairStatus.getInstance().pairFail(type, msg, code);
    }

    public void handleSuccess(PersonBean personBean, PairSuccessOtherBean pairSuccessOtherBean, byte[] image, float rate, Rect rect, int insert, boolean cameraData) {

        FacePairStatus.getInstance().pairSuccess(personBean, pairSuccessOtherBean,image, rate, rect, insert, cameraData);
    }

    public void handleFinish() {
        FacePairStatus.getInstance().pairFinish();
    }

    public void handleContinueOnce() {
        FacePairStatus.getInstance().pairDoNotThing();
    }

    public void handleNotAlive(String msg) {
        FacePairStatus.getInstance().pairNotAlive(msg);
    }

}
