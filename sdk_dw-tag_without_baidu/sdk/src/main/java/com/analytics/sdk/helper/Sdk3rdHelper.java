package com.analytics.sdk.helper;

import android.content.Context;
import android.text.TextUtils;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;
import com.qq.e.ads.cfg.GDTAD;


public final class Sdk3rdHelper {

    static final String TAG = Sdk3rdHelper.class.getSimpleName();

    public static void init3rdSDK(Context context) {
        if(AdConfig.getDefault().getAd3rdSdkConfig().isSupport3rdSdkDefaultConfig()){
            Logger.i(TAG,"START INIT GDT SDK");

            String appId = AdConfig.getDefault().getAd3rdSdkConfig().getSplashDefualtAppId();

            if(!TextUtils.isEmpty(appId)) {
                GDTAD.initSDK(context, appId, new GDTAD.InitListener() {
                    @Override
                    public void onSuccess() {
                        Logger.i(TAG,"INIT GDT SDK SUCCESS");
                    }
                });
            }

        }
    }

}
