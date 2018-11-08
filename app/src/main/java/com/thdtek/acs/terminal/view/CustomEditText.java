package com.thdtek.acs.terminal.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.thdtek.acs.terminal.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Time:2018/6/28
 * User:lizhen
 * Description:
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText implements View.OnFocusChangeListener {

    private Paint mPaint;
    private RectF mRect;
    private int mPadding = 12;
    private int mRadius = 0;
    private ExecutorService mExecutorService;
    private boolean drawLayer = false;

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.color_et_bg));
        mPaint.setStrokeWidth(getResources().getDimension(R.dimen.dimen_7dp));
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
        mExecutorService = Executors.newSingleThreadExecutor();
        setOnFocusChangeListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRect = new RectF(mPadding, mPadding, getMeasuredWidth() - mPadding, getMeasuredHeight() - mPadding);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (drawLayer) {
            mPaint.setShadowLayer(mRadius, 0, 0, R.color.black_40);
        } else {
            mPaint.setShadowLayer(0, 0, 0, R.color.black_40);
        }
        canvas.drawRoundRect(mRect, getResources().getDimension(R.dimen.dimen_60dp), getResources().getDimension(R.dimen.dimen_60dp), mPaint);
        super.onDraw(canvas);
    }

    public void drawLayer() {
        drawLayer = true;
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 20; i++) {
                    mRadius = i;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    postInvalidate();
                }
            }
        });
    }

    public void unDrawLayer() {
        drawLayer = false;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mExecutorService.shutdownNow();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            drawLayer();
        } else {
            unDrawLayer();
        }
    }
}
