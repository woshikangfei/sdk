package com.analytics.sdk.view.strategy.os;

import android.content.Context;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.AdEventActions;

public final class AndroidHack {

    static final String TAG = AndroidHack.class.getSimpleName();
    public static boolean isBlockStartActivity = false;
    /**
     * 首页等待广告落地页启动之后才打开。
     * 要求：首页的下面是广告落地页
     */
    public static boolean isMainActivityWaitAdWebActivity = false;

    public static void init(final Context context){

        long start = System.currentTimeMillis();
        if(!AdConfig.getDefault().isHookSystem()){
            Logger.i(TAG,"hack system not support");
            return;
        }
        init2(context);
        Logger.forcePrint("timeTrace","init hack used time " + (System.currentTimeMillis() - start) + " ms");
    }

    public static void init2(final Context context){
        try {
            AndroidHackHelper.injectInstrumentation(context);
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_HACK,"injectInstrumentation success"));
        } catch (Exception e) {
            e.printStackTrace();
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_HACK,"injectInstrumentation error("+e.getMessage()+")"));
        }

        try {
            AndroidHackHelper.injectIActivityManager(context);
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_HACK,"injectIActivityManager success"));
        } catch (Exception e) {
            e.printStackTrace();
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_HACK,"injectIActivityManager error("+e.getMessage()+")"));
        }

        try {
            AndroidHackHelper.injectActivityThreadHandler(context);
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_HACK,"injectActivityThreadHandler success"));
        } catch (Exception e) {
            e.printStackTrace();
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_HACK,"injectActivityThreadHandler error("+e.getMessage()+")"));
        }

        try {
            AndroidHackHelper.injectIPackageManager(context);
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_HACK,"injectIPackageManager success"));
        } catch (Exception e) {
            e.printStackTrace();
            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_HACK,"injectIPackageManager error("+e.getMessage()+")"));
        }
    }

}
