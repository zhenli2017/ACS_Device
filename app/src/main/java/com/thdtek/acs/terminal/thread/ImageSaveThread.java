package com.thdtek.acs.terminal.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.FaceFeatureHexBean;
import com.thdtek.acs.terminal.bean.ImageSaveBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.FaceFeatureDao;
import com.thdtek.acs.terminal.dao.NowPicFeatureDao;
import com.thdtek.acs.terminal.imp.person.persondownload.PersonDownLoadImp;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.BitmapUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.camera.CameraUtil;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

/**
 * Time:2018/8/28
 * User:lizhen
 * Description:
 */

public class ImageSaveThread extends BaseThread {

    private static final String TAG = ImageSaveThread.class.getSimpleName();
    private SynchronousQueue<String> mSynchronousQueue;

    @Override
    public void init(boolean initDataBase) {
        super.init(initDataBase);
        mSynchronousQueue = new SynchronousQueue<>();
    }

    public void add(ImageSaveBean bean) {
        mQueue.add(bean);
    }

    public String get() throws InterruptedException {
        return mSynchronousQueue.take();
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void close() {
        super.close();
        if (mSynchronousQueue != null) {
            mSynchronousQueue.clear();
            mSynchronousQueue = null;
        }
    }

    @Override
    public void handleData(Object face, ImageSaveBean bean) {
        if (CameraUtil.FIND_FACE_LOCK) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        PersonBean personBean = bean.getPersonBean();
        PersonDownLoadImp.getInstance().personDownLoadStart(MyApplication.getContext().getString(R.string.down_msg_person) + personBean.getName(),Const.HANDLER_DELAY_TIME_5000);
        byte[] mImageByte = bean.getData();
        boolean facePic = bean.isFacePic();
        LogUtils.d(TAG, "===========收到服务器推送的图片 =========== " + personBean.getAuth_id());
        if (mImageByte == null) {
            handleSaveImage("image 数组 == null", personBean.getName());
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(mImageByte, 0, mImageByte.length);
        if (bitmap == null) {
            handleSaveImage("生成的bitmap == null", personBean.getName());
            return;
        }
        LogUtils.d(TAG, "width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
        //图片变成640*480
        Bitmap backBitmap = BitmapUtil.getFull640Bitmap(bitmap);

        String picPath = null;
        try {
            picPath = CameraUtil.save2Person(backBitmap,
                    System.currentTimeMillis() + "_" + personBean.getName() + "_" + Const.IMAGE_TYPE_DEFAULT_JPG);
        } catch (IOException e) {
            e.printStackTrace();
            handleSaveImage("图片保存失败 = " + e.getMessage(), personBean.getName());
            return;
        }

        Bitmap newBitmap = BitmapFactory.decodeFile(picPath);
        if (newBitmap == null) {
            FileUtil.deleteFile(picPath);
            handleSaveImage("加载保存图片失败 + " + picPath, personBean.getName());
            return;
        }
        byte[] bytes = BitmapUtil.bitmap2Byte(newBitmap);
        Object faceRect = getFaceRect(face, bytes, false);
        if (faceRect == null) {
            FileUtil.deleteFile(picPath);
            handleSaveImage("加载保存的图片没有人脸 " + picPath, personBean.getName());
            return;
        }

        byte[] faceFeature = getFaceFeature(face, bytes, faceRect, false);

        if (faceFeature == null) {
            FileUtil.deleteFile(picPath);
            handleSaveImage("获取 特征值 失败", personBean.getName());
            return;
        } else {
            if (bean.isOnlyForCheck()) {
                String content = Const.HTTP_CHECK_PHOTO_IS_VALID;
                //继续检测是否重复
                boolean exist = check(face, faceFeature);
                if (exist) {//图片重复
                    content += "/";
                    content += Const.HTTP_CHECK_PHOTO_IS_EXIST;
                }

                //删除缓存
                FileUtil.deleteFile(picPath);
                handleSaveImage(content, personBean.getName());

            } else {
                //保存图片
                try {
                    long l = System.currentTimeMillis();
                    //图片保存成功后,设置特征值
                    if (facePic) {
                        LogUtils.d(TAG, "正装照 文件名 = " + l + " path = " + picPath);
                        FaceFeatureDao.insertOrReplace(personBean.getAuth_id(), personBean.getPerson_id(), faceFeature);
                        LogUtils.d(TAG, "更新 NowPicFeatureDao 的特征值");
                        NowPicFeatureDao.insertOrReplace(personBean.getAuth_id(), personBean.getPerson_id(), faceFeature, true);
                        if (!TextUtils.isEmpty(personBean.getOldFacePic())) {
                            LogUtils.d(TAG, "删除文件 = " + personBean.getOldFacePic());
                            FileUtil.deleteFile(personBean.getOldFacePic());
                        }
                    }
                    handleSaveImage(picPath + Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS, personBean.getName());
                } catch (Exception e) {
                    LogUtils.e(TAG, "保存person正式图片失败 = " + e.getMessage());
                    handleSaveImage("保存person正式图片失败 = " + e.getMessage(), personBean.getName());
                }
            }
        }


    }


    private boolean check(Object faceApi, byte[] updateFaceFeature) {
        //通过照中没有人匹配，开始找正装照
        LogUtils.d(TAG, "======== 通过照中没有人匹配，开始找正装照 ========");
        int size = mPeopleList.size();
        for (int i = 0; i < size; i++) {
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
                return true;
            }
        }
        return false;
    }


//    @Override
//    public void handleData(FaceApi faceApi, ImageSaveBean bean) {
//        PersonBean personBean = bean.getPersonBean();
//        byte[] mImageByte = bean.getData();
//        boolean facePic = bean.isFacePic();
//        LogUtils.d(TAG, "===========收到服务器推送的图片 =========== " + personBean.getAuth_id());
//        if (mImageByte == null) {
//            handleSaveImage("image 数组 == null");
//            return;
//        }
//        Bitmap bitmap = BitmapFactory.decodeByteArray(mImageByte, 0, mImageByte.length);
//        if (bitmap == null) {
//            LogUtils.e(TAG, "================= 生成的bitmap == null");
//            handleSaveImage("生成的bitmap == null");
//            return;
//        }
//        LogUtils.d(TAG, "width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
//        //图片变成640*480
//        Bitmap backBitmap = BitmapUtil.getFull640Bitmap(bitmap);
//
//        String picPath = null;
//        com.thdtek.facelibrary.FaceRect rect = null;
//        try {
//            picPath = CameraUtil.save2Person(backBitmap, System.currentTimeMillis() + "_" + personBean.getName() + "_" + Const.IMAGE_TYPE_DEFAULT_JPG);
//        } catch (IOException e) {
//            e.printStackTrace();
//            handleSaveImage("图片保存失败 = " + e.getMessage());
//            return;
//        }
//        Bitmap newBitmap = BitmapFactory.decodeFile(picPath);
//        if (newBitmap == null) {
//            LogUtils.e(TAG, "加载保存图片失败 name = " + personBean.getName());
//            FileUtil.deleteFile(picPath);
//            handleSaveImage("加载保存图片失败");
//            return;
//        }
//
//        rect = getFaceRect(new com.thdtek.facelibrary.FaceRect(), faceApi, newBitmap);
//        if (rect == null || rect.rect.width() == 0 || rect.rect.height() == 0) {
//            FileUtil.deleteFile(picPath);
//            handleSaveImage("加载保存的图片没有人脸");
//            return;
//        }
//        byte[] faceFeature = getFaceFeature(faceApi, newBitmap, rect);
//        if (faceFeature == null) {
//            FileUtil.deleteFile(picPath);
//            handleSaveImage("获取 特征值 失败");
//            return;
//        }
//        //保存图片
//        try {
//            long l = System.currentTimeMillis();
//            //图片保存成功后,设置特征值
//            if (facePic) {
//                LogUtils.d(TAG, "正装照 文件名 = " + l + " path = " + picPath);
//                FaceFeatureDao.insertOrReplace(personBean.getAuth_id(), personBean.getPerson_id(), faceFeature);
//                LogUtils.d(TAG, "更新 NowPicFeatureDao 的特征值");
//                NowPicFeatureDao.insertOrReplace(personBean.getAuth_id(), personBean.getPerson_id(), faceFeature, true);
//                if (!TextUtils.isEmpty(personBean.getOldFacePic())) {
//                    LogUtils.d(TAG, "删除文件 = " + personBean.getOldFacePic());
//                    FileUtil.deleteFile(personBean.getOldFacePic());
//                }
//            }
//            handleSaveImage(picPath + Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS);
//        } catch (Exception e) {
//            LogUtils.e(TAG, "保存person正式图片失败 = " + e.getMessage());
//            handleSaveImage("保存person正式图片失败 = " + e.getMessage());
//        }
//    }

    public void handleSaveImage(String msg, String name) {
        LogUtils.e(TAG, msg);
        if (msg.contains(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS)) {
            PersonDownLoadImp.getInstance().personDownLoadEnd(MyApplication.getContext().getString(R.string.msg_down_success) + name,Const.HANDLER_DELAY_TIME_3000);
        } else {
            PersonDownLoadImp.getInstance().personDownLoadEnd(MyApplication.getContext().getString(R.string.msg_down_fail) + name,Const.HANDLER_DELAY_TIME_3000);

        }
        if (mSynchronousQueue != null) {
            mSynchronousQueue.add(msg);
        }
    }
}
