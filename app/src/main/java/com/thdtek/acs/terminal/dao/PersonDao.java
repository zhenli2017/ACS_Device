package com.thdtek.acs.terminal.dao;

import android.text.TextUtils;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.bean.ImageSaveBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.thread.ThreadManager;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import greendao.PersonBeanDao;

public class PersonDao {

    private static final String TAG = PersonDao.class.getSimpleName();
    private static Vector<PersonBean> mPeopleBeanList = new Vector<>();

    public static void reloadPersonList() {
        mPeopleBeanList.clear();
        List<PersonBean> list = queryAll();
        if (list != null) {
            mPeopleBeanList.addAll(list);
        }
    }

    public static Vector<PersonBean> getPeopleList(boolean reload) {
        if (reload) {
            reloadPersonList();
        }
        return mPeopleBeanList;
    }

    public static PersonBeanDao getDao() {
        return DBUtil.getDaoSession().getPersonBeanDao();
    }

    public static void insert(PersonBean personBean) {
        try {
            getDao().insert(personBean);
            if (!mPeopleBeanList.contains(personBean)) {
                mPeopleBeanList.add(personBean);
            }
            LogUtils.d(TAG, "insert mPeopleBeanList size = " + mPeopleBeanList.size() + " AUid = " + personBean.getAuth_id() + " pId = " + personBean.getPerson_id());
        } catch (Exception e) {
            LogUtils.e(TAG, "person id = " + personBean.getPerson_id() + " error = " + e.getMessage());
        }

    }

    public static void inserOrReplace(PersonBean personBean) {
        getDao().insertOrReplace(personBean);
        mPeopleBeanList.add(personBean);
        LogUtils.d(TAG, "mPeopleBeanList size = " + mPeopleBeanList.size() + " id = " + personBean.getAuth_id() + " pId = " + personBean.getPerson_id());
    }

    public static void update(PersonBean personBean) {
        getDao().update(personBean);
        if (!mPeopleBeanList.contains(personBean)) {
            LogUtils.d(TAG,"person update not contains");
            mPeopleBeanList.add(personBean);
        } else {
            LogUtils.d(TAG,"person update contains");
            mPeopleBeanList.remove(personBean);
            mPeopleBeanList.add(personBean);
        }
        LogUtils.d(TAG, "update mPeopleBeanList size = " + mPeopleBeanList.size() + " AUid = " + personBean.getAuth_id() + " pId = " + personBean.getPerson_id()+" msg = "+personBean.toString());
    }

    public static void delete(PersonBean personBean) {
        deleteAuthority(personBean.getAuth_id());
    }

    public static boolean deleteAuthority(long authorityId) {
        PersonBean personBean = query2AuthorityId(authorityId);
        if (personBean == null) {
            LogUtils.e(TAG, "不存在这个人,无法删除 deleteAuthority");
            return false;
        }
        getDao().deleteByKey(personBean.getAuth_id());
        mPeopleBeanList.remove(personBean);
        NowPicFeatureDao.delete(authorityId);
        FaceFeatureDao.delete(authorityId);


        if (!TextUtils.isEmpty(personBean.getFacePic())) {
            FileUtil.deleteFile(personBean.getFacePic());
        }

        LogUtils.d(TAG, "删除权限成功 = " + authorityId + " size = " + mPeopleBeanList.size());
        return true;
    }

    public static boolean deletePerson(long personId) {
        PersonBean personBean = query2PersonId(personId);
        if (personBean == null) {
            LogUtils.d(TAG, "不存在这个人,无法删除 deletePerson");
            return false;
        }
        return deleteAuthority(personBean.getAuth_id());
    }

    public static List<PersonBean> queryAll() {
        return getDao().queryBuilder().list();
    }

    public static long queryAllSize() {
        List<PersonBean> personBeans = queryAll();
        if (personBeans == null || personBeans.size() == 0) {
            return 0L;
        } else {
            return personBeans.size();
        }
    }

    public static PersonBean query2PersonId(long id) {
        List<PersonBean> list = getDao().queryBuilder().where(PersonBeanDao.Properties.Person_id.eq(id)).build().list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static PersonBean query2AuthorityId(long id) {
        List<PersonBean> list = getDao().queryBuilder().where(PersonBeanDao.Properties.Auth_id.eq(id)).build().list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static PersonBean query2Name(String name) {
        List<PersonBean> list = getDao().queryBuilder().where(PersonBeanDao.Properties.Name.eq(name)).build().list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static PersonBean query2ICCard(String icNumber) {
        List<PersonBean> list = PersonDao.getDao().queryBuilder().where(PersonBeanDao.Properties.Employee_card_id.eq(icNumber)).list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static PersonBean query2IDCard(String idNumber) {
        List<PersonBean> list = PersonDao.getDao().queryBuilder().where(PersonBeanDao.Properties.ID_no.eq(idNumber)).list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }
    public static PersonBean query2Fid(String fid) {
        List<PersonBean> list = PersonDao.getDao().queryBuilder().where(PersonBeanDao.Properties.Fid.eq(fid)).list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static synchronized String[] insertPerson(Msg.Message.RsyncPersonRsp rsyncPersonRsp) {
        LogUtils.d(TAG, "准备 \"添加\" Person信息 = " + rsyncPersonRsp.getPersonId());
        PersonBean personBean = PersonDao.query2PersonId(rsyncPersonRsp.getPersonId());
        if (personBean == null) {
            LogUtils.e(TAG, "数据库中不存在这个id,无法 insert,return " + rsyncPersonRsp.getPersonId());
            return parseMessage(false, "数据库中不存在这个personId");
        }
        personBean.setPerson_id(rsyncPersonRsp.getPersonId());
        personBean.setPerson_ts(rsyncPersonRsp.getPersonTs());
        personBean.setName(rsyncPersonRsp.getName());
        personBean.setID_no(rsyncPersonRsp.getIDNo().toLowerCase().trim());
        //设置卡号
        personBean.setEmployee_card_id(rsyncPersonRsp.getEmployeeCardId().toLowerCase().trim());
        String imagePath = "图片保存失败";
        try {
            LogUtils.d(TAG, "insert personBean.getPerson_id() = " + personBean.getPerson_id());
            ThreadManager.getImageSaveThread().add(new ImageSaveBean(personBean.getAuth_id(),
                    rsyncPersonRsp.getFacePic().toByteArray(),
                    personBean, true)
            );
            imagePath = ThreadManager.getImageSaveThread().get();
            LogUtils.d(TAG, "insert personBean.getPerson_id() = " + personBean.getPerson_id() + " imagePath = " + imagePath);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG,"insertPerson = "+e.getMessage());
        }
        //包含特定的字段表示保存成功
        if (imagePath.contains(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS)) {
            personBean.setFacePic(imagePath.replaceAll(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS, ""));
            PersonDao.update(personBean);
            LogUtils.d(TAG, "personBean.getPerson_id() = " + personBean.getPerson_id() + " 添加成功");
        } else {
            PersonDao.deletePerson(personBean.getPerson_id());
            LogUtils.d(TAG, "personBean.getPerson_id() = " + personBean.getPerson_id() + " 添加失败,删除");
        }
        return parseMessage(true, imagePath);
    }


    public static synchronized String[] updatePerson(Msg.Message.RsyncPersonRsp rsyncPersonRsp) {
        LogUtils.d(TAG, "准备 \"更新\" Person信息 = " + rsyncPersonRsp.getPersonId());
        PersonBean personBean = PersonDao.query2PersonId(rsyncPersonRsp.getPersonId());
        if (personBean == null) {
            LogUtils.e(TAG, "数据库中不存在这个id,无法更新,return");
            return parseMessage(false, "数据库中不存在这个personId");
        }
        if (rsyncPersonRsp.hasEmployeeCardId()) {
            //设置卡号
            personBean.setEmployee_card_id(rsyncPersonRsp.getEmployeeCardId().toLowerCase().trim());
        }
        if (rsyncPersonRsp.hasIDNo()) {
            personBean.setID_no(rsyncPersonRsp.getIDNo().toLowerCase().trim());
        }
        if (rsyncPersonRsp.hasName()) {
            personBean.setName(rsyncPersonRsp.getName());
        }
        if (rsyncPersonRsp.hasPersonId()) {
            personBean.setPerson_id(rsyncPersonRsp.getPersonId());
        }
        if (rsyncPersonRsp.hasPersonTs()) {
            personBean.setPerson_ts(rsyncPersonRsp.getPersonTs());
        }
        String msg = "图片保存失败";
        if (rsyncPersonRsp.hasFacePic()) {
            LogUtils.d(TAG, "update Person personBean.getPerson_id() = " + personBean.getPerson_id());
            String imagePath = "图片保存失败";
            try {
                ThreadManager.getImageSaveThread().add(new ImageSaveBean(personBean.getAuth_id(),
                        rsyncPersonRsp.getFacePic().toByteArray(),
                        personBean, true)
                );
                imagePath = ThreadManager.getImageSaveThread().get();
                LogUtils.d(TAG, "update imagePath = " + imagePath + "personBean.getPerson_id() = " + personBean.getPerson_id());
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(TAG,"insertPerson = "+e.getMessage());
            }
            //包含特定的字段表示保存成功
            if (imagePath.contains(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS)) {
                String oldFacePic = personBean.getFacePic();
                personBean.setFacePic(imagePath.replaceAll(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS, ""));
                personBean.setOldFacePic(oldFacePic);
                LogUtils.d(TAG, "update personBean.getPerson_id() = " + personBean.getPerson_id() + " update 图片解析成功");
            } else {
                msg = imagePath;
                LogUtils.d(TAG, "update personBean.getPerson_id() = " + personBean.getPerson_id() + " update 图片解析失败 " + msg);
                PersonDao.deletePerson(personBean.getPerson_id());
                LogUtils.d(TAG, "update personBean.getPerson_id() = " + personBean.getPerson_id() + " update 添加失败,删除 " + msg);
                return parseMessage(true, msg);
            }
        }
        PersonDao.update(personBean);
        return parseMessage(true, msg);
    }

    private static String[] parseMessage(boolean b, String message) {
        String[] data = new String[]{"true", ""};
        if (b) {
            data[0] = "true";
        } else {
            data[0] = String.valueOf(false);
            data[1] = message;
        }
        return data;
    }


}
