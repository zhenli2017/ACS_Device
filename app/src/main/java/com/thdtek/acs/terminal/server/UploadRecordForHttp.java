package com.thdtek.acs.terminal.server;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import greendao.AccessRecordBeanDao;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Time:2018/6/26
 * User:lizhen
 * Description:
 */

public class UploadRecordForHttp {
    private static final String TAG = UploadRecordForHttp.class.getSimpleName();
    private AccessRecordBeanDao dao = DBUtil.getDaoSession().getAccessRecordBeanDao();
    private RecordDaoForHttp recordDaoForHttp = new RecordDaoForHttp();
    private List<AccessRecordBean> mAccessRecordBeanList;
    private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    public void upload(List<AccessRecordBean> lst) {
        mAccessRecordBeanList = lst;
        if (mAccessRecordBeanList != null && mAccessRecordBeanList.size() != 0) {
            upload(lst.remove(0));
        }
    }

    //查询数据是否有未上传数据，如有则上传
    public void upload() {
        List<AccessRecordBean> lst = recordDaoForHttp.queryUnUploadRecords();
        upload(lst);
    }

    public void upload(final AccessRecordBean bean) {
        String url = (String) SPUtils.get(MyApplication.getContext(),
                Const.URL_FOR_HTTP_AUTO_UPLOAD_RECORD, "");
        if (TextUtils.isEmpty(url)) {
            return;
        }

        String t = String.valueOf(((double)(bean.getTime()))/1000);

        FormBody build = null;
        try {
            build = new FormBody.Builder()
                    .addEncoded("deviceSn", URLEncoder.encode(AppSettingUtil.getConfig().getDeviceSn(), "utf-8"))
                    .addEncoded("personID", URLEncoder.encode(bean.getFid(), "utf-8"))
                    .addEncoded("ts", URLEncoder.encode(t, "utf-8"))
                    .addEncoded("passType", URLEncoder.encode(bean.getType() + "", "utf-8"))
                    .addEncoded("photo", URLEncoder.encode(Base64Utils.ImageToBase64ByLocal(bean.getAccessImage()), "utf-8"))
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            build = new FormBody.Builder()
                    .add("deviceSn", AppSettingUtil.getConfig().getDeviceSn())
                    .add("personID", bean.getFid())
                    .add("ts", t)
                    .add("passType", bean.getType() + "")
                    .add("photo", Base64Utils.ImageToBase64ByLocal(bean.getAccessImage()))
                    .build();
        }


        LogUtils.d(TAG, "上传订单 ... " + url);

        final Request request = new Request.Builder()
                .url(url)
                .header("Content-Length", build.contentLength() + "")
                .post(build)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "上传订单失败 fid=" + bean.getFid() + " error = " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                LogUtils.d(TAG, "上传订单成功 fid=" + bean.getFid());
                try {
                    String string = response.body().string();
                    ResponseForHttp responseForHttp = new Gson().fromJson(string, ResponseForHttp.class);
                    if (responseForHttp.getStatus() == 0) {
                        //删除记录
                        FileUtil.deleteFile(bean.getAccessImage());
                        dao.delete(bean);
                    } else {
                        bean.setUploadToHttp(false);
                        dao.insertOrReplace(bean);
                    }
                    if (mAccessRecordBeanList != null && mAccessRecordBeanList.size() != 0) {
                        upload(mAccessRecordBeanList.remove(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public class ResponseForHttp {

        /**
         * status : 0
         * msg : 成功
         */

        private int status;
        private String msg;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
