package com.analytics.sdk.dynamic.a;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.banner.BannerAdListener;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.client.interstitial.InterstitialAdListener;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.analytics.sdk.client.video.RewardVideoAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.AbstractService;
import com.analytics.sdk.service.IService;
import com.analytics.sdk.service.client.IClientServcie;
import com.analytics.sdk.service.report.IReportService;
import com.analytics.sdk.service.report.entity.ReportData;

public class IClientServiceImplFixed extends AbstractService implements IClientServcie {

    static final String TAG = IClientServiceImplFixed.class.getSimpleName();

    /**
     *  带有BUG的服务实现
     */
    private IClientServcie clientServcie = null;

    public IClientServiceImplFixed() {
        super(IClientServcie.class);
    }

    public IClientServiceImplFixed(IService clientService){
        super(IClientServcie.class);
        this.clientServcie = (IClientServcie) clientService;
    }

    @Override
    public void loadSplashAd(AdRequest adRequest, SplashAdListener splashAdListener) {
        Logger.i("hotfix","loadSplashAd test *************** enter , clientServcie = " + clientServcie);
        try {
//            ReportData.obtain("fix_loadSplashAd",IReportService.Action.ACTION_AD_HOTFIX).startReport();
            SplashAdDispatcherFixed.dispatch(adRequest,splashAdListener);
//            clientServcie.loadSplashAd(adRequest,splashAdListener);
        } catch (Throwable e){
            ReportData.obtain("fix_loadSplashAd_error("+e.getMessage()+")",IReportService.Action.ACTION_HOTFIX).startReport();
            clientServcie.loadSplashAd(adRequest,splashAdListener);
        }
    }

    @Override
    public void loadBannerAd(AdRequest adRequest, BannerAdListener bannerAdListener) {
        Logger.i("hotfix","loadBannerAd*************** enter , clientServcie = " + clientServcie);
        this.clientServcie.loadBannerAd(adRequest,bannerAdListener);
    }

    @Override
    public void loadInterstitialAd(AdRequest adRequest, InterstitialAdListener interstitialAdListener) {
        Logger.i("hotfix","loadInterstitialAd*************** enter , clientServcie = " + clientServcie);
        this.clientServcie.loadInterstitialAd(adRequest,interstitialAdListener);
    }

    @Override
    public void loadRewardVideoAd(AdRequest adRequest, RewardVideoAdListener rewardVideoAdListener) {
        Logger.i("hotfix","loadRewardVideoAd*************** enter , clientServcie = " + clientServcie);
        this.clientServcie.loadRewardVideoAd(adRequest,rewardVideoAdListener);
    }

    @Override
    public void loadFeedListAd(AdRequest adRequest, FeedListAdListener feedListAdListener) {
        Logger.i("hotfix","loadFeedListAd*************** enter , clientServcie = " + clientServcie);
        this.clientServcie.loadFeedListAd(adRequest,feedListAdListener);
    }
}
