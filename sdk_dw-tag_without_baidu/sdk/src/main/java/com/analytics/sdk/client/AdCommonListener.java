package com.analytics.sdk.client;

public interface AdCommonListener extends AdListeneable{

    /**
     * 广告加载失败，error 对象包含了错误码和错误信息
     */
    void onAdError(AdError adError);

}
