package com.adsdk.demo;

import android.app.Application;
import android.util.Log;
import android.view.View;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.SdkConfiguration;

public class DemoApplication extends Application {

    static DemoApplication demoApplication;

    public static Application get(){
        return demoApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("DemoApplication", "onCreate enter");
        demoApplication = this;

        AdRequest.init(this,new SdkConfiguration.Builder()
                .setAppName("test")
                .build());

//        Ads.instance(this,"test");

    }
}
