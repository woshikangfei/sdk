package com.analytics.sdk.view.handler;

import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ad.entity.AdResponse;

public interface AdHandler extends IRecycler{

    void handleAd(AdResponse adResponse, final AdListeneable clientAdListener) throws AdSdkException;

    AdHandler EMPTY_HANDLER = new AdHandler() {
        @Override
        public boolean recycle() {
            return false;
        }

        @Override
        public void handleAd(AdResponse adResponse, AdListeneable clientAdListener) throws AdSdkException {
            Logger.i("emptyHandler","handleAd enter, empty impl");
        }
    };

    AdHandler EXCEPTION_HANDLER = new AdHandler() {
        @Override
        public boolean recycle() {
            return false;
        }

        @Override
        public void handleAd(AdResponse adResponse, AdListeneable clientAdListener) throws AdSdkException {
            throw new AdSdkException(ErrorCode.Api.ERROR_NOTFOUND_SOURCE,"handleAd enter, exception impl");
        }
    };


}
