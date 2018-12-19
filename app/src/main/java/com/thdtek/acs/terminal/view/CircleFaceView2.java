package com.thdtek.acs.terminal.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

/**
 * Time:2018/7/18
 * User:lizhen
 * Description:
 */

public class CircleFaceView2 extends View {

    private static final String TAG = CircleFaceView2.class.getSimpleName();
    private Paint mPaint;
    private RectF mRectFIn;
    private RectF mRectFInIN;
    private DashPathEffect mDashPathEffect;

    private boolean mLoop=true;

    public CircleFaceView2(Context context) {
        super(context);
        init();
    }

    public CircleFaceView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleFaceView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#15E9A4"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mRect = new Rect();
        mCenterPoint = new Point();
        mRectFOutOut = new RectF();
        mRectFOut = new RectF();
        mRectFIn = new RectF();
        mRectFInIN = new RectF();
        mDashPathEffect = new DashPathEffect(new float[]{3, 10}, 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mLoop) {
                    SystemClock.sleep(15);
                    postInvalidate();
                }
                LogUtils.e(TAG, "========== circleFaceView2 完结撒花 ==========");
            }
        }).start();

    }


    /**
     * 人脸矩形
     */
    private Rect mRect;
    private RectF mRectFOutOut;
    /**
     * 外圈弧形
     */
    private RectF mRectFOut;
    /**
     * 中心点
     */
    private Point mCenterPoint;
    /**
     * 最外圈的半径
     */
    private int mRadius;
    /**
     * 外圈小线的长度
     */
    private int mLineLength = 10;
    /**
     * 缩放比例
     */
    private float mCircleScale = 1;

    private int mAllPadding = 4;
    private int mLissPadding = 2;

    private int mStrokeWidth_4 = 2;
    private int mStrokeWidth_10 = 5;

    private int mOutStartAngleOne = -70;
    private int mOUtStartAngleTwo = 110;

    private int mInStartAngleOne = -70;
    private int mInStartAngleTwo = 110;
    private int mInStartAngleThree = 20;
    private int mInStartAngleFour = 200;

    private int mININStartAngleOne = 10;
    private int mININStartAngleTwo = 110;
    private int mININStartAngleThree = 210;

    private int mOutOutStartAngle = 0;

    private int mAngle = 5;


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

        if (mRect.width() == 0 || mRect.height() == 0) {
            return;
        }

        mPaint.setStrokeWidth(1);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mRadius, mPaint);

        //最外圈
        mPaint.setStrokeWidth(mStrokeWidth_10);
        mPaint.setPathEffect(mDashPathEffect);
        canvas.drawArc(mRectFOutOut, mOutOutStartAngle, 270, false, mPaint);
        mPaint.setPathEffect(null);
        mOutOutStartAngle = mOutOutStartAngle - mAngle < -360 ? 0 : mOutOutStartAngle - mAngle;

        //外圈
        mPaint.setStrokeWidth(mStrokeWidth_4);
        canvas.drawArc(mRectFOut, mOutStartAngleOne, 120, false, mPaint);
        canvas.drawArc(mRectFOut, mOUtStartAngleTwo, 120, false, mPaint);
        mOutStartAngleOne = mOutStartAngleOne + mAngle > 360 ? 0 : mOutStartAngleOne + mAngle;
        mOUtStartAngleTwo = mOUtStartAngleTwo + mAngle > 360 ? 0 : mOUtStartAngleTwo + mAngle;


        mPaint.setStrokeWidth(mStrokeWidth_4);
        //内圈
        canvas.drawArc(mRectFIn, mInStartAngleOne, 90, false, mPaint);
        canvas.drawArc(mRectFIn, mInStartAngleTwo, 90, false, mPaint);

        mPaint.setStrokeWidth(mStrokeWidth_10);
        canvas.drawArc(mRectFIn, mInStartAngleThree, 90, false, mPaint);
        canvas.drawArc(mRectFIn, mInStartAngleFour, 90, false, mPaint);

        mInStartAngleOne = mInStartAngleOne - mAngle < -360 ? 0 : mInStartAngleOne - mAngle;
        mInStartAngleTwo = mInStartAngleTwo - mAngle < -360 ? 0 : mInStartAngleTwo - mAngle;
        mInStartAngleThree = mInStartAngleThree - mAngle < -360 ? 0 : mInStartAngleThree - mAngle;
        mInStartAngleFour = mInStartAngleFour - mAngle < -360 ? 0 : mInStartAngleFour - mAngle;


        //内圈内圈
        mPaint.setStrokeWidth(mStrokeWidth_4);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mRadius - mAllPadding * 5 - mLineLength, mPaint);
        mPaint.setStrokeWidth(mStrokeWidth_10);
        canvas.drawArc(mRectFInIN, mININStartAngleOne, 20, false, mPaint);
        canvas.drawArc(mRectFInIN, mININStartAngleTwo, 20, false, mPaint);
        canvas.drawArc(mRectFInIN, mININStartAngleThree, 20, false, mPaint);

        mININStartAngleOne = mININStartAngleOne + mAngle > 360 ? 0 : mININStartAngleOne + mAngle;
        mININStartAngleTwo = mININStartAngleTwo + mAngle > 360 ? 0 : mININStartAngleTwo + mAngle;
        mININStartAngleThree = mININStartAngleThree + mAngle > 360 ? 0 : mININStartAngleThree + mAngle;
    }

    private Rect mLastRect = new Rect();

    public void setRect(Rect rect, int color) {

        mRect.set(rect);
//        System.out.println("left = " + mRect.left + " top = " + mRect.top + " right = " + mRect.right + " bottom = " + mRect.bottom);
        mPaint.setColor(getResources().getColor(R.color.color_scan));
        if (mRect.width() == 0 || mRect.height() == 0) {

        } else {
            //按比例缩放
            if (mCircleScale == 1.0) {
                mRect.left = (int) ((mRect.left - 20) * mCircleScale);
                mRect.top = (int) ((mRect.top - 20) * mCircleScale);
                mRect.right = (int) ((mRect.right + 20) * mCircleScale);
                mRect.bottom = (int) ((mRect.bottom + 20) * mCircleScale);
            } else {
                mRect.left = (int) ((mRect.left - 20) * mCircleScale + 20);
                mRect.top = (int) ((mRect.top - 20) * mCircleScale);
                mRect.right = (int) ((mRect.right + 20) * mCircleScale - 15);
                mRect.bottom = (int) ((mRect.bottom + 20) * mCircleScale);
            }

            if (Math.abs(mRect.left - mLastRect.left) < 15) {
                mRect.left = mLastRect.left;
            }
            if (Math.abs(mRect.top - mLastRect.top) < 15) {
                mRect.top = mLastRect.top;
            }
            if (Math.abs(mRect.right - mLastRect.right) < 15) {
                mRect.right = mLastRect.right;
            }
            if (Math.abs(mRect.bottom - mLastRect.bottom) < 15) {
                mRect.bottom = mLastRect.bottom;
            }
//            mLastRect.set(mRect);

            int halfWidth = mRect.width() / 2;
            int halfHeight = mRect.height() / 2;


            mCenterPoint.x = mRect.left + halfWidth;
            mCenterPoint.y = mRect.top + halfHeight;

            //最外圈
            mRectFOutOut.left = mCenterPoint.x - mRadius + mLissPadding * 2;
            mRectFOutOut.right = mCenterPoint.x + mRadius - mLissPadding * 2;

            mRectFOutOut.top = mCenterPoint.y - mRadius + mLissPadding * 2;
            mRectFOutOut.bottom = mCenterPoint.y + mRadius - mLissPadding * 2;

            //外圈
            mRectFOut.left = mCenterPoint.x - mRadius + mAllPadding + mLineLength;
            mRectFOut.right = mCenterPoint.x + mRadius - mAllPadding - mLineLength;

            mRectFOut.top = mCenterPoint.y - mRadius + mAllPadding + mLineLength;
            mRectFOut.bottom = mCenterPoint.y + mRadius - mAllPadding - mLineLength;

            //内圈
            mRectFIn.left = mCenterPoint.x - mRadius + mAllPadding * 2 + mLineLength + mLissPadding * 2;
            mRectFIn.right = mCenterPoint.x + mRadius - mAllPadding * 2 - mLineLength - mLissPadding * 2;

            mRectFIn.top = mCenterPoint.y - mRadius + mAllPadding * 2 + mLineLength + mLissPadding * 2;
            mRectFIn.bottom = mCenterPoint.y + mRadius - mAllPadding * 2 - mLineLength - mLissPadding * 2;

            //内圈内圈
            mRectFInIN.left = mCenterPoint.x - mRadius + mAllPadding * 4 + mLineLength + mLissPadding * 2;
            mRectFInIN.right = mCenterPoint.x + mRadius - mAllPadding * 4 - mLineLength - mLissPadding * 2;

            mRectFInIN.top = mCenterPoint.y - mRadius + mAllPadding * 4 + mLineLength + mLissPadding * 2;
            mRectFInIN.bottom = mCenterPoint.y + mRadius - mAllPadding * 4 - mLineLength - mLissPadding * 2;
            mRadius = (int) Math.sqrt(Math.pow(mRect.width() / 2, 2) + Math.pow(mRect.height() / 2, 2) + 20);
        }
    }

    public void stop() {
        mLoop = false;

    }
}
