package com.thdtek.acs.terminal.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Time:2018/7/11
 * User:lizhen
 * Description:
 */

public class CircleView extends View {

    private Paint mPaint;
    private List<CircleBean> mCircleBeanList;
    private int number = 10;

    private int[] colors = new int[]{
            getResources().getColor(R.color.color_circle),

    };

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);
        mCircleBeanList = new ArrayList<>();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mCircleBeanList.clear();
        //生成圆圈
        for (int i = 0; i < number; i++) {
            int random = new Random().nextInt(10) - 5;
            CircleBean circleBean = new CircleBean(
                    new Point(getMeasuredWidth() / 2 + random, getMeasuredHeight() / 2 + random),
                    getMeasuredWidth() / 2 - 30, 0, 0,
                    i % 2, i % 2,
                    new Random().nextInt(15), new Random().nextInt(15),
                    new Random().nextInt(18) + 5
                    , colors[new Random().nextInt(colors.length)]
            );
            mCircleBeanList.add(circleBean);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < number; i++) {
            CircleBean circleBean = mCircleBeanList.get(i);
            mPaint.setColor(circleBean.getColor());
            canvas.drawCircle(
                    circleBean.getPoint().x + circleBean.getxDiff(),
                    circleBean.getPoint().y + circleBean.getyDiff(),
                    circleBean.getRadius(),
                    mPaint
            );
            if (circleBean.getxType() == 0) {
                //x +
                if (circleBean.getxDiff() + circleBean.getOldX() > circleBean.getDiff()) {
                    //开始 -
                    circleBean.setxType(1);
                }
                circleBean.setxDiff(circleBean.getxDiff() + 1);
            } else {
                //x -
                if (circleBean.getxDiff() - circleBean.getOldX() < -circleBean.getDiff()) {
                    //开始 +
                    circleBean.setxType(0);
                }
                circleBean.setxDiff(circleBean.getxDiff() - 2);
            }
            if (circleBean.getyType() == 0) {
                //y +
                if (circleBean.getyDiff() + circleBean.getOldY() > circleBean.getDiff()) {
                    //开始 -
                    circleBean.setyType(1);
                }
                circleBean.setyDiff(circleBean.getyDiff() + 2);
            } else {
                //y -
                if (circleBean.getyDiff() - circleBean.getOldY() < -circleBean.getDiff()) {
                    //开始 +
                    circleBean.setyType(0);
                }
                circleBean.setyDiff(circleBean.getyDiff() - 2);
            }
        }

    }

    private boolean start = true;

    public void start() {
        start = true;
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    postInvalidate();
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void stop() {
        start = false;
    }

    public static class CircleBean {

        //中心点
        private Point mPoint;
        //半径
        private int radius;
        private int oldX;
        private int oldY;
        private int xType;
        private int yType;
        //x偏移量
        private int xDiff;
        //y偏移量
        private int yDiff;
        private int diff;
        private int color;

        public CircleBean(Point point, int radius, int oldX, int oldY, int xType, int yType, int xDiff, int yDiff, int diff, int color) {
            mPoint = point;
            this.radius = radius;
            this.oldX = oldX;
            this.oldY = oldY;
            this.xType = xType;
            this.yType = yType;
            this.xDiff = xDiff;
            this.yDiff = yDiff;
            this.diff = diff;
            this.color = color;
        }

        public Point getPoint() {
            return mPoint;
        }

        public void setPoint(Point point) {
            mPoint = point;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public int getOldX() {
            return oldX;
        }

        public void setOldX(int oldX) {
            this.oldX = oldX;
        }

        public int getOldY() {
            return oldY;
        }

        public void setOldY(int oldY) {
            this.oldY = oldY;
        }

        public int getxType() {
            return xType;
        }

        public void setxType(int xType) {
            this.xType = xType;
        }

        public int getyType() {
            return yType;
        }

        public void setyType(int yType) {
            this.yType = yType;
        }

        public int getxDiff() {
            return xDiff;
        }

        public void setxDiff(int xDiff) {
            this.xDiff = xDiff;
        }

        public int getyDiff() {
            return yDiff;
        }

        public void setyDiff(int yDiff) {
            this.yDiff = yDiff;
        }

        public int getDiff() {
            return diff;
        }

        public void setDiff(int diff) {
            this.diff = diff;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

}
