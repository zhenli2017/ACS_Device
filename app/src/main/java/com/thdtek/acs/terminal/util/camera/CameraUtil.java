package com.thdtek.acs.terminal.util.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;

import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.BitmapUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Time:2018/6/20
 * User:lizhen
 * Description:
 */

public class CameraUtil {
    private static final String TAG = CameraUtil.class.getSimpleName();


    /**
     * 锁定找到脸
     */
    public static boolean FIND_FACE_LOCK = false;
    /**
     * 锁定判断活体
     */
    public static boolean LIVE_FACE_LOCK = false;
    /**
     * 检测的重复次数
     */
    public static int CHECK_LIVE_COUNT = 1;
    /**
     * 查找脸成功,默认是绿色,检测失败是红色
     */
    public static boolean PAIR_FACE_SUCCESS_COLOR = true;

    /**
     * 重置摄像头检测变量
     */
    public static void resetCameraVariable(boolean reset) {
        if (reset) {
            FIND_FACE_LOCK = false;
            LIVE_FACE_LOCK = false;
            CHECK_LIVE_COUNT = 1;
            PAIR_FACE_SUCCESS_COLOR = true;
        }
    }

    /**
     * 获取照相机 预览 和 相片 的尺寸
     *
     * @param camera
     */
    public static void parameters(Camera camera) {
        List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size size;
        for (int i = 0; i < pictureSizes.size(); i++) {
            size = pictureSizes.get(i);
            LogUtils.d(TAG, "pictureSize = " + size.width + " x " + size.height);
        }
        for (int i = 0; i < previewSizes.size(); i++) {
            size = previewSizes.get(i);
            LogUtils.d(TAG, "previewSize = " + size.width + " x " + size.height);
        }
    }


    /**
     * 获取默认的出入记录截图存储路径
     *
     * @return
     */
    public static String getDefaultRecordImageDirPath() {
        return Const.DIR_IMAGE_RECORD;
    }

    /**
     * 获取默认的员工正面照保存路径
     */
    public static String getDefaultEmployeeImageDirPath() {
        return Const.DIR_IMAGE_EMPLOYEE;
    }

    /**
     * 获取默认存放身份证照片的路径
     */
    public static String getDefaultIDImageDirPath() {
        return Const.DIR_ID_IMAGE;
    }

    public static String getDefaultTempImageDirPath() {
        return Const.DIR_IMAGE_TEMP;
    }

    public static int IMAGE_MARGIN_WIDTH = 50;

    public static String save2Record(Bitmap bitmap, String fileName, Rect rect) throws IOException {
        File dir = new File(getDefaultRecordImageDirPath());
        AppSettingUtil.deleteImageOver1000(dir);
        Bitmap newBitmap = null;
        if (rect != null && rect.width() != 0 && rect.height() != 0) {
            rect.left = rect.left - IMAGE_MARGIN_WIDTH;
            if (rect.left <= 0) {
                rect.left = 0;
            }
            rect.top = rect.top - IMAGE_MARGIN_WIDTH;
            if (rect.top <= 0) {
                rect.top = 0;
            }
            rect.right = rect.right + IMAGE_MARGIN_WIDTH;
            if (rect.right >= Const.CAMERA_PREVIEW_WIDTH) {
                rect.right = Const.CAMERA_PREVIEW_WIDTH;
            }
            rect.bottom = rect.bottom + IMAGE_MARGIN_WIDTH;
            if (rect.bottom >= Const.CAMERA_PREVIEW_HEIGHT) {
                rect.bottom = Const.CAMERA_PREVIEW_HEIGHT;
            }
            newBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
        }
        if (newBitmap != null) {
            return saveImage(dir, fileName, newBitmap, Bitmap.CompressFormat.JPEG);
        } else {
            return saveImage(dir, fileName, bitmap, Bitmap.CompressFormat.JPEG);
        }
    }

    public static String save2Record(byte[] data, String fileName, Rect rect, boolean cameraData) throws IOException {
        if (cameraData) {
            if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK)) {
                return save2Record(BitmapUtil.bgr2Bitmap(data, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT), fileName, rect);
            } else {
                return save2Record(BitmapUtil.getCameraBitmap(data), fileName, rect);
            }
        } else {
            return save2Record(BitmapFactory.decodeByteArray(data, 0, data.length), fileName, rect);
        }
    }

    public static String save2Person(byte[] data, String fileName) throws IOException {
        File dir = new File(getDefaultEmployeeImageDirPath());
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (bitmap == null) {
            return "";
        }
        return saveImage(dir, fileName, bitmap, Bitmap.CompressFormat.JPEG);
    }

    public static String save2Temp(byte[] data, String fileName, Bitmap.CompressFormat format, Rect rect) throws IOException {
        File dir = new File(getDefaultTempImageDirPath());
        AppSettingUtil.deleteImageOver1000(new File(getDefaultTempImageDirPath()), 1000);
        Bitmap bitmap = null;
        if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK)) {
            bitmap = BitmapUtil.bgr2Bitmap(data, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
        } else {
            try {
                bitmap = BitmapUtil.getCameraBitmap(data);
            } catch (Exception e) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
        }
        Bitmap newBitmap = null;
        int width = IMAGE_MARGIN_WIDTH;
        if (rect != null && rect.width() != 0 && rect.height() != 0) {
            rect.left = rect.left - width;
            if (rect.left <= 0) {
                rect.left = 0;
            }
            rect.top = rect.top - width;
            if (rect.top <= 0) {
                rect.top = 0;
            }
            rect.right = rect.right + width;
            if (rect.right >= Const.CAMERA_BITMAP_WIDTH) {
                rect.right = Const.CAMERA_BITMAP_WIDTH;
            }
            rect.bottom = rect.bottom + width;
            if (rect.bottom >= Const.CAMERA_BITMAP_HEIGHT) {
                rect.bottom = Const.CAMERA_BITMAP_HEIGHT;
            }
            newBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
        }
        if (newBitmap != null) {
            return saveImage(dir, fileName, newBitmap, format);
        } else {
            return saveImage(dir, fileName, bitmap, format);
        }
    }

    public static String save2Temp(Bitmap bitmap, String fileName, Bitmap.CompressFormat format, Rect rect) throws IOException {
        File dir = new File(getDefaultTempImageDirPath());
//        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        Bitmap newBitmap = null;
        int width = IMAGE_MARGIN_WIDTH;
        if (rect != null && rect.width() != 0 && rect.height() != 0) {
            rect.left = rect.left - width;
            if (rect.left <= 0) {
                rect.left = 0;
            }
            rect.top = rect.top - width;
            if (rect.top <= 0) {
                rect.top = 0;
            }
            rect.right = rect.right + width;
            if (rect.right >= Const.CAMERA_BITMAP_WIDTH) {
                rect.right = Const.CAMERA_BITMAP_WIDTH;
            }
            rect.bottom = rect.bottom + width;
            if (rect.bottom >= Const.CAMERA_BITMAP_HEIGHT) {
                rect.bottom = Const.CAMERA_BITMAP_HEIGHT;
            }
            newBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
        }
        if (newBitmap != null) {
            return saveImage(dir, fileName, newBitmap, format);
        } else {
            return saveImage(dir, fileName, bitmap, format);
        }
    }

    public static String save2Person(Bitmap bitmap, String fileName) throws IOException {
        File dir = new File(getDefaultEmployeeImageDirPath());
        return saveImage(dir, fileName, bitmap, Bitmap.CompressFormat.JPEG);
    }

    public static String save2IDImage(Bitmap bitmap, String fileName) throws IOException {
        File dir = new File(getDefaultIDImageDirPath());
        AppSettingUtil.deleteImageDir(dir);
        return saveImage(dir, fileName, bitmap, Bitmap.CompressFormat.JPEG);
    }

    public static void deleteIDImage() {
        File dir = new File(getDefaultIDImageDirPath());
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    /**
     * 保存检测成功后截取的图片
     *
     * @return 图片的绝对路径
     */
    public static String saveImage(File dir, String fileName, Bitmap bitmap, Bitmap.CompressFormat format) throws IOException {

        File file = new File(dir, fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = FileUtil.getFileOutputString(file);
            bitmap.compress(format, 100, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        return file.getAbsolutePath();
    }

}
