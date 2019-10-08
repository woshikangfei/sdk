package com.analytics.sdk.view.handler;

import android.app.Activity;

import com.analytics.sdk.client.AdType;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.view.handler.api.splash.ApiSplashApkDownloadHandler;
import com.analytics.sdk.view.handler.api.splash.ApiSplashHandler;
import com.analytics.sdk.view.handler.api.video.APIRewardVideoHandlerImpl;
import com.analytics.sdk.view.handler.csj.banner.CSJBannerHandler2Impl;
import com.analytics.sdk.view.handler.csj.feedlist.CSJNativeADFeedListAdHandlerImpl;
import com.analytics.sdk.view.handler.csj.interstitial.CSJInterstitialHandlerImpl;
import com.analytics.sdk.view.handler.csj.splash.CSJSplashHandlerImpl;
import com.analytics.sdk.view.handler.csj.video.CSJRewardVideoHandlerImpl;
import com.analytics.sdk.view.handler.gdt.banner.GDTBannerHandlerImpl;
import com.analytics.sdk.view.handler.gdt.feedlist.GDTNative20ADFeedListAdHandlerImpl;
import com.analytics.sdk.view.handler.gdt.feedlist.GDTTemplateADFeedListAdHandlerImpl;
import com.analytics.sdk.view.handler.gdt.interstitial.GDTInterstitialHandlerImpl;
import com.analytics.sdk.view.handler.gdt.splash.GDTSplashHandlerImplV8New;
import com.analytics.sdk.view.handler.gdt.video.GDTRewardVideoHandlerImpl;

/**
 * 负责所有广告处理器的创建
 */
public abstract class AdHandlerFactory {
    /**
     * 创建开屏处理器
     */
    public abstract AdHandler createAdHandler(AdResponse adResponse);

    public static AdHandlerFactory factory(){
        return DEFAULT;
    }

    static final AdHandlerFactory DEFAULT = new AdHandlerFactory(){

        @Override
        public AdHandler createAdHandler(AdResponse adResponse) {

            Activity activity = adResponse.getClientRequest().getActivity();

            AdType requestAdType = adResponse.getClientRequest().getAdType();
            ResponseData responseData = adResponse.getResponseData();

            if(responseData.isSdkSource()){ //使用三方SDK

                if(AdType.SPLASH == requestAdType){
                    if(responseData.isGDTSource()){ //访问广点通SDK

                        return new GDTSplashHandlerImplV8New(); //广点通新版本 4.70.940
                    } else if(responseData.isCSJSource()){ //穿山甲
                        return new CSJSplashHandlerImpl();
                    }
                } else if(AdType.BANNER == requestAdType){

                    if(responseData.isGDTSource()){ //访问广点通SDK
                        return new GDTBannerHandlerImpl();
                    } else if(responseData.isCSJSource()){ //穿山甲
                        return new CSJBannerHandler2Impl(activity);
                    }
                } else if(AdType.INTERSTITIAL == requestAdType){

                    if(responseData.isGDTSource()) {
                        return new GDTInterstitialHandlerImpl();
                    } else if(responseData.isCSJSource()){
                        return new CSJInterstitialHandlerImpl(activity);
                    }

                } else if(AdType.REWARD_VIDEO == requestAdType){
                    if(responseData.isGDTSource()) {
                        return new GDTRewardVideoHandlerImpl();
                    } else if(responseData.isCSJSource()){
                        return new CSJRewardVideoHandlerImpl();
                    }

                } else if(AdType.INFORMATION_FLOW == requestAdType){
                    if(responseData.isGDTSource()) {
                        if( responseData.isTemplateFillType()){
                            //1=模板填充
                            return new GDTTemplateADFeedListAdHandlerImpl();
                        }
                        else if( responseData.isSelfRenderFillType()){
                            //广点通2.0自渲染填充
                            return new GDTNative20ADFeedListAdHandlerImpl();
                        }
                    }else if(responseData.isCSJSource()){
//                        if( responseData.getParams().get(0).getFillType()==1){
//                            //1=模板填充
//                            return new CSJTempADFeedListAdHandlerImpl();
//                        }
//                        else if( responseData.getParams().get(0).getFillType()==2){
//                            //自渲染填充
//                            return new CSJNativeADFeedListAdHandlerImpl();
//                        }
                        //自渲染填充
                        return new CSJNativeADFeedListAdHandlerImpl();
                    }
                }

            } else if(responseData.isApiSource()){ //api填充
                if(AdType.SPLASH == requestAdType){

                    if(adResponse.getResponseData().hasStrategyWithWebView()){
                        return new ApiSplashHandler();
                    } else {
                        return new ApiSplashApkDownloadHandler();
                    }

                } else if(AdType.BANNER == requestAdType){
                    return new CSJBannerHandler2Impl(activity); //API和穿山甲的实现在一起
                } else if(AdType.INTERSTITIAL == requestAdType){
                    return new CSJInterstitialHandlerImpl(activity);
                } else if(AdType.REWARD_VIDEO == requestAdType){
                    return new APIRewardVideoHandlerImpl();
                }
            }

            return AdHandler.EXCEPTION_HANDLER;
        }
    };

}
