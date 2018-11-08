package com.thdtek.acs.terminal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Time:2018/6/28
 * User:lizhen
 * Description:
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * 创建文件夹
     *
     * @param dir
     */
    public static void createDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            Log.d(TAG, "FileUtil createDir " + dir + " = " + mkdirs);
        } else {
            Log.d(TAG, "FileUtil createDir " + dir + " 文件已经存在");
        }
    }

    /**
     * 写入数据到文件
     *
     * @param dir      文件父路径
     * @param fileName 文件的绝对路径
     * @param data     写入的数据
     * @throws IOException
     */
    public static String write2File(File dir, String fileName, String data) throws IOException {
        if (dir == null) {
            throw new NullPointerException("FileUtil write2File dir is null");
        }
        if (TextUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("FileUtil write2File fileName is null");
        }
        return write2File(dir + "/" + fileName, data);
    }

    /**
     * 写入数据到文件
     *
     * @param fileName 文件的绝对路径
     * @param data     写入的数据
     * @throws IOException
     */
    public static String write2File(String fileName, String data) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = getFileOutputString(checkFileExist(fileName));
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        return fileName;
    }

    /**
     * 获取输出流
     *
     * @param file 输出流文件的绝对路径
     * @return
     * @throws FileNotFoundException
     */
    public static FileOutputStream getFileOutputString(String file) throws IOException {
        return new FileOutputStream(checkFileExist(file));
    }

    public static FileOutputStream getFileOutputString(File file) throws IOException {
        return new FileOutputStream(checkFileExist(file));
    }

    /**
     * 读取文件转成string类型
     *
     * @param file 读取的文件的绝对路径
     * @return
     * @throws IOException
     */
    public static String readFile2String(String file) throws IOException {
        FileInputStream fileInputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fileInputStream = getFileInputStream(checkFileExist(file));
            byte[] bytes = new byte[8 * 1024];
            int len;
            while ((len = fileInputStream.read(bytes)) != -1) {
                stringBuilder.append(new String(bytes, 0, len));
            }
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 读取文件变成bitmap
     *
     * @param file 文件的绝对路径
     * @return
     * @throws IOException
     */
    public static Bitmap readFile2Bitmap(String file) throws IOException {
        FileInputStream fileInputStream = null;
        Bitmap bitmap;
        try {
            fileInputStream = getFileInputStream(checkFileExist(file));
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return bitmap;
    }

    /**
     * 获取一个输入流
     *
     * @param file 输入流的文件的绝对路径
     * @return
     * @throws FileNotFoundException
     */
    public static FileInputStream getFileInputStream(String file) throws IOException {
        return new FileInputStream(checkFileExist(file));
    }

    public static FileInputStream getFileInputStream(File file) throws IOException {
        return new FileInputStream(checkFileExist(file));
    }

    /**
     * 删除文件
     *
     * @param file 文件的绝对路径
     */
    public static void deleteFile(String file) {
        try {
            LogUtils.d(TAG, "删除文件 path = " + file);
            checkFileExist(file).delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查文件是否存在
     *
     * @throws FileNotFoundException
     */
    public static File checkFileExist(File file) throws IOException {
        if (file == null || !file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static File checkFileExist(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


    public static String inputStreamToString(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        // StandardCharsets.UTF_8.name() > JDK 7
        return result.toString("UTF-8");
    }

    public static String fileToString(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        return inputStreamToString(inputStream);
    }

    public static void copyLicense(Context context) {
        if (Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI)) {
            FileUtil.createDir(Const.DIR_LICENSE);
            InputStream open = null;
            FileOutputStream fileOutputStream =null;
            try {
                File licensePemFile = new File(Const.DIR_LICENSE + File.separator + "license_public.x509.pem");
                if (!licensePemFile.exists()) {
                    open = context.getAssets().open("license_public.x509.pem");
                    fileOutputStream = new FileOutputStream(licensePemFile);
                    int len = 0;
                    while ((len = open.read()) != -1) {
                        fileOutputStream.write(len);
                    }
                    open.close();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (open != null) {
                    try {
                        open.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static void copyFile(File src, File dest) {

    }
}
