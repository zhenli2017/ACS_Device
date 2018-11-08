package com.thdtek.acs.terminal.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;


public class RectView extends View {
    private static final String TAG = RectView.class.getSimpleName();
    private Paint mPaint;
    private final int RECT_LENGTH = 30;

    private int RECT_WIDTH = 5;
    private Rect mRect;

    private Shader mShader;
    private int mDrawWidth;
    private int mDrawHeight;
    /**
     * 缩放比例
     */
    private float mCircleScale = 1;
    private int[] mColors = new int[]{
            getResources().getColor(R.color.color_start),
            getResources().getColor(R.color.color_middle),
            getResources().getColor(R.color.color_end)
    };
    private float[] mPositions = new float[]{0.0f, 0.8f, 1.0f};

    public RectView(Context context) {
        super(context);
        init();
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mRect = new Rect(0, 0, 0, 0);

        mPaint.setColor(getResources().getColor(R.color.color_scan));
        //边框的宽度
        mPaint.setStrokeWidth(1);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        mCircleScale = measuredWidth / (float) Const.CAMERA_PREVIEW_WIDTH;
        Const.CAMERA_SCALE_NUMBER = mCircleScale;
        Const.CAMERA_MIN_LEFT = (measuredWidth - getResources().getDisplayMetrics().widthPixels) / 2;
        Const.CAMERA_MAX_RIGHT = Const.CAMERA_MIN_LEFT + getResources().getDisplayMetrics().widthPixels;

        LogUtils.d(TAG, "==== measuredWidth = " + measuredWidth + " scaleNumber = " + mCircleScale + " left = " + Const.CAMERA_MIN_LEFT + " right = " + Const.CAMERA_MAX_RIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //没有找到人脸
        if (mRect.width() == 0 || mRect.height() == 0) {
            return;
        }


//        System.out.println("left = " + mRect.left + " right = " + mRect.right + " top = " + mRectTop + " bottom = " + mRect.bottom);
        //左上角
        canvas.drawRect(mRectLeft, mRectTop - RECT_WIDTH, mRectLeft + RECT_LENGTH, mRectTop, mPaint);
        canvas.drawRect(mRectLeft - RECT_WIDTH, mRectTop - RECT_WIDTH, mRectLeft, mRectTop + RECT_LENGTH, mPaint);
        //右上角
        canvas.drawRect(mRectRight - RECT_LENGTH, mRectTop - RECT_WIDTH, mRectRight, mRectTop, mPaint);
        canvas.drawRect(mRectRight, mRectTop - RECT_WIDTH, mRectRight + RECT_WIDTH, mRectTop + RECT_LENGTH, mPaint);
        //左下角
        canvas.drawRect(mRectLeft, mRectBottom, mRectLeft + RECT_LENGTH, mRectBottom + RECT_WIDTH, mPaint);
        canvas.drawRect(mRectLeft - RECT_WIDTH, mRectBottom - RECT_LENGTH, mRectLeft, mRectBottom + RECT_WIDTH, mPaint);
        //右下角
        canvas.drawRect(mRectRight - RECT_LENGTH, mRectBottom, mRectRight + RECT_WIDTH, mRectBottom + RECT_WIDTH, mPaint);
        canvas.drawRect(mRectRight, mRectBottom - RECT_LENGTH, mRectRight + RECT_WIDTH, mRectBottom, mPaint);


//        if (mLineCurrentHeight >= mDrawHeight) {
//            if (mLineAlpha > 80) {
//                mLineAlpha = mLineAlpha - 15;
//            } else {
//                mLineCurrentHeight = 1;
//                mLineAlpha = 255;
//            }
//            mPaint.setAlpha(mLineAlpha);
//        } else {
//            mLineCurrentHeight = mLineCurrentHeight + RECT_WIDTH;
//        }
//        mShader = new LinearGradient(
//                0, mRect.top, 0, mRect.top + mLineCurrentHeight,
//                mColors,
//                mPositions,
//                Shader.TileMode.CLAMP);
//
//        mPaint.setShader(mShader);

//        System.out.println("mRect.top = " + mRect.top + " mLineCurrentHeight = " + mLineCurrentHeight);
        //竖线
//        for (int i = 0; i < mDrawWidth - RECT_WIDTH; i = i + RECT_WIDTH) {
//            canvas.drawLine(mRect.left + i + RECT_WIDTH, mRect.top, mRect.left + i + RECT_WIDTH, mRect.top + mLineCurrentHeight, mPaint);
//        }
//        canvas.drawLine(mRect.left, mRect.top, mRect.left, mRect.top + mLineCurrentHeight, mPaint);
//        //横线
//        for (int i = 0; i < mLineCurrentHeight - RECT_WIDTH; i = i + RECT_WIDTH) {
//            canvas.drawLine(mRect.left, mRect.top + i + RECT_WIDTH, mRect.right, mRect.top + i + RECT_WIDTH, mPaint);
//        }
//        canvas.drawLine(mRect.left, mRect.top + mLineCurrentHeight, mRect.right, mRect.top + mLineCurrentHeight, mPaint);

    }

    private int mLineCurrentHeight = 1;
    private int mLineAlpha = 255;

    private int mRectLeft;
    private int mRectRight;
    private int mRectTop;
    private int mRectBottom;

    public void setRect(Rect rect, int color) {

        mRect = rect;

        //按比例缩放坐标
        mRectLeft = (int) ((mRect.left - 15) * mCircleScale);
        mRectTop = (int) ((mRect.top - 15) * mCircleScale);
        mRectRight = (int) ((mRect.right + 15) * mCircleScale);
        mRectBottom = (int) ((mRect.bottom + 15) * mCircleScale);
        //获取缩放后的宽高
        mDrawWidth = mRect.width();
        mDrawHeight = mRect.height();
        postInvalidate();
    }

    public void resetScan() {
        mLineCurrentHeight = 1;
    }
}
