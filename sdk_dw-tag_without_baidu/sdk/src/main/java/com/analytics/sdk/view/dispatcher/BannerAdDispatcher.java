package com.analytics.sdk.view.dispatcher;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.banner.BannerAdListener;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.view.handler.AdHandler;

/**
 * 分发横幅请求与并埋点上报
 */
// TODO: 2019/6/15 埋点要在dispatch中处理？还是加一个对Handler的代理实现？
public class BannerAdDispatcher extends BasicAdDispatcher{

    static final String TAG = BannerAdDispatcher.class.getSimpleName();

    private BannerAdListener clientBannerListener;

    private BannerAdDispatcher(AdRequest adRequest){
        super(adRequest);
    }

    @Override
    protected boolean isExecuteAdHandlerOnMainThread() {
        return false;
    }

    @Override
    protected boolean onReceiveEventAction(String action, AdResponse adResponse, Object arg2) {
        if(AdEventActions.ACTION_AD_ERROR.equals(action)) {
            clientBannerListener.onAdError((AdError) arg2);
        } else if(AdEventActions.ACTION_AD_CLICK.equals(action)) {
            clientBannerListener.onAdClicked();
        } else if(AdEventActions.ACTION_AD_DISMISS.equals(action)) {
            clientBannerListener.onAdDismissed();
        } else if(AdEventActions.ACTION_AD_EXPOSURE.equals(action)) {
            clientBannerListener.onAdExposure();
        } else if(AdEventActions.ACTION_AD_SHOW.equals(action)) {
            clientBannerListener.onAdShow();
        }
        return true;
    }

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_CLIENT;
    }

    /**
     * 分发
     */
    public static boolean dispatch(final AdRequest adRequest, AdListeneable adListeneable) {
        return new BannerAdDispatcher(adRequest).dispatchRequest(adListeneable);
    }

    @Override
    public void executeAdHandler(AdHandler adHandler, AdResponse adResponse,AdListeneable adListeneable) throws AdSdkException {
        final BannerAdListener clientBannerListener = (BannerAdListener) adListeneable;
        this.clientBannerListener = clientBannerListener;
        adHandler.handleAd(adResponse,adListeneable);
    }

    @Override
    public void dispatchErrorResponse(AdRequest adRequest,AdError adError, AdListeneable adListeneable) {
        final BannerAdListener clientBannerListener = (BannerAdListener) adListeneable;
        clientBannerListener.onAdError(adError);
    }

}

