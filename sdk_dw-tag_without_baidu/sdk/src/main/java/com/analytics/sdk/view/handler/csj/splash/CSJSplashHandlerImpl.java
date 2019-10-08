package com.analytics.sdk.view.handler.csj.splash;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.DataSource;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.BeanUtils;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.helper.SkipViewCoundownTimer;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ErrorMessage;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.view.handler.common.SplashBasicHandler;
import com.analytics.sdk.view.handler.common.SplashViewBuilder;
import com.analytics.sdk.view.handler.csj.TTAdManagerHolder;
import com.analytics.sdk.view.strategy.crack.CSJProxyApplication;
import com.analytics.sdk.view.strategy.crack.ProxyContext;
import com.analytics.sdk.view.strategy.os.AndroidHack;
import com.analytics.sdk.view.strategy.os.AndroidHackHelper;
import com.analytics.sdk.view.strategy.os.PackageManagerHack;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 穿山甲开屏处理
 */
public class CSJSplashHandlerImpl extends SplashBasicHandler {

    static final String TAG = "CSJSplashHandlerImpl";

    protected ClickLoction location = new ClickLoction();
    private TTAdNative mTTAdNative;
    private SkipViewCoundownTimer countDownTimer;
    private TextView skipButton;
    private Activity activity;
    private TTFeedAd sttFeedAd;
    protected List<String> arrSkipTrackUrl = new ArrayList<>();

    @Override
    protected void onHandleAd(AdResponse adResponse, AdListeneable clientAdListener,ConfigBeans configBeans) throws AdSdkException {
        this.activity = adResponse.getClientRequest().getActivity();

        if(adResponse.getResponseData().isTemplateFillType()) { //正常开屏填充
            handleSplashWithNormal();
        } else if(adResponse.getResponseData().isSelfRenderFillType()) { //信息流填充
            handleSplashWithFeedList();
        }
    }

    private void handleSplashWithFeedList() throws AdSdkException {

        final AdRequest adClientRequest = adResponse.getClientRequest();
        Logger.i(TAG,"handleSplashWithFeedList enter , " + adClientRequest);

        final Activity activity = adClientRequest.getActivity();

        ConfigBeans configBeans = adResponse.getResponseData().getValidConfigBeans();

        String appId = configBeans.getAppId();
        String appName = configBeans.getAppName();

        PublicUtils.initCSJAppId(activity,appId,appName);
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(activity);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(activity);

        try {
            //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
            if (configBeans.getWidth()<=0||configBeans.getHeight()<=0){
                configBeans.setWidth(1080);
                configBeans.setHeight(1920);
            }
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(configBeans.getSlotId())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(configBeans.getWidth(), configBeans.getHeight())
                    .setAdCount(1)
                    .build();
            //step5:请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
            mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
                @Override
                public void onError(int code, String message) {
                    Logger.i(TAG,"handleSplashWithNormal onError enter");

                    AdError adError = new AdError(code,ErrorMessage.Ad.ERROR_GET_ADS+"(src="+DataSource.SDK_CSJ +")");

                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));

                }

                @Override
                public void onFeedAdLoad(List<TTFeedAd> ads) {
                    if (ads == null || ads.isEmpty()) {
                        AdError adError = new AdError(ErrorCode.SDKCSJ.ERROR_RESPONSE_DATA_EMPTY,ErrorMessage.Ad.ERROR_DATA_EMPTY + "(src="+DataSource.SDK_CSJ +")");
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));
                        return;
                    }
                    TTFeedAd ttFeedAd = ads.get(0);

                    sttFeedAd = ttFeedAd;

                    YdtAdBean ydtAdBean = new YdtAdBean();
                    List<YdtAdBean.MetaGroupBean> metaGroupBeans = new ArrayList<>();
                    YdtAdBean.MetaGroupBean metaGroupBean = new YdtAdBean.MetaGroupBean();
                    metaGroupBean.setAdTitle(ttFeedAd.getTitle());
                    List<String> imageList = new ArrayList<>();
                    for (int k = 0;k < ttFeedAd.getImageList().size();k++){
                        imageList.add(ttFeedAd.getImageList().get(k).getImageUrl());
                    }
                    metaGroupBean.setImageUrl(imageList);
                    metaGroupBean.setAppSize(ttFeedAd.getAppSize());
                    List<String> descList=new ArrayList<>();
                    descList.add(ttFeedAd.getDescription());
                    metaGroupBean.setDescs(descList);
                    List<String> iconList=new ArrayList<>();
                    iconList.add(ttFeedAd.getIcon().getImageUrl());
                    metaGroupBean.setIconUrls(iconList);
                    metaGroupBean.setInteractionType(ttFeedAd.getInteractionType());
                    metaGroupBeans.add(metaGroupBean);
                    ydtAdBean.setMetaGroup(metaGroupBeans);

                    SplashViewBuilder.build(adResponse,ydtAdBean,new SplashViewBuilder.BuildListener(){

                        @Override
                        public void onShow(SplashViewBuilder builder) {
                            touTiaoClick(builder.getSplashRootLayout(),builder.getAdSkipView(),5300);
                        }

                        @Override
                        public void onError() {
                            onAdError();
                        }

                        @Override
                        public void onSkipClicked() {
                            BeanUtils.track(arrSkipTrackUrl,activity,location);
                            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
                            stopCountDownTimer();
                        }
                    }).start();

                }
            });
        }catch (Exception e){
            Logger.i(TAG,"CSJSplashHandlerImpl AdSdkException = " + e.getMessage());
            throw new AdSdkException(ErrorCode.Api.ERROR_HANDLE,e);
        }

    }

    public void touTiaoClick(final ViewGroup splashRootLayout,final TextView adSkipView,final int skipTime) {

        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(splashRootLayout);
        Logger.i(TAG,"handleClick");

        sttFeedAd.registerViewForInteraction(splashRootLayout, clickViewList, null, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse));
                Logger.i(TAG,"onAdClicked");
                stopCountDownTimer();
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                Logger.i(TAG,"onAdCreativeClick");
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                countDownTimer = new SkipViewCoundownTimer(adSkipView, new SkipViewCoundownTimer.OnFinishListener() {
                    @Override
                    public void onFinish() {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
                    }
                }, skipTime, 1000);
                countDownTimer.start();
                Logger.i(TAG,"onAdShow");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_SHOW,adResponse));
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse));
            }
        });

        switch (sttFeedAd.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                sttFeedAd.setActivityForDownloadApp(activity);
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:

                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                stopCountDownTimer();
                break;
            default:
        }

    }

    public void onAdError(){
        AdError adError = new AdError(ErrorCode.SDKCSJ.ERROR_NO_AD,"返回广告失败(src="+DataSource.SDK_CSJ +")");
        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));
    }

    private void handleSplashWithNormal() throws AdSdkException {
        final AdRequest adClientRequest = adResponse.getClientRequest();

        Logger.i(TAG,"handleSplashWithNormal enter , " + adClientRequest);

        int fetchDelay = adClientRequest.getTimeoutMs();
        final Activity activity = adClientRequest.getActivity();

        try {
            ConfigBeans configBeans = adResponse.getResponseData().getValidConfigBeans();

            final String pkg = configBeans.getPkg();
            String appId = configBeans.getAppId();
            String appName = configBeans.getAppName();

            if(!activity.getPackageName().equals(pkg)) {
                PackageManagerHack.enable();
                appName = "花生头条";
                ProxyContext proxyContext = new ProxyContext(activity,pkg);
                TTAdManagerHolder.init(proxyContext,appId, appName);
                AndroidHackHelper.manageProxyContext(pkg,proxyContext);
                try {
                    Class classH =  Class.forName("com.ss.android.downloadlib.a.h");
                    Class [] params = { Context.class };
                    Method method = classH.getMethod("a",params);
                    Object[] objects = { activity };
                    method.invoke(null,objects);
                }catch (Exception e){
                    Logger.i(TAG,"init exception = " + e.getMessage());
                }
            } else {
                PublicUtils.initCSJAppId(activity,appId,appName);
            }

            Logger.i(TAG,configBeans.toString());

            final long startTime = System.currentTimeMillis();

            //step2:创建TTAdNative对象
            mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);

            if (configBeans.getWidth()<=0 || configBeans.getHeight()<=0){
                configBeans.setWidth(1080);
                configBeans.setHeight(1920);
            }
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(configBeans.getSlotId())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(configBeans.getWidth(), configBeans.getHeight())
                    .build();
            //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
            mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                @Override
                @MainThread
                public void onError(int code, String message) {
                    Logger.i(TAG,"handleSplashWithNormal onError enter , code = " + code + " , message = " + message);

                    AdError adError = new AdError(code,ErrorMessage.Ad.ERROR_GET_ADS + "(src="+DataSource.SDK_CSJ +"), ext = " + (AdConfig.getDefault().isPrintLog() ? message : "empty"));

                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));

                    tryRemoveInjectProxyContext(pkg);

                }

                @Override
                @MainThread
                public void onTimeout() {
                    Logger.i(TAG,"handleSplashWithNormal onTimeout enter");

                    AdError adError = new AdError(ErrorCode.SDKCSJ.ERROR_TIMEOUT,ErrorMessage.Ad.ERROR_GET_ADS_TIMEOUT + "(src="+DataSource.SDK_CSJ+")");

                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));
                    tryRemoveInjectProxyContext(pkg);
                }

                @Override
                @MainThread
                public void onSplashAdLoad(TTSplashAd ad) {
                    Logger.i(TAG,"handleSplashWithNormal onSplashAdLoad enter");
                    onRequestEnd();
                    if (ad == null) {
                        AdError adError = new AdError(ErrorCode.SDKCSJ.ERROR_RESPONSE_DATA_EMPTY,ErrorMessage.Ad.ERROR_DATA_EMPTY+"(src="+DataSource.SDK_CSJ +")");
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));
                        return;
                    }

                    View splashView = CSJSplashAdViewBuilder.build(adResponse,ad);

                    ViewGroup adContainer = adClientRequest.getAdContainer();
                    adContainer.addView(splashView);

                    Logger.i(TAG,"handleSplashWithNormal onSplashAdLoad exit , used time = " + (System.currentTimeMillis() - startTime));

                }
            }, fetchDelay);
            Logger.i(TAG,"CSJSplashHandlerImpl.handleSplashWithNormal exit");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(TAG,"CSJSplashHandlerImpl.handleSplashWithNormal AdSdkException = " + e.getMessage());
            throw new AdSdkException(ErrorCode.SDKCSJ.ERROR_HANDLE,ErrorMessage.Ad.ERROR_HANDLE);
        }
    }

    private void stopCountDownTimer() {
        if (countDownTimer !=null){
            countDownTimer.cancel();
        }
    }

    @Override
    public boolean recycle() {
        super.recycle();
        stopCountDownTimer();
        countDownTimer = null;
        mTTAdNative = null;
        adResponse = null;
        activity = null;
        skipButton = null;
        return true;
    }


}
