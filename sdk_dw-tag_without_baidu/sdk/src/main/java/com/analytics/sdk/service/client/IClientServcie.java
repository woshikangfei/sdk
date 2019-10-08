package com.analytics.sdk.service.client;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.banner.BannerAdListener;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.client.interstitial.InterstitialAdListener;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.analytics.sdk.client.video.RewardVideoAdListener;
import com.analytics.sdk.service.IService;

public interface IClientServcie extends IService {

    /**
     * 加载开屏广告
     */
    void loadSplashAd(final AdRequest adRequest, final SplashAdListener splashAdListener);

    /**
     * 加载横幅广告
     */
    void loadBannerAd(final AdRequest adRequest,final BannerAdListener bannerAdListener);

    /**
     * 加载插屏广告
     */
    void loadInterstitialAd(final AdRequest adRequest,final InterstitialAdListener interstitialAdListener);

    /**
     * 加载激励视频
     */
    void loadRewardVideoAd(final AdRequest adRequest,final RewardVideoAdListener rewardVideoAdListener);

    /**
     * 加载信息流中的广告
     */
    void loadFeedListAd(final AdRequest adRequest,final FeedListAdListener feedListAdListener);

}
