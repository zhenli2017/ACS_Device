package com.thdtek.acs.terminal.server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.bean.ConfigBean;
import com.thdtek.acs.terminal.bean.ImageSaveBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.http.serverpush.PushContext;
import com.thdtek.acs.terminal.imp.person.persondownload.PersonDownLoadImp;
import com.thdtek.acs.terminal.thread.ThreadManager;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppSettingUtil2;
import com.thdtek.acs.terminal.util.BitmapUtil;
import com.thdtek.acs.terminal.util.CodeUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.util.camera.CameraUtil;
import com.thdtek.acs.terminal.util.tts.TtsUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;
import greendao.PersonBeanDao;

public class AppHttpServer extends NanoHTTPD {

    private static final String TAG = AppHttpServer.class.getSimpleName();
    private static final String META = "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">";
    private HeartbeatThreadForHttp mHeartbeatThread;

    public AppHttpServer() {
        super(8088);

    }

    public void start() {
        //开启server
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "\nRunning! Point your browsers to http://localhost:8080/ \n");

        //开启心跳线程
        mHeartbeatThread = new HeartbeatThreadForHttp();
        mHeartbeatThread.start();

    }

    public void stop() {
        mHeartbeatThread.interrupt();
    }


    @Override
    public Response serve(IHTTPSession session) {

        LogUtils.d(TAG, "session.getUri()=" + session.getUri());
        String uri = session.getUri();
        if (!TextUtils.isEmpty(uri) && uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }

        Method method = session.getMethod();


        if (Method.POST == method) {
            Map<String, String> body = new HashMap<>();
            try {
                session.parseBody(body);

                LogUtils.d(TAG, "session.getParms() post =" + session.getParms());

                if (!verifyKey(session)) {
                    return failure(103);
                }

                if ("/setTime".equalsIgnoreCase(uri)) {
                    return setTime(session);
                } else if ("/setDeviceKey".equalsIgnoreCase(uri)) {
                    return setDeviceKey(session);
                } else if ("/setDeviceInfo".equalsIgnoreCase(uri)) {
                    return setDeviceInfo(session);
                } else if ("/setPassConfig".equalsIgnoreCase(uri)) {
                    return setPassConfig(session);
                } else if ("/setPerson".equalsIgnoreCase(uri)) {
                    return setPerson(session);
                } else if ("/removePerson".equalsIgnoreCase(uri)) {
                    return removePerson(session);
                } else if ("/removeRecord".equalsIgnoreCase(uri)) {
                    return removeRecord(session);
                } else if ("/updateAPK".equalsIgnoreCase(uri)) {
                    return updateAPK(session);
                } else if ("/setVisitorConfig".equalsIgnoreCase(uri)) {
                    return setVisitorConfig(session);
                } else if ("/setHeartBeat".equalsIgnoreCase(uri)) {
                    return setHeartBeat(session);
                } else if ("/setRecordCallback".equalsIgnoreCase(uri)) {
                    return setRecordCallback(session);
                } else if ("/check".equalsIgnoreCase(uri)) {
                    return check(session);
                } else if ("/photo".equalsIgnoreCase(uri)) {
                    return photo(session);
                } else if ("/reboot".equalsIgnoreCase(uri)) {
                    return reboot();
                } else if ("/open".equalsIgnoreCase(uri)) {
                    return open();
                } else {
                    return newFixedLengthResponse(Response.Status.NOT_IMPLEMENTED, NanoHTTPD.MIME_HTML, htmlErrorMsg("Unimplemented interface:" + uri + "(" + method + ")"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
        }

        if (Method.GET == method) {
            LogUtils.d(TAG, "session.getHeaders() get =" + session.getHeaders());

            if (!verifyKey(session)) {
                return failure(103);
            }

            if ("/getTime".equalsIgnoreCase(uri)) {
                return getTime();
            } else if ("/getDeviceInfo".equalsIgnoreCase(uri)) {
                return getDeviceInfo();
            } else if ("/getPassConfig".equalsIgnoreCase(uri)) {
                return getPassConfig();
            } else if ("/getPerson".equalsIgnoreCase(uri)) {
                return getPerson(session);
            } else if ("/listPerson".equalsIgnoreCase(uri)) {
                return listPerson();
            } else if ("/listRecord".equalsIgnoreCase(uri)) {
                return listRecord(session);
            } else if ("/open".equalsIgnoreCase(uri)) {
                return open();
            } else if ("/reboot".equalsIgnoreCase(uri)) {
                return reboot();
            } else if ("/getVisitorConfig".equalsIgnoreCase(uri)) {
                return getVisitorConfig();
            } else if ("/getHeartBeat".equalsIgnoreCase(uri)) {
                return getHeartBeat();
            } else if ("/getRecordCallback".equalsIgnoreCase(uri)) {
                return getRecordCallback();
            } else {
                return newFixedLengthResponse(Response.Status.NOT_IMPLEMENTED, NanoHTTPD.MIME_HTML, htmlErrorMsg("Unimplemented interface:" + uri + "(" + method + ")"));
            }
        }

        return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_HTML, htmlErrorMsg("Sorry, METHOD_NOT_ALLOWED"));

    }


    //=================================================================================


    public static LinkedBlockingQueue<byte[]> mPhotoQueue = new LinkedBlockingQueue(3);
    public static boolean isTakingPictures = false;

    private Response photo(IHTTPSession session) {
        String tipsBefore = session.getParms().get("tipsAfter");
        String tipsAfter = session.getParms().get("tipsAfter");
        String count = session.getParms().get("count");

        if (TextUtils.isEmpty(tipsBefore)) {
            tipsBefore = MyApplication.getContext().getString(R.string.photo_tips_before);

        }
        if (TextUtils.isEmpty(tipsAfter)) {
            tipsAfter = MyApplication.getContext().getString(R.string.photo_tips_after);
        }

        int countInt = 1;
        try {
            countInt = Integer.parseInt(count);
        } catch (Exception e) {
            e.printStackTrace();
            return failure(102);
        }

        final String finalTipsBefore = tipsBefore;
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                TtsUtil.getInstance().stop();
                TtsUtil.getInstance().speak(finalTipsBefore);
            }
        });
        if (isTakingPictures) {
            return failure(123);
        } else {
            isTakingPictures = true;
            mPhotoQueue.clear();
            PersonDownLoadImp.getInstance().personDownLoadStart(MyApplication.getContext().getString(R.string.photo_tips_before), Const.HANDLER_DELAY_TIME_3000);
            SystemClock.sleep(3000);

            Map<String, Object> data = new HashMap<>();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < countInt; i++) {
                try {
                    byte[] bytes = mPhotoQueue.take();
                    list.add(Base64Utils.cameraDataToBase64(bytes));
                    SystemClock.sleep(1000);
                    mPhotoQueue.clear();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            data.put("photos", list);
            isTakingPictures = false;
            PersonDownLoadImp.getInstance().personDownLoadEnd(MyApplication.getContext().getString(R.string.photo_tips_after), Const.HANDLER_DELAY_TIME_3000);

            final String finalTipsAfter = tipsAfter;
            ThreadPool.getThread().execute(new Runnable() {
                @Override
                public void run() {
                    TtsUtil.getInstance().stop();
                    TtsUtil.getInstance().speak(finalTipsAfter);
                }
            });
            return success(data);
        }


    }

    private Response check(IHTTPSession session) {
        String photo = session.getParms().get("photo");
        byte[] imgByte = Base64.decode(photo, Base64.DEFAULT);

        if (imgByte == null) {
            LogUtils.d(TAG, "imgByte == null");
            return failure(127);
        }

        LogUtils.d(TAG, "imgByte == " + imgByte.length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        if (bitmap == null) {
            LogUtils.d(TAG, "bitmap == null");
            return failure(127);
        }

        try {
            CameraUtil.saveImage(new File(Const.DIR_IMAGE_TEMP),
                    "server_photo_" + System.currentTimeMillis() + Const.IMAGE_TYPE_DEFAULT_JPG,
                    bitmap,
                    Bitmap.CompressFormat.JPEG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
        //图片变成640*480
        Bitmap newBitmap = BitmapUtil.getFull640Bitmap(bitmap);


        //检测图片特征值是否可用、是否重复
        ThreadManager.getImageSaveThread().add(
                new ImageSaveBean(
                        0,
                        BitmapUtil.bitmap2Byte(newBitmap),
                        new PersonBean(),
                        true,
                        true)
        );
        String imagePath = "图片保存失败";
        try {
            imagePath = ThreadManager.getImageSaveThread().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean isValid = imagePath.contains(Const.HTTP_CHECK_PHOTO_IS_VALID);
        boolean isExist = imagePath.contains(Const.HTTP_CHECK_PHOTO_IS_EXIST);

        Map<String, Object> data = new HashMap<>();
        data.put("isValid", isValid);
        data.put("isExist", isExist);

        return success(data);

    }

    private Response getRecordCallback() {
        String url = (String) SPUtils.get(MyApplication.getContext(), Const.URL_FOR_HTTP_AUTO_UPLOAD_RECORD, "");

        Map<String, Object> data = new HashMap<>();
        data.put("url", url);

        return success(data);
    }

    private Response setRecordCallback(IHTTPSession session) {
        String url = session.getParms().get("url");

        if (TextUtils.isEmpty(url)) {
            return failure(101);
        }

        String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(url).matches()) {
            return failure(122);
        }

        SPUtils.put(MyApplication.getContext(), Const.URL_FOR_HTTP_AUTO_UPLOAD_RECORD, url, true);

        return success();
    }

    private Response getHeartBeat() {
        String url = (String) SPUtils.get(MyApplication.getContext(), Const.URL_FOR_HTTP_HEARTBEAT, "");
        int period = (int) SPUtils.get(MyApplication.getContext(), Const.PERIOD_FOR_HTTP_HEARTBEAT, -1);

        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("period", period);

        return success(data);
    }

    private Response setHeartBeat(IHTTPSession session) {
        String url = session.getParms().get("url");
        String period = session.getParms().get("period");

        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(period)) {
            return failure(101);
        }

        String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(url).matches()) {
            return failure(122);
        }

        int period2;
        try {
            period2 = Integer.parseInt(period);
        } catch (Exception e) {
            e.printStackTrace();
            return failure(102);
        }

        if (period2 < 1) {
            return failure(121);
        }

        SPUtils.put(MyApplication.getContext(), Const.URL_FOR_HTTP_HEARTBEAT, url, true);
        SPUtils.put(MyApplication.getContext(), Const.PERIOD_FOR_HTTP_HEARTBEAT, period2, true);

        return success();

    }

    private Response getVisitorConfig() {
        ConfigBean bean = AppSettingUtil.getConfig(true);
        String passValue = bean.getGuestOpenDoorNumber();
        int passType = bean.getGuestOpenDoorType();

        Map<String, Object> map = new HashMap<>();
        map.put("passValue", passValue);
        map.put("passType", passType);

        return success(map);
    }

    private Response setVisitorConfig(IHTTPSession session) {
        String passValue = session.getParms().get("passValue");
        String passType = session.getParms().get("passType");


        if (TextUtils.isEmpty(passValue) && TextUtils.isEmpty(passType)) {
            return failure(101);
        }


        Msg.Message.Config.Builder builder = Msg.Message.Config.newBuilder();
        if (!TextUtils.isEmpty(passValue)) {
            builder.setVisitorCardNo(passValue);
        }
        if (!TextUtils.isEmpty(passType)) {
            builder.setVisitorOpenDoorType(Integer.parseInt(passType));
        }

        Msg.Message.SetConfigReq setConfigReq = Msg.Message.SetConfigReq.newBuilder()
                .setConfig(builder.build())
                .build();

        Msg.Message message = Msg.Message.newBuilder()
                .setSetConfigReq(setConfigReq)
                .build();

        Msg.Message rsp = new PushContext().onResponse(message, -1);
        if (rsp != null) {
            int status = rsp.getSetConfigRsp().getStatus();
            return resultFromOtherPlaces(status);
        }

        return failure(10000);
    }

    private Response reboot() {
        LogUtils.d(TAG, "准备重启");
        Msg.Message.DeviceCtrlReq.CtrlType ctrl
                = Msg.Message.DeviceCtrlReq.CtrlType.forNumber(0);//0--重启

        Msg.Message.DeviceCtrlReq deviceCtrlReq = Msg.Message.DeviceCtrlReq.newBuilder()
                .setCtrl(ctrl)
                .build();

        Msg.Message message1 = Msg.Message.newBuilder()
                .setDeviceCtrlReq(deviceCtrlReq)
                .build();

        LogUtils.d(TAG, "json----->message=" + message1);
        Msg.Message rsp = new PushContext().onResponse(message1, -1);
        if (rsp != null) {
            int status = rsp.getDeviceCtrlRsp().getStatus();
            return resultFromOtherPlaces(status);
        }
        return failure(10000);
    }

    private Response open() {

        Msg.Message.DeviceCtrlReq.CtrlType ctrl
                = Msg.Message.DeviceCtrlReq.CtrlType.forNumber(1);//1--开门

        Msg.Message.DeviceCtrlReq deviceCtrlReq = Msg.Message.DeviceCtrlReq.newBuilder()
                .setCtrl(ctrl)
                .build();

        Msg.Message message1 = Msg.Message.newBuilder()
                .setDeviceCtrlReq(deviceCtrlReq)
                .build();

        LogUtils.d(TAG, "json----->message=" + message1);
        Msg.Message rsp = new PushContext().onResponse(message1, -1);
        if (rsp != null) {
            int status = rsp.getDeviceCtrlRsp().getStatus();
            return resultFromOtherPlaces(status);
        }
        return failure(10000);
    }

    private Response updateAPK(IHTTPSession session) {
        String url = session.getParms().get("url");
        if (TextUtils.isEmpty(url)) {
            return failure(101);
        }

        String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(url).matches()) {
            return failure(122);
        }

        Msg.Message.UpdateAPKReq.UpdateFlag updateFlag
                = Msg.Message.UpdateAPKReq.UpdateFlag.forNumber(0);

        Msg.Message.UpdateAPKReq updateAPKReq = Msg.Message.UpdateAPKReq.newBuilder()
                .setApkUrl(url)
                .setFlag(updateFlag)
                .build();

        Msg.Message message1 = Msg.Message.newBuilder()
                .setUpdateApkReq(updateAPKReq)
                .build();

        LogUtils.d(TAG, "json----->message=" + message1);
        Msg.Message rsp = new PushContext().onResponse(message1, -1);
        if (rsp != null) {
            int status = rsp.getUpdateApkRsp().getStatus();
            return resultFromOtherPlaces(status);
        }

        return failure(10000);
    }

    private Response removeRecord(IHTTPSession session) {
        String ts = session.getParms().get("ts");
        RecordDaoForHttp dao = new RecordDaoForHttp();
        if (TextUtils.isEmpty(ts)) {
            return failure(101);
        }
        long ts2 = -1L;
        try {
            ts2 = Long.parseLong(ts);
        } catch (Exception e) {
            e.printStackTrace();
            return failure(102);
        }

        if (ts2 > System.currentTimeMillis() || ts2 < 0) {
            return failure(102);
        }

        dao.deleteByTs(ts2);
        return success();
    }

    private Response listRecord(IHTTPSession session) {
        Map<String, String> parms = session.getParms();
        String count = parms.get("count");
        String ts = parms.get("ts");

        if (TextUtils.isEmpty(count)) {
            return failure(101);
        }

        if (TextUtils.isEmpty(ts)) {
            return failure(101);
        }

        int count2;
        try {
            count2 = Integer.parseInt(count);
        } catch (Exception e) {
            e.printStackTrace();
            return failure(102);
        }

        long ts2;
        try {
            ts2 = Long.parseLong(ts);
        } catch (Exception e) {
            e.printStackTrace();
            return failure(102);
        }

        RecordDaoForHttp dao = new RecordDaoForHttp();
        System.out.println("TS = " + ts2);
        System.out.println("COUNT = " + count2);
        List<AccessRecordBean> lst = dao.queryByTsAndCount(ts2, count2);
//        LogUtils.d(TAG, "查询流水结果lst=" + lst);

        if (lst == null) {
            lst = new ArrayList<>();
        }

        List<Map<String, Object>> lstData = new ArrayList<>();
        for (int i = 0; i < lst.size(); i++) {
            String fid = lst.get(i).getFid();
            long time = lst.get(i).getTime();
            int type = lst.get(i).getType();
            String name = lst.get(i).getPersonName();
            Map<String, Object> item = new HashMap<>();
            item.put("personID", fid);
            item.put("name", name);
            item.put("ts", time);
            item.put("passType", type);
//            item.put("photo", photo);

            lstData.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("record", lstData);

        return success(data);

    }

    private Response removePerson(IHTTPSession session) {
        //[1,2,3]
        String ids = session.getParms().get("id");
        if (TextUtils.isEmpty(ids)) {
            return failure(101);
        }

        if (!ids.startsWith("[") || !ids.endsWith("]")) {
            return failure(102);
        }

        LogUtils.d(TAG, "需要删除的人员fid=" + ids);

        String[] idsArr = null;
        if (ids.equals("[]")) {
            idsArr = new String[0];
        } else {
            ids = ids.substring(1, ids.length() - 1);
            idsArr = ids.split(",");
        }

        PersonDaoForHttp dao = new PersonDaoForHttp();
        dao.deleteByFid(idsArr);

        return success();
    }

    private Response listPerson() {
        PersonDaoForHttp dao = new PersonDaoForHttp();
        List<PersonBean> lst = dao.queryAll();
        if (lst == null) {
            lst = new ArrayList<>();
        }

        List<Map<String, Object>> lst2 = new ArrayList<>();
        for (int i = 0; i < lst.size(); i++) {
            PersonBean bean = lst.get(i);

            Map<String, Object> item = new HashMap<>();
            item.put("id", bean.getFid());
            item.put("name", bean.getName());
            item.put("IC_NO", bean.getEmployee_card_id());
            item.put("ID_NO", bean.getID_no());
            item.put("passCount", bean.getCount());
            item.put("startTs", bean.getStart_ts());
            item.put("endTs", bean.getEnd_ts());
//            item.put("photo", Base64Utils.ImageToBase64ByLocal(bean.getFacePic()));

            lst2.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("person", lst2);

        return success(data);
    }

    private Response getPerson(IHTTPSession session) {
        String fid = session.getParms().get("id");
        PersonDaoForHttp dao = new PersonDaoForHttp();
        PersonBean bean = dao.queryByFid(fid);
        if (bean == null) {
            return failure(126);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", bean.getFid());
        data.put("name", bean.getName());
        data.put("IC_NO", bean.getEmployee_card_id());
        data.put("ID_NO", bean.getID_no());
        data.put("passCount", bean.getCount());
        data.put("startTs", bean.getStart_ts());
        data.put("endTs", bean.getEnd_ts());
        data.put("photo", Base64Utils.ImageToBase64ByLocal(bean.getFacePic()));
        return success(data);
    }


    /**
     * 保存人员信息
     * <p>
     * 注意事项
     * 1.http模式添加fid字段 作为id使用
     * 2.http模式中person_id、auth_id以时间戳代替保存
     *
     * @param session
     * @return
     */
    private Response setPerson(IHTTPSession session) {


        Map<String, String> parms = session.getParms();
        String id = parms.get("id");
        String name = parms.get("name");
        String IC_NO = parms.get("IC_NO");
        String ID_NO = parms.get("ID_NO");
        String photo = parms.get("photo");
        String passCount = parms.get("passCount");
        String startTs = parms.get("startTs");
        String endTs = parms.get("endTs");
        String personalizedPermissions = session.getParms().get("personalizedPermissions");

        if (TextUtils.isEmpty(id)) {
            return failure(101);
        }
        long passCountLong = 10000;
        double startTsLong = 111111111.0d;
        double endTsLong = 99999999999.0d;

        if (!TextUtils.isEmpty(passCount)) {
            try {
                passCountLong = Long.parseLong(passCount);
            } catch (Exception e) {
                LogUtils.e(TAG, "107 " + e.getMessage());
                return failure(107);
            }
        }

        if (!TextUtils.isEmpty(startTs)) {

            try {
                startTsLong = Double.parseDouble(startTs);
            } catch (Exception e) {
                LogUtils.e(TAG, "109 " + e.getMessage());
                return failure(109);
            }
        }
        if (!TextUtils.isEmpty(endTs)) {

            try {
                endTsLong = Double.parseDouble(endTs);
            } catch (Exception e) {
                LogUtils.e(TAG, "111 " + e.getMessage());
                return failure(111);
            }
        }

        if (passCountLong < 0) {
            LogUtils.e(TAG, "108 error ");
            return failure(108);
        }

        if (startTsLong < 0) {
            LogUtils.e(TAG, "110 error ");
            return failure(110);
        }

        if (endTsLong < 0) {
            LogUtils.e(TAG, "112 error ");
            return failure(112);
        }
        PersonDaoForHttp daoHttp = new PersonDaoForHttp();
        PersonBean bean = daoHttp.queryByFid(id);

        PersonBeanDao dao = DBUtil.getDaoSession().getPersonBeanDao();
        if (bean == null) {
            LogUtils.d(TAG, "setPerson 没有这个人");
            bean = new PersonBean();
            long t = System.currentTimeMillis() / 1000;
            bean.setPerson_id(t);
            bean.setAuth_id(t);
            bean.setFid(id);

            if (!TextUtils.isEmpty(name)) {
                bean.setName(name);
            }

            if (!TextUtils.isEmpty(IC_NO)) {
                bean.setEmployee_card_id(IC_NO);
            }

            if (!TextUtils.isEmpty(ID_NO)) {
                bean.setID_no(ID_NO);
            }
            if (!TextUtils.isEmpty(personalizedPermissions)) {
                bean.setPersonalizedPermissions(personalizedPermissions);
            }

            bean.setCount(passCountLong);

            bean.setStart_ts(startTsLong );
            bean.setEnd_ts(endTsLong) ;

            if (!TextUtils.isEmpty(photo)) {
                byte[] b = Base64.decode(photo, Base64.DEFAULT);
                ThreadManager.getImageSaveThread().add(
                        new ImageSaveBean(
                                bean.getAuth_id(),
                                b,
                                bean,
                                true));

                String imagePath = "图片保存失败";
                try {
                    imagePath = ThreadManager.getImageSaveThread().get();
                    if (imagePath.contains(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS)) {
                        imagePath = imagePath.replace(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS, "");
                        bean.setFacePic(imagePath);
                    } else {
                        return failure(104);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //保存图片================================================================end
            }
            dao.insert(bean);
            daoHttp.addCache(bean);
        } else {//本地存在此人员信息、则其id、fid、auth_id、person_id不用更新
            LogUtils.d(TAG, "setPerson 有这个人");
            if (!TextUtils.isEmpty(name)) {
                bean.setName(name);
            }
            if (!TextUtils.isEmpty(IC_NO)) {
                bean.setEmployee_card_id(IC_NO);
            }
            if (!TextUtils.isEmpty(ID_NO)) {
                bean.setID_no(ID_NO);
            }
            if (!TextUtils.isEmpty(passCount)) {
                bean.setCount(passCountLong);
            }
            if (!TextUtils.isEmpty(startTs)) {
                bean.setStart_ts(startTsLong);
            }
            if (!TextUtils.isEmpty(endTs)) {
                bean.setEnd_ts(endTsLong);
            }
            if (!TextUtils.isEmpty(personalizedPermissions)) {
                bean.setPersonalizedPermissions(personalizedPermissions);
            }
            if (!TextUtils.isEmpty(photo)) {
                //保存图片================================================================start
                byte[] b = Base64.decode(photo, Base64.DEFAULT);
                ThreadManager.getImageSaveThread().add(
                        new ImageSaveBean(
                                bean.getAuth_id(),
                                b,
                                bean,
                                true));

                String imagePath = "图片保存失败";
                try {
                    imagePath = ThreadManager.getImageSaveThread().get();
                    if (imagePath.contains(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS)) {
                        String oldFacePic = bean.getFacePic();
                        bean.setFacePic(imagePath.replaceAll(Const.PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS, ""));
                        bean.setOldFacePic(oldFacePic);

                    } else {
                        return failure(104);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            dao.update(bean);
            daoHttp.upateCache(bean);
        }

        return success();
    }

    private Response getPassConfig() {
        ConfigBean bean = AppSettingUtil.getConfig(true);

        float IDFaceRate = bean.getIdFeaturePairNumber();
        float faceRate = bean.getFaceFeaturePairNumber();
//        int passField = bean.getDoorType();
        int passType = bean.getOpenDoorType();

        Map<String, Object> data = new HashMap<>();
        data.put("IDFaceRate", IDFaceRate);
        data.put("faceRate", faceRate);
//        data.put("passField", passField);
        data.put("passType", passType);

        return success(data);
    }

    private Response setPassConfig(IHTTPSession session) {
        String IDFaceRate = session.getParms().get("IDFaceRate");
        String faceRate = session.getParms().get("faceRate");
//        String passField = session.getParms().get("passField");
        String passType = session.getParms().get("passType");

        if (TextUtils.isEmpty(IDFaceRate) &&
                TextUtils.isEmpty(faceRate) &&
//                TextUtils.isEmpty(passField) &&
                TextUtils.isEmpty(passType)) {
            return failure(101);
        }

        Msg.Message.Config.Builder builder = Msg.Message.Config.newBuilder();

        if (!TextUtils.isEmpty(IDFaceRate)) {
            float IDFaceRate_long;
            try {
                IDFaceRate_long = Float.parseFloat(IDFaceRate);
            } catch (Exception e) {
                return failure(113);
            }
            if (IDFaceRate_long < 0 || IDFaceRate_long > 1) {
                return failure(114);
            }
            builder.setIdCardFaceFeaturePairNumber(IDFaceRate_long);
        }


        if (!TextUtils.isEmpty(faceRate)) {
            float faceRate_long;
            try {
                faceRate_long = Float.parseFloat(faceRate);
            } catch (Exception e) {
                return failure(115);
            }
            if (faceRate_long < 0 || faceRate_long > 1) {
                return failure(116);
            }
            builder.setFaceFeaturePairNumber(faceRate_long);
        }


//        if(!TextUtils.isEmpty(passField)){
//            int passField_long;
//            try {
//                passField_long = Integer.parseInt(passField);
//            }catch (Exception e){
//                return failure(117);
//            }
//            builder.setDoorType(passField_long);
//        }


        if (!TextUtils.isEmpty(passType)) {
            int passType_long;
            try {
                passType_long = Integer.parseInt(passType);
            } catch (Exception e) {
                return failure(119);
            }
            builder.setOpenDoorType(passType_long);
        }

        Msg.Message.SetConfigReq setConfigReq = Msg.Message.SetConfigReq.newBuilder()
                .setConfig(builder.build())
                .build();

        Msg.Message message = Msg.Message.newBuilder()
                .setSetConfigReq(setConfigReq)
                .build();

        Msg.Message rsp = new PushContext().onResponse(message, -1);
        if (rsp != null) {
            int status = rsp.getSetConfigRsp().getStatus();
            return resultFromOtherPlaces(status);
        }

        return failure(10000);
    }

    private Response setDeviceInfo(IHTTPSession session) {
        String cameraDetectType = session.getParms().get("cameraDetectType");
        String faceFeaturePairNumber = session.getParms().get("faceFeaturePairNumber");
        String faceFeaturePairSuccessOrFailWaitTime = session.getParms().get("faceFeaturePairSuccessOrFailWaitTime");
        String openDoorType = session.getParms().get("openDoorType");
        String openDoorContinueTime = session.getParms().get("openDoorContinueTime");
        String doorType = session.getParms().get("doorType");
        String deviceName = session.getParms().get("deviceName");
        String deviceDefendTime = session.getParms().get("deviceDefendTime");
        String deviceMusicSize = session.getParms().get("deviceSoundSize");
        String appWelcomeMsg = session.getParms().get("appWelcomeMsg");
        String idCardFaceFeaturePairNumber = session.getParms().get("idCardFaceFeaturePairNumber");


        if (TextUtils.isEmpty(cameraDetectType) &&
                TextUtils.isEmpty(faceFeaturePairNumber) &&
                TextUtils.isEmpty(faceFeaturePairSuccessOrFailWaitTime) &&
                TextUtils.isEmpty(openDoorType) &&
                TextUtils.isEmpty(openDoorContinueTime) &&
                TextUtils.isEmpty(doorType) &&
                TextUtils.isEmpty(deviceName) &&
                TextUtils.isEmpty(deviceDefendTime) &&
                TextUtils.isEmpty(deviceMusicSize) &&
                TextUtils.isEmpty(appWelcomeMsg) &&
                TextUtils.isEmpty(idCardFaceFeaturePairNumber)
                ) {
            return error(101);
        }
        int cameraDetectTypeInt = -1;
        float faceFeaturePairNumberFloat = -1.0f;
        int faceFeaturePairSuccessOrFailWaitTimeInt = -1;
        int openDoorTypeInt = -1;
        int openDoorContinueTimeInt = -1;
        int doorTypeInt = -1;
        int deviceMusicSizeInt = -1;
        float idCardFaceFeaturePairNumberFloat = -1.0f;
        if (!TextUtils.isEmpty(idCardFaceFeaturePairNumber)) {
            try {
                idCardFaceFeaturePairNumberFloat = Float.parseFloat(idCardFaceFeaturePairNumber);
            } catch (Exception e) {
                LogUtils.e(TAG, "setDeviceInfo = " + e.getMessage());
                return failure(107);
            }
        }
        if (!TextUtils.isEmpty(deviceMusicSize)) {
            try {
                deviceMusicSizeInt = Integer.parseInt(deviceMusicSize);
            } catch (Exception e) {
                LogUtils.e(TAG, "setDeviceInfo = " + e.getMessage());
                return failure(107);
            }
        }
        if (!TextUtils.isEmpty(doorType)) {
            try {
                doorTypeInt = Integer.parseInt(doorType);
            } catch (Exception e) {
                LogUtils.e(TAG, "setDeviceInfo = " + e.getMessage());
                return failure(107);
            }
        }
        if (!TextUtils.isEmpty(openDoorContinueTime)) {
            try {
                openDoorContinueTimeInt = Integer.parseInt(openDoorContinueTime);
            } catch (Exception e) {
                LogUtils.e(TAG, "setDeviceInfo = " + e.getMessage());
                return failure(107);
            }
        }
        if (!TextUtils.isEmpty(openDoorType)) {
            try {
                openDoorTypeInt = Integer.parseInt(openDoorType);
            } catch (Exception e) {
                LogUtils.e(TAG, "setDeviceInfo = " + e.getMessage());
                return failure(107);
            }
        }
        if (!TextUtils.isEmpty(faceFeaturePairSuccessOrFailWaitTime)) {
            try {
                faceFeaturePairSuccessOrFailWaitTimeInt = Integer.parseInt(faceFeaturePairSuccessOrFailWaitTime);
            } catch (Exception e) {
                LogUtils.e(TAG, "setDeviceInfo = " + e.getMessage());
                return failure(107);
            }
        }
        if (!TextUtils.isEmpty(cameraDetectType)) {
            try {
                cameraDetectTypeInt = Integer.parseInt(cameraDetectType);
            } catch (Exception e) {
                LogUtils.e(TAG, "setDeviceInfo = " + e.getMessage());
                return failure(107);
            }
        }
        if (!TextUtils.isEmpty(faceFeaturePairNumber)) {
            try {
                faceFeaturePairNumberFloat = Float.parseFloat(faceFeaturePairNumber);
            } catch (Exception e) {
                LogUtils.e(TAG, "setDeviceInfo = " + e.getMessage());
                return failure(107);
            }
        }

        Msg.Message.Config.Builder builder = Msg.Message.Config.newBuilder();
        if (!TextUtils.isEmpty(deviceName)) {
            builder.setDeviceName(deviceName);
        }
        if (!TextUtils.isEmpty(appWelcomeMsg)) {
            builder.setAppWelcomeMsg(appWelcomeMsg);
        }
        if (!TextUtils.isEmpty(deviceDefendTime)) {
            builder.setDeviceDefendTime(deviceDefendTime);
        }
        if (cameraDetectTypeInt != 0) {
            builder.setCameraDetectType(cameraDetectTypeInt);
        }
        if (faceFeaturePairNumberFloat != -1.f) {
            builder.setFaceFeaturePairNumber(faceFeaturePairNumberFloat);
        }
        if (faceFeaturePairSuccessOrFailWaitTimeInt != -1) {
            builder.setFaceFeaturePairSuccessOrFailWaitTime(faceFeaturePairSuccessOrFailWaitTimeInt);
        }
        if (openDoorContinueTimeInt != -1) {
            builder.setOpenDoorContinueTime(openDoorContinueTimeInt);
        }
        if (doorTypeInt != -1) {
            builder.setDoorType(doorTypeInt);
        }
        if (openDoorTypeInt != -1) {
            builder.setOpenDoorType(openDoorTypeInt);
        }
        if (deviceMusicSizeInt != -1) {
            builder.setDeviceMusicSize(deviceMusicSizeInt);
        }
        if (idCardFaceFeaturePairNumberFloat != -1.0f) {
            builder.setIdCardFaceFeaturePairNumber(idCardFaceFeaturePairNumberFloat);
        }

        Msg.Message.Config config = builder.build();

        Msg.Message.SetConfigReq setConfigReq = Msg.Message.SetConfigReq.newBuilder()
                .setConfig(config)
                .build();

        Msg.Message message = Msg.Message.newBuilder()
                .setSetConfigReq(setConfigReq)
                .build();

        Msg.Message rsp = new PushContext().onResponse(message, -1);
        if (rsp != null) {
            int status = rsp.getSetConfigRsp().getStatus();
            return resultFromOtherPlaces(status);
        }
        return error(1);
    }

    private Response getDeviceInfo() {
        String sn = AppSettingUtil.getConfig().getDeviceSn();
        String name = AppSettingUtil.getConfig().getDeviceName();
        String version = "1";
        String APKVersion = AppSettingUtil2.getDeviceAppVersion();

        Map<String, Object> data = new HashMap<>();
        data.put("sn", sn);
        data.put("name", name);
        data.put("version", version);
        data.put("APKVersion", APKVersion);
        data.put("cameraDetectType", AppSettingUtil.getConfig().getCameraDetectType());
        data.put("deviceSoundSize", AppSettingUtil.getConfig().getDeviceMusicSize());
        data.put("appWelcomeMsg", AppSettingUtil.getConfig().getAppWelcomeMsg());
        data.put("faceFeaturePairNumber", AppSettingUtil.getConfig().getFaceFeaturePairNumber());
        data.put("openDoorContinueTime", AppSettingUtil.getConfig().getOpenDoorContinueTime());
        data.put("faceFeaturePairSuccessOrFailWaitTime", AppSettingUtil.getConfig().getFaceFeaturePairSuccessOrFailWaitTime());
        data.put("doorType", AppSettingUtil.getConfig().getDoorType());
        data.put("openDoorType", AppSettingUtil.getConfig().getOpenDoorType());
        data.put("deviceDefendTime", AppSettingUtil.getConfig().getDeviceDefendTime());
        data.put("idCardFaceFeaturePairNumber", AppSettingUtil.getConfig().getIdFeaturePairNumber());

        return success(data);
    }

    private boolean verifyKey(IHTTPSession session) {
        String key = session.getParms().get("key");
        if (TextUtils.isEmpty(key)) {
            return false;
        }

        String aesKeyLocal = AppSettingUtil.getDeviceAesKey();

        LogUtils.d(TAG, "请求秘钥:" + key);
        LogUtils.d(TAG, "终端秘钥:" + aesKeyLocal);
        if (TextUtils.isEmpty(aesKeyLocal)) {
            LogUtils.d(TAG, "终端秘钥:空,使用默认秘钥:abc");
            aesKeyLocal = "abc";
        }

        if (!key.equals(aesKeyLocal)) {
            return false;
        }

        return true;

    }

    private Response setDeviceKey(IHTTPSession session) {

        String newKey = session.getParms().get("newKey");
        String oldKey = session.getParms().get("oldKey");


        if (TextUtils.isEmpty(newKey) || TextUtils.isEmpty(oldKey)) {
            return failure(101);
        }

        String aesKeyLocal = AppSettingUtil.getDeviceAesKey();
        LogUtils.d(TAG, "local device_key=" + aesKeyLocal);
        if (oldKey.equals(aesKeyLocal) || TextUtils.isEmpty(aesKeyLocal)) {
            AppSettingUtil.setDeviceAesKey(newKey);
            return success();
        } else {
            return failure(103);
        }

    }

    private Response getTime() {
        Map<String, Object> data = new HashMap<>();
        data.put("ts", System.currentTimeMillis());

        RspHelper rspHelper = new RspHelper();
        rspHelper.setStatus(0);
        rspHelper.setMsg("ok");
        rspHelper.setData(data);

        return newFixedLengthResponse(rspHelper.toJsonString());
    }

    private Response setTime(IHTTPSession session) {
        Map<String, String> parms = session.getParms();
        String ts = parms.get("ts");
        System.out.println("settime ts = " + ts);
        if (TextUtils.isEmpty(ts)) {
            return error(101);//缺少参数
        }
        long time = -1L;
        try {
            time = Long.parseLong(ts);
        } catch (Exception e) {
            return error(102);//参数错误
        }
        boolean success = false;
        if (time != -1L) {
            success = HWUtil.setClientSystemTime(time / 1000);
        }
        if (success) {
            return resultFromOtherPlaces(0);
        } else {
            return failure(10000);
        }
    }


    //=================================================================================

    private Response resultFromOtherPlaces(int status) {
        return newFixedLengthResponse(
                Response.Status.OK,
                META,
                new RspHelper(status, CodeUtil.getStringByCode(status)).toJsonString());
    }

    private Response failure(int status) {
        return newFixedLengthResponse(
                Response.Status.OK,
                META,
                new RspHelper(status, CodeUtil.getStringByCode(status)).toJsonString());

    }

    private Response success() {
        return newFixedLengthResponse(
                Response.Status.OK,
                META,
                new RspHelper(0, CodeUtil.getStringByCode(0)).toJsonString());
    }

    private Response success(Object data) {
        return newFixedLengthResponse(
                Response.Status.OK,
                META,
                new RspHelper(
                        0,
                        CodeUtil.getStringByCode(0),
                        data)
                        .toJsonString());
    }

    private Response success(String jsonData) {

        Map<String, Object> map = new HashMap<>();
        map.put("status", 0);
        map.put("msg", CodeUtil.getStringByCode(0));
        map.put("data", jsonData);

        return newFixedLengthResponse(
                Response.Status.OK,
                META,
                new Gson().toJson(map)
        );
    }

    private String htmlErrorMsg(String msg) {
        String temp = "<html><head>" + META + "</head><body><h1>Android Server</h1>\n";
        temp += "<p>" + msg + "!</p>";
        temp += "</body></html>\n";
        return temp;
    }

    private Response error(int status) {
        return newFixedLengthResponse(
                Response.Status.OK,
                META,
                new RspHelper(
                        status,
                        CodeUtil.getStringByCode(status)
                ).toJsonString());

    }
}
