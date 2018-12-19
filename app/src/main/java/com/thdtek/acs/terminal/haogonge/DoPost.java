package com.thdtek.acs.terminal.haogonge;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DoPost {
    private final static String TAG = DoPost.class.getSimpleName();

    public String post(String baseUrl, String body) throws IOException {
        return post(baseUrl, body, 8000, 8000);
    }

    public String post(String baseUrl, String body, int ConnTimeMs, int ReadTimeMs) throws IOException {
        // 请求的参数转换为byte数组
        byte[] postData = body.getBytes();
        // 新建一个URL对象
        URL url = new URL(baseUrl);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(ConnTimeMs);
        //设置从主机读取数据超时
        urlConn.setReadTimeout(ReadTimeMs);
        // Post请求必须设置允许输出 默认false
        urlConn.setDoOutput(true);
        //设置请求允许输入 默认是true
        urlConn.setDoInput(true);
        // Post请求不能使用缓存
        urlConn.setUseCaches(false);
        // 设置为Post请求
        urlConn.setRequestMethod("POST");
        //设置本次连接是否自动处理重定向
        urlConn.setInstanceFollowRedirects(true);
        // 配置请求Content-Type
        urlConn.setRequestProperty("Content-Type", "application/json");
        // 开始连接
        urlConn.connect();
        // 发送请求参数
        DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
        dos.write(postData);
        dos.flush();
        dos.close();
        // 判断请求是否成功
        String result = null;
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
            result = streamToString(urlConn.getInputStream());
        } else {
            Log.e(TAG, "Post方式请求失败");
        }
        // 关闭连接
        urlConn.disconnect();
        return result;
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}
