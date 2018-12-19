package com.thdtek.acs.terminal.haogonge;

import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.server.Base64Utils;
import com.thdtek.acs.terminal.server.RecordDaoForHttp;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.Date;
import java.util.List;

import greendao.AccessRecordBeanDao;

public class UploadRecordForHaogonge {

    private static final String TAG = UploadRecordForHaogonge.class.getSimpleName();
    private RecordDaoForHttp recordDaoForHttp = new RecordDaoForHttp();
    private final int UPLOAD_MAX = 10;
    AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();

    private static UploadRecordForHaogonge instance;

    public static UploadRecordForHaogonge getInstance(){
        if(instance == null){
            instance = new UploadRecordForHaogonge();
        }
        return instance;
    }

    private UploadRecordForHaogonge(){}


    private static boolean isUploading = false;
    public synchronized void upload(){
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(TAG, "上传考勤到好工e云平台 isUploading="+isUploading);

                if(isUploading){
                    return;
                }

                isUploading = true;

                try {
                    List<AccessRecordBean> lst = recordDaoForHttp.queryUnUploadRecords();

                    if(lst == null || lst.size() == 0){
                        LogUtils.d(TAG, "0条数据待上传");
                        isUploading = false;
                        return;
                    }
                    LogUtils.d(TAG, lst.size()+"条数据待上传, upload ...");


                    int count = lst.size() > UPLOAD_MAX ? UPLOAD_MAX : lst.size();
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < count; i++) {
                        AccessRecordBean bean = lst.get(i);
                        sb.append(bean.getId());
                        sb.append(" ");
                        sb.append(bean.getFid());
                        sb.append(" ");
                        sb.append(new TimeFormat().format(bean.getTime()));

                        if(HaogongeThread.uploadAttPic == 0){
                            sb.append("\n");
                        }else if(HaogongeThread.uploadAttPic == 1) {
                            sb.append(" ");
                            String img = Base64Utils.ImageToBase64ByLocal(bean.getAccessImage());
                            sb.append(img.length());
                            sb.append(" ");
                            sb.append(img);
                            sb.append("\n");
                        }
                    }

                    String bodyForSign = sb.toString();

                    long time = new Date().getTime();
                    String sign = new Sign().getSignString(HaogongeThread.sn, time, bodyForSign, HaogongeThread.encryptKey);

                    String url = HaogongeThread.url_root;
                    url += "/attLog";
                    url += "?sn="+ HaogongeThread.sn;
                    url += "&v="+ HaogongeThread.v;
                    url += "&time="+time;
                    url += "&sign="+sign;
                    url += "&index="+ HaogongeThread.uploadIndex;
                    LogUtils.d(TAG, "url=" + url);

                    String result = new DoPost().post(url, bodyForSign, 30000, 30000);

                    if(result == null){
                        LogUtils.e(TAG, "考勤上传失败");
                        HaogongeThread.reStartWork();
                    }else{
                        LogUtils.i(TAG, "upload("+count+"条考勤记录):"+result);
                        HaogongeThread.uploadIndex ++;

                        if("index值错误".equals(result)){
                            LogUtils.e(TAG, "考勤上传失败");
                            HaogongeThread.reStartWork();
                        }

                        if ("OK".equalsIgnoreCase(result)) {
                            //删除记录
                            for (int i = 0; i < count; i++) {
                                AccessRecordBean bean = lst.get(i);
                                FileUtil.deleteFile(bean.getAccessImage());
                                accessRecordBeanDao.delete(bean);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    LogUtils.e(TAG, "考勤上传失败");
                    HaogongeThread.reStartWork();
                }

                isUploading = false;

                upload();
            }
        });
    }


}
