package com.analytics.sdk.view.dispatcher;

import android.os.Build;
import android.view.ViewGroup;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.IAdService;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.service.ad.StrategyHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdShowStrategy;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.handler.AdHandlerFactory;

import java.util.List;

/**
 * 分发开屏请求与并埋点上报
 */
public class SplashAdDispatcher extends BasicAdDispatcher {

    static final String TAG = "SplashAdDispatcher";

    private SplashAdListener splashAdListener;

    private SplashAdDispatcher(AdRequest adRequest){
        super(adRequest);
    }

    /**
     * 分发
     */
    public static boolean dispatch(final AdRequest adRequest, AdListeneable adListeneable) {
        return new SplashAdDispatcher(adRequest).dispatchRequest(adListeneable);
    }

    @Override
    protected boolean isExecuteAdHandlerOnMainThread() {
        return false;
    }

    @Override
    protected boolean isSupportSerialCall() {
        return true;
    }

    @Override
    public void executeAdHandler(final AdHandler adHandler, final AdResponse adResponse,final AdListeneable adListeneable) throws AdSdkException {

        this.splashAdListener = (SplashAdListener) adListeneable;

        ViewGroup adContainer = adResponse.getClientRequest().getAdContainer();

        if(adContainer == null){
            splashAdListener.onAdError(new AdError(ErrorCode.RequestParams.ERROR_AD_CONTAINER_ISNULL,"广告容器为空"));
            return;
        }

        Logger.i(TAG,"executeAdHandler enter , adContainer = " + adContainer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Logger.i(TAG,"executeAdHandler enter , adContainer isAttachedToWindow = " + adContainer.isAttachedToWindow() + " , isShown = " + adContainer.isShown());
        }

        adHandler.handleAd(adResponse,adListeneable);
    }

    @Override
    protected boolean onReceiveEventAction(String action, AdResponse adResponse, Object arg2) {

        Logger.i(TAG,"ClientSplashEventNotifier#handle event action = " + action);

        if(AdEventActions.ACTION_AD_ERROR.equals(action)) {
            if (adResponse.getResponseData().isHitErrorApiAd()){
                try {
                    this.adHandler.recycle();

                    AdRequest newAdRequest = SdkHelper.buildNextRequest(this.adRequest);
                    this.adRequest = newAdRequest;
                    adResponse.resetAdRequest(newAdRequest);

                    IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
                    adStrategyService.applyStrategy(newAdRequest);

                    adResponse.clear3rdSdkConfig(); //表示要走API的流程
                    AdHandler newAdHandler = AdHandlerFactory.factory().createAdHandler(adResponse);
                    this.adRequest.setRecycler(newAdHandler);
                    this.adHandler = newAdHandler;

                    newAdHandler.handleAd(adResponse,adListeneable);

                } catch (Exception e) {
                    e.printStackTrace();
                    splashAdListener.onAdError((AdError) arg2);
                }
            } else {
                splashAdListener.onAdError((AdError) arg2);
            }

        } else if(AdEventActions.ACTION_AD_CLICK.equals(action)) {
            splashAdListener.onAdClicked();
            if (adResponse.getResponseData().isHitClickApiAd()){
                try {
                    this.adHandler.recycle();

                    AdRequest newAdRequest = SdkHelper.buildNextRequest(this.adRequest);
                    this.adRequest = newAdRequest;
                    adResponse.resetAdRequest(newAdRequest);

                    IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
                    adStrategyService.applyStrategy(newAdRequest);

                    adResponse.clear3rdSdkConfig(); //表示要走API的流程
                    AdHandler newAdHandler = AdHandlerFactory.factory().createAdHandler(adResponse);
                    this.adRequest.setRecycler(newAdHandler);
                    this.adHandler = newAdHandler;

                    newAdHandler.handleAd(adResponse,adListeneable);
                } catch (AdSdkException e) {
                    e.printStackTrace();
                }
            }

        } else if(AdEventActions.ACTION_AD_DISMISS.equals(action)) {
            splashAdListener.onAdDismissed();
        } else if(AdEventActions.ACTION_AD_EXPOSURE.equals(action)) {
            splashAdListener.onAdExposure();
        } else if(AdEventActions.ACTION_AD_SHOW.equals(action)) {
            splashAdListener.onAdShow();
        }

        return false;
    }


    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_CLIENT.clone();
    }

    @Override
    public void dispatchErrorResponse(AdRequest adRequest,AdError adError, AdListeneable adListeneable) {
        final SplashAdListener clientSplashListener = (SplashAdListener) adListeneable;
        clientSplashListener.onAdError(adError);
    }

}

