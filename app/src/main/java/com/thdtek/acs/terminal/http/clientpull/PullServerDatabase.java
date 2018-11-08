package com.thdtek.acs.terminal.http.clientpull;

/**
 * Time:2018/7/4
 * User:lizhen
 * Description:
 */
@Deprecated
public class PullServerDatabase {
    private final String TAG = PullServerDatabase.class.getSimpleName();

//    public void checkServer() {
//        long lastActionId = (long) SPUtils.get(MyApplication.getContext(), Const.DATABASE_LAST_ACTION_ID, 0L);
//        System.out.println("lastActionId get = " + lastActionId);
//        Msg.Message.RsyncActionReq rsyncActionReq = Msg.Message.RsyncActionReq.newBuilder()
//                .setLastActionId(lastActionId)
//                .build();
//        Msg.Message msg = Msg.Message.newBuilder()
//                .setRsyncActionReq(rsyncActionReq)
//                .build();
//
//        new SendMsgHelper().request(msg, 40000, new RequestCallback() {
//            @Override
//            public void onSuccess(Msg.Message message) {
//                LogUtils.e(TAG, "和服务器数据同步请求成功 msg = " + message.toString());
//                handleSuccess(message);
//            }
//
//            @Override
//            public void onFailure(Msg.Message message) {
//                LogUtils.e(TAG, "和服务器数据同步请求失败");
//            }
//
//            @Override
//            public void onTimeout() {
//                LogUtils.e(TAG, "和服务器数据同步请求超时");
//            }
//        });
//    }
//
//    private final int INSERT = 0;
//    private final int UPDATE_PERSON = 1;
//    private final int UPDATE_AUTHORITY = 2;
//    private final int DELETE = 3;
//
//    private void handleSuccess(Msg.Message message) {
//        Msg.Message.RsyncActionRsp rsyncActionRsp = message.getRsyncActionRsp();
//        boolean success = false;
//        switch (rsyncActionRsp.getActionType()) {
//            case INSERT:
//                success = insert(rsyncActionRsp);
//                break;
//            case UPDATE_PERSON:
//            case UPDATE_AUTHORITY:
//                success = updatePerson(rsyncActionRsp);
////                updateAuthority(rsyncActionRsp);
//                break;
//            case DELETE:
//                success = delete(rsyncActionRsp);
//                break;
//            default:
//                break;
//        }
//        if (rsyncActionRsp.hasActionId()) {
//            System.out.println("lastActionId put = " + rsyncActionRsp.getActionId());
//            SPUtils.put(MyApplication.getContext(), Const.DATABASE_LAST_ACTION_ID, rsyncActionRsp.getActionId());
//            SPUtils.put(MyApplication.getContext(), Const.DATABASE_LAST_ACTION_TIME, rsyncActionRsp.getActionTs());
//        } else {
//            System.out.println("lastActionId 没有");
//        }
//    }
//
//    public boolean insert(Msg.Message.RsyncActionRsp rsyncActionRsp) {
//
//        Msg.Message.PersonInfo personInfo = rsyncActionRsp.getPerson();
//        PersonBean personBean = new PersonBean();
//        //设置personId
//        personBean.setPerson_id(personInfo.getPersonId());
//
//        //设置名字
//        personBean.setName(personInfo.getName());
//        //设置工号
//        personBean.setEmployee_card_id(personInfo.getEmployeeCardId());
//        //设置身份证号
//        personBean.setID_no(personInfo.getIDNo());
//        String imagePath = saveFaceFeatureAndPic(personInfo.getPersonId(), personInfo.getFacePic().toByteArray(), true);
//        if (!TextUtils.isEmpty(imagePath)) {
//            personBean.setFacePic(imagePath);
//        }
//        //保存到数据库
//        PersonDao.insert(personBean);
//
//        return true;
//
//    }
//
//    public boolean updatePerson(Msg.Message.RsyncActionRsp rsyncActionRsp) {
//        Msg.Message.PersonInfo personInfo = rsyncActionRsp.getPerson();
//        PersonBean personBean = PersonDao.query2PersonId(personInfo.getPersonId());
//        if (personBean == null) {
//            personBean = new PersonBean();
//            PersonDao.insert(personBean);
//        }
//        if (personInfo.hasPersonId()) {
//            personBean.setPerson_id(personInfo.getPersonId());
//        }
//        if (personInfo.hasCount()) {
//            personBean.setCount(personInfo.getCount());
//        }
//        if (personInfo.hasEmployeeCardId()) {
//            personBean.setEmployee_card_id(personInfo.getEmployeeCardId());
//        }
//        if (personInfo.hasEndTs()) {
//            personBean.setEnd_ts(personInfo.getEndTs());
//        }
//        if (personInfo.hasStartTs()) {
//            personBean.setStart_ts(personInfo.getStartTs());
//        }
//        if (personInfo.hasFacePic()) {
//            String imagePath = saveFaceFeatureAndPic(personInfo.getPersonId(), personInfo.getFacePic().toByteArray(), false);
//            if (!TextUtils.isEmpty(imagePath)) {
//                personBean.setFacePic(imagePath);
//            }
//        }
//        if (personInfo.hasIDNo()) {
//            personBean.setID_no(personInfo.getIDNo());
//        }
//        if (personInfo.hasName()) {
//            personBean.setName(personInfo.getName());
//        }
//        PersonDao.update(personBean);
//        return true;
//    }
//
//    public boolean updateAuthority(Msg.Message.RsyncActionRsp rsyncActionRsp) {
//        return true;
//    }
//
//    public boolean delete(Msg.Message.RsyncActionRsp rsyncActionRsp) {
//        Msg.Message.PersonInfo personInfo = rsyncActionRsp.getPerson();
//        PersonBean personBean = PersonDao.query2PersonId(personInfo.getPersonId());
//        if (personBean == null) {
//            LogUtils.d(TAG, "没有这个person ID,无法删除");
//            return true;
//        }
//        PersonDao.delete(personBean);
//        try {
//            FileUtil.deleteFile(personBean.getFacePic());
//        } catch (IOException e) {
//            LogUtils.e(TAG, "员工图片删除失败 filePath = " + personBean.getFacePic() + " error mag = " + e.getMessage());
//            return false;
//        }
//        return true;
//    }
//
//    public String saveFaceFeatureAndPic(long personId, byte[] mImageByte, boolean insert) {
//        if (mImageByte == null) {
//            return null;
//        }
//        Bitmap bitmap = BitmapFactory.decodeByteArray(mImageByte, 0, mImageByte.length);
//        if (bitmap == null) {
//            LogUtils.e(TAG, "生成的bitmap == null");
//            return null;
//        }
//        if (bitmap.getWidth() != Const.CAMERA_BITMAP_WIDTH || bitmap.getHeight() != Const.CAMERA_BITMAP_HEIGHT) {
//            LogUtils.e(TAG, "图片宽高不正确 width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
//            return null;
//        }
//        final byte[] yuv420sp = FaceUtil.getInstance().bmp2YUV(bitmap);
//        //获取图片人脸
//        Rect rect = FaceUtil.getInstance().getFaceApiOther().MaxFaceFeatureDetect(yuv420sp, Const.CAMERA_BITMAP_WIDTH, Const.CAMERA_BITMAP_HEIGHT);
//        if (rect == null || rect.width() == 0 || rect.height() == 0) {
//            return null;
//        }
//        //获取特征值
//        FaceFeature faceFeature = FaceUtil.getInstance().getFaceApiOther().ExtractMaxFaceFeatur(yuv420sp, Const.CAMERA_BITMAP_WIDTH, Const.CAMERA_BITMAP_HEIGHT, rect);
//        if (faceFeature.getFeatureBytes().length <= 4) {
//            return null;
//        }
//        byte[] featureBytes = faceFeature.getFeatureBytes();
//        String picPath = null;
//        if (featureBytes != null) {
//            //保存图片
//            try {
//                long l = System.currentTimeMillis();
//                System.out.println("文件名 = " + l);
//                picPath = CameraUtil.save2Person(mImageByte, l + ".webp");
//                //图片保存成功后,设置特征值
//                if (insert) {
//                    NowPicFeatureDao.insert(personId, featureBytes);
//                } else {
//                    NowPicFeatureDao.update(personId, featureBytes);
//                }
//            } catch (IOException e) {
//                LogUtils.e(TAG, "保存person正式图片失败 = " + e.getMessage());
//            }
//        } else {
//            LogUtils.e(TAG, "Person update 获取特征值失败,不保存图片");
//        }
//        return picPath;
//    }
}
