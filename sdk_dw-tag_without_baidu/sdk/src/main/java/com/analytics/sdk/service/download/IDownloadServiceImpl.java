package com.analytics.sdk.service.download;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.helper.DownloadUtils;
import com.analytics.sdk.service.AbstractService;
import com.analytics.sdk.service.ad.entity.AdResponse;


public class IDownloadServiceImpl extends AbstractService implements IDownloadService {

    public IDownloadServiceImpl() {
        super(IDownloadService.class);
    }

    @Override
    public void download(AdResponse adResponse, String clickUrl, String apkName) {
        try {
            DownloadUtils downloadUtils = new DownloadUtils(AdClientContext.getClientContext());
            downloadUtils.downloadApk(adResponse,clickUrl,apkName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
