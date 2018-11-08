package com.thdtek.acs.terminal.dao;

import android.util.LongSparseArray;

import com.thdtek.acs.terminal.bean.FaceFeatureHexBean;
import com.thdtek.acs.terminal.bean.NowPicFeatureHexBean;
import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.List;

import greendao.FaceFeatureHexBeanDao;
import greendao.NowPicFeatureHexBeanDao;

/**
 * Time:2018/7/7
 * User:lizhen
 * Description:
 */

public class FaceFeatureDao {
    private static final String TAG = FaceFeatureDao.class.getSimpleName();

    private static LongSparseArray<FaceFeatureHexBean> mMap = new LongSparseArray<>();

    public static LongSparseArray<FaceFeatureHexBean> getMap(boolean reload) {
        if (reload) {
            mMap.clear();
            List<FaceFeatureHexBean> list = queryAll();
            if (list != null) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    mMap.put(list.get(i).getAuthorityId(), list.get(i));
                }
            }
        }
        return mMap;
    }


    public static FaceFeatureHexBeanDao getDao() {
        return DBUtil.getDaoSession().getFaceFeatureHexBeanDao();
    }

    public static void insertOrReplace(long authorityId, long personId, byte[] faceFeature) {
        try {
            FaceFeatureHexBean faceFeatureHexBean = query2AuthorityId(authorityId);
            if (faceFeatureHexBean == null) {
                faceFeatureHexBean = new FaceFeatureHexBean();
            }
            faceFeatureHexBean.setPersonId(personId);
            faceFeatureHexBean.setAuthorityId(authorityId);
            faceFeatureHexBean.setFaceFeatureHex(ByteFormatTransferUtils.bytesToHexStringNoSpace(faceFeature));
            faceFeatureHexBean.setFaceFeatureByte(faceFeature);
            getDao().insertOrReplace(faceFeatureHexBean);
            mMap.put(authorityId, faceFeatureHexBean);
        } catch (Exception e) {
            LogUtils.e(TAG, "mMap insert FaceFeatureHexBean id = " + authorityId + " error = " + e.getMessage());
        }

    }

    public static void delete(FaceFeatureHexBean bean) {
        getDao().delete(bean);
    }

    public static void delete(long authorityId) {
        getDao().deleteByKey(authorityId);
        mMap.remove(authorityId);
    }

    public static List<FaceFeatureHexBean> queryAll() {
        return getDao().queryBuilder().list();
    }

    public static FaceFeatureHexBean query2AuthorityId(long id) {
        List<FaceFeatureHexBean> list = getDao().queryBuilder().where(FaceFeatureHexBeanDao.Properties.AuthorityId.eq(id)).build().list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

}
