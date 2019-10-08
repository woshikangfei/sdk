package com.analytics.sdk.view.dispatcher;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.interstitial.InterstitialAdListener;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.view.handler.AdHandler;

/**
 * 分发插屏请求与并埋点上报
 */
// TODO: 2019/6/15 埋点要在dispatch中处理？还是加一个对Handler的代理实现？
public class InterstitialAdDispatcher extends BasicAdDispatcher{

    static final String TAG = InterstitialAdDispatcher.class.getSimpleName();
    private InterstitialAdListener clientInterstitialAdListener;

    private InterstitialAdDispatcher(AdRequest adRequest){
        super(adRequest);
    }

    @Override
    protected boolean isExecuteAdHandlerOnMainThread() {
        return false;
    }

    @Override
    protected boolean onReceiveEventAction(String action, AdResponse adResponse, Object arg2) {

        if(AdEventActions.ACTION_AD_ERROR.equals(action)) {
            clientInterstitialAdListener.onAdError((AdError) arg2);
        } else if(AdEventActions.ACTION_AD_CLICK.equals(action)) {
            clientInterstitialAdListener.onAdClicked();
        } else if(AdEventActions.ACTION_AD_DISMISS.equals(action)) {
            clientInterstitialAdListener.onAdDismissed();
        } else if(AdEventActions.ACTION_AD_EXPOSURE.equals(action)) {
            clientInterstitialAdListener.onAdExposure();
        } else if(AdEventActions.ACTION_AD_SHOW.equals(action)) {
            clientInterstitialAdListener.onAdShow();
        }

        return false;
    }

    @Override
    protected EventActionList buildEventActionList() {
        return EventActionList.create()
                .addAction(AdEventActions.ACTION_AD_REQUEST)
                .addAction(AdEventActions.ACTION_AD_CLICK)
                .addAction(AdEventActions.ACTION_AD_ERROR)
                .addAction(AdEventActions.ACTION_AD_SHOW)
                .addAction(AdEventActions.ACTION_AD_EXPOSURE)
                .addAction(AdEventActions.ACTION_AD_DISMISS);
    }

    /**
     * 分发
     */
    public static boolean dispatch(final AdRequest adRequest, AdListeneable adListeneable) {
        return new InterstitialAdDispatcher(adRequest).dispatchRequest(adListeneable);
    }

    @Override
    public void executeAdHandler(AdHandler adHandler, AdResponse adResponse,AdListeneable adListeneable) throws AdSdkException {
        clientInterstitialAdListener = (InterstitialAdListener) adListeneable;
        adHandler.handleAd(adResponse, adListeneable);
    }

    @Override
    public void dispatchErrorResponse(AdRequest adRequest,AdError adError, AdListeneable adListeneable) {
        final InterstitialAdListener clientBannerListener = (InterstitialAdListener) adListeneable;
        clientBannerListener.onAdError(adError);
    }


}

