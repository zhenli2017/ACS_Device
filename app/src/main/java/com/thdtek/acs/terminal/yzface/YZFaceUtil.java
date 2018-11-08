package com.thdtek.acs.terminal.yzface;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.HttpResponseBean;
import com.thdtek.acs.terminal.bean.HttpResponseListRecord;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.Request;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.yzface.Entity.DeviceUtil;
import com.thdtek.acs.terminal.yzface.Entity.Node;
import com.thdtek.acs.terminal.yzface.Message.BreakPacket;
import com.thdtek.acs.terminal.yzface.Message.ByteUtil;
import com.thdtek.acs.terminal.yzface.Message.DataAddPersonInfo;
import com.thdtek.acs.terminal.yzface.Message.DataFileChunk;
import com.thdtek.acs.terminal.yzface.Message.DataFileHandle;
import com.thdtek.acs.terminal.yzface.Message.DataIPInfo;
import com.thdtek.acs.terminal.yzface.Message.DataPersonInfo;
import com.thdtek.acs.terminal.yzface.Message.DataPreparewriteFile;
import com.thdtek.acs.terminal.yzface.Message.DataQueryPersonPic;
import com.thdtek.acs.terminal.yzface.Message.DataRecordInfo;
import com.thdtek.acs.terminal.yzface.Message.DataRecordInfoCount;
import com.thdtek.acs.terminal.yzface.Message.DataRecordInfoList;
import com.thdtek.acs.terminal.yzface.Message.DataVersion;
import com.thdtek.acs.terminal.yzface.Message.Message;
import com.thdtek.acs.terminal.yzface.Message.MessageType;
import com.thdtek.acs.terminal.yzface.Message.Packet;
import com.thdtek.acs.terminal.yzface.Test.ByteUtilTest;
import com.thdtek.acs.terminal.yzface.Test.DataIPInfoTest;
import com.thdtek.acs.terminal.yzface.Test.MessageTest;
import com.thdtek.acs.terminal.yzface.Test.PacketTest;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.CRC32;

/**
 * Time:2018/10/24
 * User:lizhen
 * Description:
 */

public class YZFaceUtil {
    AsyncServer mAsyncServer = null;
    AsyncDatagramSocket asyncDatagramSocket = null;
    AsyncSocket clientSocket = null;

    int localUDPListenPort;
    int localTCPListenPort;

    Node faceNode;

    DataFileHandle fileHandle = new DataFileHandle();

    CRC32 crc32 = new CRC32();
    List<DataFileChunk> dataFileChunkList = new ArrayList<DataFileChunk>();

    public void init() {
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
        String sn = "4D432D35383132543137313030343139";
        faceNode.setSN(ByteUtil.hexStringToBytes(sn));
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
                Log.d("MSG", "receive 1 " + MessageType.HOST_REQ_GET_NODE_VERSION.getDesc());

                packet.setDeviceSN(faceNode.getSN());

                message.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_VERSION.getCategory())));
                message.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_VERSION.getCommand())));
                message.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_GET_NODE_VERSION.getParameter())));

                DataVersion version = faceNode.getVersion();

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


                Packet packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(ByteUtil.hexStringToBytes("FFFFFFFF"));
                packetx.setToken(ByteUtil.hexStringToBytes("FFFFFFFF"));


                Message messagex = new Message();

                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_EVENT_MSG.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_EVENT_MSG.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_EVENT_MSG.getParameter())));

                DataRecordInfo dataRecordInfo = new DataRecordInfo();
                dataRecordInfo.setRecordSN(ByteUtil.intToByteArray(0x12345678));
                dataRecordInfo.setPersonID(ByteUtil.intToByteArray(0x12345678));
                dataRecordInfo.setDate(ByteUtil.hexStringToBytes("181019102025"));
                dataRecordInfo.setDirection(ByteUtil.intToByteArray1(0x01));
                dataRecordInfo.setStatus(ByteUtil.intToByteArray1(0x03));
                dataRecordInfo.setPicture(ByteUtil.intToByteArray1(0x01));

                messagex.setDataLength((ByteUtil.intToByteArray(dataRecordInfo.getLength())));
                messagex.setData(dataRecordInfo.toBytes());

                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);


                bbList = new ByteBufferList();
                p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_EVENT_MSG.getDesc());
                Log.d("MSG", "消息包数据:: " + packetx.toSendHexString());

//endregion
            } else if (code == MessageType.HOST_REQ_PREPARE_WRITE_FILE.getCode()) {
//region HOST_REQ_PREPARE_WRITE_FILE
                DataPreparewriteFile dataPreparewriteFile = new DataPreparewriteFile(packet.getMsg().getData());

                Log.d("MSG", "接收到消息类型 2 : " + MessageType.HOST_REQ_PREPARE_WRITE_FILE.getDesc());
                Log.d("MSG", "消息内容: " + dataPreparewriteFile.toHexString());

                int id = (int) ByteUtil.unsigned4BytesToInt(dataPreparewriteFile.getPersonID(), 0);

                fileHandle.setHandle(ByteUtil.getBytesInt(id + 1, true));
                setMessageId(dataPreparewriteFile);
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
                Log.d("MSG", "接收到消息类型 3 : " + MessageType.HOST_REQ_WRITE_FILE.getDesc());

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
                Log.d("MSG", "接收到消息类型 4 : " + MessageType.HOST_REQ_WRITE_FILE_OK_CRC.getDesc());
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
                crc32xx = setPersonImage(filebytes, crc32xx);


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
                Log.d("MSG", "接收到消息类型 5 : " + MessageType.HOST_REQ_ADD_PERSON_INFO.getDesc());

                DataAddPersonInfo dataAddPersonInfo = new DataAddPersonInfo(packet.getMsg().getData());

                Log.d("MSG", "消息内容: " + dataAddPersonInfo.toHexString());

                //解析人员信息
                Log.d("MSG", "添加人员个数: " + ByteUtil.unsigned4BytesToInt(dataAddPersonInfo.getNumberPersionInfo(), 0));
                for (int i = 0; i < dataAddPersonInfo.getPersonInfos().size(); i++) {
                    DataPersonInfo dataPersonInfo = dataAddPersonInfo.getPersonInfos().get(i);
                    Log.d("MSG", "Id: " + ByteUtil.bytesToHexString(dataPersonInfo.getId()));
                    Log.d("MSG", "Name: " + ByteUtil.bytesToHexString(dataPersonInfo.getName()));
                    Log.d("MSG", "Number: " + ByteUtil.bytesToHexString(dataPersonInfo.getNumber()));
                    Log.d("MSG", "Department: " + ByteUtil.bytesToHexString(dataPersonInfo.getDepartment()));
                    Log.d("MSG", "Job: " + ByteUtil.bytesToHexString(dataPersonInfo.getJob()));
                    Log.d("MSG", "PicNumber: " + ByteUtil.bytesToHexString(dataPersonInfo.getPicNumber()));
                    Log.d("MSG", "CardNumber: " + ByteUtil.bytesToHexString(dataPersonInfo.getCardNumber()));
                    Log.d("MSG", "TermOfValidity: " + ByteUtil.bytesToHexString(dataPersonInfo.getTermOfValidity()));
                    Log.d("MSG", "Status: " + ByteUtil.bytesToHexString(dataPersonInfo.getStatus()));
                    sendPersonInfo(dataPersonInfo);
                }


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

                Log.d("MSG", "接收到消息类型 6 : " + MessageType.HOST_REQ_READ_RECORD_INFO.getDesc());


                //生成响应消息
                int recordCapacity = 10;
                int newRecordInfos = 2;
                DataRecordInfoCount ataRecordInfoCount = new DataRecordInfoCount();
                ataRecordInfoCount.setRecordCapacity(ByteUtil.intToByteArray(recordCapacity));
                ataRecordInfoCount.setNewRecordInfos(ByteUtil.intToByteArray(newRecordInfos));

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
                Log.d("MSG", "接收到消息类型  7 : " + MessageType.HOST_REQ_DELALL_RECORD_INFO.getDesc());

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
            if (code == MessageType.HOST_REQ_RESTLALL_RECORD_INFO.getCode()) {
//region HOST_REQ_DELALL_RECORD_INFO
                Log.d("MSG", "接收到消息类型 8 : " + MessageType.HOST_REQ_RESTLALL_RECORD_INFO.getDesc());

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
            if (code == MessageType.HOST_REQ_READ_NEW_RECORD_INFO.getCode()) {
//region HOST_REQ_READ_NEW_RECORD_INFO
                Log.d("MSG", "接收到消息类型 9 : " + MessageType.HOST_REQ_READ_NEW_RECORD_INFO.getDesc());
                byte[] toReadNewRecordCount = new byte[2];
                toReadNewRecordCount = packet.getMsg().getData();

                Log.d("MSG", "消息内容: " + ByteUtil.bytesToHexString(toReadNewRecordCount));
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "要读取新纪录的数量: " + ByteUtil.getShort(toReadNewRecordCount, false));

                //生成响应消息

                DataRecordInfoList dataRecordInfoList = new DataRecordInfoList();

                List<HttpResponseListRecord.DataBean.RecordBean> listRecord = getListRecord(ByteUtil.getShort(toReadNewRecordCount, false));
                if (listRecord == null) {

                } else {
                    for (int i = listRecord.size() - 1; i >= 0; i--) {
                        DataRecordInfo dataRecordInfo1 = new DataRecordInfo();
                        dataRecordInfo1.setRecordSN(ByteUtil.hexStringToBytes(listRecord.get(i).getPersonID()));
                        dataRecordInfo1.setPersonID(ByteUtil.hexStringToBytes(listRecord.get(i).getPersonID()));
                        dataRecordInfo1.setDate(ByteUtil.hexStringToBytes(String.format(Locale.getDefault(), "%ty%<tm%<td%<tH%<tM%<tS", listRecord.get(i).getTs())));
                        dataRecordInfo1.setDirection(ByteUtil.hexStringToBytes("01"));
                        dataRecordInfo1.setStatus(ByteUtil.hexStringToBytes("03"));
                        dataRecordInfo1.setPicture(ByteUtil.hexStringToBytes("01"));
                        dataRecordInfoList.Add(dataRecordInfo1);
                    }
                }

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

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_NEW_RECORD_INFO.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());


                byte[] toSendCount = new byte[2];
                toSendCount = ByteUtil.hexStringToBytes("0002");

                packetx = new Packet();
                packetx.setDeviceSN(faceNode.getSN());
                packetx.setPassword(packet.getPassword());
                packetx.setToken(packet.getToken());

                messagex = new Message();
                messagex.setCategory((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getCategory())));
                messagex.setCommand((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getCommand())));
                messagex.setParameter((ByteUtil.intToByteArray1(MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getParameter())));

                messagex.setDataLength(ByteUtil.hexStringToBytes("00000002"));
                messagex.setData(toSendCount);
                packetx.setMsg(messagex);
                packetx.setVerify(new byte[1]);


                bbList = new ByteBufferList();
                p = ByteBuffer.wrap(packetx.toBytes(true));
                bbList.add(p);

                Thread.sleep(500);

                clientSocket.write(bbList);

                Log.d("MSG", "发送消息类型: " + MessageType.NODE_RES_READ_NEW_RECORD_INFO_END.getDesc());
                Log.d("MSG", "发送消息包数据: " + packetx.toSendHexString());


//endregion
            }
            if (code == MessageType.HOST_REQ_QUERY_PERSON_PIC.getCode()) {
//region HOST_REQ_QUERY_PERSON_PIC
                Log.d("MSG", "接收到消息类型 10 : " + MessageType.HOST_REQ_QUERY_PERSON_PIC.getDesc());
                byte[] persionId = new byte[4];
                persionId = packet.getMsg().getData();

                Log.d("MSG", "消息内容: " + ByteUtil.bytesToHexString(persionId));
                Log.d("MSG", "解析内容: ");
                Log.d("MSG", "要查询照片和指纹的人员ID: " + ByteUtil.unsigned4BytesToInt(persionId, 0));

                //生成响应消息

                DataQueryPersonPic dataQueryPersonPic = new DataQueryPersonPic();
                dataQueryPersonPic.setPersonID(ByteUtil.hexStringToBytes("00000001"));
                dataQueryPersonPic.setPicList(ByteUtil.hexStringToBytes("0000000001"));


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

                Log.d("MSG", "receive " + MessageType.HOST_REQ_SCAN_NODE.getDesc());

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

                Log.d("MSG", "receive " + MessageType.HOST_REQ_SET_NODE_TOKEN.getDesc());

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final String TAG = YZFaceUtil.class.getSimpleName();
    private String mMessage2Id;
    private static YZFaceUtil yzFaceUtil = new YZFaceUtil();

    public static YZFaceUtil getInstance() {
        return yzFaceUtil;
    }


    public void close() {
        if (mAsyncServer != null) {
            mAsyncServer.stop();
        }
    }

    public void sendPersonInfo(DataPersonInfo dataPersonInfo) throws UnsupportedEncodingException {
        String gbk = new String(dataPersonInfo.getName(), "gbk").replaceAll("\\u0000", "");
        Log.d("MSG", "Name 2 : " + gbk);
        String body = null;
        try {
            body = "id=" + URLEncoder.encode(ByteUtil.bytesToHexString(dataPersonInfo.getId()), "utf-8") +
                    "&name=" + URLEncoder.encode(gbk, "utf-8") +
                    "&IC_NO=" + URLEncoder.encode(ByteUtil.bytesToHexString(dataPersonInfo.getCardNumber()), "utf-8") +
                    "&passCount=" + URLEncoder.encode(10000 + "", "utf-8") +
                    "&startTs=" + URLEncoder.encode(111111111 + "", "utf-8") +
                    "&passType=" + URLEncoder.encode("0", "utf-8") +
                    "&endTs=" + URLEncoder.encode(99999999999L + "", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String aesKeyLocal = AppSettingUtil.getDeviceAesKey();
        if (TextUtils.isEmpty(aesKeyLocal)) {
            aesKeyLocal = "abc";
        }
        Request.post(new HttpResponseBean(), "http://" + HWUtil.getIPAddress() + ":8088/setPerson?key=" + aesKeyLocal, body);
    }


    private void setMessageId(DataPreparewriteFile dataPreparewriteFile) {
        String idMessage = dataPreparewriteFile.toHexString();
        mMessage2Id = "";
        try {
            mMessage2Id = String.format(Locale.getDefault(), "%08d", Integer.parseInt(idMessage.substring(0, idMessage.length() - 4)));
            LogUtils.d(TAG, "mMessage2Id = " + mMessage2Id);
        } catch (Exception e) {
            LogUtils.e(TAG, "error = " + e.getMessage());
        }
    }

    private int setPersonImage(byte[] filebytes, int crc32xx) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(filebytes, 0, filebytes.length);
        if (bitmap == null) {
            //失败
            crc32xx = 101;
        }
        String photo = Request.ImageToBase64ByLocal(bitmap);
        if (TextUtils.isEmpty(photo) || TextUtils.isEmpty(mMessage2Id)) {
            crc32xx = 101;
        } else {

            String body = null;
            try {
                body = "id=" + URLEncoder.encode(mMessage2Id + "", "utf-8") +
                        "&photo=" + URLEncoder.encode(photo, "utf-8") +
                        "&passCount=" + URLEncoder.encode(10000 + "", "utf-8") +
                        "&startTs=" + URLEncoder.encode(111111111 + "", "utf-8") +
                        "&passType=" + URLEncoder.encode("0", "utf-8") +
                        "&endTs=" + URLEncoder.encode(99999999999L + "", "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String aesKeyLocal = AppSettingUtil.getDeviceAesKey();
            if (TextUtils.isEmpty(aesKeyLocal)) {
                aesKeyLocal = "abc";
            }
            HttpResponseBean post = Request.post(new HttpResponseBean(), "http://" + HWUtil.getIPAddress() + ":8088/setPerson?key=" + aesKeyLocal, body);
            if (post == null) {
                crc32xx = 101;
            }
        }
        return crc32xx;
    }

    private List<HttpResponseListRecord.DataBean.RecordBean> getListRecord(int number) {
        String body = null;
        long time = (long) SPUtils.get(MyApplication.getContext(), Const.YING_ZE_LIST_RECORD_LAST_TIME, 0L);
        System.out.println("current time = " + time);
        body = "&ts=" + time + "&count=" + number + "";

        String aesKeyLocal = AppSettingUtil.getDeviceAesKey();
        if (TextUtils.isEmpty(aesKeyLocal)) {
            aesKeyLocal = "abc";
        }
        HttpResponseListRecord httpResponseListRecord = Request.get(new HttpResponseListRecord(), "http://" + HWUtil.getIPAddress() + ":8088/listRecord?key=" + aesKeyLocal + body);
        if (httpResponseListRecord == null) {
            return null;
        }
        HttpResponseListRecord.DataBean data = httpResponseListRecord.getData();
        if (data == null) {
            return null;
        }
        List<HttpResponseListRecord.DataBean.RecordBean> record = data.getRecord();
        if (record == null || record.size() == 0) {
            return null;
        }

        SPUtils.put(MyApplication.getContext(), Const.YING_ZE_LIST_RECORD_LAST_TIME, record.get(record.size() - 1).getTs());

        return record;
    }

}
