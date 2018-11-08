package com.example.lib.interfaceTest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by YangGuobin on 2018-10-13.
 */

public class Request {

    /**
     * 本地图片转换成base64字符串
     *
     * @param imgFile 图片本地路径
     * @return
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:40:46
     */
    public static String ImageToBase64(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

        InputStream in = null;
        byte[] data = null;

        // 读取图片字节数组
        try {
            File f = new File(imgFile);
            in = new FileInputStream(f);

            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BASE64Encoder().encode(data);
    }

    public static void Base64ToImage(String image64, String filePath) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

        byte[] photo = null;
        try {
            photo = new BASE64Decoder().decodeBufferToByteBuffer(image64).array();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(filePath));
            fileOutputStream.write(photo);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String get(String url) {
        InputStream is = null;
        ByteArrayOutputStream fos = null;
        HttpURLConnection conn = null;
        try {
            System.out.println("URL = " + url);
            URL url2 = new URL(url);

            conn = (HttpURLConnection) url2.openConnection();

//            conn.setRequestMethod("get");   //设置本次请求的方式 ， 默认是GET方式， 参数要求都是大写字母
            conn.setConnectTimeout(10000);//设置连接超时
            conn.setDoInput(true);//是否打开输入流,此方法默认为true
            conn.connect();//表示连接

            is = conn.getInputStream();
            fos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024 * 10];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            return new String(fos.toByteArray(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static String post(String path, String bodyJson) {
        InputStream inputStream = null;
        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        try {
            //使用HttpURLConnection获得网络数据
            URL url = new URL(path);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(urlConnection.getOutputStream());
            writer.write(bodyJson);
            writer.flush();
            writer.close();
            int code = urlConnection.getResponseCode();
            if (code == 200) {
                inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String liner;
                StringBuffer buffer = new StringBuffer();
                while ((liner = reader.readLine()) != null) {
                    buffer.append(liner);
                }
                String str = buffer.toString();
                inputStream.close();
                reader.close();
                urlConnection.disconnect();
                return str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
