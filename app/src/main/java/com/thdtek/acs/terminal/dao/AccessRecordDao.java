package com.thdtek.acs.terminal.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.bean.PairSuccessOtherBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.haogonge.UploadRecordForHaogonge;
import com.thdtek.acs.terminal.http.upload.UploadRecord;
import com.thdtek.acs.terminal.server.UploadRecordForHttp;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SwitchConst;
import com.thdtek.acs.terminal.util.camera.CameraUtil;

import java.io.IOException;
import java.util.Locale;

public class AccessRecordDao {
    private static final String TAG = AccessRecordDao.class.getSimpleName();

    public static synchronized AccessRecordBean insert(PersonBean peopleBean, PairSuccessOtherBean pairSuccessOtherBean, byte[] bytes, float rate, Rect rect, boolean cameraData) {
        AccessRecordBean accessRecordBean = new AccessRecordBean();
        accessRecordBean.setTime(pairSuccessOtherBean.getAccessTime());
        accessRecordBean.setType(AppSettingUtil.getConfig().getOpenDoorType());
        accessRecordBean.setPersonImage(peopleBean.getFacePic());
        accessRecordBean.setPersonName(peopleBean.getName());
        accessRecordBean.setCardNum(peopleBean.getEmployee_card_id());
        accessRecordBean.setIdNum(peopleBean.getID_no() == null ? "" : peopleBean.getID_no());
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
        //性别
        accessRecordBean.setGender(pairSuccessOtherBean.getGender());
        //出生日期
        accessRecordBean.setBirthday(pairSuccessOtherBean.getBirthday());
        //身份证住址
        accessRecordBean.setLocation(pairSuccessOtherBean.getLocation());
        //签发时间
        accessRecordBean.setValidityTime(pairSuccessOtherBean.getValidityTime());
        //签发机关
        accessRecordBean.setSigningOrganization(pairSuccessOtherBean.getSigningOrganization());
        //民族
        accessRecordBean.setNation(pairSuccessOtherBean.getNation());
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


        long insert = DBUtil.getDaoSession().getAccessRecordBeanDao().insert(accessRecordBean);
        LogUtils.d(TAG, "记录保存成功 = " + insert+" "+accessRecordBean.toString());

        if (SwitchConst.IS_OPEN_SOCKET_MODE) {
            UploadRecord.upload();
        }

        if (SwitchConst.IS_OPEN_HTTP_MODE) {
            UploadRecordForHttp uploadRecordForHttp = new UploadRecordForHttp();
            uploadRecordForHttp.upload();
        }

        if(SwitchConst.IS_OPEN_HAOGONGE_CLOUD){
            UploadRecordForHaogonge.getInstance().upload();
        }

        return accessRecordBean;
    }


}
