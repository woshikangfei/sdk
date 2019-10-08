package com.analytics.sdk.view.handler.common;

import android.text.TextUtils;

import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.report.AdEventReporter;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.strategy.os.AndroidHackHelper;
import com.analytics.sdk.view.strategy.os.PackageManagerHack;

public abstract class BasicAdHandler implements AdHandler {

    static final String TAG = "BasicAdHandler";

    protected AdRequest adRequest;
    protected AdResponse adResponse;
    protected ConfigBeans configBeans;
    protected EventActionList eventActionList;
    private AdEventReporter adEventReporter;
    protected long requestStartTime = 0L;

    @Override
    public void handleAd(AdResponse adResponse, AdListeneable clientAdListener) throws AdSdkException {
        this.adResponse = adResponse;
        this.adRequest = adResponse.getClientRequest();

        try {
            this.configBeans = adResponse.getResponseData().getValidConfigBeans();
        } catch (Exception e){
            e.printStackTrace();
        }

        this.eventActionList = buildEventActionList();
        Logger.i(TAG,"handleAd " + adResponse + " , configBeans = " + configBeans);

        if(this.eventActionList != null){
            EventScheduler.addEventListener(eventActionList,adEventReporter = new AdEventReporter(adResponse.getClientRequest()));
        }

        onRequestStart();

        onHandleAd(adResponse,clientAdListener,configBeans);

    }

    protected abstract EventActionList buildEventActionList();
    protected abstract void onHandleAd(AdResponse adResponse, AdListeneable clientAdListener,ConfigBeans configBeans) throws AdSdkException;

    @Override
    public boolean recycle() {

        Logger.i(TAG,"recycle enter");
        if(this.eventActionList != null){
            EventScheduler.deleteEventListener(this.eventActionList,adEventReporter);
        }

        if(adEventReporter != null){
            adEventReporter.recycle();
            adEventReporter = null;
        }

        return true;
    }

    protected void onRequestStart(){
        requestStartTime = System.currentTimeMillis();
        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_REQUEST,adResponse));
    }

    protected void onRequestEnd(){
        long end = System.currentTimeMillis();

        Logger.i(TAG,"** request end, used time = " + (end - requestStartTime));
    }

    protected void tryRemoveInjectProxyContext(String disrealPackageName){
        if(!TextUtils.isEmpty(disrealPackageName)) {
            AndroidHackHelper.removeInjectProxyContext(disrealPackageName);
            PackageManagerHack.disable();
        }
    }

}
