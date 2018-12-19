package com.thdtek.acs.terminal.haogonge;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;

import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.ImageSaveBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.FaceFeatureDao;
import com.thdtek.acs.terminal.dao.NowPicFeatureDao;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.server.PersonDaoForHttp;
import com.thdtek.acs.terminal.thread.ThreadManager;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.CodeUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.DeviceSnUtil;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import greendao.AccessRecordBeanDao;
import greendao.PersonBeanDao;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 对接好工e云平台
 * 使用案例:
 * HaogongeThread.startWork(context);
 * HaogongeThread.stopWork();
 * HaogongeThread.restartWork();
 */
public class HaogongeThread extends Thread {

    private static final String TAG = HaogongeThread.class.getSimpleName();
    public static String sn;
    public static String v;
    public static String deviceModel;
    public static String manufacturer;

    public static String url_root;
    private static Context mContext;
    //好工e--轮询时间间隔，单位秒
    public static double transInterval = 10.0f;
    //好工e--是否需要上传考勤照片，0不上传，1上传
    public static int uploadAttPic = 1;
    //好工e--通信秘钥
    public static String encryptKey = "";
    //好工e--验证方式,可选择local,remote,默认local
    public static String verifyType = "";
    //好工e--设备掉线后，人员对比通过后判断开闸与否，可选择open、close,默认open
    public static String offlineAction = "";
    //okhttp
    public static OkHttpClient mOkHttpClient;
    public static OkHttpClient mOkHttpClientForImg;
    //实例
    private static HaogongeThread instance;
    //全局开关
    private static boolean mSwitch = true;
    //设置默认值-好工e云平台没有推送设置的参数
    private final long mPassCount = 999999999L;
    private final double mStartTs = 0.0d;
    private final double mEndTs = System.currentTimeMillis() * 2;
    //cmd
    private static final String CMD_DATA_UPDATE_FACE = "DATA UPDATE FACE";
    private static final String CMD_DATA_DELETE_FACE = "DATA DELETE FACE";
    private static final String CMD_DATA_UPDATE_USER = "DATA UPDATE USER";
    private static final String CMD_DATA_DELETE_USER = "DATA DELETE USER";
    private static final String CMD_DATA_DELETE_CARD = "DATA DELETE CARD";
    private static final String CMD_CLEAR_DATA = "CLEAR DATA";
    private static final String CMD_SET_OPTIONS = "SET OPTIONS";
    //上传考勤标志 初始值0，每次请求的值=上次index+1
    public static long uploadIndex = 0;


    @Override
    public void run() {
        super.run();

        while (mSwitch){

            try {
                go();
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                sendBroadCast("", "", Const.VIEW_STATUS_OFF_LINE);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //程序入口
    public static void startWork(Context context){
        mSwitch = true;
        mContext = context;
        if(mContext == null){
            throw new RuntimeException("context can not null");
        }

        if(instance == null){
            instance = new HaogongeThread();
        }
        instance.start();
    }

    //程序出口
    public static void stopWork(){
        mSwitch = false;
        if(instance != null){
            instance.interrupt();
        }
    }

    public static void reStartWork(){
        mSwitch = true;
        if(instance != null){
            instance.interrupt();
        }
    }

    private void go() throws Exception {
        //初始化
        init();

        //每次启动都需注册
        registerDevice();

        //同步时间--若失败则放弃本次同步
        syncTime();

        //上传考勤
        UploadRecordForHaogonge.getInstance().upload();

        //轮询服务器
        for(;;){

            getCmd();

            Thread.sleep((long) (transInterval * 1000));
        }

    }


    private void init(){
        LogUtils.d(TAG, "init ...");

        boolean init_defalut = (boolean) SPUtils.get(mContext, Const.haogonge_init_default, false);
        if(init_defalut == false){
            SPUtils.put(mContext, Const.haogonge_transInterval, 10f);
            SPUtils.put(mContext, Const.haogonge_uploadAttPic, 1);
            SPUtils.put(mContext, Const.haogonge_verifyType, "remote");
            SPUtils.put(mContext, Const.haogonge_offlineAction, "open");
            SPUtils.put(mContext, Const.haogonge_url, "http://clock.haogonge.com:63722/iclock");
//            SPUtils.put(mContext, Const.haogonge_url, "http://hurongoray.oicp.net:59376/iclock");
            SPUtils.put(mContext, Const.haogonge_model, AppUtil.getModel());
            SPUtils.put(mContext, Const.haogonge_code, AppUtil.getManufacturer());
            SPUtils.put(mContext, Const.haogonge_interface_version, "v0.0.1");

            SPUtils.put(mContext, Const.haogonge_init_default, true);
        }


        transInterval = (float) SPUtils.get(mContext, Const.haogonge_transInterval, 10f);
        uploadAttPic = (int) SPUtils.get(mContext, Const.haogonge_uploadAttPic, 1);
        verifyType = (String) SPUtils.get(mContext, Const.haogonge_verifyType, "");
        offlineAction = (String) SPUtils.get(mContext, Const.haogonge_offlineAction, "");
        sn = DeviceSnUtil.getDeviceSn();
        url_root = (String) SPUtils.get(mContext, Const.haogonge_url, "");
        deviceModel = (String) SPUtils.get(mContext, Const.haogonge_model, "");
        manufacturer = (String) SPUtils.get(mContext, Const.haogonge_code, "");
        v = (String) SPUtils.get(mContext, Const.haogonge_interface_version, "");


        //okHttp
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        mOkHttpClientForImg = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60*2, TimeUnit.SECONDS)
                .writeTimeout(60*2, TimeUnit.SECONDS)
                .build();



        LogUtils.d(TAG, "init success");

    }


    private void registerDevice() throws IOException {
        LogUtils.d(TAG, "register ...");
        String url = url_root;
        url += "/registerDevice";
        url += "?sn="+sn;
        url += "&v="+v;
        url += "&deviceModel="+deviceModel;
        url += "&manufacturer="+manufacturer;
        LogUtils.d(TAG, "url=" + url);

        FormBody formBody = new FormBody.Builder().build();

        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        final Call call = mOkHttpClient.newCall(request);

        Response response = call.execute();
        if(response.isSuccessful()){

            sendBroadCast("", "", Const.VIEW_STATUS_ON_LINE);

            //归零上传标志
            uploadIndex = 0;

            //解析配置参数
            String respStr = response.body().string();
            LogUtils.d(TAG, "register:" + respStr);
            String[] arr = respStr.split("\n");
            if(arr != null){
                for (int i = 0; i < arr.length; i++) {
                    String item = arr[i];
                    String[] arrItem = item.split("=");
                    if(arr != null && arrItem.length == 2){
                        String key = arrItem[0];
                        String val = arrItem[1];

                        if("transInterval".equals(key)){
                            transInterval = Double.parseDouble(val);
                            SPUtils.put(mContext, Const.haogonge_transInterval, (float)transInterval);
                        }
                        else if("uploadAttPic".equals(key)){
                            uploadAttPic = Integer.parseInt(val);
                            SPUtils.put(mContext, Const.haogonge_uploadAttPic, uploadAttPic);
                        }
                        else if("encryptKey".equals(key)){
                            encryptKey = val;
                        }
                        else if("verifyType".equals(key)){
                            verifyType = val;
                            SPUtils.put(mContext, Const.haogonge_verifyType, verifyType);
                        }
                        else if("offlineAction".equals(key)){
                            offlineAction = val;
                            SPUtils.put(mContext, Const.haogonge_offlineAction, offlineAction);
                        }

                    }

                }
            }


            if(TextUtils.isEmpty(encryptKey)){
                LogUtils.e(TAG, "注册失败 encryptKey is null");
                throw new RuntimeException("encryptKey is null");
            }


            if(transInterval < 1){
                LogUtils.e(TAG, "轮询服务器间隔时间太短(应该大等于1秒) transInterval="+transInterval);
                throw new RuntimeException("轮询服务器间隔时间太短(应该大等于1秒) transInterval="+transInterval);
            }

            LogUtils.i(TAG, "设备注册成功");

        }else{
            LogUtils.e(TAG, "注册失败");
            throw new RuntimeException("注册失败");
        }
    }


    private void syncTime() throws IOException {
        LogUtils.d(TAG, "syncTime ...");
        String url = url_root;
        url += "/syncTime";
        url += "?sn="+sn;
        url += "&v="+v;
        LogUtils.d(TAG, "url=" + url);

        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);

        Response response = call.execute();
        if(response.isSuccessful()){

            sendBroadCast("", "", Const.VIEW_STATUS_ON_LINE);

            String respStr = response.body().string();
            LogUtils.d(TAG, "syncTime:" + respStr);

            String timeStr = respStr;
            long time = -1;
            try {
                time = new TimeFormat().parse(timeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(time < 0){
                //返回失败
                LogUtils.e(TAG, "时间同步失败-ime="+time);
            }else{
                //设置时间
                boolean re = HWUtil.setClientSystemTime((time / 1000));
                if(re){
                    //返回成功
                    LogUtils.i(TAG, "时间同步成功");
                }else{
                    //返回失败
                    LogUtils.e(TAG, "时间同步失败-底层api报错");
                }
            }
        }else{
            LogUtils.e(TAG, "时间同步失败");
        }
    }

    private void updateCmdResult(String cmd, long id, int code, String msg) throws IOException {
        List<BodyItem> lst = new ArrayList<>();
        BodyItem item = new BodyItem();
        item.setCmd(cmd);
        item.setId(id);
        item.setCode(code);
        item.setMsg(msg);
        lst.add(item);
        updateCmdResult(lst);
    }

    private void updateCmdResult(List<BodyItem> lst) throws IOException {
        LogUtils.d(TAG, "updateCmdResult ...");

        if(lst == null){
            LogUtils.d(TAG, "updateCmdResult List<BodyItem> lst = null");
            return;
        }

        //body
        BodyForUpdateCmdResult body = new BodyForUpdateCmdResult();
        for (int i = 0; i < lst.size(); i++) {
            body.add(lst.get(i));
        }

        String bodyForSign = body.getBodyString();

        long time = new Date().getTime();
        String sign = new Sign().getSignString(sn, time, bodyForSign, encryptKey);

        String url = url_root;
        url += "/updateCmdResult";
        url += "?sn="+sn;
        url += "&v="+v;
        url += "&time="+time;
        url += "&sign="+sign;
        LogUtils.d(TAG, "url=" + url);

        String result = new DoPost().post(url, bodyForSign);
        if(TextUtils.isEmpty(result)){
            LogUtils.e(TAG, "updateCmdResult:失败");
        }else{
            LogUtils.i(TAG, "updateCmdResult:"+result);
        }

    }


    private void getCmd() throws IOException, InterruptedException {
        LogUtils.d(TAG, "getCmd ...");
        String url = url_root;
        url += "/getCmd";
        url += "?sn="+sn;
        url += "&v="+v;
        LogUtils.d(TAG, "url=" + url);

        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClientForImg.newCall(request);

        Response response = call.execute();
        if(response.isSuccessful()){

            sendBroadCast("", "", Const.VIEW_STATUS_ON_LINE);

            String respStr = response.body().string();
            LogUtils.d(TAG, "getCmd:" + respStr);

            if(!TextUtils.isEmpty(respStr)){
                String[] arr_cmd_all = respStr.split("\n");
                if(arr_cmd_all != null && arr_cmd_all.length > 0){
                    for (int i = 0; i < arr_cmd_all.length; i++) {
                        String cmd_item = arr_cmd_all[i];
                        if(!TextUtils.isEmpty(cmd_item)){
                            String[] arr_cmd_item = cmd_item.split(":");
                            if(arr_cmd_item != null && arr_cmd_item.length == 3){
                                String cmd0 = arr_cmd_item[1];
                                String cmd1 = arr_cmd_item[1];
                                String cmd2 = arr_cmd_item[2];
                                String CMD_ID = cmd1;
                                if(!TextUtils.isEmpty(cmd2)){

                                    LogUtils.d(TAG, "cmd:" + cmd2);

                                    if(cmd2.startsWith(CMD_DATA_UPDATE_FACE)){
                                        updateFace(CMD_ID, cmd2);
                                    }
                                    else if(cmd2.startsWith(CMD_DATA_UPDATE_USER)){
                                        updateUser(CMD_ID, cmd2);
                                    }
                                    else if(cmd2.startsWith(CMD_DATA_DELETE_FACE)){
                                        deleteFace(CMD_ID, cmd2);
                                    }
                                    else if(cmd2.startsWith(CMD_DATA_DELETE_CARD)){
                                        deleteCard(CMD_ID, cmd2);
                                    }
                                    else if(cmd2.startsWith(CMD_DATA_DELETE_USER)){
                                        deleteUser(CMD_ID, cmd2);
                                    }
                                    else if(cmd2.startsWith(CMD_CLEAR_DATA)){
                                        clearData(CMD_ID);
                                    }
                                    else if(cmd2.startsWith(CMD_SET_OPTIONS)){
                                        setOptions(CMD_ID, cmd2);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }else {
            LogUtils.e(TAG, "getCmd失败");
        }
    }


    /**
     * 更新或者添加人员表中人脸和姓名
     *
     */
    private void updateFace(String cmdId, String cmd) throws InterruptedException, IOException {
        String[] arr_cmd_cell = cmd.split(" ");

        String PIN = null; //对应人员表fid字段，全局唯一
        String NAME = null;
        String SIZE = null;
        String TMP = null;

        //name字段特殊，判断name中是否有空格
        //DATA UPDATE FACE PIN=25847 NAME=王 川 SIZE=31100 TMP=/9j/4AAQSkZJRg
        int i1 = cmd.indexOf("NAME=");
        int i2 = cmd.indexOf("SIZE=");
        String nameStr = cmd.substring(i1, i2).trim();
        if(nameStr.contains(" ")){
            nameStr.replace(" ", "&");
        }
        NAME = nameStr.split("=")[1];

        //其他3个字段
        for (int j = 0; j < arr_cmd_cell.length; j++) {
            String cell = arr_cmd_cell[j];
            if(cell.startsWith("PIN=")){
                PIN = cell.split("=")[1];
            }
            else if(cell.startsWith("SIZE=")){
                SIZE = cell.split("=")[1];
            }
            else if(cell.startsWith("TMP=")){
                TMP = cell.split("=")[1];
            }
        }

        LogUtils.d(TAG, "NAME="+NAME);
        LogUtils.d(TAG, "PIN="+PIN);
        LogUtils.d(TAG, "SIZE="+SIZE);
        LogUtils.d(TAG, "TMP= ... ...");

        PersonBeanDao dao = DBUtil.getDaoSession().getPersonBeanDao();
        PersonDaoForHttp daoForHttp = new PersonDaoForHttp();
        PersonBean bean = daoForHttp.queryByFid(PIN);
        if(bean == null){
            LogUtils.d(TAG, "updateFace 本地不存在此人-"+NAME);

            //查询是否超过最大人数
            List<PersonBean> list = PersonDao.getDao().queryBuilder().list();
            if (list != null && list.size() > Const.PERSON_MAX_COUNT) {
                LogUtils.e(TAG, "人员已满 无法继续添加");
                updateCmdResult(CMD_DATA_UPDATE_FACE, Long.parseLong(cmdId), -1, "人员已满 无法继续添加");
                return;
            }

            bean = new PersonBean();

            SystemClock.sleep(10);
            long t = System.currentTimeMillis();
            bean.setPerson_id(t);
            bean.setAuth_id(t);
            bean.setFid(PIN);


            if (!TextUtils.isEmpty(NAME)) {
                bean.setName(NAME);
            }


            //设置默认值---好工e平台无法推送
            bean.setCount(mPassCount);
            bean.setStart_ts(mStartTs);
            bean.setEnd_ts(mEndTs);

            if (!TextUtils.isEmpty(TMP)) {
                byte[] b = Base64.decode(TMP, Base64.DEFAULT);
                ThreadManager.getImageSaveThread().add(
                        new ImageSaveBean(
                                bean.getAuth_id(),
                                b,
                                bean,
                                true));
                String imagePath = "图片保存失败";

                imagePath = ThreadManager.getImageSaveThread().get();
                if (imagePath.contains(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS)) {
                    imagePath = imagePath.replace(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS, "");
                    bean.setFacePic(imagePath);
                } else {
                    updateCmdResult(
                            CMD_DATA_UPDATE_FACE,
                            Long.parseLong(cmdId),
                            -1,
                            CodeUtil.getStringByCode(104));

                    return;
                }

                //保存图片================================================================end
                LogUtils.d(TAG, "图片保存路径="+imagePath);
            }
            dao.insert(bean);
            daoForHttp.addCache(bean);
            LogUtils.i(TAG, "updateFace 人员保存成功-"+NAME);
        }
        else{
            LogUtils.d(TAG, "updateFace 本地已经存在此人-"+NAME);

            if (!TextUtils.isEmpty(NAME)) {
                bean.setName(NAME);
            }

            if (!TextUtils.isEmpty(TMP)) {
                //保存图片================================================================start
                byte[] b = Base64.decode(TMP, Base64.DEFAULT);
                ThreadManager.getImageSaveThread().add(
                        new ImageSaveBean(
                                bean.getAuth_id(),
                                b,
                                bean,
                                true));

                String imagePath = "图片保存失败";
                imagePath = ThreadManager.getImageSaveThread().get();
                if (imagePath.contains(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS)) {
                    String oldFacePic = bean.getFacePic();
                    bean.setFacePic(imagePath.replaceAll(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS, ""));
                    bean.setOldFacePic(oldFacePic);

                } else {
                    updateCmdResult(
                            CMD_DATA_UPDATE_FACE,
                            Long.parseLong(cmdId),
                            -1,
                            CodeUtil.getStringByCode(104));

                    return;
                }

                //保存图片================================================================end
                LogUtils.d(TAG, "图片保存路径="+imagePath);
            }

            dao.update(bean);
            daoForHttp.upateCache(bean);
            LogUtils.i(TAG, "updateFace 人员修改成功-"+NAME);

        }
        //回复好工e
        updateCmdResult(
                CMD_DATA_UPDATE_FACE,
                Long.parseLong(cmdId),
                0,
                "ok");
    }

    /**
     * 更新或者添加人员表中工号和姓名
     */
    private void updateUser(String cmdId, String cmd) throws InterruptedException, IOException {
        String[] arr_cmd_cell = cmd.split(" ");

        String PIN = null;
        String NAME = null;
        String CARD = null;

        //name字段特殊，判断name中是否有空格
        //DATA UPDATE USER PIN=1936 NAME=yang CARD=88888888
        int i1 = cmd.indexOf("NAME=");
        int i2 = cmd.indexOf("CARD=");
        String nameStr = cmd.substring(i1, i2).trim();
        if(nameStr.contains(" ")){
            nameStr.replace(" ", "&");
        }
        NAME = nameStr.split("=")[1];

        //其他2个字段
        for (int j = 0; j < arr_cmd_cell.length; j++) {
            String cell = arr_cmd_cell[j];
            if(cell.startsWith("PIN=")){
                PIN = cell.split("=")[1];
            }
            else if(cell.startsWith("CARD=")){
                CARD = cell.split("=")[1];
            }
        }

        PersonBeanDao dao = DBUtil.getDaoSession().getPersonBeanDao();
        PersonDaoForHttp daoForHttp = new PersonDaoForHttp();
        PersonBean bean = daoForHttp.queryByFid(PIN);
        if(bean == null){
            LogUtils.d(TAG, "updateUser 本地不存在此人-"+NAME);

            //查询是否超过最大人数
            List<PersonBean> list = PersonDao.getDao().queryBuilder().list();
            if (list != null && list.size() > Const.PERSON_MAX_COUNT) {
                LogUtils.e(TAG, "人员已满 无法继续添加");
                updateCmdResult(CMD_DATA_UPDATE_USER, Long.parseLong(cmdId), -1, "人员已满 无法继续添加");
                return;
            }

            bean = new PersonBean();
            SystemClock.sleep(10);
            long t = System.currentTimeMillis();
            bean.setPerson_id(t);
            bean.setAuth_id(t);
            bean.setFid(PIN);


            if (!TextUtils.isEmpty(NAME)) {
                bean.setName(NAME);
            }


            //设置默认值---好工e平台无法推送
            bean.setCount(mPassCount);
            bean.setStart_ts(mStartTs);
            bean.setEnd_ts(mEndTs);

            //工号
            bean.setEmployee_card_id(CARD);

            dao.insert(bean);
            daoForHttp.addCache(bean);
            LogUtils.i(TAG, "updateUser 人员保存成功-"+NAME);
        }
        else{
            LogUtils.d(TAG, "updateUser 本地已经存在此人-"+NAME);

            if (!TextUtils.isEmpty(NAME)) {
                bean.setName(NAME);
            }

            if(!TextUtils.isEmpty(CARD)){
                bean.setEmployee_card_id(CARD);
            }

            dao.update(bean);
            daoForHttp.upateCache(bean);
            LogUtils.i(TAG, "updateUser 人员修改成功-"+NAME);

        }
        //回复好工e
        updateCmdResult(
                CMD_DATA_UPDATE_USER,
                Long.parseLong(cmdId),
                0,
                "ok");
    }

    private void deleteFace(String cmdId, String cmd) throws IOException {
        String[] arr_cmd_cell = cmd.split(" ");
        String PIN = arr_cmd_cell[3].split("=")[1];

        PersonBeanDao dao = DBUtil.getDaoSession().getPersonBeanDao();
        PersonDaoForHttp daoForHttp = new PersonDaoForHttp();
        PersonBean bean = daoForHttp.queryByFid(PIN);
        if(bean == null){
            LogUtils.e(TAG, "deleteFace 本地不存在此人");
            updateCmdResult(CMD_DATA_DELETE_FACE, Long.parseLong(cmdId), -1, "此人不存在");
            return;
        }else{
            //删除2个特征值
            NowPicFeatureDao.delete(bean.getAuth_id());
            FaceFeatureDao.delete(bean.getAuth_id());

            //删除2个文件
            if (!TextUtils.isEmpty(bean.getFacePic())) {
                FileUtil.deleteFile(bean.getFacePic());
            }
            if (!TextUtils.isEmpty(bean.getOldFacePic())) {
                FileUtil.deleteFile(bean.getOldFacePic());
            }

            //人脸db
            bean.setFacePic("");
            bean.setOldFacePic("");
            dao.update(bean);

            //更新缓存
            daoForHttp.upateCache(bean);

            LogUtils.i(TAG, "deleteFace 人脸删除成功-"+bean.getName());

        }

        updateCmdResult(CMD_DATA_DELETE_FACE, Long.parseLong(cmdId), 0, "OK");
    }

    private void deleteCard(String cmdId, String cmd) throws IOException {
        String[] arr_cmd_cell = cmd.split(" ");
        String PIN = arr_cmd_cell[3].split("=")[1];

        PersonBeanDao dao = DBUtil.getDaoSession().getPersonBeanDao();
        PersonDaoForHttp daoForHttp = new PersonDaoForHttp();
        PersonBean bean = daoForHttp.queryByFid(PIN);
        if(bean == null){
            LogUtils.e(TAG, "deleteCard 本地不存在此人");
            updateCmdResult(CMD_DATA_DELETE_CARD, Long.parseLong(cmdId), -1, "此人不存在");
            return;
        }else{
            //修改bean 员工卡号清空
            bean.setEmployee_card_id("");

            //修改db
            dao.update(bean);

            //更新缓存
            daoForHttp.upateCache(bean);

            LogUtils.i(TAG, "deleteCard 卡删除成功-"+bean.getName());

        }
        updateCmdResult(CMD_DATA_DELETE_CARD, Long.parseLong(cmdId), 0, "OK");
    }

    private void deleteUser(String cmdId, String cmd) throws IOException {
        String[] arr_cmd_cell = cmd.split(" ");
        String PIN = arr_cmd_cell[3].split("=")[1];

        PersonBeanDao dao = DBUtil.getDaoSession().getPersonBeanDao();
        PersonDaoForHttp daoForHttp = new PersonDaoForHttp();
        PersonBean bean = daoForHttp.queryByFid(PIN);
        if(bean == null){
            LogUtils.e(TAG, "deleteUser 本地不存在此人");
            updateCmdResult(CMD_DATA_DELETE_USER, Long.parseLong(cmdId), -1, "此人不存在");
            return;
        }else{
            PersonDao.deleteAuthority(bean.getAuth_id());
            LogUtils.i(TAG, "deleteUser 人员删除成功-"+bean.getName());
        }
        updateCmdResult(CMD_DATA_DELETE_USER, Long.parseLong(cmdId), 0, "OK");
    }

    private void clearData(String cmdId) throws IOException {
        //删除所有人
        PersonBeanDao dao = DBUtil.getDaoSession().getPersonBeanDao();
        List<PersonBean> perosnList = dao.loadAll();
        if(perosnList != null){
            for (int i = 0; i < perosnList.size(); i++) {
                PersonBean bean = perosnList.get(i);
                PersonDao.deleteAuthority(bean.getAuth_id());
                LogUtils.d(TAG, "clearData 人员删除成功-"+bean.getName());
            }

            LogUtils.d(TAG, "所有人员删除成功");
        }

        //删除所有考勤
        AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();
        accessRecordBeanDao.deleteAll();
        LogUtils.d(TAG, "所有考勤删除成功");
        LogUtils.i(TAG, "clearData 命令执行成功");
        updateCmdResult(CMD_CLEAR_DATA, Long.parseLong(cmdId), 0, "OK");
    }

    private void setOptions(String cmdId, String cmd) throws IOException {
        String[] arr_cmd_cell = cmd.split(" ");
        String options = arr_cmd_cell[2].split("=")[1];

        String[] arr_options = options.split(",");
        if(arr_options != null){
            for (int j = 0; j < arr_options.length; j++) {
                String option = arr_options[j];
                String[] arr_option_item = option.split("=");
                String key = arr_option_item[0];
                String val = arr_option_item[1];
                if("uploadAttPic".equals(key)){
                    this.uploadAttPic = Integer.parseInt(val);
                    SPUtils.put(mContext, Const.haogonge_uploadAttPic, uploadAttPic);
                }
                if("transInterval".equals(key)){
                    this.transInterval = Double.parseDouble(val);
                    SPUtils.put(mContext, Const.haogonge_transInterval, (float)transInterval);
                }
                if("verifyType".equals(key)){
                    this.verifyType = val;
                    SPUtils.put(mContext, Const.haogonge_verifyType, verifyType);
                }
                if("offlineAction".equals(key)){
                    this.offlineAction = val;
                    SPUtils.put(mContext, Const.haogonge_offlineAction, offlineAction);
                }

            }
            LogUtils.i(TAG, "setOptions 命令执行成功");
        }
        updateCmdResult(CMD_SET_OPTIONS, Long.parseLong(cmdId), 0, "OK");
    }

    //首页ui调整
    private void sendBroadCast(String weatherType, String weatherCode, int status) {
        Intent intent = new Intent();
        intent.setAction(Const.WEATHER);
        intent.putExtra(Const.WEATHER_TYPE, weatherType);
        intent.putExtra(Const.WEATHER_CODE, weatherCode);
        intent.putExtra(Const.DEVICE_ON_LINE, status);
        LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);

    }
}
