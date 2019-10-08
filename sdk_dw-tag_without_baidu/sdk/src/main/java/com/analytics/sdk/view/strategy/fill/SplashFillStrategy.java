package com.analytics.sdk.view.strategy.fill;

import android.view.View;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.analytics.sdk.view.strategy.AdEmptyListeners;

public class SplashFillStrategy implements SplashAdListener , ViewLoadListener{

    private SplashAdListener splashAdListener;
    private ViewLoadListener viewLoadListener;

    public static final SplashFillStrategy EMPTY = new SplashFillStrategy(null,null);

    public SplashFillStrategy(SplashAdListener splashAdListener,ViewLoadListener viewLoadListener){
        this.splashAdListener = (splashAdListener == null ? AdEmptyListeners.EMPTY_SPLASH_AD_LISTENER : splashAdListener);
        this.viewLoadListener = (viewLoadListener == null ? AdEmptyListeners.EMPTY_VIEW_LOAD_LISTNER : viewLoadListener);
    }

    @Override
    public void onAdError(AdError adError) {
        splashAdListener.onAdError(adError);
    }

    @Override
    public void onAdClicked() {
        splashAdListener.onAdClicked();
    }

    @Override
    public void onAdShow() {
        splashAdListener.onAdShow();
    }

    @Override
    public void onAdExposure() {
        splashAdListener.onAdExposure();
    }

    @Override
    public void onAdDismissed() {
        splashAdListener.onAdDismissed();
    }

    @Override
    public boolean onViewLoaded(View view) {
        return viewLoadListener.onViewLoaded(view);
    }

    @Override
    public boolean onlyGetViewData(Object adData) {
        return viewLoadListener.onlyGetViewData(adData);
    }

}
