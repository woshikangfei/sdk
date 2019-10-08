package com.analytics.sdk.view.handler.gdt.interstitial;

import android.app.Activity;

import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.view.handler.common.BasicAdHandler;
import com.analytics.sdk.view.strategy.click.GDTInterstitialHookStrategy;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.comm.util.AdError;

//插屏
public class GDTInterstitialHandlerImpl extends BasicAdHandler {

    static final String TAG = "GDTInterstitialHandlerImpl";

    private UnifiedInterstitialAD unifiedInterstitialAD;

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_HANDLER.clone()
                .addActionList(AdEventActions.BASE_INTERSTITIAL);
    }

    @Override
    protected void onHandleAd(AdResponse adResponse, AdListeneable clientAdListener, ConfigBeans configBeans) throws AdSdkException {
        loadInterstitial(adResponse.getClientRequest().getActivity());
    }

    @Override
    public boolean recycle() {
        super.recycle();
        destoryInterstitialAd();
        return true;
    }

    private void destoryInterstitialAd(){
        if(unifiedInterstitialAD != null){
            unifiedInterstitialAD.close();
            unifiedInterstitialAD.destroy();
            unifiedInterstitialAD = null;
        }
    }

    private void loadInterstitial(Activity activity) {

        try {
            unifiedInterstitialAD = new UnifiedInterstitialAD(activity, configBeans.getAppId(), configBeans.getSlotId(),new UnifiedInterstitialADListener() {
                @Override
                public void onADReceive() { //广告加载成功,插屏2.0广告加载完毕，此回调后才可以调用 show 方法
                    Logger.i(TAG,"loadInterstitial onADReceive enter , tid = " + Thread.currentThread().getId());

                    if(unifiedInterstitialAD!=null){
                        EventScheduler.dispatch(Event.obtain(AdEventActions.Interstitial.ACTOIN_AD_RECEIVE,adResponse));
                        unifiedInterstitialAD.show();
                        try {
                            new GDTInterstitialHookStrategy().hook(unifiedInterstitialAD,adResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        com.analytics.sdk.client.AdError adError = new com.analytics.sdk.client.AdError(ErrorCode.Interstitial.ERROR_HANDLE_EXCEPTION,"ad null");
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));
                    }
                }

                @Override
                public void onNoAD(AdError adError) {
                    Logger.i(TAG,"loadInterstitial onNoAD enter");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,new com.analytics.sdk.client.AdError(adError.getErrorCode(),adError.getErrorMsg())));
                }

                @Override
                public void onADOpened() { //广告展开时回调
                    Logger.i(TAG,"loadInterstitial onADOpened enter");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_SHOW,adResponse));
                    EventScheduler.dispatch(Event.obtain(AdEventActions.Interstitial.ACTION_AD_OPENED,adResponse));
                }

                @Override
                public void onADExposure() {
                    Logger.i(TAG,"loadInterstitial onADExposure enter");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse));
                }

                @Override
                public void onADClicked() {
                    Logger.i(TAG,"loadInterstitial onADClicked enter");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse));
                }

                @Override
                public void onADLeftApplication() { //广告点击离开应用时回调
                    Logger.i(TAG,"loadInterstitial onADLeftApplication enter");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.Interstitial.ACTION_AD_LEFT_APPLICATION,adResponse));
                }

                @Override
                public void onADClosed() {
                    Logger.i(TAG,"loadInterstitial onADClosed enter");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
                }
            });

            Logger.i(TAG,"loadInterstitial enter");
            unifiedInterstitialAD.loadAD();
            Logger.i(TAG,"loadInterstitial exit");
        } catch (Exception e){
            e.printStackTrace();
            com.analytics.sdk.client.AdError adError = new com.analytics.sdk.client.AdError(ErrorCode.Interstitial.ERROR_HANDLE_EXCEPTION,"处理异常："+e.getMessage());
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));
        }

    }


}
