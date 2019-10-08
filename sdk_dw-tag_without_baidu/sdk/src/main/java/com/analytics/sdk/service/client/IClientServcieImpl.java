package com.analytics.sdk.service.client;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.banner.BannerAdListener;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.client.interstitial.InterstitialAdListener;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.analytics.sdk.client.video.RewardVideoAdListener;
import com.analytics.sdk.common.helper.Preconditions;
import com.analytics.sdk.service.AbstractService;
import com.analytics.sdk.view.dispatcher.BannerAdDispatcher;
import com.analytics.sdk.view.dispatcher.FeedListAdDispatcher;
import com.analytics.sdk.view.dispatcher.InterstitialAdDispatcher;
import com.analytics.sdk.view.dispatcher.RewardVideoAdDispatcher;
import com.analytics.sdk.view.dispatcher.SplashAdDispatcher;

public class IClientServcieImpl extends AbstractService implements IClientServcie{

    public IClientServcieImpl() {
        super(IClientServcie.class);
    }

    @Override
    public boolean isSupportHotfix() {
        return true;
    }

    @Override
    public void loadSplashAd(AdRequest adRequest, SplashAdListener splashAdListener) {
        SplashAdDispatcher.dispatch(adRequest,Preconditions.checkNotNull(splashAdListener));
    }

    @Override
    public void loadBannerAd(AdRequest adRequest, BannerAdListener bannerAdListener) {
        BannerAdDispatcher.dispatch(adRequest,Preconditions.checkNotNull(bannerAdListener));
    }

    @Override
    public void loadInterstitialAd(AdRequest adRequest, InterstitialAdListener interstitialAdListener) {
        InterstitialAdDispatcher.dispatch(adRequest,Preconditions.checkNotNull(interstitialAdListener));
    }

    @Override
    public void loadRewardVideoAd(AdRequest adRequest, RewardVideoAdListener rewardVideoAdListener) {
        RewardVideoAdDispatcher.dispatch(adRequest,Preconditions.checkNotNull(rewardVideoAdListener));
    }

    @Override
    public void loadFeedListAd(AdRequest adRequest, FeedListAdListener feedListAdListener) {
        FeedListAdDispatcher.dispatch(adRequest,Preconditions.checkNotNull(feedListAdListener));
    }
}
