package com.analytics.sdk.client;

import android.content.Context;

import com.analytics.sdk.BuildConfig;
import com.analytics.sdk.common.cache.CacheHelper;
import com.analytics.sdk.common.data.DataProvider;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.http.toolbox.HttpHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.common.runtime.broadcast.LocalBroadcastManager;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.debug.DebugReceiver;
import com.analytics.sdk.exception.AdSdkRuntimeException;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.IAdService;
import com.analytics.sdk.service.report.GlobalEventReporter;
import com.analytics.sdk.view.activity.FloatWindowActivity;
import com.analytics.sdk.view.strategy.os.AndroidHack;

import org.json.JSONException;

public class AdClientContext {

    static final String TAG = AdClientContext.class.getSimpleName();

    public static long sInitTime = 0;

    static Context gContext;
    static SdkConfiguration gSdkConfiguration;
    public static int displayWidth;
    public static int displayHeight;
    public static int statusBarHeight;

    static void init(Context context, SdkConfiguration sdkConfiguration){

        // TODO: 2019/7/1 加异常上报
        if(context == null){
            throw new AdSdkRuntimeException("context is null");
        }

        if(sdkConfiguration == null){
            throw new AdSdkRuntimeException("sdkConfiguration is null");
        }

        sInitTime = System.currentTimeMillis();

        gContext = context;
        gSdkConfiguration = new SdkConfiguration.Builder(sdkConfiguration).build();
        displayHeight = UIHelper.getScreenHeight(context);
        displayWidth = UIHelper.getScreenWidth(context);
        statusBarHeight = UIHelper.getStatusBarHeight(context);

        long start = System.currentTimeMillis();

        //基础技术层初始化
        ThreadExecutor.init();
        CacheHelper.init(context);
        DebugReceiver.startReceiver(context);
        HttpHelper.init(context);
        DataProvider.initDefault(context);
        LocalBroadcastManager.init(context);
        Logger.forcePrint("timeTrace","init base used time " + (System.currentTimeMillis() - start) + " ms");

        //业务层初始化
        AdConfig.printDefault();
        ServiceManager.init(context);
        GlobalEventReporter.getDefault().registerSelf();
        AndroidHack.init(context);

        if(AdConfig.getDefault().isPrintLog()){
            FloatWindowActivity.showFloatWindow(context);
        }

        Logger.forcePrint("timeTrace","init used time " + (System.currentTimeMillis() - start) + " ms");

    }

    public static void init(Context context){
        if(context == null){
            throw new AdSdkRuntimeException("context is null");
        }

        IAdService adService = ServiceManager.getService(IAdService.class);
        try {
            adService.initAdConfig(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static long getInitTime(){
        return sInitTime;
    }

    public static Context getClientContext(){

        if(gContext == null){
            throw new RuntimeException("please init Sdk");
        }

        return gContext;
    }

    public static boolean isRealy() {
        return (AdClientContext.gContext != null);
    }

}
