package com.analytics.sdk.view.handler.csj.feedlist;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.view.handler.AdHandler;

/**
 * 模板
 */
public class CSJTempADFeedListAdHandlerImpl implements AdHandler {

    static final String TAG = CSJTempADFeedListAdHandlerImpl.class.getSimpleName();
    //广告回调监听\
    FeedListAdListener feedListAdListener;



    /**
     * 开屏填充开屏的方法，也可信息路填充开屏
     */
    public void handleAd(final AdResponse adResponse, final AdListeneable clientAdListener) throws AdSdkException{
        feedListAdListener = (FeedListAdListener) clientAdListener;
        feedListAdListener.onAdError(new AdError(10000,"检测后台配置，不支持的广告配置"));
    }



    @Override
    public boolean recycle() {
        return true;
    }

}
