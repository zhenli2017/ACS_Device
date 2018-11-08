package com.thdtek.acs.terminal.http.upload;

import com.google.protobuf.ByteString;
import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.socket.core.RequestCallback;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.io.IOException;
import java.util.List;

import greendao.AccessRecordBeanDao;

/**
 * Time:2018/6/26
 * User:lizhen
 * Description:
 */

public class UploadRecord {
    private static final String TAG = UploadRecord.class.getSimpleName();
    private static boolean UPLOADING = false;

    public static void setUploadingFalse() {
        LogUtils.e(TAG, "设置上传 UPLOADING = false");
        UPLOADING = false;
    }

    public static void upload() {
        if(!Const.IS_OPEN_AUTO_UPLOAD_PASS_RECORD){
            LogUtils.d(TAG, "当前模式不自动上传流水");
            return;
        }

        if (UPLOADING) {
            LogUtils.d(TAG, "当前正在上传,本次跳过");
            return;
        }
        LogUtils.d(TAG, "========== 开始上传数据 =========");
        UPLOADING = true;
        try {
            uploadRecord();
        } catch (Exception e) {
            LogUtils.e(TAG, "upload error = " + e.getMessage());
            UPLOADING = false;
        }
    }

    /**
     * 上传通过记录
     */
    private static void uploadRecord() {
        final AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();
        List<AccessRecordBean> list = accessRecordBeanDao.queryBuilder().list();
        if (list == null || list.size() == 0) {
            LogUtils.d(TAG, "数据库中出入记录为 null || size = 0,return");
            UPLOADING = false;
            return;
        }
        final AccessRecordBean accessRecordBean = list.get(0);
        ByteString bytes = null;
        try {
            bytes = ByteString.readFrom(FileUtil.getFileInputStream(accessRecordBean.getAccessImage()));
        } catch (IOException e) {
            LogUtils.e(TAG, "读取通过图片失败");

        }
        Msg.Message.RsyncPassRecordReq.Builder builder = Msg.Message.RsyncPassRecordReq.newBuilder()
                .setPassTs(accessRecordBean.getTime() / 1000)
                .setPersonId(accessRecordBean.getPersonId())
                .setAuthId(accessRecordBean.getAuthorityId())
                .setCount(accessRecordBean.getCount())
                .setDefaultFaceFeatureRate(accessRecordBean.getDefaultFaceFeatureNumber())
                .setCurrentFaceFeatureRate(accessRecordBean.getCurrentFaceFeatureNumber())
                .setMethod(accessRecordBean.getType());
        if (bytes == null) {

        } else {
            builder.setNowImg(bytes);
        }
        Msg.Message.RsyncPassRecordReq rsyncPassRecordReq = builder.build();
        Msg.Message message = Msg.Message.newBuilder()
                .setRsyncPassRecordReq(rsyncPassRecordReq)
                .build();
        final long time1 = System.currentTimeMillis();
        new SendMsgHelper().request(message, new RequestCallback() {
            @Override
            public void onResponse(Msg.Message message) {
                Msg.Message.RsyncPassRecordRsp rsyncPassRecordRsp = message.getRsyncPassRecordRsp();
                int status = rsyncPassRecordRsp.getStatus();
                long time2 = System.currentTimeMillis();
                LogUtils.d(TAG,"上传时间 = "+(time2-time1));
                if (status == 0) {
                    uploadSuccess(accessRecordBeanDao, accessRecordBean);
                } else {
                    uploadFail();
                }
            }

            @Override
            public void onTimeout() {
                uploadFail();
            }
        });
    }

    /**
     * 上传成功
     * 删除数据库记录
     * 删除通过图片
     *
     * @param accessRecordBeanDao
     * @param bean
     */
    private static void uploadSuccess(AccessRecordBeanDao accessRecordBeanDao, AccessRecordBean bean) {
        accessRecordBeanDao.delete(bean);

        FileUtil.deleteFile(bean.getAccessImage());

        try {
            uploadRecord();
        } catch (Exception e) {
            LogUtils.e(TAG, "upload error = " + e.getMessage());
            UPLOADING = false;
        }
    }

    /**
     * 上传失败
     */
    private static void uploadFail() {
        UPLOADING = false;
    }
}
