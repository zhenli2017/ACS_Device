package com.thdtek.acs.terminal.thread;

/**
 * Time:2018/6/21
 * User:lizhen
 * Description:
 */
@Deprecated
public class FacePairThread8 extends Thread {
//
//    private final String TAG = FacePairThread8.class.getSimpleName();
//    private ArrayBlockingQueue<CameraPreviewBean> mQueue;
//
//    private LongSparseArray<NowPicFeatureHexBean> mNowPicMap;
//    private LongSparseArray<FaceFeatureHexBean> mFaceMap;
//    private Vector<PersonBean> mPeopleList;
//    private static FacePairThread8 mFacePairThread;
//    private long mFaceStatue = 1;
//    private volatile boolean mContinueOne = false;
//    private boolean mLoop = true;
//
//    public static FacePairThread8 getInstance() {
//        if (mFacePairThread == null) {
//            synchronized (FacePairThread8.class) {
//                if (mFacePairThread == null) {
//                    mFacePairThread = new FacePairThread8();
//                    mFacePairThread.start();
//                }
//            }
//        }
//        return mFacePairThread;
//    }
//
//    public FacePairThread8() {
//    }
//
//    public void init(ArrayBlockingQueue<CameraPreviewBean> queue) {
//        mQueue = queue;
//    }
//
//    public long getFaceStatue() {
//        return mFaceStatue;
//    }
//
//    public synchronized void continueOnce() {
//        mQueue.clear();
//        mContinueOne = true;
//    }
//
//
//    public void close() {
//        mLoop = false;
//        this.interrupt();
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        LogUtils.e(TAG, "=========== FacePairThread8 初始化 ===========");
//        FaceApi mFaceApi = new FaceApi();
//        try {
//            int signToken = SerialThread.getInstance().getSignToken(mFaceApi.GetToken());
//            mFaceStatue = mFaceApi.Init(signToken);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if (mFaceStatue != 0) {
//            LogUtils.e(TAG, "=========== FacePairThread8 初始化发生异常关闭 =========== " + mFaceStatue);
//            return;
//        }
//        LogUtils.e(TAG, "=========== FacePairThread8 初始化成功 =========== " + mFaceStatue);
//        mPeopleList = PersonDao.getPeopleList(true);
//        mNowPicMap = NowPicFeatureDao.getMap(true);
//        mFaceMap = FaceFeatureDao.getMap(true);
//        if (mQueue == null) {
//            throw new NullPointerException("FacePairThread8 需要调用init方法");
//        }
//
//        while (mLoop) {
//            try {
//                LogUtils.d(TAG, "====== FacePairThread8 准备接受相机捕获数据 ======");
//                CameraPreviewBean take = mQueue.take();
//                LogUtils.d(TAG, "====== FacePairThread8 收到相机捕获数据 ======");
//                handleData(mFaceApi, take);
//            } catch (InterruptedException e) {
//                LogUtils.e(TAG, "FacePairThread8 InterruptedException = " + e.getMessage());
//            } catch (Exception e) {
//                e.printStackTrace();
//                handleFail("FacePairThread8 匹配时发生异常 = " + e.getMessage());
//                CameraUtil.resetCameraVariable(true);
//            }
//        }
//        LogUtils.e(TAG, "====== FacePairThread8 完结撒花,特征值比对线程关闭 ======");
//        mFaceApi.UnInit();
//    }
//
//    private void handleData(FaceApi faceApi, CameraPreviewBean bean) {
//        LogUtils.d(TAG, "====== FacePairThread8 准备开始处理数据 ======");
//        if (FacePairStatus.getInstance().getLastFacePairSuccessAuthorityId() == Const.DEFAULT_CONITUE_AUTHORITY_ID) {
//            if (mContinueOne) {
//                //当前需要跳过匹配
//                mContinueOne = false;
//                LogUtils.d(TAG, "需要跳过本次匹配,正在匹配之前");
//                handleFinish();
//                return;
//            }
//            handlePairing();
//            LogUtils.d(TAG, "====== FacePairThread8 准备重新获取特征值 ======");
//            if (mContinueOne) {
//                //当前需要跳过匹配
//                mContinueOne = false;
//                LogUtils.d(TAG, "需要跳过本次匹配,获取特征值之前");
//                handleFinish();
//                return;
//            }
//            FaceFeature faceFeature = faceApi.ExtractMaxFaceFeatur(bean.getData(), Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, bean.getRect());
//            if (faceFeature == null || faceFeature.getFeatureBytes().length <= 4) {
//                mContinueOne = false;
//                handleFail("FacePairThread8 : 获取特征值失败");
//                return;
//            }
//
//            //设置最后一次的特征值,方便身份证比对
//            FaceTempData.getInstance().setLastFaceFeature(faceFeature);
//
//            //开始匹配特征值
//            checkFaceFeature(faceApi, bean.getData(), faceFeature.getFeatureBytes(), bean.getRect().rect);
//        } else {
//            LogUtils.d(TAG, "====== 检测上一次匹配成功的人的id,获取特征值 ====== ");
//            //上一次有成功识别的人
//            FaceFeature faceFeature = faceApi.ExtractMaxFaceFeatur(bean.getData(), Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, bean.getRect());
//            if (faceFeature == null || faceFeature.getFeatureBytes().length <= 4) {
//                mContinueOne = false;
//                //提取失败本次匹配结束
//                handleFinish();
//                return;
//            }
//            //设置最后一次的特征值,方便身份证比对
//            FaceTempData.getInstance().setLastFaceFeature(faceFeature);
//
//            PersonBean personBean = getPairLastPeople(faceApi, faceFeature.getFeatureBytes(), FacePairStatus.getInstance().getLastFacePairSuccessAuthorityId());
//            if (personBean == null || mContinueOne) {
//                mContinueOne = false;
//                FacePairStatus.getInstance().setLastFacePairSuccessAuthorityId(Const.DEFAULT_CONITUE_AUTHORITY_ID);
//                LogUtils.d(TAG, "FacePairThread8 上次匹配 : Person 信息 = null");
//                //提取失败本次匹配结束
//                handleFinish();
//                return;
//            } else {
//                LogUtils.d(TAG, "FacePairThread8 上次匹配 : Person 信息 = " + personBean);
//                //还是上次那个人
//                handleSuccess(personBean, null, 0, null, Const.FACE_PAIR_SAME_PEOPLE);
//                SystemClock.sleep(100);
//                return;
//            }
//        }
//    }
//
//    private void checkFaceFeature(FaceApi faceApi, byte[] imageData, byte[] updateFaceFeature, Rect rect) {
//
//        LogUtils.d(TAG, "=================================开始通过照片匹配===============================");
//        //获取三次学习特征值的比对结果
//        HashMap<Float, PersonBean> map = getPairMap(faceApi, updateFaceFeature);
//        long time1 = SystemClock.currentThreadTimeMillis();
//        if (map.size() == 0) {
//            //判断三次结果中是否有数据,没有数据,此时去正装照中获取比对的特征值数据
//            map = getOfficialMap(faceApi, updateFaceFeature, map);
//        }
//        if (map.size() == 0 || mContinueOne) {
//            mContinueOne = false;
//            map.clear();
//            handleFail("FacePairThread8 : 没有人高于指定阈值");
//            return;
//        }
//        //获取比对的阈值list,从大到小排序
//        ArrayList<Float> faceFeatureRateList = getFaceFeatureRateList(map);
//
//        PersonBean personBean = updateOfficialFaceHex(faceApi, updateFaceFeature, map, faceFeatureRateList);
//        if (personBean == null || mContinueOne) {
//            mContinueOne = false;
//            //更新正装照失败
//            map.clear();
//            handleFail("FacePairThread8 : 更新学习特征值失败");
//            return;
//        }
//        //检查权限
//        if (!checkPersonAccess(personBean) || mContinueOne) {
//            mContinueOne = false;
//            map.clear();
//            handleFail("FacePairThread8 : 权限不足,无法通行");
//            return;
//        }
//
//        LogUtils.d(TAG, "FacePairThread8 最终比对值 = " + faceFeatureRateList + " \nbean = " + personBean.getName());
//        long tiem2 = SystemClock.currentThreadTimeMillis();
//        System.out.println("比对耗时 = " + (tiem2 - time1));
//        handleSuccess(personBean, imageData, faceFeatureRateList.get(0), rect, Const.FACE_PAIR_NOT_SAME_PEOPLE);
//    }
//
//    private PersonBean getPairLastPeople(FaceApi faceApi, byte[] updateFaceFeature, long lastAuthorityId) {
//
//        NowPicFeatureHexBean nowPicFeatureHexBean = mNowPicMap.get(lastAuthorityId);
//        if (nowPicFeatureHexBean != null) {
//            byte[] oneByte = nowPicFeatureHexBean.getNowPicOneByte();
//            if (oneByte != null) {
//                float oneFaceFeature = faceApi.FacePairMatching(updateFaceFeature, oneByte);
//                float twoFaceFeature = 0f;
//                float threeFaceFeature = 0f;
//                LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的第一个学习特征值 = " + oneFaceFeature);
//                //第一次学习特征值比对
//                if (oneFaceFeature <= 1 && oneFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                    byte[] twoByte = nowPicFeatureHexBean.getNowPicTwoByte();
//                    if (twoByte == null) {
//                        LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的第二个学习特征值失败");
//                        return PersonDao.query2AuthorityId(lastAuthorityId);
//                    } else {
//                        twoFaceFeature = faceApi.FacePairMatching(updateFaceFeature, twoByte);
//                        LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的第二个学习特征值 = " + twoFaceFeature);
//                        //第二次学习特征值比对
//                        if (twoFaceFeature <= 1 && twoFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                            byte[] threeByte = nowPicFeatureHexBean.getNowPicThreeByte();
//                            if (threeByte == null) {
//                                LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的第三个学习特征值特征值失败");
//                                return PersonDao.query2AuthorityId(lastAuthorityId);
//                            } else {
//                                threeFaceFeature = faceApi.FacePairMatching(updateFaceFeature, threeByte);
//                                LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的第三个学习特征值特征值 = " + threeFaceFeature);
//                                //第一次学习特征值比对
//                                if (threeFaceFeature <= 1 && threeFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                                    return PersonDao.query2AuthorityId(lastAuthorityId);
//                                }
//                            }
//                        }
//                    }
//                }
//            } else {
//                LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的第一个学习特征值失败");
//            }
//        } else {
//            LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的特征值bean失败");
//        }
//
//        FaceFeatureHexBean faceFeatureHexBean = mFaceMap.get(lastAuthorityId);
//        if (faceFeatureHexBean == null || mContinueOne) {
//            mContinueOne = false;
//            LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的正装特征值失败 = " + faceFeatureHexBean);
//            return null;
//        }
//
//        float pairMatching = faceApi.FacePairMatching(updateFaceFeature, faceFeatureHexBean.getFaceFeatureByte());
//        LogUtils.d(TAG, "FacePairThread8 : 获取最后一个人的正装特征值 = " + pairMatching);
//        if (pairMatching <= 1 && pairMatching >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//            return PersonDao.query2AuthorityId(lastAuthorityId);
//        }
//        return null;
//    }
//
//    private HashMap<Float, PersonBean> getPairMap(FaceApi faceApi, byte[] updateFaceFeature) {
//        //获取特征值正常
//        HashMap<Float, PersonBean> map = new HashMap<>();
//        int size = mPeopleList.size();
//        for (int i = 0; i < size; i++) {
//            PersonBean peopleBean = mPeopleList.get(i);
//            NowPicFeatureHexBean nowPicFeatureHexBean = mNowPicMap.get(peopleBean.getAuth_id());
//            if (nowPicFeatureHexBean == null) {
//                continue;
//            }
//            if (mContinueOne) {
//                map.clear();
//                mContinueOne = false;
//                break;
//            }
//            //第一次学习特征值比对
//            byte[] oneByte = nowPicFeatureHexBean.getNowPicOneByte();
//            if (oneByte == null) {
//                continue;
//            }
//            float oneFaceFeature = faceApi.FacePairMatching(updateFaceFeature, oneByte);
//            float twoFaceFeature = 0f;
//            float threeFaceFeature = 0f;
//
//
//            //第二次学习特征值比对
//            if (oneFaceFeature <= 1 && oneFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                byte[] twoByte = nowPicFeatureHexBean.getNowPicTwoByte();
//
//                if (twoByte == null) {
//                    map.put(oneFaceFeature, peopleBean);
//                } else {
//                    twoFaceFeature = faceApi.FacePairMatching(updateFaceFeature, twoByte);
//                    //第三次学习特征值比对
//                    if (twoFaceFeature <= 1 && twoFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                        byte[] threeByte = nowPicFeatureHexBean.getNowPicThreeByte();
//                        if (threeByte == null) {
//                            map.put(Math.max(oneFaceFeature, twoFaceFeature), peopleBean);
//                        } else {
//                            threeFaceFeature = faceApi.FacePairMatching(updateFaceFeature, threeByte);
//                            if (threeFaceFeature <= 1 && threeFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                                float max = Math.max(threeFaceFeature, Math.max(oneFaceFeature, twoFaceFeature));
//                                map.put(max, peopleBean);
//                            }
//                        }
//                    }
//                }
//            }
//
////            LogUtils.d(TAG, "特征值长度 = " + oneByte.length
////                    + "\t名字 = " + peopleBean.getName()
////                    + "\t第一次学习值比对 = "
////                    + oneFaceFeature
////                    + "\t第二次学习值比对 = "
////                    + twoFaceFeature
////                    + "\t第三次学习值比对 = "
////                    + threeFaceFeature
////                    + " \t当前设置阈值 = "
////                    + AppSettingUtil.getConfig().getFaceFeaturePairNumber());
//        }
//        return map;
//    }
//
//    private HashMap<Float, PersonBean> getOfficialMap(FaceApi faceApi, byte[] updateFaceFeature, HashMap<Float, PersonBean> map) {
//        //通过照中没有人匹配，开始找正装照
//        LogUtils.d(TAG, "======== 通过照中没有人匹配，开始找正装照 ========");
//        int size = mPeopleList.size();
//        for (int i = 0; i < size; i++) {
//            PersonBean peopleBean = mPeopleList.get(i);
//            FaceFeatureHexBean faceFeatureHexBean = mFaceMap.get(peopleBean.getAuth_id());
//            if (faceFeatureHexBean == null) {
//                continue;
//            }
//            if (mContinueOne) {
//                map.clear();
//                mContinueOne = false;
//                break;
//            }
//            byte[] faceFeatureByte = faceFeatureHexBean.getFaceFeatureByte();
//            float v = faceApi.FacePairMatching(updateFaceFeature, faceFeatureByte);
//            //特征值匹配
////            LogUtils.d(TAG, "特征值长度 = " + faceFeatureByte.length + "\t名字 = " + peopleBean.getName() + "\t正装照片特征值匹配 = " + v + " \t当前设置阈值 = " + AppSettingUtil.getConfig().getFaceFeaturePairNumber());
//            if (v <= 1 && v >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                map.put(v, peopleBean);
//            }
//        }
//        return map;
//    }
//
//    private ArrayList<Float> getFaceFeatureRateList(HashMap<Float, PersonBean> map) {
//        //对比对后的值进行排序,从小到大
//        Set<Float> faceFeatureList = map.keySet();
//        ArrayList<Float> floats = new ArrayList<>(faceFeatureList);
//        Collections.sort(floats);
////        StringBuilder stringBuilder = new StringBuilder();
////        for (int i = 0; i < faceFeatureList.size(); i++) {
////            stringBuilder.append("名字 = ");
////            stringBuilder.append(map.get(floats.get(i)).getName());
////            stringBuilder.append(" 通过比对值 = ");
////            stringBuilder.append(floats.get(i));
////        }
////        LogUtils.d(TAG, "通过值比对超过阈值的人 = " + stringBuilder.toString());
////        LogUtils.d(TAG, "======== 开始排序，从大到小 ========");
//
//        //比对的值从大到小
//        Collections.reverse(floats);
////        LogUtils.d(TAG, "正装照的特征值列表 = " + floats);
//        return floats;
//    }
//
//    private PersonBean updateOfficialFaceHex(FaceApi faceApi, byte[] updateFaceFeature, HashMap<Float, PersonBean> map, ArrayList<Float> faceFeatureRateList) {
//        //取第一个最大的值作为识别的人
//        Float accordFeatureNumber = faceFeatureRateList.get(0);
//        PersonBean personBean = map.get(accordFeatureNumber);
//        FaceFeatureHexBean faceFeatureHexBean = FaceFeatureDao.query2AuthorityId(personBean.getAuth_id());
//        //判断这个人是否存在正装照
//        if (faceFeatureHexBean == null || mContinueOne) {
//            mContinueOne = false;
//            return null;
//        }
//        byte[] faceFeatureByte = faceFeatureHexBean.getFaceFeatureByte();
//        float personFeatureNumber = faceApi.FacePairMatching(updateFaceFeature, faceFeatureByte);
//        LogUtils.d(TAG, "FacePairThread8 正装照更新匹配 = " + personFeatureNumber + " 名称 = " + personBean.getName());
//
//        if (personFeatureNumber >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()
//                && personFeatureNumber <= AppSettingUtil.getConfig().getFaceFeaturePairNumber() + 0.05) {
//            LogUtils.d(TAG, "FacePairThread8 ======== 学习照特征值更新 ======= " + personFeatureNumber);
//            NowPicFeatureDao.insertOrReplace(personBean.getAuth_id(), personBean.getPerson_id(), updateFaceFeature, false);
//        }
//        return personBean;
//    }
//
//    private boolean checkPersonAccess(PersonBean personBean) {
//        boolean time = AuthorityUtil.checkTimeInTime(
//                personBean.getStart_ts(),
//                personBean.getEnd_ts(),
//                System.currentTimeMillis(),
//                AuthorityUtil.TIME_TYPE_HOUR
//        );
//        boolean count = AuthorityUtil.checkCount(personBean.getCount());
//        LogUtils.d(TAG, "FacePairThread8 time = " + time + " count = " + count);
//        return time && count;
//    }
//
//    private void handlePairing() {
//        FacePairStatus.getInstance().pairIng();
//    }
//
//    private void handleFail(String msg) {
//        LogUtils.d(TAG, msg);
//        FacePairStatus.getInstance().pairFail(Const.OPEN_DOOR_TYPE_FACE, msg);
//    }
//
//    private void handleSuccess(PersonBean personBean, byte[] image, float rate, Rect rect, int insert) {
//        FacePairStatus.getInstance().pairSuccess(personBean, image, rate, rect, insert, true);
//    }
//
//    private void handleFinish() {
//        FacePairStatus.getInstance().pairFinish();
//    }
}
