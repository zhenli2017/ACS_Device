package com.thdtek.acs.terminal.yzface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.bean.ConfigBean;
import com.thdtek.acs.terminal.bean.HttpResponseBean;
import com.thdtek.acs.terminal.bean.HttpResponseListRecord;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.AccessRecordDao;
import com.thdtek.acs.terminal.dao.FaceFeatureDao;
import com.thdtek.acs.terminal.dao.NowPicFeatureDao;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.AuthorityUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.Request;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.yzface.Entity.DeviceUtil;
import com.thdtek.acs.terminal.yzface.Entity.Node;
import com.thdtek.acs.terminal.yzface.Message.BreakPacket;
import com.thdtek.acs.terminal.yzface.Message.ByteUtil;
import com.thdtek.acs.terminal.yzface.Message.DataAddPersonInfo;
import com.thdtek.acs.terminal.yzface.Message.DataCardParameter;
import com.thdtek.acs.terminal.yzface.Message.DataDate;
import com.thdtek.acs.terminal.yzface.Message.DataFileChunk;
import com.thdtek.acs.terminal.yzface.Message.DataFileHandle;
import com.thdtek.acs.terminal.yzface.Message.DataGetPersonPic;
import com.thdtek.acs.terminal.yzface.Message.DataIPInfo;
import com.thdtek.acs.terminal.yzface.Message.DataIdList;
import com.thdtek.acs.terminal.yzface.Message.DataPersonDBInfo;
import com.thdtek.acs.terminal.yzface.Message.DataPersonInfo;
import com.thdtek.acs.terminal.yzface.Message.DataPersonInfoList;
import com.thdtek.acs.terminal.yzface.Message.DataPreparewriteFile;
import com.thdtek.acs.terminal.yzface.Message.DataQueryPersonPic;
import com.thdtek.acs.terminal.yzface.Message.DataRecordInfo;
import com.thdtek.acs.terminal.yzface.Message.DataRecordInfoCount;
import com.thdtek.acs.terminal.yzface.Message.DataRecordInfoList;
import com.thdtek.acs.terminal.yzface.Message.DataSN;
import com.thdtek.acs.terminal.yzface.Message.DataVersion;
import com.thdtek.acs.terminal.yzface.Message.Message;
import com.thdtek.acs.terminal.yzface.Message.MessageType;
import com.thdtek.acs.terminal.yzface.Message.Packet;
import com.thdtek.acs.terminal.yzface.Test.ByteUtilTest;
import com.thdtek.acs.terminal.yzface.Test.DataIPInfoTest;
import com.thdtek.acs.terminal.yzface.Test.MessageTest;
import com.thdtek.acs.terminal.yzface.Test.PacketTest;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Handler;
import java.util.zip.CRC32;

import greendao.AccessRecordBeanDao;
import greendao.PersonBeanDao;

/**
 * Time:2018/10/24
 * User:lizhen
 * Description:
 */

public class YZFaceUtil3 {

    AsyncServer mAsyncServer = null;
    AsyncDatagramSocket asyncDatagramSocket = null;
    AsyncSocket clientSocket = null;

    int localUDPListenPort;
    int localTCPListenPort;

    Node faceNode;

    DataFileHandle fileHandle = new DataFileHandle();

    CRC32 crc32 = new CRC32();
    List<DataFileChunk> dataFileChunkList = new ArrayList<DataFileChunk>();

    byte[] monitorStatus = new byte[1];
    byte[] gateHoldTime = new byte[2];
    byte[] faceThreshold = new byte[1];

    DataCardParameter dataCardParameter = new DataCardParameter();
    private MyHandler mHandler;

    public void init() {
        mHandler = new MyHandler(this);
        mHandler.sendEmptyMessageDelayed(1, Const.HANDLER_DELAY_TIME_1000);
        byte[] uv = new byte[19200];

        int min = 0;
        int max = 255;
        Random random = new Random();
        int num = random.nextInt(max) % (max - min + 1) + min;

        for (int i = 0; i < 19200; i++) {
            uv[i] = (byte) (random.nextInt(max) % (max - min + 1) + min);
        }

        int uv80 = 0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < 19200; i++) {
            if (Math.abs(uv[i] - 0x80) < 12) {
                uv80++;
            }
        }
        Log.d("UV", "UV80 count :" + uv80 / 19200.0f);

        Log.d("UV", "UV AL cost :" + (System.currentTimeMillis() - time) + "ms");

        //初始化 dataCardParameter
        dataCardParameter.setWGEnable(ByteUtil.hexStringToBytes("01"));
        dataCardParameter.setWGType(ByteUtil.hexStringToBytes("01"));
        dataCardParameter.setWGbitOrder(ByteUtil.hexStringToBytes("01"));
        dataCardParameter.setDataType(ByteUtil.hexStringToBytes("01"));

        //初始化人脸识别阈值
        faceThreshold[0] = 92;

        ByteUtilTest.HexStringToBytes();

        DataIPInfoTest.ConstructionByBuffer();
        DataIPInfoTest.ToByteBuffer();

        MessageTest.ConstructionByBuffer();
        MessageTest.ToByteBuffer();

        PacketTest.ConstructionByBuffer();
        PacketTest.ToByteBuffer();

        PacketTest.ConstructionByBuffer2();
        PacketTest.ToByteBuffer2();

        PacketTest.CloneTest();
        PacketTest.TransformTest();
        PacketTest.TransformTest2();
        PacketTest.TransformTest3();


        faceNode = new Node();
        String sn = AppSettingUtil.getConfig().getDeviceSn();
        boolean save = (boolean) SPUtils.get(MyApplication.getContext(), SP_SAVE_SN, false);
        if (save) {
            faceNode.setSN(ByteUtil.hexStringToBytes(sn));
        } else {
            faceNode.setSN(sn.getBytes());
        }
        Log.d("Node", "IpInfo" + faceNode.getIpInfo().toHexString());

        mAsyncServer = AsyncServer.getDefault();

        localUDPListenPort = ByteUtil.byte2int0(faceNode.getIpInfo().getLocalUDPListenPort());
        localTCPListenPort = ByteUtil.byte2int0(faceNode.getIpInfo().getLocalTCPListenPort());

        try {
            asyncDatagramSocket = mAsyncServer.openDatagram(new InetSocketAddress(localUDPListenPort), true);
            asyncDatagramSocket.setDataCallback(new DataCallback() {
                @Override
                public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {

                    ProcessUDP(bb);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        mAsyncServer.listen(DeviceUtil.getLocalInetAddress(), localTCPListenPort, new ListenCallback() {

            @Override
            public void onAccepted(AsyncSocket socket) {
                clientSocket = socket;
                Log.d("TCP", "onAccepted" + socket.toString());


                socket.setDataCallback(new DataCallback() {
                    @Override
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                        ProcessTCP(bb);
                    }
                });
            }

            @Override
            public void onListening(AsyncServerSocket socket) {

            }

            @Override
            public void onCompleted(Exception ex) {

            }
        });
    }

    void ProcessTCP(ByteBufferList bb) {
        try {

            byte[] buffer = bb.getAllByteArray();
            Log.d("TCP", "接收消息包: " + ByteUtil.bytesToHexString(buffer));

            Packet packet = null;
            BreakPacket breakPacket = null;
            Message message = null;

            //检测数据是全包
            if (Packet.isPacket(buffer)) {
                packet = new Packet(buffer, false);
                message = packet.getMsg();
            } else {

                return;
            }


            int code = message.getCode();
            //Log.d("MSG","receive code "+code);
            if (code == MessageType.HOST_REQ_GET_NODE_VERSION.getCode()) {
//region HOST_REQ_GET_NODE_VERSION
                Log.d("MSG", "receive 0 =" + MessageType.HOST_REQ_GET_NODE_VERSION.getDesc());

                packet.setDeviceSN(faceNode.getSN());

                message.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_VERSION.getCategory())));
                message.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_VERSION.getCommand())));
                message.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_VERSION.getParameter())));
                //========== 读取版本号 ==========
                DataVersion version = new DataVersion();
                version.setMajor(new byte[]{0x30, 0x31});
                version.setRevision(new byte[]{0x00, 0x00});
                //========== 读取版本号 ==========
                message.setDataLength((ByteUtil.intToByteArray(version.getLength())));
                message.setData(version.toBytes());

                packet.setMsg(message);
                packet.setVerify(new byte[1]);


                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packet.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "Send " + MessageType.NODE_RES_GET_NODE_VERSION.getDesc());
                Log.d("MSG", "消息包数据:: " + packet.toSendHexString());
//endregion
            } else if (code == MessageType.HOST_REQ_PREPARE_WRITE_FILE.getCode()) {
//region HOST_REQ_PREPARE_WRITE_FILE
                DataPreparewriteFile dataPreparewriteFile = new DataPreparewriteFile(packet.getMsg().getData());

                Log.d("MSG", "接收到消息类型: 1 = " + MessageType.HOST_REQ_PREPARE_WRITE_FILE.getDesc());
                Log.d("MSG", "消息内容: " + dataPreparewriteFile.toHexString());

                int id = (int) ByteUtil.unsigned4BytesToInt(dataPreparewriteFile.getPersonID(), 0);

                fileHandle.setHandle(ByteUtil.getBytesInt(id + 1, true));
                //========== 获取图片下发的人员id ==========
                setMessageId(dataPreparewriteFile);
                //========== 获取图片下发的人员id ==========
                //
                packet.setDeviceSN(faceNode.getSN());

                message.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_PREPARE_WRITE_FILE.getCategory())));
                message.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_PREPARE_WRITE_FILE.getCommand())));
                message.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_PREPARE_WRITE_FILE.getParameter())));

                message.setDataLength((ByteUtil.intToByteArray(fileHandle.getLength())));
                message.setData(fileHandle.toBytes());

                packet.setMsg(message);
                packet.setVerify(new byte[1]);


                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packet.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_PREPARE_WRITE_FILE.getDesc());
                Log.d("MSG", "消息包数据: " + packet.toSendHexString());
//endregion
            } else if (code == MessageType.HOST_REQ_WRITE_FILE.getCode()) {
//region HOST_REQ_WRITE_FILE
                Log.d("MSG", "接收到消息类型: 2 = " + MessageType.HOST_REQ_WRITE_FILE.getDesc());

                DataFileChunk filechunk = new DataFileChunk(packet.getMsg().getData());
                Log.d("MSG", "消息内容: " + filechunk.toHexString());


                dataFileChunkList.add(filechunk);
                // crc32.update( filechunk.getChunk(),0, filechunk.getChunk().length);
                // Log.d("MSG","crc32: "+crc32.getValue());
                // Log.d("MSG","crc32x: "+ByteUtil.getCRC32P1(filechunk.getChunk()));
                Log.d("MSG", "crc32x: " + ByteUtil.getCRC32P2(filechunk.getChunk()));
                ////

                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_WRITE_FILE_SAVE_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_WRITE_FILE_SAVE_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_WRITE_FILE_SAVE_OK.getParameter())));


                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);


                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_WRITE_FILE_SAVE_OK.getDesc());
                Log.d("MSG", "消息包数据:: " + packetx.toSendHexString());
//endregion
            } else if (code == MessageType.HOST_REQ_WRITE_FILE_OK_CRC.getCode()) {
//region HOST_REQ_WRITE_FILE_OK_CRC
                Log.d("MSG", "接收到消息类型 3 : " + MessageType.HOST_REQ_WRITE_FILE_OK_CRC.getDesc());
                Log.d("MSG", "消息内容: " + ByteUtil.bytesToHexString(packet.getMsg().getData()));

                int size = 0;
                for (int i = 0; i < dataFileChunkList.size(); i++) {
                    size += dataFileChunkList.get(i).getChunk().length;
                }
                byte[] filebytes = new byte[size];

                int index = 0;
                for (int i = 0; i < dataFileChunkList.size(); i++) {
                    System.arraycopy(dataFileChunkList.get(i).getChunk(), 0, filebytes, index, dataFileChunkList.get(i).getChunk().length);
                    index += dataFileChunkList.get(i).getChunk().length;
                }


                int crc32x = ByteUtil.getCRC32P2(filebytes);
                if (crc32x < 0) crc32x = ~crc32x;
                int crc32xx = (int) ByteUtil.unsigned4BytesToInt(packet.getMsg().getData(), 0);
                if (crc32xx < 0) crc32xx = ~crc32xx;
                Log.d("MSG", "crc32x: " + crc32x + "crc32xx" + crc32xx);
                Log.d("MSG", "FILE SIZE: " + filebytes.length);
                //========== 保存图片 ==========
                crc32xx = setPersonImage(filebytes, crc32xx);
                //========== 保存图片 ==========
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_WRITE_FILE_OK_CRC.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_WRITE_FILE_OK_CRC.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_WRITE_FILE_OK_CRC.getParameter())));

                messagex.setDataLength((ByteUtil.intToByteArray(0x00000001)));

                if (crc32x == crc32xx) {
                    messagex.setData(ByteUtil.intToByteArray1(0x01));
                    Log.d("MSG", MessageType.NODE_RES_WRITE_FILE_OK_CRC.getDesc() + "校验OK");
                } else {
                    messagex.setData(ByteUtil.intToByteArray1(0x00));
                    Log.d("MSG", MessageType.NODE_RES_WRITE_FILE_OK_CRC.getDesc() + "校验NG");
                }
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);


                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_WRITE_FILE_OK_CRC.getDesc());
                Log.d("MSG", "消息包数据:: " + packetx.toSendHexString());

                dataFileChunkList.clear();
//endregion
            } else if (code == MessageType.HOST_REQ_ADD_PERSON_INFO.getCode()) {
//region HOST_REQ_ADD_PERSON_INFO
                Log.d("MSG", "接收到消息类型: 4 = " + MessageType.HOST_REQ_ADD_PERSON_INFO.getDesc());

                DataAddPersonInfo dataAddPersonInfo = new DataAddPersonInfo(packet.getMsg().getData());

                Log.d("MSG", "消息内容: " + dataAddPersonInfo.toHexString());

                //========== 添加人员 ==========
                Log.d("MSG", "添加人员个数: " + ByteUtil.unsigned4BytesToInt(dataAddPersonInfo.getNumberPersionInfo(), 0));
                for (int i = 0; i < dataAddPersonInfo.getPersonInfos().size(); i++) {
                    DataPersonInfo dataPersonInfo = dataAddPersonInfo.getPersonInfos().get(i);
                    sendPersonInfo(dataPersonInfo);
                }
                //========== 添加人员 ==========
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));


                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);


                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_READ_RECORD_INFO.getCode()) {
//region HOST_REQ_READ_RECORD_INFO

                Log.d("MSG", "接收到消息类型: 5 = " + MessageType.HOST_REQ_READ_RECORD_INFO.getDesc());


                //========== 流水记录 ==========
                AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();
                //记录总个数
                int recordCapacity = (int) accessRecordBeanDao.queryBuilder().count();
                //新纪录个数
                long lastId = (long) SPUtils.get(MyApplication.getContext(), SP_LAST_RECORD_ID, 0L);
                long newRecordInfos = accessRecordBeanDao.queryBuilder().where(AccessRecordBeanDao.Properties.Id.gt(lastId)).count();
                DataRecordInfoCount ataRecordInfoCount = new DataRecordInfoCount();
                ataRecordInfoCount.setRecordCapacity(ByteUtil.intToByteArray(recordCapacity));
                ataRecordInfoCount.setNewRecordInfos(ByteUtil.intToByteArray((int) newRecordInfos));
                //========== 流水记录 ==========
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_RECORD_INFO.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_RECORD_INFO.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_RECORD_INFO.getParameter())));

                messagex.setDataLength((ByteUtil.intToByteArray(ataRecordInfoCount.getLength())));
                messagex.setData(ataRecordInfoCount.toBytes());
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);


                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_RECORD_INFO.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());

//endregion
            }
            if (code == MessageType.HOST_REQ_DELALL_RECORD_INFO.getCode()) {
//region HOST_REQ_DELALL_RECORD_INFO
                Log.d("MSG", "接收到消息类型: 6 = " + MessageType.HOST_REQ_DELALL_RECORD_INFO.getDesc());

                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                //========== 删除所有记录 =========
                try {
                    AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();
                    accessRecordBeanDao.deleteAll();
                    FileUtil.deleteDir(Const.DIR_IMAGE_RECORD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //========== 删除所有记录 =========
                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);


                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_RESTLALL_RECORD_INFO.getCode()) {
//region HOST_REQ_DELALL_RECORD_INFO
                Log.d("MSG", "接收到消息类型: 7 = " + MessageType.HOST_REQ_RESTLALL_RECORD_INFO.getDesc());

                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                //========== 把所有记录设置为新纪录 ==========
                SPUtils.put(MyApplication.getContext(), SP_LAST_RECORD_ID, 0L);
                //========== 把所有记录设置为新纪录 ==========
                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_READ_NEW_RECORD_INFO.getCode()) {
//region HOST_REQ_READ_NEW_RECORD_INFO
                Log.d("MSG", "接收到消息类型: 8 = " + MessageType.HOST_REQ_READ_NEW_RECORD_INFO.getDesc());
                byte[] toReadNewRecordCount = new byte[2];
                toReadNewRecordCount = packet.getMsg().getData();

                Log.d("MSG", "消息内容: " + ByteUtil.bytesToHexString(toReadNewRecordCount));
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "要读取新纪录的数量: " + ByteUtil.getShort(toReadNewRecordCount, false));

                //========== 上传流水记录 ==========
                uploadRecord(ByteUtil.getShort(toReadNewRecordCount, false), packet);
                //========== 上传流水记录 ==========


                byte[] toSendCount = new byte[2];
                toSendCount = ByteUtil.hexStringToBytes("0002");

                Packet packetx1 = new Packet();
                packetx1.setDeviceSN(faceNode.getSN());
                packetx1.setPassword(packet.getPassword());
                packetx1.setToken(packet.getToken());

                Message messagex1 = new Message();
                messagex1.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getCategory())));
                messagex1.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getCommand())));
                messagex1.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getParameter())));

                messagex1.setDataLength(ByteUtil.hexStringToBytes("00000002"));
                messagex1.setData(toSendCount);
                packetx1.setMsg(messagex1);
                packetx1.setVerify(new byte[1]);


                ByteBufferList bbList1 = new ByteBufferList();
                ByteBuffer p1 = ByteBuffer.wrap(packetx1.toBytes(true));
                bbList1.add(p1);
                clientSocket.write(bbList1);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx1.toSendHexString());


//endregion
            }
            if (code == MessageType.HOST_REQ_QUERY_PERSON_PIC.getCode()) {
//region HOST_REQ_QUERY_PERSON_PIC
                Log.d("MSG", "接收到消息类型: 9 = " + MessageType.HOST_REQ_QUERY_PERSON_PIC.getDesc());
                byte[] persionId = new byte[4];
                persionId = packet.getMsg().getData();

                Log.d("MSG", "消息内容: " + ByteUtil.bytesToHexString(persionId));
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "要查询照片和指纹的人员ID: " + ByteUtil.unsigned4BytesToInt(persionId, 0));

                //生成响应消息

                PersonBean personBean = PersonDao.query2Fid(ByteUtil.bytesToHexString(persionId));
                DataQueryPersonPic dataQueryPersonPic = null;
                if (personBean == null) {

                } else {
                    dataQueryPersonPic = new DataQueryPersonPic();
                    dataQueryPersonPic.setPersonID(ByteUtil.hexStringToBytes("00000001"));
                    if (!TextUtils.isEmpty(personBean.getFacePic())) {
                        dataQueryPersonPic.setPicList(ByteUtil.hexStringToBytes("0100000000"));
                    }

                }
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_QUERY_PERSON_PIC.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_QUERY_PERSON_PIC.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_QUERY_PERSON_PIC.getParameter())));

                messagex.setDataLength((ByteUtil.intToByteArray(dataQueryPersonPic.getLength())));
                messagex.setData(dataQueryPersonPic.toBytes());
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_QUERY_PERSON_PIC.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());


//endregion
            }
            if (code == MessageType.HOST_REQ_GET_PERSON_PIC.getCode()) {
//region HOST_REQ_GET_PERSON_PIC
                Log.d("MSG", "接收到消息类型: 10 = " + MessageType.HOST_REQ_GET_PERSON_PIC.getDesc());

                DataGetPersonPic dataGetPersonPic = new DataGetPersonPic(packet.getMsg().getData());
                Log.d("MSG", "消息内容: " + dataGetPersonPic.toHexString());
                Log.d("MSG", "解析内容: ");

                int fileType = ByteUtil.getInt(dataGetPersonPic.getFileType(), true);
                int fileSN = ByteUtil.getInt(dataGetPersonPic.getFileSN(), true);
                int personIdOrRecordSN = ByteUtil.getInt(dataGetPersonPic.getPersonIdOrRecordSN(), false);

                if (fileType == 0x01) {
                    Log.d("MSG", "要获取人员头像文件");
                    Log.d("MSG", "要获取人员头像的序号: " + fileSN);
                    Log.d("MSG", "要获取人员头像文件的人员ID: " + personIdOrRecordSN);
                } else if (fileType == 0x02) {

                } else if (fileType == 0x03) {
                    Log.d("MSG", "要获取记录照片文件");
                    Log.d("MSG", "要获取记录照片文件的记录序号: " + personIdOrRecordSN);
                }

//endregion
            }
            if (code == MessageType.HOST_REQ_WRITE_NODE_SN.getCode()) {
//region HOST_REQ_WRITE_NODE_SN
                Log.d("MSG", "接收到消息类型: 11 = " + MessageType.HOST_REQ_WRITE_NODE_SN.getDesc());

                DataSN dataSN = new DataSN(packet.getMsg().getData());
                Log.d("MSG", "消息内容: " + dataSN.toHexString());
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "前缀: " + ByteUtil.bytesToHexString(dataSN.getPrefix()));
                Log.d("MSG", "SN: " + ByteUtil.bytesToHexString(dataSN.getSn()));
                Log.d("MSG", "后缀: " + ByteUtil.bytesToHexString(dataSN.getSuffix()));

                //========== 保存SN =========
                ConfigBean config = AppSettingUtil.getConfig();
                config.setDeviceSn(ByteUtil.bytesToHexString(dataSN.getSn()));
                AppSettingUtil.saveConfig(config);
                SPUtils.put(MyApplication.getContext(), SP_SAVE_SN, true);
                faceNode.setSN(dataSN.getSn());
                //========== 保存SN =========
                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());

//endregion
            }
            if (code == MessageType.HOST_REQ_READ_NODE_SN.getCode()) {
//region HOST_REQ_READ_NODE_SN
                Log.d("MSG", "接收到消息类型: 12 = " + MessageType.HOST_REQ_READ_NODE_SN.getDesc());

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_SN.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_SN.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_SN.getParameter())));

                //========= 读取SN =========
                messagex.setDataLength((ByteUtil.intToByteArray(faceNode.getSN().length)));
                messagex.setData(faceNode.getSN());
                //========= 读取SN =========
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_NODE_SN.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_READ_PERSON_DB_INFO.getCode()) {
//region HOST_REQ_READ_PERSON_DB_INFO
                Log.d("MSG", "接收到消息类型: 13 = " + MessageType.HOST_REQ_READ_PERSON_DB_INFO.getDesc());

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_PERSON_DB_INFO.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_PERSON_DB_INFO.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_PERSON_DB_INFO.getParameter())));

                //========== 读取所有人数 ==========
                DataPersonDBInfo dataPersonDBInfo = new DataPersonDBInfo();
                dataPersonDBInfo.setMaxCapacity(ByteUtil.intToByteArray(10000));
                dataPersonDBInfo.setCurrentCount(ByteUtil.intToByteArray((int) PersonDao.queryAllSize()));
                //========== 读取所有人数 ==========

                messagex.setDataLength((ByteUtil.intToByteArray(dataPersonDBInfo.getLength())));
                messagex.setData(dataPersonDBInfo.toBytes());
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_PERSON_DB_INFO.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_DELL_ALL_PERSON_INFO.getCode()) {
//region HOST_REQ_DELL_ALL_PERSON_INFO

                Log.d("MSG", "接收到消息类型: 14 = " + MessageType.HOST_REQ_DELL_ALL_PERSON_INFO.getDesc());

                //========== 实际删除人员信息 ==========
                PersonDao.getDao().deleteAll();
                PersonDao.getPeopleList(false).clear();
                FileUtil.deleteDir(Const.DIR_IMAGE_EMPLOYEE);
                NowPicFeatureDao.getDao().deleteAll();
                NowPicFeatureDao.getMap(false).clear();
                FaceFeatureDao.getDao().deleteAll();
                FaceFeatureDao.getMap(false).clear();
                //========== 实际删除人员信息 ==========

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_READ_ALL_PERSON_INFO.getCode()) {
//region HOST_REQ_READ_ALL_PERSON_INFO
                Log.d("MSG", "接收到消息类型: 15 = " + MessageType.HOST_REQ_READ_ALL_PERSON_INFO.getDesc());

                //获取一部分人员信息

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_ALL_PERSON_INFO.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_ALL_PERSON_INFO.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_ALL_PERSON_INFO.getParameter())));

                //========== 读取所有人员 ==========
                List<PersonBean> personBeanList = PersonDao.queryAll();
                DataPersonInfoList dataPersonInfoList = new DataPersonInfoList();
                for (int i = 0; i < personBeanList.size(); i++) {
                    DataPersonInfo dataPersonInfo = new DataPersonInfo();
                    PersonBean personBean = personBeanList.get(i);
                    if (personBean == null) {

                    } else {
                        dataPersonInfo.setId(ByteUtil.hexStringToBytes(personBean.getFid() + ""));

                        dataPersonInfo.setName(ByteUtil.hexStringToBytes(personBean.getName_yingze()));

                        dataPersonInfo.setNumber(ByteUtil.hexStringToBytes(personBean.getPersonNumber()));

                        dataPersonInfo.setDepartment(ByteUtil.hexStringToBytes(personBean.getDepartment()));

                        dataPersonInfo.setJob(ByteUtil.hexStringToBytes(personBean.getPosition()));

                        dataPersonInfo.setPicNumber(ByteUtil.hexStringToBytes(personBean.getPicNumber()));

                        dataPersonInfo.setCardNumber(ByteUtil.hexStringToBytes(personBean.getEmployee_card_id()));

                        dataPersonInfo.setTermOfValidity(ByteUtil.hexStringToBytes(personBean.getTermOfValidity()));

                        dataPersonInfo.setStatus(ByteUtil.hexStringToBytes(personBean.getCardStatus()));

                    }
                    dataPersonInfoList.Add(dataPersonInfo);
                    if (i % 10 == 0 || i == personBeanList.size() - 1) {

                        dataPersonInfoList.setPersonInfoCount(ByteUtil.getBytesInt(dataPersonInfoList.size(), true));
                        messagex.setDataLength((ByteUtil.intToByteArray(dataPersonInfoList.getLength())));
                        messagex.setData(dataPersonInfoList.toBytes());
                        packetx.setMsg(messagex);
                        packetx.setVerify(new byte[1]);

                        ByteBufferList bbList = new ByteBufferList();
                        ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                        bbList.add(p);

                        clientSocket.write(bbList);

                        Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_ALL_PERSON_INFO.getDesc());
                        Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
                        dataPersonInfoList = new DataPersonInfoList();
                    }
                }
                //========== 读取所有人员 ==========

                //----------------------------------------------------------------------------
                packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_ALL_PERSON_INFO_END.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_ALL_PERSON_INFO_END.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_ALL_PERSON_INFO_END.getParameter())));

                byte[] countx = new byte[4];
                countx = ByteUtil.intToByteArray(2);

                messagex.setDataLength((ByteUtil.intToByteArray(countx.length)));
                messagex.setData(countx);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList1 = new ByteBufferList();
                ByteBuffer p1 = ByteBuffer.wrap(packetx.toBytes(true));
                bbList1.add(p1);

                clientSocket.write(bbList1);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_ALL_PERSON_INFO_END.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());


//endregion
            }
            if (code == MessageType.HOST_REQ_READ_PERSON_INFO.getCode()) {
//region HOST_REQ_READ_PERSON_INFO
                Log.d("MSG", "接收到消息类型: 16 = " + MessageType.HOST_REQ_READ_PERSON_INFO.getDesc());

                byte[] id = new byte[4];
                id = packet.getMsg().getData();

                Log.d("MSG", "消息内容: " + ByteUtil.bytesToHexString(id));
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "ID: " + ByteUtil.bytesToHexString(id));

                //========== 获取ID=id的个人信息 ==========
                DataPersonInfo dataPersonInfo = new DataPersonInfo();
                PersonBean personBean = PersonDao.query2Fid(ByteUtil.bytesToHexString(id));
                if (personBean == null) {

                } else {
                    dataPersonInfo.setId(ByteUtil.hexStringToBytes(personBean.getFid() + ""));

                    dataPersonInfo.setName(ByteUtil.hexStringToBytes(personBean.getName_yingze()));

                    dataPersonInfo.setNumber(ByteUtil.hexStringToBytes(personBean.getPersonNumber()));

                    dataPersonInfo.setDepartment(ByteUtil.hexStringToBytes(personBean.getDepartment()));

                    dataPersonInfo.setJob(ByteUtil.hexStringToBytes(personBean.getPosition()));

                    dataPersonInfo.setPicNumber(ByteUtil.hexStringToBytes(personBean.getPicNumber()));

                    dataPersonInfo.setCardNumber(ByteUtil.hexStringToBytes(personBean.getEmployee_card_id()));

                    dataPersonInfo.setTermOfValidity(ByteUtil.hexStringToBytes(personBean.getTermOfValidity()));

                    dataPersonInfo.setStatus(ByteUtil.hexStringToBytes(personBean.getCardStatus()));

                }

                //========== 获取ID=id的个人信息 ==========
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_PERSON_INFO.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_PERSON_INFO.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_PERSON_INFO.getParameter())));


                messagex.setDataLength((ByteUtil.intToByteArray(dataPersonInfo.getLength())));
                messagex.setData(dataPersonInfo.toBytes());
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_PERSON_INFO.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());


//endregion
            }
            if (code == MessageType.HOST_REQ_DELL_PERSON_INFO_BY_IDS.getCode()) {
//region HOST_REQ_DELL_PERSON_INFO_BY_IDS
                Log.d("MSG", "接收到消息类型: 17 = " + MessageType.HOST_REQ_DELL_PERSON_INFO_BY_IDS.getDesc());

                DataIdList dataIdList = new DataIdList(packet.getMsg().getData());
                Log.d("MSG", "消息内容: " + dataIdList.toHexString());
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "IDCount: " + ByteUtil.bytesToHexString(dataIdList.getIdCount()));
                //========== 删除人 ==========
                for (int i = 0; i < dataIdList.getDataIdList().size(); i++) {
                    PersonBean personBean = PersonDao.query2Fid(ByteUtil.bytesToHexString(dataIdList.getDataIdList().get(i).getId()));
                    if (personBean == null) {

                    } else {
                        PersonDao.deleteAuthority(personBean.getAuth_id());
                    }
                }
                //========== 删除人 ==========

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_READ_NODE_TCP.getCode()) {
//region HOST_REQ_READ_NODE_TCP
                Log.d("MSG", "接收到消息类型: 18 = " + MessageType.HOST_REQ_READ_NODE_TCP.getDesc());


                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_TCP.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_TCP.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_TCP.getParameter())));


                messagex.setDataLength((ByteUtil.intToByteArray(faceNode.getIpInfo().getLength())));
                messagex.setData(faceNode.getIpInfo().toBytes());
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_NODE_TCP.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());


//endregion
            }
            if (code == MessageType.HOST_REQ_OPEN_MONITOR.getCode()) {
//region HOST_REQ_OPEN_MONITOR
                Log.d("MSG", "接收到消息类型: 19 = " + MessageType.HOST_REQ_OPEN_MONITOR.getDesc());

                //========== 保存实时状态 ==========
                SPUtils.put(MyApplication.getContext(), SP_CURRENT_STATUE, 1);
                //========== 保存实时状态 ==========
                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_CLOSE_MONITOR.getCode()) {
//region HOST_REQ_CLOSE_MONITOR
                Log.d("MSG", "接收到消息类型: 20 = " + MessageType.HOST_REQ_CLOSE_MONITOR.getDesc());

                //========== 保存实时状态 ==========
                SPUtils.put(MyApplication.getContext(), SP_CURRENT_STATUE, 0);
                //========== 保存实时状态 ==========
                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_STATUS_MONITOR.getCode()) {
//region HOST_REQ_STATUS_MONITOR
                Log.d("MSG", "接收到消息类型: 21 = " + MessageType.HOST_REQ_STATUS_MONITOR.getDesc());

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_STATUS_MONITOR.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_STATUS_MONITOR.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_STATUS_MONITOR.getParameter())));


                //========== 获取实时状态 ==========
                int status = (int) SPUtils.get(MyApplication.getContext(), SP_CURRENT_STATUE, 0);
                monitorStatus[0] = (byte) status;
                messagex.setDataLength((ByteUtil.intToByteArray(monitorStatus.length)));
                messagex.setData(monitorStatus);
                //========== 获取实时状态 ==========
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_STATUS_MONITOR.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_INIT_NODE.getCode()) {
//region HOST_REQ_INIT_NODE
                Log.d("MSG", "接收到消息类型: 22 = " + MessageType.HOST_REQ_INIT_NODE.getDesc());

                ///TODO: 恢复初始化状态，
                //
                //========== 删除所有记录 =========
                try {
                    AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();
                    accessRecordBeanDao.deleteAll();
                    FileUtil.deleteDir(Const.DIR_IMAGE_RECORD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //========== 删除所有记录 =========
                //========== 实际删除人员信息 ==========
                PersonDao.getDao().deleteAll();
                PersonDao.getPeopleList(false).clear();
                FileUtil.deleteDir(Const.DIR_IMAGE_EMPLOYEE);
                NowPicFeatureDao.getDao().deleteAll();
                NowPicFeatureDao.getMap(false).clear();
                FaceFeatureDao.getDao().deleteAll();
                FaceFeatureDao.getMap(false).clear();
                //========== 实际删除人员信息 ==========
                //删除配置
                DBUtil.getDaoSession().getConfigBeanDao().deleteAll();
                SPUtils.put(MyApplication.getContext(), SP_TCP_PORT, Integer.toHexString(8000));
                SPUtils.put(MyApplication.getContext(), SP_UDP_PORT, Integer.toHexString(8101));
                SPUtils.put(MyApplication.getContext(), SP_SAVE_SN, false);
                SPUtils.put(MyApplication.getContext(), SP_OPEN_DOOR_TIME, "0002");
                SPUtils.put(MyApplication.getContext(), SP_LAST_RECORD_ID, 0L);
                SPUtils.put(MyApplication.getContext(), SP_CURRENT_STATUE, 0);


                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_GET_NODE_DATE.getCode()) {
//region HOST_REQ_GET_NODE_DATE
                Log.d("MSG", "接收到消息类型: 23 = " + MessageType.HOST_REQ_GET_NODE_DATE.getDesc());

                ///TODO: 获取系统日期
                //使用BCD编码
                String yearMD = String.format(Locale.getDefault(), "%tF", System.currentTimeMillis());
                String hourMS = String.format(Locale.getDefault(), "%tT", System.currentTimeMillis());
                String[] yearMDSplit = yearMD.split("-");
                String[] hourMSSplit = hourMS.split(":");
                DataDate dataDate = new DataDate();
                dataDate.setYear(ByteUtil.hexStringToBytes(yearMDSplit[0].substring(2, 4)));
                dataDate.setMonth(ByteUtil.hexStringToBytes(yearMDSplit[1]));
                dataDate.setWeek(new byte[]{(byte) Calendar.getInstance().get(Calendar.DAY_OF_WEEK)});
                dataDate.setDate(ByteUtil.hexStringToBytes(yearMDSplit[2]));
                dataDate.setHour(ByteUtil.hexStringToBytes(hourMSSplit[0]));
                dataDate.setMinute(ByteUtil.hexStringToBytes(hourMSSplit[1]));
                dataDate.setSecond(ByteUtil.hexStringToBytes(hourMSSplit[2]));
                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_DATE.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_DATE.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_DATE.getParameter())));

                messagex.setDataLength((ByteUtil.intToByteArray(dataDate.getLength())));
                messagex.setData(dataDate.toBytes());
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_GET_NODE_DATE.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_SET_NODE_DATE.getCode()) {
//region HOST_REQ_SET_NODE_DATE
                Log.d("MSG", "接收到消息类型: 24 = " + MessageType.HOST_REQ_GET_NODE_DATE.getDesc());

                DataDate dataDate = new DataDate(packet.getMsg().getData());
//                Log.d("MSG", "消息内容: " + dataDate.toHexString());
//                Log.d("MSG", "解析内容: ");
//                Log.d("MSG", "年: " + ByteUtil.bytesToHexString(dataDate.getYear()));
//                Log.d("MSG", "月: " + ByteUtil.bytesToHexString(dataDate.getMonth()));
//                Log.d("MSG", "周: " + ByteUtil.bytesToHexString(dataDate.getWeek()));
//                Log.d("MSG", "日: " + ByteUtil.bytesToHexString(dataDate.getDate()));
//                Log.d("MSG", "时: " + ByteUtil.bytesToHexString(dataDate.getHour()));
//                Log.d("MSG", "分: " + ByteUtil.bytesToHexString(dataDate.getMinute()));
//                Log.d("MSG", "秒: " + ByteUtil.bytesToHexString(dataDate.getSecond()));
                //========== 设置时间 ==========
                Date parse = new SimpleDateFormat("yy-MM-DD-HH-mm-ss").parse(
                        ByteUtil.bytesToHexString(dataDate.getYear()) + "-"
                                + ByteUtil.bytesToHexString(dataDate.getMonth()) + "-"
                                + ByteUtil.bytesToHexString(dataDate.getDate()) + "-"
                                + ByteUtil.bytesToHexString(dataDate.getHour()) + "-"
                                + ByteUtil.bytesToHexString(dataDate.getMinute()) + "-"
                                + ByteUtil.bytesToHexString(dataDate.getSecond())
                );
                long time = parse.getTime();
                LogUtils.d(TAG, String.format(Locale.getDefault(), "%tc", time));
                HWUtil.setClientSystemTime(time);
                //========== 设置时间 ==========

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());

//endregion
            }
            if (code == MessageType.HOST_REQ_OPEN_NODE_GATE.getCode()) {
//region HOST_REQ_OPEN_NODE_GATE
                Log.d("MSG", "接收到消息类型: 25 = " + MessageType.HOST_REQ_OPEN_NODE_GATE.getDesc());

                //========== 开闸 ==========
                HWUtil.openDoorRelay();
                //========== 开闸 ==========


                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_CLOSE_NODE_GATE.getCode()) {
//region HOST_REQ_CLOSE_NODE_GATE
                Log.d("MSG", "接收到消息类型: 26 = " + MessageType.HOST_REQ_CLOSE_NODE_GATE.getDesc());

                //========== 关闸 ==========
                HWUtil.closeDoorRelay();
                //========== 关闸 ==========

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_GET_NODE_GATE_HOLD_TIME.getCode()) {
//region HOST_REQ_GET_NODE_GATE_HOLD_TIME
                Log.d("MSG", "接收到消息类型: 27 = " + MessageType.HOST_REQ_GET_NODE_GATE_HOLD_TIME.getDesc());

                ///TODO: 获取闸机保持时间。
                //gateHoldTime


                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_GATE_HOLD_TIME.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_GATE_HOLD_TIME.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_GATE_HOLD_TIME.getParameter())));

                //========== 开锁保持时间 =========
                String openDoorTime = (String) SPUtils.get(MyApplication.getContext(), SP_OPEN_DOOR_TIME, "0002");
                System.out.println("=========== openDoorTime = " + openDoorTime);
                byte[] bytes = ByteUtil.hexStringToBytes(openDoorTime);
                System.out.println("========= " + ByteUtil.bytesToHexString(bytes) + "   " + bytes.length);
//                messagex.setDataLength(ByteUtil.getBytesShort((short) (AppSettingUtil.getConfig().getOpenDoorContinueTime()/1000),true));
                messagex.setDataLength(ByteUtil.hexStringToBytes("00000002"));
                messagex.setData(bytes);
                //========== 开锁保持时间 =========

                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_GET_NODE_GATE_HOLD_TIME.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_SET_NODE_GATE_HOLD_TIME.getCode()) {
//region HOST_REQ_SET_NODE_GATE_HOLD_TIME
                Log.d("MSG", "接收到消息类型: 28 = " + MessageType.HOST_REQ_SET_NODE_GATE_HOLD_TIME.getDesc());

                gateHoldTime = packet.getMsg().getData();
                Log.d("MSG", "消息内容: " + ByteUtil.bytesToHexString(gateHoldTime));
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "闸机保持时间: " + ByteUtil.getInt(gateHoldTime, true));

                //========== 保存开门持续时间 ==========
                int openDoorTime = 2;
                try {
                    openDoorTime = Integer.parseInt(ByteUtil.bytesToHexString(gateHoldTime), 16);
                } catch (Exception e) {
                    LogUtils.d(TAG, "保存开门持续时间 = " + e.getMessage());
                }
                LogUtils.d(TAG, "保存开门持续时间 = " + openDoorTime);
                SPUtils.put(MyApplication.getContext(), SP_OPEN_DOOR_TIME, ByteUtil.bytesToHexString(gateHoldTime));
                ConfigBean config = AppSettingUtil.getConfig();
                config.setOpenDoorContinueTime(openDoorTime * 1000);
                AppSettingUtil.saveConfig(config);
                //========== 保存开门持续时间 ==========

                ///TODO: 设置闸机保持时间。
                //gateHoldTime

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_GET_NODE_CARD_PARAMETER.getCode()) {
//region HOST_REQ_GET_NODE_CARD_PARAMETER
                Log.d("MSG", "接收到消息类型: 29 = " + MessageType.HOST_REQ_GET_NODE_CARD_PARAMETER.getDesc());

                ///TODO: 获取CARD 参数。
                //dataCardParameter


                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_CARD_PARAMETER.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_CARD_PARAMETER.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_CARD_PARAMETER.getParameter())));

                messagex.setDataLength((ByteUtil.intToByteArray(dataCardParameter.getLength())));
                messagex.setData(dataCardParameter.toBytes());
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_GET_NODE_CARD_PARAMETER.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_SET_NODE_CARD_PARAMETER.getCode()) {
//region HOST_REQ_SET_NODE_CARD_PARAMETER
                Log.d("MSG", "接收到消息类型: 30 = " + MessageType.HOST_REQ_SET_NODE_CARD_PARAMETER.getDesc());

                dataCardParameter = new DataCardParameter(packet.getMsg().getData());
                Log.d("MSG", "消息内容: " + dataCardParameter.toHexString());
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "WG输出功能开关: " + ByteUtil.bytesToHexString(dataCardParameter.getWGEnable()));
                Log.d("MSG", "WG输出类型: " + ByteUtil.bytesToHexString(dataCardParameter.getWGType()));
                Log.d("MSG", "WG字节顺序: " + ByteUtil.bytesToHexString(dataCardParameter.getWGbitOrder()));
                Log.d("MSG", "输出数据类型: " + ByteUtil.bytesToHexString(dataCardParameter.getDataType()));

                //dataCardParameter.getWGType() - 01:维根26三字节,02:维根34四字节,03:维根26二字节,04:维根66八字节,05:禁用
                //dataCardParameter.getWGEnable() - 01:启用,02:禁用
                //dataCardParameter.getDataType() - 01:人员ID,02:卡号
                //dataCardParameter.getWGbitOrder() - 01:高位在前低位在后,02:地位在前高位在后
                ///TODO: 设置闸机保持时间。
                //gateHoldTime

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_GET_NODE_FACE_THRESHOLD.getCode()) {
//region HOST_REQ_GET_NODE_FACE_THRESHOLD
                Log.d("MSG", "接收到消息类型: 31 = " + MessageType.HOST_REQ_GET_NODE_FACE_THRESHOLD.getDesc());

                ///TODO: 获取CARD 参数。
                //faceThreshold


                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_FACE_THRESHOLD.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_FACE_THRESHOLD.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_FACE_THRESHOLD.getParameter())));

                ConfigBean config = AppSettingUtil.getConfig();

                //========== 人脸比对阈值 ==========
                int facePairNumber = (int) (config.getFaceFeaturePairNumber() * 100);
                String s = Integer.toHexString(facePairNumber);
                byte[] bytes = ByteUtil.hexStringToBytes(s);
                //========== 人脸比对阈值 ==========
                messagex.setDataLength((ByteUtil.intToByteArray(bytes.length)));
                messagex.setData(bytes);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_GET_NODE_FACE_THRESHOLD.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
            if (code == MessageType.HOST_REQ_SET_NODE_FACE_THRESHOLD.getCode()) {
//region HOST_REQ_SET_NODE_FACE_THRESHOLD
                Log.d("MSG", "接收到消息类型: 32 = " + MessageType.HOST_REQ_SET_NODE_FACE_THRESHOLD.getDesc());

                faceThreshold = packet.getMsg().getData();
                Log.d("MSG", "消息内容: " + ByteUtil.bytesToHexString(faceThreshold));
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "人脸识别阈值: " + ByteUtil.bytesToHexString(faceThreshold));


                //========== 人脸比对阈值 ==========
                ConfigBean config = AppSettingUtil.getConfig();
                config.setFaceFeaturePairNumber(Integer.parseInt(ByteUtil.bytesToHexString(faceThreshold), 16) / 100f);
                AppSettingUtil.saveConfig(config);
                //========== 人脸比对阈值 ==========

                //----------------------------------------------------------------------------
                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                ByteBufferList bbList = new ByteBufferList();
                ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_OK.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());
//endregion
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ProcessUDP(ByteBufferList bb) {
        InetSocketAddress remoteAddress = asyncDatagramSocket.getRemoteAddress();
        Log.d("UDP", remoteAddress.getHostString() + " " + remoteAddress.getPort());

        try {
            Packet packet = new Packet(bb.getAllByteArray(), false);
            Message message = new Message();

            int code = packet.getMsg().getCode();
            Log.d("MSG", "receive code " + code);
            if (code == MessageType.HOST_REQ_SCAN_NODE.getCode()) {

                Log.d("MSG", "receive 1 = " + MessageType.HOST_REQ_SCAN_NODE.getDesc());

                byte[] NodeToken = new byte[2];
                System.arraycopy(packet.getMsg().getData(), 0, NodeToken, 0, NodeToken.length);

                if (ByteUtil.byte2int(NodeToken) != ByteUtil.byte2int(faceNode.getNodeToken())) {

                    packet.setDeviceSN(faceNode.getSN());

                    message.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_SCAN_NODE.getCategory())));
                    message.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_SCAN_NODE.getCommand())));
                    message.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_SCAN_NODE.getParameter())));

                    DataIPInfo info = faceNode.getIpInfo();

                    message.setDataLength((ByteUtil.intToByteArray(info.getLength())));
                    message.setData(info.toBytes());

                    packet.setMsg(message);
                    packet.setVerify(new byte[1]);

                    asyncDatagramSocket.send(remoteAddress, ByteBuffer.wrap(packet.toBytes(true)));

                    Log.d("MSG", "Send " + MessageType.NODE_RES_SCAN_NODE.getDesc());
                }
            } else if (code == MessageType.HOST_REQ_SET_NODE_TOKEN.getCode()) {

                Log.d("MSG", "receive 2 = " + MessageType.HOST_REQ_SET_NODE_TOKEN.getDesc());

                packet.setDeviceSN(faceNode.getSN());

                message.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                message.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                message.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                byte[] NodeToken = new byte[2];
                System.arraycopy(packet.getMsg().getData(), 0, NodeToken, 0, NodeToken.length);

                faceNode.setNodeToken(NodeToken);

                message.setDataLength((ByteUtil.intToByteArray(NodeToken.length)));
                message.setData(NodeToken);
                packet.setMsg(message);
                packet.setVerify(new byte[1]);

                asyncDatagramSocket.send(remoteAddress, ByteBuffer.wrap(packet.toBytes(true)));

                Log.d("MSG", "Send " + MessageType.NODE_RES_OK.getDesc());
            } else if (code == MessageType.HOST_REQ_READ_NODE_TCP.getCode()) {

                Log.d("MSG", "receive 3 = " + MessageType.HOST_REQ_READ_NODE_TCP.getDesc());

                //  byte[] NodeToken = new byte[2];
                //  System.arraycopy(packet.getMsg().getData(),0, NodeToken,0,NodeToken.length );
                // if(ByteUtil.byte2int(NodeToken) != ByteUtil.byte2int(faceNode.getNodeToken())) {

                packet.setDeviceSN(faceNode.getSN());

                message.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_TCP.getCategory())));
                message.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_TCP.getCommand())));
                message.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NODE_TCP.getParameter())));

                DataIPInfo info = faceNode.getIpInfo();
                message.setDataLength((ByteUtil.intToByteArray(info.getLength())));
                message.setData(info.toBytes());

                packet.setMsg(message);
                packet.setVerify(new byte[1]);

                asyncDatagramSocket.send(remoteAddress, ByteBuffer.wrap(packet.toBytes(true)));

                Log.d("MSG", "Send " + MessageType.NODE_RES_READ_NODE_TCP.getDesc());
                // }
            } else if (code == MessageType.HOST_REQ_WRITE_NODE_TCP.getCode()) {
                Log.d("MSG", "receive 4 = " + MessageType.HOST_REQ_WRITE_NODE_TCP.getDesc());

                DataIPInfo info = faceNode.getIpInfo();

                DataIPInfo dataIPInfo = new DataIPInfo(packet.getMsg().getData());
                Log.d("MSG", "消息内容: " + dataIPInfo.toHexString());
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "IP: " + ByteUtil.bytesToHexString(dataIPInfo.getIP()));
                //解析其他参数
                //udp端口
                String udpPort = ByteUtil.bytesToHexString(dataIPInfo.getLocalUDPListenPort());
                SPUtils.put(MyApplication.getContext(), SP_UDP_PORT, udpPort);
                String tcpPort = ByteUtil.bytesToHexString(dataIPInfo.getLocalTCPListenPort());
                SPUtils.put(MyApplication.getContext(), SP_TCP_PORT, tcpPort);
                LogUtils.d(TAG, "udpPort = " + udpPort + " tcpPort = " + tcpPort);


                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                Message messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_OK.getParameter())));

                messagex.setData(null);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);

                asyncDatagramSocket.send(remoteAddress, ByteBuffer.wrap(packetx.toBytes(true)));

                Log.d("MSG", "Send " + MessageType.NODE_RES_OK.getDesc());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = YZFaceUtil3.class.getSimpleName();
    private String mMessage2Id;
    private static YZFaceUtil3 yzFaceUtil = new YZFaceUtil3();

    public static YZFaceUtil3 getInstance() {
        return yzFaceUtil;
    }


    public void close() {
        if (mAsyncServer != null) {
            mAsyncServer.stop();
        }
    }

    public void sendPersonInfo(DataPersonInfo dataPersonInfo) throws UnsupportedEncodingException {
        PersonBean personBean = PersonDao.query2Fid(ByteUtil.bytesToHexString(dataPersonInfo.getId()));
        if (personBean == null) {
            long count = PersonDao.getDao().queryBuilder().count();
            if (count > 10000) {
                return;
            }
            personBean = new PersonBean();
            long authorityId = System.currentTimeMillis();
            personBean.setAuth_id(authorityId);
            personBean.setPerson_id(authorityId);
            personBean.setFid(ByteUtil.bytesToHexString(dataPersonInfo.getId()));
        }
        String gbk = new String(dataPersonInfo.getName(), "gbk").replaceAll("\\u0000", "");
        personBean.setName(gbk);
        personBean.setCount(AuthorityUtil.NEVER_NUMBER);
        personBean.setStart_ts(AuthorityUtil.NEVER_START_TIME);
        personBean.setEnd_ts(AuthorityUtil.NEVER_END_TIME);
        personBean.setDepartment(ByteUtil.bytesToHexString(dataPersonInfo.getDepartment()));
        personBean.setName_yingze(ByteUtil.bytesToHexString(dataPersonInfo.getName()));
        personBean.setPosition(ByteUtil.bytesToHexString(dataPersonInfo.getJob()));
        personBean.setPersonNumber(ByteUtil.bytesToHexString(dataPersonInfo.getNumber()));
        personBean.setTermOfValidity(ByteUtil.bytesToHexString(dataPersonInfo.getTermOfValidity()));
        personBean.setCardStatus(ByteUtil.bytesToHexString(dataPersonInfo.getStatus()));
        personBean.setPicNumber(ByteUtil.bytesToHexString(dataPersonInfo.getPicNumber()));
        personBean.setEmployee_card_id(ByteUtil.bytesToHexString(dataPersonInfo.getCardNumber()));
        PersonDao.inserOrReplace(personBean);
    }


    private void setMessageId(DataPreparewriteFile dataPreparewriteFile) {
        String idMessage = dataPreparewriteFile.toHexString();
        System.out.println("========== "+idMessage);
        mMessage2Id = "";
        try {
            mMessage2Id = Integer.parseInt(idMessage.substring(0, idMessage.length() - 4),16)+"";
            LogUtils.d(TAG, "mMessage2Id = " + mMessage2Id);
        } catch (Exception e) {
            LogUtils.e(TAG, "error = " + e.getMessage());
        }
    }

    private int setPersonImage(byte[] filebytes, int crc32xx) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(filebytes, 0, filebytes.length);
        if (bitmap == null) {
            LogUtils.d(TAG, "====== setPersonImage bitmap = null");
            crc32xx = 101;
        }
        String photo = Request.ImageToBase64ByLocal(bitmap);
        if (TextUtils.isEmpty(photo) || TextUtils.isEmpty(mMessage2Id)) {
            LogUtils.d(TAG, "====== setPersonImage photo = null || mMessage2Id == null");
            crc32xx = 101;
        } else {

            String body = null;
            try {
                body = "id=" + URLEncoder.encode(mMessage2Id + "", "utf-8") +
                        "&count=" + URLEncoder.encode("10000", "utf-8") +
                        "&startTs=" + URLEncoder.encode("-1", "utf-8") +
                        "&endTs=" + URLEncoder.encode("-1", "utf-8") +
                        "&photo=" + URLEncoder.encode(photo, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String aesKeyLocal = AppSettingUtil.getDeviceAesKey();
            if (TextUtils.isEmpty(aesKeyLocal)) {
                aesKeyLocal = "abc";
            }
            HttpResponseBean post = Request.post(new HttpResponseBean(), "http://" + HWUtil.getIPAddress() + ":8088/setPerson?key=" + aesKeyLocal, body);
            if (post == null || post.getStatus() != 0) {
                LogUtils.d(TAG, "====== post == null");
                crc32xx = 101;
            }
        }
        return crc32xx;
    }


    public String add0(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return "000000000000000000000000000000000000000000000000000000000000";
        }
        if (msg.length() >= 30) {
            return msg;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 60 - msg.length(); i++) {
            stringBuilder.append("0");
        }
        return msg + stringBuilder.toString();
    }

    public String add0(String msg, int number) {
        if (TextUtils.isEmpty(msg)) {
            return "000000000000000000000000000000000000000000000000000000000000";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < number - msg.length(); i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString() + msg;
    }

    public void uploadRecord(int number, Packet packet) {

        //生成响应消息
        DataRecordInfoList dataRecordInfoList = new DataRecordInfoList();
        //获取上次的记录id
        long recordId = (long) SPUtils.get(MyApplication.getContext(), SP_LAST_RECORD_ID, 0L);
        AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();
        List<AccessRecordBean> list = accessRecordBeanDao.queryBuilder().where(AccessRecordBeanDao.Properties.Id.gt(recordId)).limit(number).orderDesc().list();
        System.out.println("uploadRecord = " + list.size());
        if (list == null || list.size() == 0) {
        } else {
            SPUtils.put(MyApplication.getContext(), SP_LAST_RECORD_ID, list.get(list.size() - 1).getId());

            for (int i = 0; i < list.size(); i++) {
                DataRecordInfo dataRecordInfo1 = new DataRecordInfo();

                dataRecordInfo1.setRecordSN(ByteUtil.hexStringToBytes(add0(Long.toHexString(list.get(i).getId()), 8)));
                dataRecordInfo1.setPersonID(ByteUtil.hexStringToBytes(list.get(i).getFid()));
                dataRecordInfo1.setDate(ByteUtil.hexStringToBytes(String.format(Locale.getDefault(), "%ty%<tm%<td%<tH%<tM%<tS", (long) (list.get(i).getTime()))));
                dataRecordInfo1.setDirection(ByteUtil.hexStringToBytes("01"));
                dataRecordInfo1.setStatus(ByteUtil.hexStringToBytes("03"));
                dataRecordInfo1.setPicture(ByteUtil.hexStringToBytes("01"));
                dataRecordInfoList.Add(dataRecordInfo1);

                if (i % 10 == 0 || i == list.size() - 1) {

                    dataRecordInfoList.setRecordCunt(ByteUtil.getBytesShort((short) dataRecordInfoList.size(), true));

                    Packet packetx = new Packet();
                    packetx.setDeviceSN(faceNode.getSN());
                    packetx.setPassword(packet.getPassword());
                    packetx.setToken(packet.getToken());

                    Message messagex = new Message();
                    messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO.getCategory())));
                    messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO.getCommand())));
                    messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO.getParameter())));

                    messagex.setDataLength((ByteUtil.intToByteArray(dataRecordInfoList.getLength())));
                    messagex.setData(dataRecordInfoList.toBytes());
                    packetx.setMsg(messagex);
                    packetx.setVerify(new byte[1]);


                    ByteBufferList bbList = new ByteBufferList();
                    ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                    bbList.add(p);

                    clientSocket.write(bbList);
                    dataRecordInfoList = new DataRecordInfoList();
                }
            }
        }
    }

    public static class MyHandler extends android.os.Handler {

        private WeakReference<YZFaceUtil3> mWeakReference;

        public MyHandler(YZFaceUtil3 yzFaceUtil3) {
            mWeakReference = new WeakReference<YZFaceUtil3>(yzFaceUtil3);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            YZFaceUtil3 yzFaceUtil3 = mWeakReference.get();
            if (yzFaceUtil3 == null) {
                return;
            }
            int status = (int) SPUtils.get(MyApplication.getContext(), SP_CURRENT_STATUE, 0);


            Packet packetx = new Packet();
            packetx.setDeviceSN(yzFaceUtil3.faceNode.getSN());
            packetx.setPassword(ByteUtil.hexStringToBytes("FFFFFFFF"));
            packetx.setToken(ByteUtil.hexStringToBytes("FFFFFFFF"));


            Message messagex = new Message();

            messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_EVENT_MSG.getCategory())));
            messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_EVENT_MSG.getCommand())));
            messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_EVENT_MSG.getParameter())));

            if (status == 0) {
                //不开启实时监控
            } else {
                //开始实时监控
                long lastId = (long) SPUtils.get(MyApplication.getContext(), SP_LAST_RECORD_ID, 0L);
                long newRecordInfos = DBUtil.getDaoSession().getAccessRecordBeanDao().queryBuilder().where(AccessRecordBeanDao.Properties.Id.gt(lastId)).count();
                if (newRecordInfos > 0) {
                    //获取上次的记录id
                    long recordId = (long) SPUtils.get(MyApplication.getContext(), SP_LAST_RECORD_ID, 0L);
                    AccessRecordBeanDao accessRecordBeanDao = DBUtil.getDaoSession().getAccessRecordBeanDao();
                    List<AccessRecordBean> list = accessRecordBeanDao.queryBuilder().where(AccessRecordBeanDao.Properties.Id.gt(recordId)).limit((int) newRecordInfos).orderDesc().list();
                    System.out.println(list);
                    if (list == null || list.size() == 0) {
                    } else {
                        SPUtils.put(MyApplication.getContext(), SP_LAST_RECORD_ID, list.get(list.size() - 1).getId());
                        for (int i = 0; i < list.size(); i++) {
                            DataRecordInfo dataRecordInfo1 = new DataRecordInfo();

                            dataRecordInfo1.setRecordSN(ByteUtil.hexStringToBytes(yzFaceUtil3.add0(Long.toHexString(list.get(i).getId()), 8)));
                            dataRecordInfo1.setPersonID(ByteUtil.hexStringToBytes(list.get(i).getFid()));
                            dataRecordInfo1.setDate(ByteUtil.hexStringToBytes(String.format(Locale.getDefault(), "%ty%<tm%<td%<tH%<tM%<tS", (long) (list.get(i).getTime()))));
                            dataRecordInfo1.setDirection(ByteUtil.hexStringToBytes("01"));
                            dataRecordInfo1.setStatus(ByteUtil.hexStringToBytes("03"));
                            dataRecordInfo1.setPicture(ByteUtil.hexStringToBytes("01"));

                            messagex.setDataLength((ByteUtil.intToByteArray(dataRecordInfo1.getLength())));
                            messagex.setData(dataRecordInfo1.toBytes());

                            packetx.setMsg(messagex);
                            packetx.setVerify(new byte[1]);
                            ByteBufferList bbList = new ByteBufferList();
                            ByteBuffer p = ByteBuffer.wrap(packetx.toBytes(true));
                            bbList.add(p);
                            yzFaceUtil3.clientSocket.write(bbList);
                            if (i == 500) {
                                break;
                            }
                        }
                        byte[] toSendCount = ByteUtil.hexStringToBytes("0002");
                        Packet packetx1 = new Packet();
                        packetx1.setDeviceSN(yzFaceUtil3.faceNode.getSN());
                        packetx1.setPassword(packetx1.getPassword());
                        packetx1.setToken(packetx1.getToken());

                        Message messagex1 = new Message();
                        messagex1.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getCategory())));
                        messagex1.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getCommand())));
                        messagex1.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getParameter())));

                        messagex1.setDataLength(ByteUtil.hexStringToBytes("00000002"));
                        messagex1.setData(toSendCount);
                        packetx1.setMsg(messagex1);
                        packetx1.setVerify(new byte[1]);

                        ByteBufferList bbList1 = new ByteBufferList();
                        ByteBuffer p1 = ByteBuffer.wrap(packetx1.toBytes(true));
                        bbList1.add(p1);
                        yzFaceUtil3.clientSocket.write(bbList1);

                        Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getDesc());
                        Log.d("MSG", "发送消息包数据: " + packetx1.toSendHexString());
                    }
                }
            }
            yzFaceUtil3.mHandler.sendEmptyMessageDelayed(1, Const.HANDLER_DELAY_TIME_1000);
        }
    }

    public static final String SP_TCP_PORT = "sp_tcp_port";
    public static final String SP_UDP_PORT = "sp_udp_port";
    public static final String SP_SAVE_SN = "sp_save_sn";
    public static final String SP_OPEN_DOOR_TIME = "sp_open_door_time";
    public static final String SP_LAST_RECORD_ID = "sp_last_record_id";
    public static final String SP_CURRENT_STATUE = "sp_current_statue";
}
