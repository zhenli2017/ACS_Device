package com.thdtek.acs.terminal.http.serverpush.pushImp;

import com.google.protobuf.ByteString;
import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import greendao.AccessRecordBeanDao;

/**
 * Time:2018/7/4
 * User:ygb
 * Description:
 */

public class PushQueryPassRecord extends PushBaseImp {
    private static final String TAG = PushQueryPassRecord.class.getSimpleName();

    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {

        LogUtils.d(TAG, "服务器向终端查询数据 FETCH_PASS_RECORD_REQ接口");
        Msg.Message.FetchPassRecordRsp.Builder rspBuilder = Msg.Message.FetchPassRecordRsp.newBuilder();
        Msg.Message.FetchPassRecordRsp rsp = null;

        Msg.Message.FetchPassRecordReq req = message.getFetchPassRecordReq();
        if(req.hasStartTs() && req.hasAutoDelete() && req.hasCount()){
            double startTs = req.getStartTs();
            boolean autoDelete = req.getAutoDelete();
            long count = req.getCount();
            LogUtils.d(TAG, "startTs="+startTs);
            LogUtils.d(TAG, "autoDelete="+autoDelete);
            LogUtils.d(TAG, "count="+count);

            //查询本地数据库所有流水
            final AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();
            List<AccessRecordBean> list = accessRecordBeanDao.queryBuilder().list();
            if (list != null || list.size() != 0) {
                LogUtils.d(TAG, "本地数据库所有流水记录条数="+list.size());

                //排除不符合条件的流水
                Iterator<AccessRecordBean> it = list.iterator();
                while(it.hasNext()){
                    AccessRecordBean bean = it.next();
                    if(bean.getTime() < startTs){
                        it.remove();
                    }
                }

                if (list.size() != 0) {
                    LogUtils.d(TAG, "符合条件的流水记录条数="+list.size());

                    rspBuilder.setHasMore(list.size() > count);

                    long max = list.size();
                    if(list.size() > count){
                        max = count;
                        LogUtils.d(TAG, "上传流水条数="+count);
                    }
                    for (int i = 0; i < max; i++) {
                        final AccessRecordBean bean = list.get(i);
                        Msg.Message.SimplePassRecord.Builder simplePassRecordBuilder
                                = Msg.Message.SimplePassRecord.newBuilder();
                        Msg.Message.SimplePassRecord simplePassRecord;

                        simplePassRecordBuilder.setPersonId(bean.getPersonId());
                        simplePassRecordBuilder.setPassTs(bean.getTime());
                        simplePassRecordBuilder.setMethod(bean.getType());
                        ByteString now_img = getNowImg(bean);
                        if(now_img != null){
                            simplePassRecordBuilder.setNowImg(now_img);
                        }
                        simplePassRecord = simplePassRecordBuilder.build();

                        rspBuilder.addRecordList(i, simplePassRecord);

                        //终端删除流水--bug 若服务器未收到上传数据本地又已经删除
                        if(autoDelete){
                            ThreadPool.getThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtils.d(TAG, "已经添加到上传对象，删除本地记录");
                                    accessRecordBeanDao.delete(bean);
                                    FileUtil.deleteFile(bean.getAccessImage());
                                }
                            });

                        }
                    }


                }else{
                    LogUtils.d(TAG, "没有符合条件的流水记录");
                }
            }else{
                LogUtils.d(TAG, "没有符合条件的流水记录=");
            }



        }

        rsp = rspBuilder.build();
        Msg.Message message1 = Msg.Message.newBuilder()
                .setFetchPassRecordRsp(rsp)
                .build();

//        new SendMsgHelper().response(message1, seq);
        return message1;
    }


    //获取流水抓拍img
    private ByteString getNowImg(AccessRecordBean bean){
        ByteString bytes = null;
        try {
            bytes = ByteString.readFrom(FileUtil.getFileInputStream(bean.getAccessImage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
