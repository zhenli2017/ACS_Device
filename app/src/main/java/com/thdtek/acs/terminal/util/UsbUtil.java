package com.thdtek.acs.terminal.util;

import android.text.TextUtils;

import com.thdtek.facelibrary.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 * Time:2017/9/26
 * User:lizhen
 * Description:
 */

public class UsbUtil {

    private static final String TAG = UsbUtil.class.getSimpleName();

    /**
     * 使用deviceSn 进行MD5加密,长度32位
     *
     * @param path
     * @return
     */
    public static boolean checkKeyFile(String path) {
        File file = new File(path + "/" + Const.USB_KEY_FILE_NAME);
        boolean exists = file.exists();
        return exists;
//        FileInputStream fileInputStream = null;
//        if (exists) {
//            try {
//                fileInputStream = new FileInputStream(file);
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
//                String line = null;
//                StringBuilder stringBuilder = new StringBuilder();
//                while ((line = bufferedReader.readLine()) != null) {
//                    stringBuilder.append(line);
//                    stringBuilder.append("=");
//                }
////                return handleKeyFileMessage(stringBuilder.toString());
//                return true;
//            } catch (IOException e) {
//                LogUtils.e(TAG, "USB文件读取发生异常 = " + e.getMessage());
//                return false;
//            } finally {
//                if (fileInputStream != null) {
//                    try {
//                        fileInputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } else {
//            return false;
//        }
    }

    private static boolean handleKeyFileMessage(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return false;
        }
        String[] split = msg.split("=");

        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < split.length; i = i + 2) {
            if (i + 1 < split.length) {
                hashMap.put(split[i], split[i + 1]);
            } else {
                hashMap.put(split[i], "");
            }
        }
        String value = hashMap.get(DeviceSnUtil.getDeviceSn());
        if (TextUtils.isEmpty(value)) {
            return false;
        }

        return TextUtils.equals(Md5.getMd5(DeviceSnUtil.getDeviceSn()), value);
    }

    /**
     * 复制log 到usb中
     *
     * @param path
     */
    public static void copyLogToUsb(String path) {

        File dir = new File(Const.DIR_LOG);
        if (!dir.exists()) {
            LogUtils.d(TAG, " ==== log文件不存在,无法复制");
            return;
        }

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            LogUtils.d(TAG, " ==== file name = " + files[i].getName()+" path = "+path+" name = "+ files[i].getName());
            try {
                copyFile(files[i], path, files[i].getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            LogUtils.d(TAG, " ==== Copy Log Success");
        }
    }

    /**
     * 拷贝文件
     *
     * @param file_ori  原来文件
     * @param dir_dest  文件新目录
     * @param file_name 文件名
     */
    public static void copyFile(File file_ori, String dir_dest, String file_name) throws IOException {
        File dir_dest_file = new File(dir_dest, "log");
        if (!dir_dest_file.exists()) {
            boolean mkdirs = dir_dest_file.mkdirs();
        }
        File file_dest = new File(dir_dest_file, file_name);
        System.out.println("file-dest = "+file_dest.getName());
        if (!file_dest.exists()) {
            boolean newFile = file_dest.createNewFile();
            if (!newFile) {
                LogUtils.e(TAG, " ==== USB 复制log文件失败,newFile 失败");
                return;
            }
        }
        FileInputStream fileInputStream = new FileInputStream(file_ori);
        FileOutputStream fileOutputStream = new FileOutputStream(file_dest);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
    }
}
