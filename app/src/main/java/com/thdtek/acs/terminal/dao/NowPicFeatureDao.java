package com.thdtek.acs.terminal.dao;

import android.util.LongSparseArray;

import com.thdtek.acs.terminal.bean.NowPicFeatureHexBean;
import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.List;

import greendao.NowPicFeatureHexBeanDao;

/**
 * Time:2018/7/7
 * User:lizhen
 * Description:
 */

public class NowPicFeatureDao {
    private static final String TAG = NowPicFeatureDao.class.getSimpleName();
    private static LongSparseArray<NowPicFeatureHexBean> mMap = new LongSparseArray<>();

    public static LongSparseArray<NowPicFeatureHexBean> getMap(boolean reload) {
        if (reload) {
            mMap.clear();
            List<NowPicFeatureHexBean> list = queryAll();
            if (list != null) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    mMap.put(list.get(i).getAuthorityId(), list.get(i));
                }
            }
        }
        return mMap;
    }

    public static void put2Map(NowPicFeatureHexBean bean) {
        mMap.put(bean.getAuthorityId(), bean);

    }

    public static NowPicFeatureHexBeanDao getDao() {
        return DBUtil.getDaoSession().getNowPicFeatureHexBeanDao();
    }

    public static void insertOrReplace(long authorityId, long personId, byte[] faceFeature, boolean saveAll) {
        try {
            NowPicFeatureHexBean nowPicFeatureHexBean = query2AuthorityId(authorityId);
            if (nowPicFeatureHexBean == null) {
                nowPicFeatureHexBean = new NowPicFeatureHexBean();
            }
            nowPicFeatureHexBean.setPersonId(personId);
            nowPicFeatureHexBean.setAuthorityId(authorityId);

            if (saveAll) {
                nowPicFeatureHexBean.setLastIndex(2);
                nowPicFeatureHexBean.setNowPicTwoHex("");
                nowPicFeatureHexBean.setNowPicTwoByte(null);

                nowPicFeatureHexBean.setNowPicThreeHex("");
                nowPicFeatureHexBean.setNowPicThreeByte(null);

                nowPicFeatureHexBean.setNowPicOneHex("");
                nowPicFeatureHexBean.setNowPicOneByte(null);

            } else if (nowPicFeatureHexBean.getLastIndex() == 0) {
                System.out.println("======== NowPicFeatureHexBean 2");
                nowPicFeatureHexBean.setNowPicTwoHex(ByteFormatTransferUtils.bytesToHexStringNoSpace(faceFeature));
                nowPicFeatureHexBean.setNowPicTwoByte(faceFeature);
                nowPicFeatureHexBean.setLastIndex(1);
            } else if (nowPicFeatureHexBean.getLastIndex() == 1) {
                System.out.println("======== NowPicFeatureHexBean 3");
                nowPicFeatureHexBean.setNowPicThreeHex(ByteFormatTransferUtils.bytesToHexStringNoSpace(faceFeature));
                nowPicFeatureHexBean.setNowPicThreeByte(faceFeature);
                nowPicFeatureHexBean.setLastIndex(2);
            } else {
                System.out.println("======== NowPicFeatureHexBean 1");
                nowPicFeatureHexBean.setNowPicOneHex(ByteFormatTransferUtils.bytesToHexStringNoSpace(faceFeature));
                nowPicFeatureHexBean.setNowPicOneByte(faceFeature);
                nowPicFeatureHexBean.setLastIndex(0);
            }

            getDao().insertOrReplace(nowPicFeatureHexBean);

            mMap.put(authorityId, nowPicFeatureHexBean);
            LogUtils.d(TAG, "mMap NowPicFeatureDao size = " + mMap.size() + " id = " + authorityId);
        } catch (Exception e) {
            LogUtils.e(TAG, "mMap insert NowPicFeatureDao id = " + authorityId + " error = " + e.getMessage());
        }

    }

    public static void delete(NowPicFeatureHexBean bean) {
        getDao().delete(bean);
        mMap.remove(bean.getAuthorityId());
        LogUtils.d(TAG, "当前NOW PIC 剩余数量 = " + mMap.size());
    }

    public static void delete(long authorityId) {
        getDao().deleteByKey(authorityId);
        mMap.remove(authorityId);
        LogUtils.d(TAG, "当前NOW PIC 剩余数量 = " + mMap.size());
    }

    public static List<NowPicFeatureHexBean> queryAll() {
        return getDao().queryBuilder().list();
    }

    public static NowPicFeatureHexBean query2AuthorityId(long id) {
        List<NowPicFeatureHexBean> list = getDao().queryBuilder().where(NowPicFeatureHexBeanDao.Properties.AuthorityId.eq(id)).build().list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

}
