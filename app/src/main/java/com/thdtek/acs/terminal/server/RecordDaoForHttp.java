package com.thdtek.acs.terminal.server;

import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.util.DBUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import greendao.AccessRecordBeanDao;

public class RecordDaoForHttp {

    private static final String TAG = RecordDaoForHttp.class.getSimpleName();
    private static final AccessRecordBeanDao dao = DBUtil.getDaoSession().getAccessRecordBeanDao();

    public List<AccessRecordBean> queryByTsAndCount(long ts, int count){
        QueryBuilder<AccessRecordBean> builder = dao.queryBuilder();
        return builder.where(AccessRecordBeanDao.Properties.Time.gt(ts))
                .limit(count)
                .list();
    }

    public void addSimulationRecord(int count){
        for (int i = 1; i <= count; i++) {
            AccessRecordBean bean = new AccessRecordBean();
            bean.setTime(i);
            bean.setType(2);
            dao.insert(bean);
        }
    }

    //删除ts以及ts以前的记录
    public void deleteByTs(long ts){
        QueryBuilder<AccessRecordBean> builder = dao.queryBuilder();
        builder.where(AccessRecordBeanDao.Properties.Time.le(ts))
                .buildDelete()
//                .forCurrentThread()
                .executeDeleteWithoutDetachingEntities();

    }


    public List<AccessRecordBean> queryUnUploadRecords(){
        List<AccessRecordBean> lst = dao.queryBuilder()
                .where(AccessRecordBeanDao.Properties.UploadToHttp.eq(false))
                .list();
        return lst;
    }
}
