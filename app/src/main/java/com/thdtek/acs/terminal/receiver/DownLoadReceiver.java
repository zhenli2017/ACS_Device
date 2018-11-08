package com.thdtek.acs.terminal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thdtek.acs.terminal.util.Const;

/**
 * Time:2018/9/25
 * User:lizhen
 * Description:
 */

public class DownLoadReceiver extends BroadcastReceiver {

    private ProgressBar mProgressBar;

    public DownLoadReceiver( ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        String string = bundle.getString(Const.DOWN_LOAD_CURRENT_STATUS);
        if (TextUtils.equals(string, Const.DOWN_LOAD_START)) {
            start(bundle);
        } else if (TextUtils.equals(string, Const.DOWN_LOAD_ING)) {
            ing(bundle);
        } else if (TextUtils.equals(string, Const.DOWN_LOAD_END)) {
            end(bundle);
        }
    }

    private void start(Bundle bundle) {
        mProgressBar.setVisibility(View.VISIBLE);
        long fileLength = bundle.getLong(Const.DOWN_LOAD_FILE_LENGTH);
        long currentLength = bundle.getLong(Const.DOWN_LOAD_CURRENT_LENGTH);
        mProgressBar.setProgress((int) (currentLength / (float)fileLength * 100));

    }

    private void ing(Bundle bundle) {
        long fileLength = bundle.getLong(Const.DOWN_LOAD_FILE_LENGTH);
        long currentLength = bundle.getLong(Const.DOWN_LOAD_CURRENT_LENGTH);
        mProgressBar.setProgress((int) (currentLength / (float)fileLength * 100));
    }

    private void end(Bundle bundle) {
        mProgressBar.setVisibility(View.GONE);
    }
}
