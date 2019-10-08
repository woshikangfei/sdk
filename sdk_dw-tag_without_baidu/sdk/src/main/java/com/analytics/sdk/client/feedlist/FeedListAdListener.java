package com.analytics.sdk.client.feedlist;


import com.analytics.sdk.client.AdCommonListener;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;

import java.util.List;

/**
 * 信息流
 */
public interface FeedListAdListener extends AdCommonListener {

    void onAdLoaded(List<AdView> adViewList);

    void onAdClicked(AdView adView);

    void onAdDismissed(AdView adView);

    void onADExposed(AdView adView);

    /**
     * 视频广告，视频加载回调
     */
    void onVideoLoad();

    void onVideoPause();

    //视频开始播放
    void onVideoStart();
}
