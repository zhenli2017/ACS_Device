package com.example.lib.interfaceTest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Time:2018/10/29
 * User:lizhen
 * Description:
 */

public class InterfaceTest {
    public static final String HTTP_URL = "http://192.168.137.55:8088/";
//    public static final String HTTP_URL = "http://192.168.0.19:8088/";

    public static void main(String[] args) {
//        setDeviceKey();
//        setDeviceInfo();
//        getDeviceInfo();
        setTime();
//        getTime();
        setPerson();
//        getPerson();
//        listPerson();
//        removePerson();
//        listRecord();
//        removeRecord();
//        setRecordCallback();
//        getRecordCallback();
//        reboot();
//        openDoor();
        setHeartBeat();
//        getHeartBeat();
//        check();
//        photo();
//        parseBase64Image();

    }


    /**
     * 秘钥接口
     * URL:setDeviceKey
     * method:POST
     */
    public static void setDeviceKey() {
        //key第一次的时候需要传默认值(abc),再次修改需要传上次设置的值
        String url = HTTP_URL + "setDeviceKey?key=abc";
        String body = null;
        try {
            body = "oldKey=" + URLEncoder.encode("abc", "utf-8") +
                    "&newKey=" + URLEncoder.encode("admin", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("setDeviceKey = " + post);
    }

    /**
     * 设置设备基础信息
     * URL:setDeviceInfo
     * method:POST
     */
    public static void setDeviceInfo() {
        String url = HTTP_URL + "setDeviceInfo?key=abc";
        String body = null;
        try {
            body = "cameraDetectType=" + URLEncoder.encode("0", "utf-8") +
                    "&faceFeaturePairNumber=" + URLEncoder.encode("0.92", "utf-8") +
                    "&faceFeaturePairSuccessOrFailWaitTime=" + URLEncoder.encode("2000", "utf-8") +
                    "&openDoorType=" + URLEncoder.encode("0", "utf-8") +
                    "&openDoorContinueTime=" + URLEncoder.encode("2000", "utf-8") +
                    "&doorType=" + URLEncoder.encode("34", "utf-8") +
                    "&idCardFaceFeaturePairNumber=" + URLEncoder.encode("0.8", "utf-8") +
                    "&appWelcomeMsg=" + URLEncoder.encode("@,识别成功", "gbk") +
                    "&deviceSoundSize=" + URLEncoder.encode("0", "utf-8") +
                    "&deviceDefendTime=" + URLEncoder.encode("21:50", "utf-8") +
                    "&deviceName=" + URLEncoder.encode("1号机", "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("setDeviceInfo = " + post);

    }

    /**
     * 获取设备信息
     * URL:getDeviceInfo
     * method:GET
     */
    public static void getDeviceInfo() {
        String url = HTTP_URL + "getDeviceInfo?key=abc";

        String get = Request.get(url);
        System.out.println("getDeviceInfo = " + get);

    }

    /**
     * 设置设备时间
     * URL:setTime
     * method:POST
     */
    public static void setTime() {
        String url = HTTP_URL + "setTime?key=abc";
        String body = null;
        try {
            body = "ts=" + URLEncoder.encode((System.currentTimeMillis()) + "", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("setTime = " + post);
    }

    /**
     * 获取设备时间
     * URI:getTime
     * method:GET
     */
    public static void getTime() {
        String url = HTTP_URL + "getTime?key=abc";

        String get = Request.get(url);
        System.out.println("getTime = " + get);
    }

    /**
     * 创建过闸人员
     * URI:setPerson
     * method:POST
     */
    public static void setPerson() {
        String url = HTTP_URL + "setPerson?key=abc";
        String body = null;
        try {
            body = "id=" + URLEncoder.encode("1", "utf-8") +
                    "&name=" + URLEncoder.encode("胖虎1", "utf-8") +
                    "&IC_NO=" + URLEncoder.encode("7267e840", "utf-8") +//40e86772
                    "&ID_NO=" + URLEncoder.encode("990307189207026735", "utf-8") +
                    "&personalizedPermissions=" + URLEncoder.encode("0+2", "utf-8") +
                    "&passCount=" + URLEncoder.encode("10000", "utf-8") +
                    "&startTs=" + URLEncoder.encode(111111111.0d + "", "utf-8") +
                    "&endTs=" + URLEncoder.encode(99999999999.0d + "", "utf-8") +
                    "&photo=" + URLEncoder.encode(Request.ImageToBase64("d://file/terminal/943.jpg"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("setPerson = " + post);
    }

    /**
     * 根据ID获取人员信息
     * URI:getPerson
     * method:GET
     * photo 内的数据需要替换 \n \r \t,否则图片无法使用
     * String s1 = photo.replaceAll("\\\\n", "");
     */
    public static void getPerson() {
        String url = HTTP_URL + "getPerson?key=abc&id=1";
        String get = Request.get(url);
        System.out.println("getPerson = " + get);
    }

    /**
     * 获取人员列表
     * URI:listPerson
     * method:GET
     */
    public static void listPerson() {
        String url = HTTP_URL + "listPerson?key=abc&id=1";
        String get = Request.get(url);
        System.out.println("listPerson = " + get);
    }

    /**
     * 根据ID删除人员
     * URI:removePerson
     * method:POST
     */
    public static void removePerson() {
        String url = HTTP_URL + "removePerson?key=abc";
        String body = null;
        try {
            body = "id=" + URLEncoder.encode("[1,2]", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("removePerson = " + post);
    }

    /**
     * 获取过闸流水日志
     * URI:listRecord
     * method:GET
     */
    public static void listRecord() {
        String url = HTTP_URL + "listRecord?key=abc&ts=" + (System.currentTimeMillis() - 1000000) + "&count=100";
        String get = Request.get(url);
        System.out.println("listRecord = " + get);
    }

    /**
     * 删除过闸流水日志
     * URI:removeRecord
     * method:POST
     */
    public static void removeRecord() {
        String url = HTTP_URL + "removeRecord?key=abc";
        String body = null;
        try {
            body = "ts=" + URLEncoder.encode((System.currentTimeMillis() - 10000) + "", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("removeRecord = " + post);
    }

    /**
     * 设置过闸数据自动上传的URL
     * URI:setRecordCallback
     * method:POST
     */
    public static void setRecordCallback() {
        String url = HTTP_URL + "setRecordCallback?key=abc";
        String body = null;
        try {
            body = "url=" + URLEncoder.encode("http://192.168.137.1:8888", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("setRecordCallback = " + post);
    }

    /**
     * 获取过闸数据自动上传的URL
     * URI:getRecordCallback
     * method:GET
     */
    public static void getRecordCallback() {
        String url = HTTP_URL + "getRecordCallback?key=abc";
        String get = Request.get(url);
        System.out.println("getRecordCallback = " + get);
    }

    /**
     * 重启设备
     * URI:reboot
     * method:POST/GET
     */
    public static void reboot() {
        String url = HTTP_URL + "reboot?key=abc";
        String get = Request.get(url);
        System.out.println("reboot = " + get);
    }

    /**
     * 开闸
     * URI:open
     * method:POST/GET
     */
    public static void openDoor() {
        String url = HTTP_URL + "open?key=abc";
        String get = Request.get(url);
        System.out.println("open = " + get);
    }

    /**
     * 配置心跳URL
     * URI:setHeartBeat
     * method:POST
     */
    public static void setHeartBeat() {
        String url = HTTP_URL + "setHeartBeat?key=abc";
        String body = null;
        try {
            body = "url=" + URLEncoder.encode("http://192.168.0.9:8888", "utf-8") +
                    "&period=" + URLEncoder.encode("5", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("setHeartBeat = " + post);
    }

    /**
     * 获取心跳URL
     * URI:getHeartBeat
     * method:GET
     */
    public static void getHeartBeat() {
        String url = HTTP_URL + "getHeartBeat?key=abc";
        String get = Request.get(url);
        System.out.println("getHeartBeat = " + get);
    }

    /**
     * 更新终端APK
     * URI:updateAPK
     * method:POST
     */
    public static void updateAPK() {
        String url = HTTP_URL + "updateAPK?key=abc";
        String body = null;
        try {
            body = "url=" + URLEncoder.encode("http://www.tianmao.com", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("updateAPK = " + post);
    }

    /**
     * 检查照片是否可用、重复
     * URI:check
     * method:POST
     */
    public static void check() {
        String url = HTTP_URL + "check?key=abc";
        String body = null;
        try {
            body = "photo=" + URLEncoder.encode(Request.ImageToBase64("d://file/terminal/temp.jpg"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("check = " + post);
    }

    /**
     * 拍照
     * URI:photo
     * method:POST
     */
    public static void photo() {
        String url = HTTP_URL + "photo?key=abc";
        String body = null;
        try {
            body = "tipsBefore=" + URLEncoder.encode("大声喊,茄子", "gbk") +
                    "&tipsAfter=" + URLEncoder.encode("拍照结束啦", "gbk") +
                    "&count=" + URLEncoder.encode("1", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String post = Request.post(url, body);
        System.out.println("check = " + post);
    }

    /**
     * image.txt 内只有photo的数据,不包含其它数据
     */
    public static void parseBase64Image() {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("d://file/terminal/server/image.txt")));
            String s = bufferedReader.readLine().replaceAll("\\\\n", "");
            System.out.println(s);
            Request.Base64ToImage(s, "d://file/terminal/server/temp3.jpg");
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
