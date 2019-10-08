package com.analytics.sdk.client;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.analytics.sdk.client.banner.BannerAdListener;
import com.analytics.sdk.client.feedlist.AdSize;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.client.interstitial.InterstitialAdListener;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.analytics.sdk.client.video.RewardVideoAdListener;
import com.analytics.sdk.common.helper.Preconditions;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.exception.AdSdkRuntimeException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.service.client.IClientServcie;
import com.analytics.sdk.service.report.entity.ReportData;
import com.analytics.sdk.view.handler.IRecycler;
import com.analytics.sdk.view.handler.gdt.splash.SplashVersion;

import java.util.Iterator;
import java.util.UUID;

/**
 * 一个广告请求
 */
public class AdRequest {

    static final String TAG = "AdRequest";

    static final int DEFAULT_TIMEOUT_MS = 5 * 1000;
    /**
     * 唯一的请求ID UUID
     */
    private String requestId;
    /**
     * 代码位ID
     */
    private String codeId;
    /**
     * 请求来源的UI
     */
    private Activity activity;
    /**
     * 请求什么类型的广告
     * 查看SoltTypeEm中定义的类型
     * 标识请求的是：开屏 | 信息流等
     */
    AdType adType = AdType.UNKNOWN;
    /**
     * 请求广告的尺寸
     */
    private AdSize adSize;
    /**
     * 广告请求的数量
     */
    private int adRequestCount = 1;
    /**
     * 超时时间
     */
    private int timeoutMs = DEFAULT_TIMEOUT_MS;
    /**
     * 开屏底部LOGO
     */
    private Bitmap splashBottomLogo;
    /**
     * 广告的容器
     */
    public ViewGroup adContainer;

    /**
     * 激励视频的奖励名称
     */
    private String rewardName;
    /**
     * 奖励的金币数
     */
    private int rewardAmount;
    /**
     * 激励视频参数： 用户唯一标识
     */
    private String userID;
    /**
     * 请求的资源释放
     */
    private IRecycler recycler;
    /**
     * 请求时间
     */
    private long requestTime = System.currentTimeMillis();
    /**
     * 请求是否被释放
     */
    private volatile boolean isRecycled = false;
    /**
     * 处理的版本
     */
    private int handlerVersion = 0;

    private int refresh = 0;
    /**
     * 视图的风格样式配置
     */
    private LayoutStyle layoutStyle = LayoutStyle.EMPTY;
    /**
     * 追加扩展参数
     */
    private Bundle extParameters = new Bundle();

    public boolean isRecycled(){
        return isRecycled;
    }

    private AdRequest(AdType adType){
        this.adType = adType;
        this.requestId = UUID.randomUUID().toString();
    }

    public Bitmap getSplashBottomLogo() {
        return splashBottomLogo;
    }

    public String getCodeId() {
        return codeId;
    }

    public String getRewardName() {
        return rewardName;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public int getHandlerVersion(){
        return handlerVersion;
    }

    public String getUserID() {
        return userID;
    }

    public ViewGroup getAdContainer() {
        return adContainer;
    }

    public LayoutStyle getLayoutStyle() {
        return layoutStyle;
    }

    public Activity getActivity() {
        return activity;
    }

    public AdType getAdType() {
        return adType;
    }

    public int getTimeoutMs(){
        return timeoutMs;
    }

    public AdSize getAdSize() {
        return adSize;
    }

    public int getAdRequestCount() {
        return adRequestCount;
    }

    public String getRequestId() {
        return requestId;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRecycler(IRecycler recycler) {
        this.recycler = recycler;
    }

    public int getRefresh() {
        return refresh;
    }

    public Bundle getExtParameters(){
        return extParameters;
    }

    public static class Builder {

        private String codeId;
        private Activity activity;
        private AdType adType = AdType.UNKNOWN;
        private AdSize adSize;
        private int adRequestCount = 1;
        private int timeoutMs = DEFAULT_TIMEOUT_MS;
        private Bitmap splashBottomLogo;
        private ViewGroup adContainer;
        private String rewardName;
        private int rewardAmount;
        private String userID;
        private int refresh = 0;
        private LayoutStyle layoutStyle = LayoutStyle.EMPTY;
        private Bundle extParameters = new Bundle();

        public Builder(Activity activity){
            this.activity = activity;
        }

        public Builder(AdRequest adRequest){

            this.adType = adRequest.getAdType();
            this.activity = adRequest.getActivity();
            this.adRequestCount = adRequest.getAdRequestCount();
            this.adSize = adRequest.getAdSize();
            this.codeId = adRequest.getCodeId();
            this.timeoutMs = adRequest.getTimeoutMs();
            this.splashBottomLogo = adRequest.getSplashBottomLogo();
            this.adContainer = adRequest.getAdContainer();
            this.rewardAmount = adRequest.getRewardAmount();
            this.rewardName = adRequest.getRewardName();
            this.userID = adRequest.getUserID();
            this.refresh = adRequest.getRefresh();
            this.layoutStyle = adRequest.getLayoutStyle();
            this.extParameters = new Bundle(adRequest.getExtParameters());
        }

        public Builder setRewardName(String rewardName) {
            this.rewardName = rewardName;
            return this;
        }

        public Builder appendParameter(String key,String value){
            this.extParameters.putString(key, value);
            return this;
        }

        public Builder appendParameter(String key,int value){
            this.extParameters.putInt(key, value);
            return this;
        }

        public Builder appendParameter(String key,boolean value){
            this.extParameters.putBoolean(key, value);
            return this;
        }

        public Builder appendParameters(Bundle bundle){
            this.extParameters.putAll(bundle);
            return this;
        }

        public Builder setRewardAmount(int rewardAmount) {
            this.rewardAmount = rewardAmount;
            return this;
        }

        public Builder setRefresh(int refresh) {
            this.refresh = refresh;
            return this;
        }

        public Builder setLayoutStyle(LayoutStyle layoutStyle){
            this.layoutStyle = layoutStyle;
            return this;
        }

        public Builder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        public Builder setCodeId(String codeId) {
            this.codeId = codeId;
            return this;
        }

        public Builder setAdContainer(ViewGroup adContainer) {
            this.adContainer = adContainer;
            return this;
        }

        public Builder setSplashBottomLogo(Bitmap splashBottomLogo) {
            this.splashBottomLogo = splashBottomLogo;
            return this;
        }

        public Builder setAdSize(AdSize adSize) {
            this.adSize = adSize;
            return this;
        }

        public Builder setTimeoutMs(int timeoutMs){
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder setAdRequestCount(int adRequestCount) {
            this.adRequestCount = adRequestCount;
            return this;
        }

        public AdRequest build(){
            AdRequest request = new AdRequest(adType);

            if (TextUtils.isEmpty(codeId)) {
                throw new AdSdkRuntimeException("codeId is empty");
            }

            if(activity == null){
                throw new AdSdkRuntimeException("activity is null");
            }

            request.activity = activity;
            request.adRequestCount = adRequestCount;
            request.adSize = adSize;
            request.codeId = codeId;
            request.timeoutMs = timeoutMs;
            request.splashBottomLogo = splashBottomLogo;
            request.adContainer = adContainer;
            request.rewardAmount = rewardAmount;
            request.rewardName = rewardName;
            request.userID = userID;
            request.refresh = refresh;
            request.layoutStyle = layoutStyle;
            request.extParameters = extParameters;

            return request;
        }

    }

    static final int BRCOUNT = 1;

    public static void init(Context context, SdkConfiguration sdkConfiguration){
        Logger.i(TAG,"init enter , " + sdkConfiguration,BRCOUNT);
        if(!AdClientContext.isRealy()){
            AdClientContext.init(context,sdkConfiguration);
        }
    }

    /**
     * 加载开屏广告
     */
    public AdRequest loadSplashAd(final SplashAdListener splashAdListener){
        check();
        throwIfNotOnMainThread();
        Logger.i(TAG,"loadSplashAd enter , " + this,BRCOUNT);
        this.adType = AdType.SPLASH;
        this.handlerVersion = SplashVersion.V2;
        //策略
        IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
        //广告类型
        IClientServcie clientServcie = ServiceManager.getService(IClientServcie.class);
        //ViewGroup  广告容器
        adContainer = adStrategyService.applyStrategy(this);
        clientServcie.loadSplashAd(this,Preconditions.checkNotNull(splashAdListener));
        return this;
    }

    /**
     * 加载横幅广告
     */
    public AdRequest loadBannerAd(final BannerAdListener bannerAdListener){
        check();
        Logger.i(TAG,"loadBannerAd enter , " + this,BRCOUNT);
        this.adType = AdType.BANNER;
        IClientServcie clientServcie = ServiceManager.getService(IClientServcie.class);
        IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
        adContainer = adStrategyService.applyStrategy(this);
        clientServcie.loadBannerAd(this,Preconditions.checkNotNull(bannerAdListener));
        return this;
    }

    /**
     * 加载插屏广告
     */
    public AdRequest loadInterstitialAd(final InterstitialAdListener interstitialAdListener){
        check();
        Logger.i(TAG,"loadInterstitialAd enter , " + this,BRCOUNT);
        this.adType = AdType.INTERSTITIAL;
        IClientServcie clientServcie = ServiceManager.getService(IClientServcie.class);
        clientServcie.loadInterstitialAd(this,Preconditions.checkNotNull(interstitialAdListener));
        return this;
    }

    /**
     * 加载激励视频
     */
    public AdRequest loadRewardVideoAd(final RewardVideoAdListener rewardVideoAdListener){
        check();
        Logger.i(TAG,"loadRewardVideoAd enter , " + this,BRCOUNT);
        this.adType = AdType.REWARD_VIDEO;
        IClientServcie clientServcie = ServiceManager.getService(IClientServcie.class);
        clientServcie.loadRewardVideoAd(this,Preconditions.checkNotNull(rewardVideoAdListener));
        return this;
    }

    /**
     * 加载信息流中的广告
     */
    public AdRequest loadFeedListAd(final FeedListAdListener feedListAdListener){
        check();
        Logger.i(TAG,"loadFeedListAd enter , " + this,BRCOUNT);
        this.adType = AdType.INFORMATION_FLOW;
        IClientServcie clientServcie = ServiceManager.getService(IClientServcie.class);
        clientServcie.loadFeedListAd(this,Preconditions.checkNotNull(feedListAdListener));
        return this;
    }

    void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("must be invoked from the main thread.");
        }
    }

    void check(){
        if(!AdClientContext.isRealy()){
            throw new AdSdkRuntimeException("please invoke init");
        }
    }

    /**
     * 将这个请求对应的资源都释放掉
     */
    public boolean recycle(){

        isRecycled = true;

        ThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(recycler != null){
                    recycler.recycle();
                    recycler = null;
                }

                if(splashBottomLogo != null && !splashBottomLogo.isRecycled()){
                    splashBottomLogo.recycle();
                    splashBottomLogo = null;
                }

                extParameters.clear();

            }
        });

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdRequest adRequest = (AdRequest) o;

        if (!getRequestId().equals(adRequest.getRequestId())) return false;
        return getCodeId().equals(adRequest.getCodeId());
    }

    @Override
    public int hashCode() {
        int result = getRequestId().hashCode();
        result = 31 * result + getCodeId().hashCode();
        return result;
    }

    @Override
    public String toString() {

        StringBuilder extParametersStringBuilder = null;

        if(extParameters.size() > 0){
            extParametersStringBuilder = new StringBuilder();
            for(Iterator<String> iter = extParameters.keySet().iterator();iter.hasNext();){
                String key = iter.next();
                extParametersStringBuilder.append("key = ").append(key).append(" ,  value = ").append(extParameters.getString(key));
            }
        }

        return "AdRequest{" +
                "codeId='" + codeId + '\'' +
                ", requestId=" + requestId +
                ", activity=" + activity +
                ", adType=" + adType +
                ", adSize=" + adSize +
                ", adRequestCount=" + adRequestCount +
                ", timeoutMs=" + timeoutMs +
                ", splashBottomLogo=" + splashBottomLogo +
                ", rewardAmount=" + rewardAmount +
                ", rewardName=" + rewardName +
                ", userID=" + userID +
                ", extParameters=" + (extParametersStringBuilder != null ? extParametersStringBuilder.toString() : "empty") +
                '}';
    }

}
