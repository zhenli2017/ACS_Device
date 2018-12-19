package com.thdtek.acs.terminal.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hwit.HwitManager;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseCameraActivity;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.ADBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.bean.VideoBean;
import com.thdtek.acs.terminal.receiver.DownLoadReceiver;
import com.thdtek.acs.terminal.receiver.TimeReceiver;
import com.thdtek.acs.terminal.receiver.WeatherReceiver;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DownLoadVideo;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.Md5;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.view.RectView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thdtek.acs.terminal.util.Const.DIR_VIDEO;

/**
 * Time:2018/10/10
 * User:lizhen
 * Description:
 */

public class MainActivity3 extends BaseCameraActivity {

    private static final int REQUEST_CAMERA_PHOTO_ACTIVITY = 0;
    private FrameLayout mFrameLayout;
    private RectView mCircleFaceView2;
    private ViewPager mViewPagerOne;
    private ViewPager mViewPagerTwo;
    private VideoView mVideoView;
    private TextView mTvTitle;
    private Chronometer mTvTime;
    private TextView mTvName;
    private TextView mTvState;
    private CircleImageView mCircleImageView;
    private ImageView mIvTempImage;
    private TextView mTvDownVideo;
    private TextView mTvDownPerson;
    private ProgressBar mProgressBar;
    private VideoReceiver mVideoReceiver;

    private Handler mHandlerViewPager;

    private static final int HANDLER_VIEW_PAGER_ONE = 0;
    private static final int HANDLER_VIEW_PAGER_TWO = 1;
    private int HANDLER_ONE_DELAY_TIME = 1000;
    private int HANDLER_TWO_DELAY_TIME = 1000;

    private int mCurrentVideoIndex = 0;
    private int mVideoFailCount = 0;
    private ImageView mIvLoading;


    @Override
    public int getLayout() {
        return R.layout.activity_main3;
    }

    @Override
    public void init() {
    }

    @Override
    public void initView() {
        mFrameLayout = findViewById(R.id.frameLayout);
        mCircleFaceView2 = findViewById(R.id.circleFaceView);
        mViewPagerOne = findViewById(R.id.viewPagerOne);
        mViewPagerTwo = findViewById(R.id.viewPagerTwo);
        mVideoView = findViewById(R.id.videoView);
        mIvTempImage = findViewById(R.id.iv_temp_image);
        mTvDownVideo = findViewById(R.id.tv_down_video);
        mTvDownPerson = findViewById(R.id.tv_down_load_person);
        mProgressBar = findViewById(R.id.pb);
        mTvTime = findViewById(R.id.tv_time);
        ImageView ivBg = findViewById(R.id.iv_bg);
        Glide.with(this).load(R.mipmap.ic_main_three_bg).into(ivBg);
        mIvLoading = findViewById(R.id.iv_loading);
        Glide.with(this).load(R.mipmap.ic_loading_main_three).into(mIvLoading);
        mTvTime.setText(String.format(Locale.getDefault(), "%tT", System.currentTimeMillis()));
        mTvTime.start();
        mTvTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                mTvTime.setText(String.format(Locale.getDefault(), "%tT", System.currentTimeMillis()));
            }
        });

        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText(SPUtils.get(MyApplication.getContext(), Const.MAIN_TITLE, getResources().getString(R.string.title)) + "");
        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cpuTemp = HwitManager.HwitGetCpuTemp();
                System.out.println("cpuTemp = " + cpuTemp);
            }
        });
        mTvName = findViewById(R.id.tv_name);
        mTvState = findViewById(R.id.tv_state);
        mCircleImageView = findViewById(R.id.circleImageView);

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startVideo();
            }
        });
        mHandlerViewPager = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_VIEW_PAGER_ONE:
                        handleViewPagerOne();
                        break;
                    case HANDLER_VIEW_PAGER_TWO:
                        handleViewPagerTwo();
                        break;
                    case Const.HANDLER_DOWN_LOAD_VIDEO:
                        DownLoadVideo.getInstance().downLoadVideo(MyApplication.getContext());
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        startVideo();
        mHandlerViewPager.sendEmptyMessageDelayed(Const.HANDLER_DOWN_LOAD_VIDEO, Const.HANDLER_DELAY_TIME_3000);
        mVideoReceiver = new VideoReceiver();
        IntentFilter intentFilter = new IntentFilter(Const.DOWN_LOAD_VIDEO_RECEIVER);
        LocalBroadcastManager.getInstance(this).registerReceiver(mVideoReceiver, intentFilter);
        //开始清除小的video文件
        DownLoadVideo.getInstance().clearMinSizeFile();
        //初始化轮播数据
        String adMessage = (String) SPUtils.get(MyApplication.getContext(), Const.DOWN_LOAD_SP_AD_MESSAGE, "");
        System.out.println("===================== ADBean 缓存信息 = " + adMessage);
        if (!TextUtils.isEmpty(adMessage)) {
            ADBean adBean = new Gson().fromJson(adMessage, ADBean.class);
            LogUtils.d(TAG, "========== handleADMessage 开机读取广告数据 ==========");
            handleADMessage(adBean, false);
        } else {
            List<String> imageOneList = new ArrayList<>();
            List<String> imageTwoList = new ArrayList<>();
            imageOneList.add("1");
            imageTwoList.add("ic_ad_default_one");
            imageTwoList.add("ic_ad_default_two");
            imageTwoList.add("ic_ad_default_three");
            imageTwoList.add("ic_ad_default_four");
            MainViewPagerAdapter mainViewPagerAdapterOne = new MainViewPagerAdapter(this, imageOneList);
            mViewPagerOne.setAdapter(mainViewPagerAdapterOne);

            MainViewPagerAdapter mainViewPagerAdapterTwo = new MainViewPagerAdapter(this, imageTwoList);
            mViewPagerTwo.setAdapter(mainViewPagerAdapterTwo);
        }

    }

    private void handleViewPagerOne() {
        mViewPagerOne.setCurrentItem(mViewPagerOne.getCurrentItem() + 1);
        mHandlerViewPager.removeMessages(HANDLER_VIEW_PAGER_ONE);
        mHandlerViewPager.sendEmptyMessageDelayed(HANDLER_VIEW_PAGER_ONE, HANDLER_ONE_DELAY_TIME);
    }

    private void handleViewPagerTwo() {
        mViewPagerTwo.setCurrentItem(mViewPagerTwo.getCurrentItem() + 1);
        mHandlerViewPager.removeMessages(HANDLER_VIEW_PAGER_TWO);
        mHandlerViewPager.sendEmptyMessageDelayed(HANDLER_VIEW_PAGER_TWO, HANDLER_TWO_DELAY_TIME);
    }

    @Override
    public void firstResume() {
        super.firstResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
        closeCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        if (mHandlerViewPager != null) {
            mHandlerViewPager.removeCallbacksAndMessages(null);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoReceiver);
    }

    public void startVideo() {

        String path = "";
        path = (String) SPUtils.get(this, Const.DOWN_LOAD_NEW_VIDEO_PATH);
        if (TextUtils.isEmpty(path)) {
            String firstImage = (String) SPUtils.get(this, Const.DOWN_LOAD_FIRST_IMAGE);
            if (TextUtils.isEmpty(firstImage)) {
            } else {
                Glide.with(this).load(firstImage).into(mIvTempImage);
            }
            return;
        }

        String[] split = path.split(";");
        if (split.length == 0) {
            return;
        }

        //删除已经删除的文件,由于正在播放的时候不会删除文件,所以此时检查
        File dir = new File(DIR_VIDEO);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            String existFile = "";
            for (int i = 0; i < split.length; i++) {
                existFile = existFile + ";" + split[i];
            }
            for (File file1 : files) {
                if (!existFile.contains(file1.getName()) && !file1.getName().contains("temp")) {
                    file1.delete();
                    LogUtils.d(TAG, "视频在后台已经删除,本地删除文件 -> " + file1.getName());
                }
            }
        }

        String s = split[mCurrentVideoIndex % split.length];
        mCurrentVideoIndex++;
        File file = new File(s);
        if (s.contains("_temp") || file.length() <= Const.VIDEO_MIN_SIZE || !file.exists()) {
            LogUtils.e(TAG, "视频文件有问题,不播放 = " + file.getName() + " length = " + file.length());
            if (mVideoFailCount <= Const.VIDEO_MAX_FAIL_COUNT) {
                mVideoFailCount++;
                startVideo();
            } else {
                LogUtils.e(TAG, "视频播放重复次数超过指定次数,不重复重试 = " + Const.VIDEO_MAX_FAIL_COUNT);
            }
        } else {
            mIvTempImage.setVisibility(View.INVISIBLE);
            mVideoFailCount = 0;
            LogUtils.e(TAG, "播放视频 = " + file.getName());
            mVideoView.setVideoPath(s);
            mVideoView.start();
        }
    }

    @Override
    public String getTAG() {
        return MainActivity3.class.getSimpleName();
    }

    @Override
    public void handleOneMinuteNoPeople(Message message) {

    }

    @Override
    public TimeReceiver getTimeReceiver() {
        return new TimeReceiver(mTvTime, null, null);
    }

    @Override
    public WeatherReceiver getWeatherReceiver() {
        return null;
    }

    @Override
    public DownLoadReceiver getDownLoadReceiver() {
        return new DownLoadReceiver(mProgressBar);
    }

    @Override
    public void initCameraSurfaceView(final SurfaceView surfaceView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.addView(surfaceView);
            }
        });
    }

    @Override
    public boolean getSuccessViewVisible() {
        return mCircleImageView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void handleDownLoadPersonStart(Message msg) {
        mTvDownPerson.setVisibility(View.VISIBLE);
        mTvDownPerson.setText((String) msg.obj);
    }

    @Override
    public void handleDownLoadPersonEnd(Message msg) {
        mTvDownPerson.setVisibility(View.VISIBLE);
        mTvDownPerson.setText((String) msg.obj);
    }

    @Override
    public void handleDownLoadPersonFinish() {
        mTvDownPerson.setVisibility(View.GONE);
    }

    @Override
    public void handleNotFindFaceUI(Object rect, int color) {
        mCircleFaceView2.setRect(mRect, color);
    }

    @Override
    public void handleFindFaceUI(Object rect, int color) {
        mCircleFaceView2.setRect(mRect, color);
    }

    @Override
    public void handleFacePairReadyUI() {

    }

    @Override
    public void handleFacePairingUI() {
        mTvName.setVisibility(View.INVISIBLE);
        mCircleImageView.setVisibility(View.INVISIBLE);
//        mTvState.setText(R.string.pairing);
//        mTvState.setVisibility(View.VISIBLE);
        mIvLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleFacePairSuccessUI(Message message, PersonBean personBean) {
        mTvState.setVisibility(View.INVISIBLE);
        mIvLoading.setVisibility(View.INVISIBLE);
        mTvName.setText(personBean.getName());
        Glide.with(this).load(personBean.getFacePic()).error(R.mipmap.ic_photo_default).centerCrop().into(mCircleImageView);
        mTvName.setVisibility(View.VISIBLE);
        mCircleImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleFacePairFailUI(Message message) {
        mIvLoading.setVisibility(View.INVISIBLE);
        mCircleImageView.setVisibility(View.INVISIBLE);
        mTvName.setVisibility(View.INVISIBLE);
        mTvState.setText(R.string.pair_fail);
        mTvState.setVisibility(View.VISIBLE);
    }

    @Override
    public void handlePairFailNoVisibleUI() {

    }

    @Override
    public void handleThisPairOverUI() {
        mIvLoading.setVisibility(View.INVISIBLE);
        mTvName.setVisibility(View.INVISIBLE);
        mTvState.setVisibility(View.INVISIBLE);
        mCircleImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void handleNotAliveUI(String msg) {

    }

    @Override
    public void handleNotAliveFinishUI(String msg) {

    }

    public class VideoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mVideoView == null) {
                LogUtils.e(TAG, "VideoReceiver mVideoView = null");
                return;
            }
            Bundle extras = intent.getExtras();
            if (extras == null) {
                LogUtils.e(TAG, "VideoReceiver extras = null");
                return;
            }
            String string = extras.getString(Const.DOWN_LOAD_VIDEO_STATUE);
            if (TextUtils.equals(string, Const.DOWN_LOAD_START)) {
                LogUtils.d(TAG, "开始下载视频文件");
                mTvDownVideo.setVisibility(View.VISIBLE);
            } else if (TextUtils.equals(string, Const.DOWN_LOAD_VIDEO_END)) {
                LogUtils.d(TAG, "一个视频文件下载结束");
                if (mVideoView.isPlaying()) {
                    LogUtils.d(TAG, "当前视频正在播放,不作操作");
                } else {
                    LogUtils.d(TAG, "当前视频没有播放,开始播放");
                    mVideoFailCount = 0;
                    startVideo();
                }
            } else if (TextUtils.equals(string, Const.DOWN_LOAD_VIDEO_FINISH)) {
                LogUtils.d(TAG, "所有视频文件下载结束");
                mTvDownVideo.setVisibility(View.GONE);
            } else if (TextUtils.equals(string, Const.DOWN_LOAD_AD_IMAGE)) {

                String extrasString = extras.getString(Const.DOWN_LOAD_AD_MESSAGE);

                System.out.println("============ " + extrasString + " ============");
                if (TextUtils.isEmpty(extrasString)) {
                    LogUtils.e(TAG, "广告数据为空");
                    return;
                }
                SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_SP_AD_MESSAGE, extrasString);
                ADBean adBean = new Gson().fromJson(extrasString, ADBean.class);
                System.out.println("adBean.toString() = " + adBean.toString());
                handleADMessage(adBean, true);
            }
        }

    }

    private void handleADMessage(ADBean adBean, boolean downLoadVedio) {

        List<ADBean.DataBean> data = adBean.getData();
        if (data == null || data.size() == 0) {
            LogUtils.e(TAG, "广告数据为空");
            return;
        }

        for (int i = 0; i < data.size(); i++) {
            ADBean.DataBean dataBean = data.get(i);
            int type = dataBean.getType();
            if (type == 1 && downLoadVedio) {
                //视频
                String video_urls = dataBean.getVideo_urls();
                mIvTempImage.setVisibility(View.INVISIBLE);
                Type gsonType = new TypeToken<List<VideoBean>>() {
                }.getType();
                List<VideoBean> videoBeanList = new Gson().fromJson(video_urls, gsonType);
                if (videoBeanList == null || videoBeanList.size() == 0) {
                    LogUtils.d(TAG, "========== 没有视频url");
                    if (mVideoView != null && mVideoView.isPlaying()) {
                        LogUtils.d(TAG, "==== 停止播放视频");
                        mVideoView.stopPlayback();
                    }
                    mIvTempImage.setVisibility(View.VISIBLE);
                    DownLoadVideo.getInstance().clearSDData(MyApplication.getContext());
                    DownLoadVideo.getInstance().clearAllFile();
                    continue;
                }
                //需要下载的url
                String urls = "";
                //已经存在的url video文件
                String existFileName = "";
                File dir = new File(DIR_VIDEO);
                String[] dirList = dir.list();
                String dirListName = "";
                for (int k = 0; k < dirList.length; k++) {
                    dirListName = dirListName + ";" + dirList[k];
                }
                for (int k = 0; k < videoBeanList.size(); k++) {
                    //1.对url进行md5加密获取文件名
                    String md5 = Md5.md5(videoBeanList.get(k).getVideo_url());
                    existFileName = existFileName + ";" + DIR_VIDEO + "/" + md5;
                    //2.判断当前已经有的文件是否存在这个文件
                    if (dirListName.contains(md5)) {
                        //存在这个video文件
                        LogUtils.d(TAG, "========== 这个video文件存在,不下载 -> " + md5 + " url -> " + videoBeanList.get(k).getVideo_url());
                        dirListName = dirListName.replaceAll(md5, "");
                    } else {
                        //不存在video文件
                        urls = urls + ";" + videoBeanList.get(k).getVideo_url();
                    }
                }
                DownLoadVideo.getInstance().downLoadVideo(MyApplication.getContext(), existFileName, dirListName, urls);
            } else if (type == 2) {

                //广告位1
                ArrayList<String> list = new ArrayList<>();
                String urls = dataBean.getUrls();
                String[] split = urls.split(";");
                for (int k = 0; k < split.length; k++) {
                    if (!TextUtils.isEmpty(split[k])) {
                        list.add("http://" + AppSettingUtil.getConfig().getServerIp() + ":" + split[k]);
                    }
                }
                if (list.size() == 0) {
                    list.add("1");
                    mHandlerViewPager.removeMessages(HANDLER_VIEW_PAGER_ONE);
                } else {
                    mHandlerViewPager.removeMessages(HANDLER_VIEW_PAGER_ONE);
                    HANDLER_ONE_DELAY_TIME = dataBean.getRotation_seconds() * 1000;
                    mHandlerViewPager.sendEmptyMessageDelayed(HANDLER_VIEW_PAGER_ONE, dataBean.getRotation_seconds() * 1000);
                }

                MainViewPagerAdapter mainViewPagerAdapterOne = new MainViewPagerAdapter(this, list);
                mViewPagerOne.setAdapter(mainViewPagerAdapterOne);

            } else if (type == 3) {
                //广告位2
                ArrayList<String> list = new ArrayList<>();
                String urls = dataBean.getUrls();
                String[] split = urls.split(";");
                for (int k = 0; k < split.length; k++) {
                    if (!TextUtils.isEmpty(split[k])) {
                        list.add("http://" + AppSettingUtil.getConfig().getServerIp() + ":" + split[k]);
                    }
                }
                if (list.size() == 0) {
                    list.add("ic_ad_default_one");
                    list.add("ic_ad_default_two");
                    list.add("ic_ad_default_three");
                    list.add("ic_ad_default_four");
                    mHandlerViewPager.removeMessages(HANDLER_VIEW_PAGER_TWO);
                    HANDLER_TWO_DELAY_TIME = 3 * 1000;
                    mHandlerViewPager.sendEmptyMessageDelayed(HANDLER_VIEW_PAGER_TWO, 3 * 1000);

                } else {
                    mHandlerViewPager.removeMessages(HANDLER_VIEW_PAGER_TWO);
                    HANDLER_TWO_DELAY_TIME = dataBean.getRotation_seconds() * 1000;
                    mHandlerViewPager.sendEmptyMessageDelayed(HANDLER_VIEW_PAGER_TWO, dataBean.getRotation_seconds() * 1000);
                }
                MainViewPagerAdapter mainViewPagerAdapterTwo = new MainViewPagerAdapter(this, list);
                mViewPagerTwo.setAdapter(mainViewPagerAdapterTwo);
            }
        }
    }
}
