package com.thdtek.acs.terminal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.intellif.FaceRect;
import com.thdtek.acs.terminal.util.camera.Bmp2YUV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Time:2018/6/20
 * User:lizhen
 * Description:
 */

public class BitmapUtil {

    private static final String TAG = BitmapUtil.class.getSimpleName();

    /**
     * 摄像头数据转成jpeg类型的bitmap
     *
     * @param bytes
     * @return bitmap
     */
    public static Bitmap getCameraBitmap(byte[] bytes) {
        LogUtils.d(TAG, "========== 开始照相机数据转成bitmap ==========");
        YuvImage yuvimage = new YuvImage(bytes, ImageFormat.NV21, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT), 100, baos);
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inPreferredConfig = Bitmap.Config.RGB_565;
        byte[] jdata = baos.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bfo);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * BGR 格式的数据转成 RGB 格式数据
     * 注意:仅仅适用于保存图片,不能使用此方法转数据后到云天励飞算法去计算人脸或特征值等内容,会无法获取信息
     * @param data
     * @param width
     * @param height
     * @return
     */
    public static Bitmap bgr2Bitmap(byte[] data, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int row = height - 1, col = width - 1;
        for (int i = data.length - 1; i >= 3; i -= 3) {
            int color = data[i - 2] & 0xFF;
            color += (data[i - 1] << 8) & 0xFF00;
            color += ((data[i]) << 16) & 0xFF0000;
            bmp.setPixel(col--, row, color);
            if (col < 0) {
                col = width - 1;
                row--;
            }
        }
        return bmp;
    }

    /**
     * 获取图片的宽高
     * @param bytes
     * @return
     */
    public static int[] getBitmapWidthAndHeight(byte[] bytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * 使用Matrix将Bitmap压缩到指定大小
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);

    }

    public static Bitmap drawBitmap(FaceRect faceRect, Bitmap previewBitmap) {
        if (previewBitmap == null) {
            return null;
        } else {
            int w = previewBitmap.getWidth();
            int h = previewBitmap.getHeight();
            Bitmap newb = previewBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas cv = new Canvas(newb);
            Paint paint = new Paint();
            paint.setColor(-65536);
            paint.setTextSize(30.0F);
            paint.setStrokeWidth(4.0F);
            paint.setColor(-16711936);
            paint.setTextSize(32.0F);
            paint.setStyle(Paint.Style.STROKE);
            cv.drawRect((float) faceRect.dRectLeft, (float) faceRect.dRectTop, (float) faceRect.dRectRight, (float) faceRect.dRectBottom, paint);
            cv.save();
            cv.restore();
            return newb;
        }
    }

    /**
     * 旋转Image
     * @param srcImage
     * @param angle
     * @return
     */
    public static Bitmap rotateImage(Bitmap srcImage, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        return Bitmap.createBitmap(srcImage, 0, 0, srcImage.getWidth(), srcImage.getHeight(), matrix, true);
    }

    /**
     * 获取指定矩形的图片
     * @param jpegImage
     * @param faceRect
     * @return
     */
    public static Bitmap getFaceCutImage(byte[] jpegImage, Rect faceRect) {
        if (jpegImage == null) {
            return null;
        } else {
            Bitmap imageBmp = BitmapFactory.decodeStream(new ByteArrayInputStream(jpegImage));
            return cutBitmap(imageBmp, faceRect);
        }
    }

    public static Bitmap FaceCutImage(Bitmap jpegImage, FaceRect faceRect) {
        return Bitmap.createBitmap(jpegImage, faceRect.dRectLeft, faceRect.dRectTop, jpegImage.getWidth(), jpegImage.getHeight(), (Matrix) null, false);
    }

    /**
     * 获取指定矩形的图片
     * @param YUVImage 摄像头数据
     * @param width
     * @param height
     * @param faceRect
     * @return
     */
    private Bitmap getFaceCutImage(byte[] YUVImage, int width, int height, Rect faceRect) {
        if (YUVImage != null && faceRect != null) {
            YuvImage image = new YuvImage(YUVImage, 17, width, height, (int[]) null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
            Bitmap imageBmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

            try {
                stream.close();
            } catch (IOException var9) {
                var9.printStackTrace();
            }

            return cutBitmap(imageBmp, faceRect);
        } else {
            return null;
        }
    }

    public static Bitmap cutBitmap(Bitmap imageBmp, Rect faceRect) {
        if (imageBmp != null && faceRect != null) {
            int left = faceRect.left;
            int top = faceRect.top;
            int right = faceRect.right;
            int bottom = faceRect.bottom;
            int startLeft = left - (int) ((double) (right - left) * 0.3D);
            int startTop = top - (int) ((double) (bottom - top) * 0.3D);
            int width = (int) ((double) (right - left) * 1.6D);
            int height = (int) ((double) (bottom - top) * 1.6D);
            startLeft = startLeft < 0 ? 0 : startLeft;
            startTop = startTop < 0 ? 0 : startTop;
            width = width + startLeft > imageBmp.getWidth() ? imageBmp.getWidth() - startLeft : width;
            height = height + startTop > imageBmp.getHeight() ? imageBmp.getHeight() - startTop : height;
            return Bitmap.createBitmap(imageBmp, startLeft, startTop, width, height);
        } else {
            return null;
        }
    }

    public static Bitmap getBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return bitmap;
    }

    public static byte[] getJpegData(Context context, Uri uri) {
        Bitmap bitmap = null;
        bitmap = getBitmap(context, uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }


    public static byte[] convertColorToByte(int[] color) {
        if (color == null) {
            return null;
        } else {
            byte[] data = new byte[color.length * 3];

            for (int i = 0; i < color.length; ++i) {
                data[i * 3] = (byte) (color[i] >> 8 & 255);
                data[i * 3 + 1] = (byte) (color[i] >> 16 & 255);
                data[i * 3 + 2] = (byte) (color[i] >> 24 & 255);
            }

            return data;
        }
    }
    public static byte[] bitmap2Byte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] bitmap2RGB(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] rgba = buffer.array();
        byte[] pixels = new byte[rgba.length / 4 * 3];
        int count = rgba.length / 4;

        for (int i = 0; i < count; ++i) {
            pixels[i * 3] = rgba[i * 4];
            pixels[i * 3 + 1] = rgba[i * 4 + 1];
            pixels[i * 3 + 2] = rgba[i * 4 + 2];
        }

        return pixels;
    }

    public static byte[] getPixelsBGR(Bitmap image) {
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        image.copyPixelsToBuffer(buffer);
        byte[] temp = buffer.array();
        byte[] pixels = new byte[temp.length / 4 * 3];

        for (int i = 0; i < temp.length / 4; ++i) {
            pixels[i * 3] = temp[i * 4 + 2];
            pixels[i * 3 + 1] = temp[i * 4 + 1];
            pixels[i * 3 + 2] = temp[i * 4];
        }

        return pixels;
    }

    public static Bitmap getFull640Bitmap(Bitmap bitmap) {
        Bitmap backBitmap = null;
        if (bitmap.getWidth() < Const.CAMERA_BITMAP_WIDTH || bitmap.getHeight() < Const.CAMERA_BITMAP_HEIGHT) {
            LogUtils.d(TAG, "===========开始补白===========");
            backBitmap = Bitmap.createBitmap(Const.CAMERA_BITMAP_WIDTH, Const.CAMERA_BITMAP_HEIGHT, Bitmap.Config.RGB_565);
            backBitmap.eraseColor(Color.BLUE);
            Paint paint = new Paint();
            Canvas canvas = new Canvas(backBitmap);
            canvas.drawBitmap(bitmap,
                    Const.CAMERA_BITMAP_WIDTH / 2 - bitmap.getWidth() / 2,
                    Const.CAMERA_BITMAP_HEIGHT / 2 - bitmap.getHeight() / 2,
                    paint);

        } else if (bitmap.getWidth() != Const.CAMERA_BITMAP_WIDTH || bitmap.getHeight() != Const.CAMERA_BITMAP_HEIGHT) {
            LogUtils.d(TAG, "===========开始拉伸===========");
            backBitmap = Bmp2YUV.zoomImg(bitmap, Const.CAMERA_BITMAP_WIDTH, Const.CAMERA_BITMAP_HEIGHT);
        } else {
            backBitmap = bitmap;
            LogUtils.d(TAG, "===========图片是" + backBitmap.getWidth() + "x" + backBitmap.getHeight() + "不做操作===========");
        }
        return backBitmap;
    }

    public static Bitmap getFullBitmap(Bitmap bitmap, int width, int height) {
        Bitmap backBitmap = null;
        if (bitmap.getWidth() < width || bitmap.getHeight() < height) {
            LogUtils.d(TAG, "===========开始补白===========");
            backBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            backBitmap.eraseColor(Color.BLUE);
            Paint paint = new Paint();
            Canvas canvas = new Canvas(backBitmap);
            canvas.drawBitmap(bitmap,
                    width / 2 - bitmap.getWidth() / 2,
                    height / 2 - bitmap.getHeight() / 2,
                    paint);

        } else if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
            LogUtils.d(TAG, "===========开始拉伸===========");
            backBitmap = Bmp2YUV.zoomImg(bitmap, width, height);
        } else {
            backBitmap = bitmap;
            LogUtils.d(TAG, "===========图片是" + backBitmap.getWidth() + "x" + backBitmap.getHeight() + "不做操作===========");
        }
        return backBitmap;
    }
}
