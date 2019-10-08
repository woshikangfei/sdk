package com.analytics.sdk.view.handler.api.splash;

import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdShowStrategy;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.download.IDownloadService;
import com.analytics.sdk.view.handler.common.SplashBasicHandler;

import java.util.List;

public class ApiSplashApkDownloadHandler extends SplashBasicHandler {

    @Override
    protected void onHandleAd(AdResponse adResponse, AdListeneable clientAdListener, ConfigBeans configBeans) throws AdSdkException {

        List<AdShowStrategy> configWeiList = adResponse.getResponseData().getStrategyList();
        if (configWeiList != null && configWeiList.size() > 0) {
            AdShowStrategy adShowStrategy = configWeiList.get(0);
            String title = adShowStrategy.getTitle();
            String click_url = adShowStrategy.getClick_url();
            IDownloadService service = ServiceManager.getService(IDownloadService.class);
            service.download(adResponse,click_url,title);
        }

    }
}
