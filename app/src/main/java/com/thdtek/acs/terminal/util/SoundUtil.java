package com.thdtek.acs.terminal.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.text.TextUtils;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Time:2018/7/13
 * User:lizhen
 * Description:
 */

public class SoundUtil {

    private static SoundPool mSoundPool;
    private static int mSoundId;
    private static SoundPool mSoundPoolShutter;
    private static int mSoundIdShutter = -1;


    public static void setVolume(int value) {
        //初始化音频管理器
        AudioManager mAudioManager = (AudioManager) MyApplication.getContext().getSystemService(AUDIO_SERVICE);
        //获取系统最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获取设备当前音量
//        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (value >= maxVolume) {
            value = maxVolume;
        }
        if (value <= 0) {
            value = 0;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, value, AudioManager.FLAG_PLAY_SOUND);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_PLAY_SOUND);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, value, AudioManager.FLAG_PLAY_SOUND);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, value, AudioManager.FLAG_PLAY_SOUND);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, value, AudioManager.FLAG_PLAY_SOUND);
    }


    public static void soundWelcome(Context context) {
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        }
        if (TextUtils.isEmpty(AppSettingUtil.getConfig().getAppWelcomeMusic())) {
            mSoundId = mSoundPool.load(context, R.raw.music_2, 1);
        } else {

            mSoundId = mSoundPool.load(AppSettingUtil.getConfig().getAppWelcomeMusic(), 1);
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(mSoundId, 1, 1, 1, 0, 1.0f);
            }
        });
    }
    public static void findID() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_RING, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
    }
    public static void openDoor() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_RING, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
    }


    public static void soundShutter(final int loop) {

        if(mSoundPoolShutter == null){
            mSoundPoolShutter = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            mSoundPoolShutter.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundPool.play(mSoundIdShutter, 1, 1, 1, loop, 1.0f);
                }
            });
        }

        if(mSoundIdShutter == -1){
            mSoundIdShutter = mSoundPoolShutter.load(MyApplication.getContext(), R.raw.shutter, 1);
        }


        mSoundPoolShutter.play(mSoundIdShutter, 1, 1, 1, loop, 1.0f);

    }

}
