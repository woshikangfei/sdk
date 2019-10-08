package com.analytics.sdk.view.handler.csj.splash;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.analytics.sdk.R;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.helper.SkipViewCoundownTimer;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.StrategyHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.bytedance.sdk.openadsdk.TTSplashAd;

public class CSJSplashAdViewBuilder {

    static final String TAG = CSJSplashAdViewBuilder.class.getSimpleName();

    public static View build(final AdResponse adResponse, final TTSplashAd ttSplashAd){

        Activity activity = adResponse.getClientRequest().getActivity();

        final View splashView = activity.getLayoutInflater().inflate(R.layout.jhsdk_splash_with_gdt_and_csj_sdk_fill_layout,null);
        final ViewGroup adContainer = splashView.findViewById(R.id.splash_ad_bitmap_layout);
        //跳过按钮
        final TextView skipButton = splashView.findViewById(R.id.skip_ad);
        skipButton.setVisibility(View.VISIBLE);
        //获取SplashView
        View view = ttSplashAd.getSplashView();
        adContainer.removeAllViews();

        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
        adContainer.addView(view);
//                        mSplashContainer.addView(tvAdClose);
        //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
        ttSplashAd.setNotAllowSdkCountdown();

        int skipTime = 5300;

        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
        //开屏广告加倒计时和误点操作
//                    //TODO:跳过样式优化
        skipButton.setText("跳过(" + skipTime/ 1000 + ")");
        final SkipViewCoundownTimer countDownTimer = new SkipViewCoundownTimer(skipButton, new SkipViewCoundownTimer.OnFinishListener() {
            @Override
            public void onFinish() {
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
            }
        }, skipTime, 1000);
        countDownTimer.start();
        if(!StrategyHelper.isHit(adResponse.getClientRequest())){
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countDownTimer.cancel();
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
                }
            });
        }

        //设置SplashView的交互监听器
        ttSplashAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {

                Logger.i(TAG,"handleSplashWithNormal onAdClicked enter , param type = " + type);
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse));
            }

            @Override
            public void onAdShow(View view, int type) {
                Logger.i(TAG,"handleSplashWithNormal onAdShow enter , param type = " + type);
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_SHOW,adResponse));
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse));
            }

            @Override
            public void onAdSkip() {
                Logger.i(TAG,"handleSplashWithNormal onAdSkip enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
            }

            @Override
            public void onAdTimeOver() {
                Logger.i(TAG,"handleSplashWithNormal onAdTimeOver enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
            }
        });

        return splashView;
    }

}
