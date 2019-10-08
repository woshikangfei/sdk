package com.analytics.sdk.view.strategy.click;

import android.view.ViewGroup;

import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.service.ad.StrategyHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;

public final class SplashCountdownClickStrategy {

    private CountdownStrategyRunner countdownStrategyRunner;

    public void apply(ViewGroup splashStrategyView, AdResponse adResponse){

        unapply();

        if(StrategyHelper.isHitCountdownStrategy(adResponse)){
            countdownStrategyRunner = new CountdownStrategyRunner(splashStrategyView,adResponse);
            ThreadExecutor.runOnAndroidHandlerThread(countdownStrategyRunner,3 * 1000);
        }

    }

    public void unapply(){
        if(countdownStrategyRunner != null){
            ThreadExecutor.removeOnAndroidHandlerThread(countdownStrategyRunner);
            countdownStrategyRunner = null;
        }
    }

    static class CountdownStrategyRunner implements Runnable {

        private ViewGroup splashStrategyView;
        private AdResponse adResponse;

        public CountdownStrategyRunner(ViewGroup splashStrategyView,AdResponse adResponse){
            this.splashStrategyView = splashStrategyView;
            this.adResponse = adResponse;
        }

        @Override
        public void run() {
            StrategyHelper.sendSimulateEventWithStrategyService(splashStrategyView,adResponse);
        }
    }


}
