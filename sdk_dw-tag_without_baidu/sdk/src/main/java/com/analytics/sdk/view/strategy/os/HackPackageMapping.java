package com.analytics.sdk.view.strategy.os;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import com.analytics.sdk.BuildConfig;
import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.config.AdConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class HackPackageMapping {

    public static final String GDT_WEBVIEW_ACTIVITY = "com.qq.e.ads.ADActivity";

    public static boolean isGQTWebActivity(Activity activity){

        if(activity == null){
            return false;
        }

        String activityName = activity.getClass().getName();
        return GDT_WEBVIEW_ACTIVITY.equals(activityName);
    }

    public static boolean isMainActivity(Activity activity) {

        if(activity == null){
            return false;
        }

        if(activity.getClass().getName().equals(AdConfig.getDefault().getAd3rdSdkConfig().getMainActivityName())) {
            return true;
        }

        return false;
    }

    public static boolean isMainActivity(Intent intent) {

        if(intent == null){
            return false;
        }

        if(intent.getComponent() == null){
            return false;
        }

        String className = intent.getComponent().getClassName();

        if(className != null && className.equals(AdConfig.getDefault().getAd3rdSdkConfig().getMainActivityName())) {
            return true;
        }

        return false;
    }

    public static boolean isSplashActivity(Intent intent) {

        if(intent == null){
            return false;
        }

        if(intent.getComponent() == null){
            return false;
        }

        String className = intent.getComponent().getClassName();

        if(className != null && className.equals(AdConfig.getDefault().getAd3rdSdkConfig().getSplashActivityName())) {
            return true;
        }

        return false;
    }

    public static boolean isSplashActivity(Activity activity){
        if(activity == null){
            return false;
        }
        if(activity.getClass().getName().equals(AdConfig.getDefault().getAd3rdSdkConfig().getSplashActivityName())) {
            return true;
        }
        return false;
    }

}




