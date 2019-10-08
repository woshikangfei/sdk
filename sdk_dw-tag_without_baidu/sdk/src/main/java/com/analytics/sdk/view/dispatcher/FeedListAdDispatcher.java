package com.analytics.sdk.view.dispatcher;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.view.handler.AdHandler;

import java.util.List;

/**
 * 分发插屏请求与并埋点上报
 */
// TODO: 2019/6/15 埋点要在dispatch中处理？还是加一个对Handler的代理实现？
public class FeedListAdDispatcher extends BasicAdDispatcher{

    static final String TAG = FeedListAdDispatcher.class.getSimpleName();

    private FeedListAdListener clientFeedListListener;

    private FeedListAdDispatcher(AdRequest adRequest){
        super(adRequest);
    }

    @Override
    protected boolean isExecuteAdHandlerOnMainThread() {
        return false;
    }

    @Override
    protected boolean onReceiveEventAction(String action, AdResponse adResponse, Object arg2) {
        if(AdEventActions.ACTION_AD_ERROR.equals(action)) {
            clientFeedListListener.onAdError((AdError) arg2);
        } else if(AdEventActions.ACTION_AD_CLICK.equals(action)) {
            clientFeedListListener.onAdClicked((AdView) arg2);
        } else if(AdEventActions.ACTION_AD_DISMISS.equals(action)) {
            clientFeedListListener.onAdDismissed((AdView) arg2);
        } else if(AdEventActions.ACTION_AD_EXPOSURE.equals(action)) {
            clientFeedListListener.onADExposed((AdView) arg2);
        } else if(AdEventActions.FeedList.ACTION_AD_LOADED.equals(action)) {
            clientFeedListListener.onAdLoaded((List<AdView>) arg2);
        }
        return true;
    }

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_CLIENT.addActionList(AdEventActions.BASE_FEEDLIST);
    }

    /**
     * 分发
     */
    public static boolean dispatch(final AdRequest adRequest, AdListeneable adListeneable) {
        return new FeedListAdDispatcher(adRequest).dispatchRequest(adListeneable);
    }

    @Override
    public void executeAdHandler(AdHandler adHandler, AdResponse adResponse,AdListeneable adListeneable) throws AdSdkException {
        clientFeedListListener = (FeedListAdListener) adListeneable;
        adHandler.handleAd(adResponse,adListeneable);
    }

    @Override
    public void dispatchErrorResponse(AdRequest adRequest,AdError adError, AdListeneable adListeneable) {
        final FeedListAdListener clientFeedListAdListener = (FeedListAdListener) adListeneable;
        clientFeedListAdListener.onAdError(adError);
    }

}

