package com.thdtek.acs.terminal.server;

import android.text.TextUtils;
import android.util.Base64;

import com.thdtek.acs.terminal.util.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Base64Utils {

    /**
     * 本地图片转换成base64字符串
     *
     * @param imgFile 图片本地路径
     * @return
     */
    public static String ImageToBase64ByLocal(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理


        InputStream in = null;
        byte[] data = new byte[0];

        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);

            data = new byte[in.available()];
            System.out.println("in.available() = " + in.available());
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(data, Base64.DEFAULT);

    }

    public static String cameraDataToBase64(byte[] data) {
        if (data == null) {
            data = new byte[0];
        }
        return Base64.encodeToString(
                BitmapUtil.bitmap2Byte(
                        BitmapUtil.getCameraBitmap(
                                data)), Base64.DEFAULT);
    }


    /**
     * 在线图片转换成base64字符串
     *
     * @param imgURL 图片线上路径
     * @return
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:43:18
     */
    public static String ImageToBase64ByOnline(String imgURL) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            // 创建URL
            URL url = new URL(imgURL);
            byte[] by = new byte[1024];
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            // 将内容读取内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            // 关闭流
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(data.toByteArray(), Base64.DEFAULT);
    }


    /**
     * base64字符串转换成图片
     *
     * @param imgStr      base64字符串
     * @param imgFilePath 图片存放路径
     * @return
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:42:17
     */
    public static boolean Base64ToImage(String imgStr, String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片

        if (TextUtils.isEmpty(imgStr)) {
            return false;
        }

        try {
            // Base64解码
            byte[] b = Base64.decode(imgStr, Base64.DEFAULT);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }

            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            return false;
        }

    }


}
