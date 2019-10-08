package com.analytics.sdk.helper;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.analytics.sdk.client.splash.SplashAdListener;

public class SkipViewCoundownTimer extends CountDownTimer {

    TextView skipButton;
    OnFinishListener finishListener;

    public interface OnFinishListener {
        void onFinish();
    }

    public SkipViewCoundownTimer(TextView skipButton,OnFinishListener onFinishListener,long millisInFuture, long countDownInterval){
        super(millisInFuture, countDownInterval);
        this.skipButton = skipButton;
        this.finishListener = onFinishListener;
    }

    private SkipViewCoundownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onFinish() {
        finishListener.onFinish();
    }
    @Override
    public void onTick(long millisUntilFinished) {
        if (skipButton != null) {
            skipButton.setText("跳过(" + millisUntilFinished / 1000 + ")");
        }
    }

}
