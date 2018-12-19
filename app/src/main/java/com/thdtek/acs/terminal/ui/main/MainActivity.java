package com.thdtek.acs.terminal.ui.main;

import android.content.Intent;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hwit.HwitManager;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseCameraActivity;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.receiver.DownLoadReceiver;
import com.thdtek.acs.terminal.receiver.TimeReceiver;
import com.thdtek.acs.terminal.receiver.WeatherReceiver;
import com.thdtek.acs.terminal.ui.cameraphoto.CameraPhotoActivity;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DeviceSnUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.util.SwitchConst;
import com.thdtek.acs.terminal.util.ToastUtil;
import com.thdtek.acs.terminal.view.CircleFaceView2;
import com.thdtek.acs.terminal.view.CircleView;
import com.thdtek.acs.terminal.view.RectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseCameraActivity {


    //根据人脸位置画框
    private CircleFaceView2 mDrawRectView;
    private ImageView mBgView;
    private Chronometer mTvTimeHour;
    private TextView mTvTimeDate;
    private TextView mTvTimeWeek;
    private TextView mTvName;
    private CircleImageView mCircleImageView;
    private TextView mTvWorkId;
    private LottieAnimationView mLottie;
    private LinearLayout mLinePerson;
    private RelativeLayout mRePairing;
    private ImageView mIvPairFail;
    private CircleView mCustomView;
    private TextView mTvC;
    private ImageView mIvWeather;
    private LinearLayout mLineWeather;
    private View mViewConnect;
    private TextView mTvTitle;

    private TextView mTvDownLoadPerson;
    private ProgressBar mProgressBar;


    @Override
    public String getTAG() {
        return MainActivity.class.getSimpleName();
    }


    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {

    }

    @Override
    public void initView() {
        String language = Locale.getDefault().toString();
        ImageView ic_pairing = findViewById(R.id.ic_pairing);
        LogUtils.d(TAG, "====== 当前系统语言 ====== " + language);
        if (language.contains(Const.LANGUAGE_EN)) {
            //英文
            Glide.with(this).load(R.mipmap.ic_identificaition_en).into(ic_pairing);
        } else if (language.equals(Const.LANGUAGE_ZH_CN)) {
            //中文
            Glide.with(this).load(R.mipmap.ic_pairing).into(ic_pairing);
        } else if (Const.LANGUAGE_ZH_HK.equals(language) || Const.LANGUAGE_ZH_TW.equals(language)) {
            //繁体
            Glide.with(this).load(R.mipmap.ic_fan_ti_pairing).into(ic_pairing);
        }
        TextView tvApkVersion = findViewById(R.id.tv_apk_version);
        tvApkVersion.setText( AppUtil.getAppVersionName(this) + " - " + HWUtil.getIPAddress());
        mViewConnect = findViewById(R.id.view_connect);

        if(SwitchConst.IS_OPEN_YING_ZE){
            mViewConnect.setVisibility(View.GONE);
        }else{
            mViewConnect.setVisibility(View.VISIBLE);
        }
//        mViewConnect.setVisibility(SwitchConst.IS_OPEN_SOCKET_MODE ? View.VISIBLE : View.INVISIBLE);
        mViewConnect.setSelected(false);
        mDrawRectView = findViewById(R.id.drawRectView);
        mFrameLayout = findViewById(R.id.frameLayout);
        mBgView = findViewById(R.id.image_bg);
        Glide.with(this).load(R.mipmap.ic_main_bg).into(mBgView);

        mTvTimeHour = findViewById(R.id.tv_time_hour);
        mTvTimeDate = findViewById(R.id.tv_time_date);
        mTvC = findViewById(R.id.tv_c);
        mIvWeather = findViewById(R.id.image_weather);
        mTvTimeWeek = findViewById(R.id.tv_time_week);

        mTvName = findViewById(R.id.tv_name);
        mCircleImageView = findViewById(R.id.circleImageView);
        mTvWorkId = findViewById(R.id.tv_work_id);

        mLottie = findViewById(R.id.lottie);
        mLinePerson = findViewById(R.id.line_Person);

        mCustomView = findViewById(R.id.customView);
        mCustomView.start();

        mRePairing = findViewById(R.id.rl_pairing);
        mIvPairFail = findViewById(R.id.iv_pair_fail);

        mLineWeather = findViewById(R.id.line_weather);

        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText(SPUtils.get(MyApplication.getContext(), Const.MAIN_TITLE, getResources().getString(R.string.title)) + "");

        //下载进度
        mTvDownLoadPerson = findViewById(R.id.tv_down_load_person);
        mProgressBar = findViewById(R.id.pb);

        mTvTimeHour.setText(String.format(Locale.getDefault(), "%tT", System.currentTimeMillis()));
        mTvTimeDate.setText(String.format(Locale.getDefault(), "%tm/%<td", System.currentTimeMillis()));
        mTvTimeWeek.setText(String.format(Locale.getDefault(), "   %tA", System.currentTimeMillis()));
        mTvTimeHour.start();
        mTvTimeHour.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                mTvTimeHour.setText(String.format(Locale.getDefault(), "%tT", System.currentTimeMillis()));
            }
        });

        mTvTimeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mTvTimeHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HWUtil.closeFan();
            }
        });
        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HWUtil.openFan();
            }
        });
        if (SwitchConst.IS_OPEN_HTTP_MODE) {
            mIvWeather.setVisibility(View.GONE);
            mTvC.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawRectView.stop();
        System.exit(0);

    }

    @Override
    public void handleOneMinuteNoPeople(Message message) {

    }

    @Override
    public TimeReceiver getTimeReceiver() {
        return new TimeReceiver(mTvTimeHour, mTvTimeDate, mTvTimeWeek);
    }

    @Override
    public WeatherReceiver getWeatherReceiver() {
        return new WeatherReceiver(mTvC, mIvWeather, mViewConnect);
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
        mTvDownLoadPerson.setVisibility(View.VISIBLE);
        mTvDownLoadPerson.setText((String) msg.obj);
    }

    @Override
    public void handleDownLoadPersonEnd(Message msg) {
        mTvDownLoadPerson.setVisibility(View.VISIBLE);
        mTvDownLoadPerson.setText((String) msg.obj);
    }

    @Override
    public void handleDownLoadPersonFinish() {
        mTvDownLoadPerson.setVisibility(View.GONE);
    }

    @Override
    public void handleNotFindFaceUI(Object rect, int color) {
        mDrawRectView.setRect(mRect, color);
    }

    @Override
    public void handleFindFaceUI(Object rect, int color) {
        mDrawRectView.setRect(mRect, color);
    }

    @Override
    public void handleFacePairReadyUI() {

    }

    @Override
    public void handleFacePairingUI() {
        mLottie.setVisibility(View.INVISIBLE);
        mIvPairFail.setVisibility(View.INVISIBLE);
        mLinePerson.setVisibility(View.INVISIBLE);
        mRePairing.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleFacePairSuccessUI(Message message, PersonBean personBean) {
        //隐藏和显示view
        mRePairing.setVisibility(View.INVISIBLE);
        mLottie.setVisibility(View.INVISIBLE);
        mIvPairFail.setVisibility(View.INVISIBLE);
        mLinePerson.setVisibility(View.VISIBLE);
        mTvName.setText(personBean.getName());
        mTvWorkId.setText(getString(R.string.worker_number) + personBean.getEmployee_card_id());
        Glide.with(this).load(personBean.getFacePic()).error(R.mipmap.ic_photo_default).centerCrop().into(mCircleImageView);

    }

    @Override
    public void handleFacePairFailUI(Message message) {
        mLinePerson.setVisibility(View.INVISIBLE);
        mRePairing.setVisibility(View.INVISIBLE);
        mLottie.setVisibility(View.INVISIBLE);
        parseFailUi(message.arg1);
        mIvPairFail.setVisibility(View.VISIBLE);
    }

    public void parseFailUi(int code) {
        String language = Locale.getDefault().getLanguage();
        switch (code) {
            case Const.FACE_PAIR_ERROR_CODE_EXCEPTION:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_error_en : R.mipmap.ic_unkonw_error).into(mIvPairFail);
                break;
            case Const.FACE_PAIR_ERROR_CODE_FACE_FEATURE_FAIL:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_facefeture_error_en : R.mipmap.ic_get_face_feature_fail).into(mIvPairFail);
                break;
            case Const.FACE_PAIR_ERROR_CODE_NOT_LOGIN:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_not_register_en : R.mipmap.ic_no_login).into(mIvPairFail);
                break;
            case Const.FACE_PAIR_ERROR_CODE_NOT_AUTHORITY:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_no_authority_en : R.mipmap.ic_no_authority).into(mIvPairFail);
                break;
            case Const.FACE_PAIR_ERROR_CODE_NOT_ID_IMAGE:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_id_message_error_en : R.mipmap.ic_id_message_error).into(mIvPairFail);
                break;
            case Const.FACE_PAIR_ERROR_CODE_FACE_RECT:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_identification_failure_en : R.mipmap.ic_pair_fail).into(mIvPairFail);
                break;
            case Const.FACE_PAIR_ERROR_CODE_NOT_GUEST_MODE:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_not_open_guest_mode_en : R.mipmap.ic_not_open_guest_mode).into(mIvPairFail);
                break;
            case Const.FACE_PAIR_ERROR_CODE_PAIR_ID_CARD:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_pair_id_card_en : R.mipmap.ic_pair_id_card).into(mIvPairFail);
                break;
            default:
                Glide.with(this).load(language.toLowerCase().contains(Const.LANGUAGE_EN) ?
                        R.mipmap.ic_identification_failure_en : R.mipmap.ic_pair_fail).into(mIvPairFail);
                break;
        }
    }


    @Override
    public void handlePairFailNoVisibleUI() {

    }

    @Override
    public void handleThisPairOverUI() {
        mLinePerson.setVisibility(View.INVISIBLE);
        mIvPairFail.setVisibility(View.INVISIBLE);
        mRePairing.setVisibility(View.INVISIBLE);
        mLottie.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleNotAliveUI(String msg) {
        mLinePerson.setVisibility(View.INVISIBLE);
        mRePairing.setVisibility(View.INVISIBLE);
        mLottie.setVisibility(View.INVISIBLE);
        Glide.with(this).load(R.mipmap.ic_not_alive).into(mIvPairFail);
        mIvPairFail.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleNotAliveFinishUI(String msg) {
        mLinePerson.setVisibility(View.INVISIBLE);
        mIvPairFail.setVisibility(View.INVISIBLE);
        mRePairing.setVisibility(View.INVISIBLE);
        mLottie.setVisibility(View.VISIBLE);
    }
}
