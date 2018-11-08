package com.thdtek.acs.terminal.ui.main;

import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseCameraActivity;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.receiver.DownLoadReceiver;
import com.thdtek.acs.terminal.receiver.TimeReceiver;
import com.thdtek.acs.terminal.receiver.WeatherReceiver;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.view.CircleFaceView2;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity2  {
//
//    //根据人脸位置画框
//    private CircleFaceView2 mDrawRectView;
//    private Chronometer mTvTimeHour;
//    private TextView mTvTimeDate;
//    private TextView mTvTimeWeek;
//    private TextView mTvName;
//    private CircleImageView mCircleImageView;
//    private TextView mTvWorkId;
//    private TextView mTvC;
//    private ImageView mIvWeather;
//    private View mViewConnect;
//    private TextView mTvTitle;
//    private TextView mTvSuccess;
//    private TextView mTvOther;
//    private TextView mTvDownLoadPerson;
//    private ProgressBar mProgressBar;
//
//    @Override
//    public String getTAG() {
//        return MainActivity2.class.getSimpleName();
//    }
//
//
//    @Override
//    public int getLayout() {
//        return R.layout.activity_main2;
//    }
//
//    @Override
//    public void init() {
//
//    }
//
//    @Override
//    public void initView() {
//
//        //连接的小红点
//        mViewConnect = findViewById(R.id.view_connect);
//        mViewConnect.setSelected(false);
//        //画人脸的圈
//        mDrawRectView = findViewById(R.id.drawRectView);
////        mCustomSurfaceView = findViewById(R.id.customSurfaceView);
//
//        //添加摄像头的layout
//        mFrameLayout = findViewById(R.id.frameLayout);
//        //时分秒
//        mTvTimeHour = findViewById(R.id.tv_time_hour);
//        //年月日
//        mTvTimeDate = findViewById(R.id.tv_time_date);
//        //天气温度
//        mTvC = findViewById(R.id.tv_c);
//        //天气图片
//        mIvWeather = findViewById(R.id.image_weather);
//        //星期
//        mTvTimeWeek = findViewById(R.id.tv_time_week);
//        //人员名称
//        mTvName = findViewById(R.id.tv_name);
//        //人员头像
//        mCircleImageView = findViewById(R.id.circleImageView);
//        //人员id
//        mTvWorkId = findViewById(R.id.tv_work_id);
//        //左上角的标题
//        mTvTitle = findViewById(R.id.tv_title);
//        mTvTitle.setText(SPUtils.get(MyApplication.getContext(), Const.MAIN_TITLE, getResources().getString(R.string.title)) + "");
//
//        //成功的信息
//        mTvSuccess = findViewById(R.id.tv_success);
//        //其他信息
//        mTvOther = findViewById(R.id.tv_other);
//        //下载进度
//        mTvDownLoadPerson = findViewById(R.id.tv_down_load_person);
//        mProgressBar = findViewById(R.id.pb);
//
//        mTvTimeHour.setText(String.format(Locale.getDefault(), "%tT", System.currentTimeMillis()));
//        mTvTimeDate.setText(String.format(Locale.getDefault(), "%tm/%<td", System.currentTimeMillis()));
//        mTvTimeWeek.setText(String.format(Locale.getDefault(), "   %tA", System.currentTimeMillis()));
//        mTvTimeHour.start();
//        mTvTimeHour.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer chronometer) {
//                mTvTimeHour.setText(String.format(Locale.getDefault(), "%tT", System.currentTimeMillis()));
//            }
//        });
//
//        mTvTimeDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//        mTvTimeHour.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//        mTvTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        //清空glide缓存
//        ThreadPool.getThread().execute(new Runnable() {
//            @Override
//            public void run() {
//                Glide.get(MyApplication.getContext()).clearDiskCache();
//            }
//        });
//    }
//
//    @Override
//    public void handleOneMinuteNoPeople(Message message) {
//
//    }
//
//
//    @Override
//    public TimeReceiver getTimeReceiver() {
//        return new TimeReceiver(mTvTimeHour, mTvTimeDate, mTvTimeWeek);
//    }
//
//    @Override
//    public WeatherReceiver getWeatherReceiver() {
//        return new WeatherReceiver(mTvC, mIvWeather, mViewConnect);
//    }
//
//    @Override
//    public DownLoadReceiver getDownLoadReceiver() {
//        return new DownLoadReceiver(mProgressBar);
//    }
//
//
//    @Override
//    public void initCameraSurfaceView(SurfaceView surfaceView) {
//        mFrameLayout.addView(surfaceView);
//    }
//
//    @Override
//    public boolean getSuccessViewVisible() {
//        return mCircleImageView.getVisibility() == View.VISIBLE;}
//
//    @Override
//    public void handleDownLoadPersonStart(Message message) {
//        mTvDownLoadPerson.setVisibility(View.VISIBLE);
//        mTvDownLoadPerson.setText(getResources().getString(R.string.down_load_person));
//
//    }
//
//    @Override
//    public void handleDownLoadPersonEnd(Message message) {
//        mTvDownLoadPerson.setVisibility(View.VISIBLE);
//        mTvDownLoadPerson.setText(getResources().getString(R.string.down_load_person_end));
//
//    }
//
//    @Override
//    public void handleDownLoadPersonFinish() {
//        mTvDownLoadPerson.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void handleNotFindFaceUI(Object rect, int color) {
//        mDrawRectView.setRect(mRect, color);
//    }
//
//    @Override
//    public void handleFindFaceUI(Object rect, int color) {
//        mDrawRectView.setRect(mRect, color);
//    }
//
//    @Override
//    public void handleFacePairReadyUI() {
//
//    }
//
//    @Override
//    public void handleFacePairingUI() {
//        mTvOther.setText(R.string.pairing);
//        mTvOther.setVisibility(View.VISIBLE);
//        showOrHideSuccess(View.INVISIBLE);
//    }
//
//    @Override
//    public void handleFacePairSuccessUI(Message message, PersonBean personBean) {
//        //设置成功数据
//        mTvName.setText(getString(R.string.person_name) + personBean.getName());
//        mTvWorkId.setText(getString(R.string.worker_number) + personBean.getEmployee_card_id());
//        Glide.with(this).load(personBean.getFacePic()).error(R.mipmap.ic_photo_default).into(mCircleImageView);
//        mTvOther.setVisibility(View.INVISIBLE);
//        //隐藏和显示view
//        showOrHideSuccess(View.VISIBLE);
//    }
//
//    @Override
//    public void handleFacePairFailUI(String msg) {
//        showOrHideSuccess(View.INVISIBLE);
//        mTvOther.setText(R.string.pair_fail);
//        mTvOther.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void handlePairFailNoVisibleUI() {
//
//    }
//
//    @Override
//    public void handleThisPairOverUI() {
//        showOrHideSuccess(View.INVISIBLE);
//        mTvOther.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void handleNotAliveUI(String msg) {
//
//    }
//
//    private void showOrHideSuccess(int visible) {
//        mTvName.setVisibility(visible);
//        mTvSuccess.setVisibility(visible);
//        mCircleImageView.setVisibility(visible);
//        mTvWorkId.setVisibility(visible);
//    }

}
