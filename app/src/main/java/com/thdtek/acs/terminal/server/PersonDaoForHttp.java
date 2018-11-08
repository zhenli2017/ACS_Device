package com.thdtek.acs.terminal.server;

import android.text.TextUtils;

import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import greendao.PersonBeanDao;

public class PersonDaoForHttp {

    private static final String TAG = PersonDaoForHttp.class.getSimpleName();
    private static final PersonBeanDao dao = DBUtil.getDaoSession().getPersonBeanDao();

//    public void insert(PersonBean personBean) {
//        long t = System.currentTimeMillis();
//        personBean.setPerson_id(t);
//        personBean.setAuth_id(t);
//
//        try {
//            long line = dao.insert(personBean);
//            if(line > 0){
//                LogUtils.d(TAG, "PersonBean插入成功 fid="+personBean.getFid());
//                addCache(personBean);
//            }else{
//                LogUtils.e(TAG, "PersonBean插入失败 fid="+personBean.getFid());
//            }
//
//        } catch (Exception e) {
//            LogUtils.e(TAG, "PersonBean插入失败 fid="+personBean.getFid());
//            e.printStackTrace();
//        }
//
//    }

    public PersonBean queryByFid(String fid){
        if(TextUtils.isEmpty(fid)){
            throw new RuntimeException("fid不能为空");
        }
        List<PersonBean> lst = dao.queryBuilder()
                .where(PersonBeanDao.Properties.Fid.eq(fid))
                .list();
        if(lst != null && lst.size() > 0){
            return lst.get(0);
        }
        return null;
    }

    public PersonBean queryByPersonId(long personId){
        List<PersonBean> lst = dao.queryBuilder()
                .where(PersonBeanDao.Properties.Person_id.eq(personId))
                .list();
        if(lst != null && lst.size() > 0){
            return lst.get(0);
        }
        return null;
    }

    public List<PersonBean> queryAll(){
        return dao.queryBuilder().list();
    }


//    public void insertOrUpdate(PersonBean bean){
//        PersonBean localData = queryByFid(bean.getFid());
//        if(localData == null){
//            insert(bean);
//        }else{
//            localData.setCount(bean.getCount());
//            localData.setEmployee_card_id(bean.getEmployee_card_id());
//            localData.setStart_ts(bean.getStart_ts());
//            localData.setEnd_ts(bean.getEnd_ts());
//            localData.setID_no(bean.getID_no());
//            localData.setName(bean.getName());
//            dao.update(localData);
//
//            upateCache(bean);
//        }
//    }

//    public void update(PersonBean bean){
//        PersonBean localData = queryByFid(bean.getFid());
//        localData.setCount(bean.getCount());
//        localData.setEmployee_card_id(bean.getEmployee_card_id());
//        localData.setStart_ts(bean.getStart_ts());
//        localData.setEnd_ts(bean.getEnd_ts());
//        localData.setID_no(bean.getID_no());
//        localData.setName(bean.getName());
//        dao.update(localData);
//
//        upateCache(bean);
//    }

    public void deleteByFid(String[] idsArr){
        if(idsArr.length == 0){
            dao.deleteAll();

            clearCache();
        }else {
            for (int i = 0; i < idsArr.length; i++) {
                String fid = idsArr[i];
                PersonBean bean = queryByFid(fid);
                if(bean != null){
                    dao.delete(bean);
                }

                clearCache(fid);
            }
        }

    }



    //更新缓存  兼容旧版本
    public void upateCache(PersonBean personBean){
        Vector<PersonBean> lst = PersonDao.getPeopleList(false);
        for (int i = 0; i < lst.size(); i++) {
            if(personBean.getFid().equals(lst.get(i).getFid())){
                lst.remove(i);
                break;
            }
        }
        lst.add(personBean);
    }

    //添加缓存  兼容旧版本
    public void addCache(PersonBean personBean){
        PersonDao.getPeopleList(false).add(personBean);
    }

    //清理缓存 兼容旧版
    public void clearCache(){
        PersonDao.getPeopleList(false).clear();
    }

    //清理缓存 兼容旧版
    public void clearCache(String fid){
        Vector<PersonBean> lst = PersonDao.getPeopleList(false);
        Iterator<PersonBean> it = lst.iterator();
        while (it.hasNext()) {
            PersonBean next = it.next();
            if(fid.equals(next.getFid())){
                lst.remove(next);
            }
        }
    }
}
