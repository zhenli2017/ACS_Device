package com.thdtek.acs.terminal.thread;

import com.thdtek.acs.terminal.bean.PairBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.HashMap;

/**
 * Time:2018/10/18
 * User:lizhen
 * Description:
 */

public class PairThreadThree extends BaseThread {
    private static final String TAG = PairThreadThree.class.getSimpleName();

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void handleData(Object faceApi, Object bean) {
        PairBean pairBean = (PairBean) bean;
        LogUtils.d(TAG, "=================================多线程比对 3 开始比对=============================== "+pairBean.getType());
        HashMap<Float, PersonBean> pairMap = null;
        if (pairBean.getType() == Const.THREAD_PAIR_TYPE_LEARN) {
            //比对学习照
            pairMap = getPairMap(faceApi, Const.THREAD_PAIR_INDEX_THREE, pairBean.getFaceFeature());
        } else {
            //比对正装照
            pairMap = getPairOfficialMap(faceApi, Const.THREAD_PAIR_INDEX_THREE, pairBean.getFaceFeature(), new HashMap<Float, PersonBean>());
        }
        LogUtils.d(TAG, "=================================多线程比对 3 结束比对===============================");
        try {
            if (pairMap == null) {
                pairMap = new HashMap<>();
            }
            ThreadManager.getSyncQueueThree().put(pairMap);
        } catch (InterruptedException e) {
            e.printStackTrace();
            LogUtils.e(TAG, "========== 线程 3 存入数据失败 ==========");
        }
    }
}
