package com.thdtek.acs.terminal.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.http.upload.UploadRecord;
import com.thdtek.acs.terminal.server.UploadRecordForHttp;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.camera.CameraUtil;

import java.io.IOException;
import java.util.Locale;

public class AccessRecordDao {
    private static final String TAG = AccessRecordDao.class.getSimpleName();

    public static synchronized AccessRecordBean insert(PersonBean peopleBean, byte[] bytes, float rate, Rect rect, boolean cameraData) {
        AccessRecordBean accessRecordBean = new AccessRecordBean();
        accessRecordBean.setTime(System.currentTimeMillis());
        accessRecordBean.setType(AppSettingUtil.getConfig().getOpenDoorType());
        accessRecordBean.setPersonImage(peopleBean.getFacePic());
        accessRecordBean.setPersonName(peopleBean.getName());
        accessRecordBean.setCardNum(peopleBean.getEmployee_card_id());
        accessRecordBean.setIdNum(peopleBean.getID_no());
        //对应http模式personId
        accessRecordBean.setFid(peopleBean.getFid());
        //人id
        accessRecordBean.setPersonId(peopleBean.getPerson_id());
        //权限id
        accessRecordBean.setAuthorityId(peopleBean.getAuth_id());
        //当前比对的阈值
        accessRecordBean.setCurrentFaceFeatureNumber(rate);
        //正装照的比对值
        accessRecordBean.setPersonRate(rate);
        //通过照的比对值
        accessRecordBean.setAccordRate(rate);
        //默认比对的阈值
        accessRecordBean.setDefaultFaceFeatureNumber(AppSettingUtil.getConfig().getFaceFeaturePairNumber());
        //剩余通过次数
        accessRecordBean.setCount(peopleBean.getCount());
        try {
            String filePath = "";
            if (bytes != null) {
                filePath = CameraUtil.save2Record(bytes,
                        accessRecordBean.getTime() + Const.IMAGE_TYPE_DEFAULT_JPG, rect, cameraData);
            } else {
                filePath = CameraUtil.save2Record(BitmapFactory.decodeFile(peopleBean.getFacePic()),
                        accessRecordBean.getTime() + Const.IMAGE_TYPE_DEFAULT_JPG, rect);
            }
            accessRecordBean.setAccessImage(filePath);
        } catch (IOException e) {
            LogUtils.e(TAG, "facePairMatchSuccess 图片保存失败 error = " + e.getMessage());
        }


//        try {
//
//            if (bytes != null) {
//                System.out.println("===============1 temp");
//                CameraUtil.save2Temp(bytes,
//                        peopleBean.getName() + String.format(Locale.getDefault(), "_%tF-%<tH-%<tM-%<tS_", System.currentTimeMillis()) + System.currentTimeMillis() + Const.IMAGE_TYPE_DEFAULT_JPG,
//                        Bitmap.CompressFormat.JPEG,
//                        null);
//            } else {
//                System.out.println("===============2 temp");
//                CameraUtil.save2Temp(BitmapFactory.decodeFile(peopleBean.getFacePic()),
//                        peopleBean.getName() + String.format(Locale.getDefault(), "_%tF-%<tH-%<tM-%<tS_", System.currentTimeMillis()) + System.currentTimeMillis() + Const.IMAGE_TYPE_DEFAULT_JPG,
//                        Bitmap.CompressFormat.JPEG,
//                        null);
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        long insert = DBUtil.getDaoSession().getAccessRecordBeanDao().insert(accessRecordBean);
        LogUtils.d(TAG, "记录保存成功 = " + insert);

        if (Const.IS_OPEN_SOCKET_MODE) {
            UploadRecord.upload();
        }

        if (Const.IS_OPEN_HTTP_MODE) {
            UploadRecordForHttp uploadRecordForHttp = new UploadRecordForHttp();
            uploadRecordForHttp.upload();
        }

        return accessRecordBean;
    }

}
