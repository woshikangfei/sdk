package com.analytics.sdk.view.strategy;

import android.view.View;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.analytics.sdk.view.strategy.fill.ViewLoadListener;

public final class AdEmptyListeners {

    public static final ViewLoadListener EMPTY_VIEW_LOAD_LISTNER = new ViewLoadListener() {
        @Override
        public boolean onViewLoaded(View view) {
            return false;
        }

        @Override
        public boolean onlyGetViewData(Object adData) {
            return false;
        }
    };

    public static final SplashAdListener EMPTY_SPLASH_AD_LISTENER = new SplashAdListener() {
        @Override
        public void onAdError(AdError adError) {

        }

        @Override
        public void onAdClicked() {

        }

        @Override
        public void onAdShow() {

        }

        @Override
        public void onAdExposure() {

        }

        @Override
        public void onAdDismissed() {

        }
    };

    public static abstract class SplashAdListenerAdapter implements SplashAdListener{

        @Override
        public void onAdError(AdError adError) {

        }

        @Override
        public void onAdClicked() {

        }

        @Override
        public void onAdShow() {

        }

        @Override
        public void onAdExposure() {

        }

        @Override
        public void onAdDismissed() {

        }
    }

}
