package com.analytics.sdk.view.handler.gdt.splash;

import android.app.Activity;
import android.view.ViewGroup;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.network.TrafficTracker;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ErrorMessage;
import com.analytics.sdk.service.ad.StrategyHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.view.handler.common.SplashBasicHandler;
import com.analytics.sdk.view.strategy.StrategyRootLayout;
import com.analytics.sdk.view.strategy.os.AndroidHack;
import com.analytics.sdk.view.strategy.os.InstrumentationHack;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

/**
 * 修改通知方式
 */
public class GDTSplashHandlerImplV8 extends SplashBasicHandler {

    static final String TAG = "GDTSplashHandlerImplV8";

    private StrategyRootLayout splashStrategyView;
    private long requestGDTTime = 0;
    boolean isHitCountdownStrategy = true;
    boolean isHitBlockMainActivityStrategy = true;

    /**
     * 开屏填充开屏的方法，也可信息流填充开屏
     */
    @Override
    protected void onHandleAd(AdResponse adResponse, AdListeneable clientAdListener,ConfigBeans configBeans) throws AdSdkException{
        if(adResponse.getResponseData().isTemplateFillType()){ //正常开屏填充
            handleSplashWithNormal(adResponse);
        } else if(adResponse.getResponseData().isSelfRenderFillType()) { //信息流填充
            handleSplashWithFeedList(adResponse);
        }
    }

    /**
     * 正常开屏填充
     */
    private void handleSplashWithNormal(final AdResponse adResponse) throws AdSdkException {

        final AdRequest adClientRequest = adResponse.getClientRequest();
        final ViewGroup adContainer = adClientRequest.getAdContainer();

        final ConfigBeans configBeans = adResponse.getResponseData().getValidConfigBeans();

        Logger.i(TAG,"handleSplashWithNormal enter , " + adClientRequest);

        final int fetchDelay = adClientRequest.getTimeoutMs();
        final Activity activity = adClientRequest.getActivity();

        splashStrategyView = (StrategyRootLayout) adContainer;

        isHitCountdownStrategy = StrategyHelper.isHitCountdownStrategy(adResponse);
        isHitBlockMainActivityStrategy = StrategyHelper.isHitBlockMainActivityStrategy(adResponse);
        InstrumentationHack.serverDelayTime = StrategyHelper.getBlockMainActivityTime(adResponse);

        Logger.i(TAG,"isHitCountdownStrategy = " + isHitCountdownStrategy + " ,isHitBlockMainActivityStrategy = " + isHitBlockMainActivityStrategy + " , serverDelayTime = " + InstrumentationHack.serverDelayTime);

        try {

            String appId = configBeans.getAppId();
            String posId = configBeans.getSlotId();

            Logger.i(TAG,"adContainer state = " + adContainer.getWindowVisibility() + " , isShown = " + adContainer.isShown());

            Logger.i(TAG,configBeans.toString());

            requestGDTTime = System.currentTimeMillis();
            TrafficTracker.beginTrace();
            new SplashAD(activity, splashStrategyView,appId, posId, new SplashADListener() {
                @Override
                public void onADDismissed() {
                    Logger.i(TAG,"handleSplashWithNormal onADDismissed()");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
                }

                @Override
                public void onNoAD(com.qq.e.comm.util.AdError adError) {

                    TrafficTracker.TrackResult trackResult = TrafficTracker.endTrace();
                    Logger.i(TAG,"onNoAD enter , " + trackResult);

                    final String extMessage = SdkHelper.buildErrorMessage(adContainer, splashStrategyView,adClientRequest,adError.getErrorCode(),adError.getErrorMsg(),requestGDTTime,trackResult);
                    AdError newAdError = new AdError(adError.getErrorCode(),adError.getErrorMsg(),extMessage);
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,newAdError));

                }

                @Override
                public void onADPresent() {

                    Logger.i(TAG,"onADPresent enter = " + TrafficTracker.endTrace());

                    long initTime = AdClientContext.getInitTime();
                    long current = System.currentTimeMillis();
                    Logger.i(TAG,"handleSplashWithNormal onADPresent() enter , GDT used time = " + (current - requestGDTTime) + " ms" + " , total(init+gdt) used time = " + (current - initTime));

                    //添加悬浮
//                    new FloatViewStrategy().apply(activity,splashStrategyView);

                    onRequestEnd();

                    splashStrategyView.apply(adResponse);

                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_SHOW,adResponse));

                }

                @Override
                public void onADClicked() {
                    Logger.i(TAG,"handleSplashWithNormal onADClicked()");

                    //当用户进行了点击,则忽略所有策略（误点后也会走click，将这两个变量置为初始化也没问题）
                    isHitCountdownStrategy = false;
                    isHitBlockMainActivityStrategy = false;

                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse));

                }

                @Override
                public void onADTick(long millisUntilFinished) {
                    Logger.i(TAG,"handleSplashWithNormal onADTick() , millisUntilFinished = " + millisUntilFinished);

                    //millisUntilFinished = 4977
                    //millisUntilFinished = 4478
//                    millisUntilFinished = 3978
//                    millisUntilFinished = 3479
//                    millisUntilFinished = 2980
//                    millisUntilFinished = 2481
//                    millisUntilFinished = 1981
//                    millisUntilFinished = 1481
//                    millisUntilFinished = 981
//                    millisUntilFinished = 481

                    //还剩1S时

                    if(millisUntilFinished <= 1600 && millisUntilFinished >= 1000){
                        if(isHitCountdownStrategy) {
                            AndroidHack.isMainActivityWaitAdWebActivity = true;
                            AndroidHack.isBlockStartActivity = false;
                            StrategyHelper.sendSimulateEventWithStrategyService(splashStrategyView,adResponse);
                        }
                    }

                    //时间已经读完
                    if(millisUntilFinished <= 600){

                        if(!isHitCountdownStrategy){
                            if(isHitBlockMainActivityStrategy) {
                                AndroidHack.isBlockStartActivity = true;
                                AndroidHack.isMainActivityWaitAdWebActivity = false;

                                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_BLOCK_MAINACTIVITY_C,adResponse));

                            }
                        }

                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_TICK_TIMEOVER,adResponse));
                    }
                }

                @Override
                public void onADExposure() {
                    Logger.i(TAG,"handleSplashWithNormal onADExposure()");

                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse));

                }
            }, fetchDelay);
            Logger.i(TAG,"handleSplashWithNormal exit");
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.i(TAG,"handleSplashWithNormal AdSdkException = " + e.getMessage());
            throw new AdSdkException(ErrorCode.SDKGDT.ERROR_HANDLE,ErrorMessage.Ad.ERROR_HANDLE);
        }
    }


    /**
     * 通过信息流填充开屏
     */
    @Deprecated
    void handleSplashWithFeedList(AdResponse adResponse) throws AdSdkException {
        throw new AdSdkException(ErrorCode.SDKGDT.ERROR_NO_AD,"not support");
    }

    @Override
    public boolean recycle() {
        return super.recycle();
    }
}
