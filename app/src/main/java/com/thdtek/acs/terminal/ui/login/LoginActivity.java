package com.thdtek.acs.terminal.ui.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.intellif.FaceUtils;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        int sdk_init = FaceUtils.getInstance().IFaceRecSDK_Init(Const.LICENSE_PATH, this);

        Log.d("test init ","sdk_init = "+sdk_init);

        String sdkInfo = FaceUtils.getInstance().IFaceRecSDK_GetSDKInfo();

        Log.d("test init ","sdkInfo = "+sdkInfo);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG,"===================================== onDestroy =====================================");
    }
}
