package com.analytics.sdk.service.download;

import com.analytics.sdk.service.IService;
import com.analytics.sdk.service.ad.entity.AdResponse;

public interface IDownloadService extends IService {
    void download(AdResponse adResponse, String clickUrl, String apkName);
}
