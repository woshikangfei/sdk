package com.analytics.sdk.view.dispatcher;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.video.RewardVideoAdListener;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.view.handler.AdHandler;

/**
 * 分发插屏请求与并埋点上报
 */
public class RewardVideoAdDispatcher extends BasicAdDispatcher{

    static final String TAG = RewardVideoAdDispatcher.class.getSimpleName();

    private RewardVideoAdListener rewardVideoAdListener;

    private RewardVideoAdDispatcher(AdRequest adRequest){
        super(adRequest);
    }

    /**
     * 分发
     */
    public static boolean dispatch(final AdRequest adRequest, AdListeneable adListeneable) {
        return new RewardVideoAdDispatcher(adRequest).dispatchRequest(adListeneable);
    }

    @Override
    public void executeAdHandler(AdHandler adHandler, AdResponse adResponse,AdListeneable adListeneable) throws AdSdkException {

        rewardVideoAdListener = (RewardVideoAdListener) adListeneable;

        adHandler.handleAd(adResponse,adListeneable);
    }

    @Override
    public void dispatchErrorResponse(AdRequest adRequest,AdError adError, AdListeneable adListeneable) {
        final RewardVideoAdListener clientRewardVideoListener = (RewardVideoAdListener) adListeneable;
        clientRewardVideoListener.onAdError(adError);
    }

    @Override
    protected boolean onReceiveEventAction(String action, AdResponse adResponse, Object adError) {
        if(AdEventActions.ACTION_AD_ERROR.equals(action)) {
            rewardVideoAdListener.onAdError((AdError) adError);
        } else if(AdEventActions.ACTION_AD_DISMISS.equals(action)) {
            rewardVideoAdListener.onAdDismissed();
        } else if(AdEventActions.ACTION_AD_CLICK.equals(action)) {
            rewardVideoAdListener.onAdClicked();
        } else if(AdEventActions.ACTION_AD_SHOW.equals(action)) {
            rewardVideoAdListener.onAdShow();
        } else if(AdEventActions.ACTION_AD_EXPOSURE.equals(action)) {
            rewardVideoAdListener.onAdExposure();
        } else if(AdEventActions.RewardVideo.ACTION_AD_VIDEO_REWARD.equals(action)) {
        } else if(AdEventActions.RewardVideo.ACTION_AD_VIDEO_COMPLETED.equals(action)) {
            rewardVideoAdListener.onAdVideoCompleted();
        } else if(AdEventActions.RewardVideo.ACTION_AD_VIDEO_LOADED.equals(action)) {
        } else if(AdEventActions.RewardVideo.ACTION_AD_VIDEO_CACHED.equals(action)) {
        }
        return true;
    }

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_CLIENT.clone()
                            .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_REWARD)
                            .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_COMPLETED)
                            .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_LOADED)
                            .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_CACHED);
    }

}

