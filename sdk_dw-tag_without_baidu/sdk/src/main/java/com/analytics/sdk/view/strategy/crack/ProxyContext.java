package com.analytics.sdk.view.strategy.crack;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.log.Logger;

public class ProxyContext extends ContextThemeWrapper {

    static final String TAG = "proxyHook";

    Context context;
    private String startActivityClassPrefix = "";
    private String proxyPackageName = "";
    private boolean isEnable = true;
    private ProxyApplication proxyApplication;

    public ProxyContext(Context context) {
        super(context,0);
        this.context = context;
    }

    public ProxyContext(Context base, String proxyPackageName) {
        super(base,0);
        this.context = base;
        this.proxyPackageName = proxyPackageName;
    }

    public ProxyContext(Context base, String proxyPackageName,String startActivityClassPrefix) {
        super(base,0);
        this.context = base;
        this.proxyPackageName = proxyPackageName;
        this.startActivityClassPrefix = startActivityClassPrefix;
    }


    @Override
    public ApplicationInfo getApplicationInfo() {
        if(isEnable){
            ApplicationInfo applicationInfo =  context.getApplicationInfo();
            applicationInfo.packageName = proxyPackageName;
            applicationInfo.processName = proxyPackageName;
            return applicationInfo;
        }
        return context.getApplicationInfo();
    }

    public String getOpPackageName() {
        Logger.i(TAG,"ProxyContext#getOpPackageName enter, isEnable = " + isEnable);
        if(isEnable){
            return proxyPackageName;
        }
        return context.getPackageName();
    }

    public String getBasePackageName() {
        Logger.i(TAG,"ProxyContext#getBasePackageName enter, isEnable = " + isEnable);
        if(isEnable){
            return proxyPackageName;
        }
        return context.getPackageName();
    }

    @Override
    public Resources getResources() {
        return context.getResources();
    }

    @Override
    public Resources.Theme getTheme() {
        return context.getTheme();
    }

    @Override
    public String getPackageName() {
        Logger.i(TAG,"ProxyContext#getPackageName enter, isEnable = " + isEnable);
        if(isEnable){
            return proxyPackageName;
        }
        return context.getPackageName();
    }

    private Intent injectStartActivityIntent(Intent intent){
        try {

            ComponentName componentName = intent.getComponent();
            Logger.i(TAG,"injectStartActivityIntent enter , " + componentName + " , " + Log.getStackTraceString(new Exception("injectStartActivityIntent")));

            if(componentName != null){
                String className = componentName.getClassName();
                String packageName = componentName.getPackageName();

                if(proxyPackageName.equals(packageName) && (className != null && className.startsWith(startActivityClassPrefix))) {
                    intent.setClassName(context.getPackageName(),className);
                    intent.setPackage(context.getPackageName());
                    Logger.i(TAG,"injectStartActivityIntent reset packageName name");
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return intent;
    }

    private Intent injectSendBroadcastIntent(Intent intent){
        try {
            String packageName = intent.getPackage();

            if(proxyPackageName.equals(packageName)) {
                intent.setPackage(context.getPackageName());
                Logger.i(TAG,"injectSendBroadcastIntent reset packageName name");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return intent;
    }

    public String getStartActivityClassPrefix() {
        return startActivityClassPrefix;
    }

    @Override
    public void startActivity(Intent intent) {
        Logger.i(TAG,"startActivity context = " + context);
        intent = injectStartActivityIntent(intent);
        AdClientContext.getClientContext().startActivity(intent);
    }

    @Override
    public ComponentName startService(Intent service) {
        injectStartActivityIntent(service);
        return context.startService(service);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        intent = injectStartActivityIntent(intent);
        AdClientContext.getClientContext().startActivity(intent, options);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        intent = injectSendBroadcastIntent(intent);
        context.sendBroadcast(intent);
    }

    @Override
    public void sendBroadcast(Intent intent, @Nullable String receiverPermission) {
        intent = injectSendBroadcastIntent(intent);
        context.sendBroadcast(intent, receiverPermission);
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, @Nullable String receiverPermission) {
        intent = injectSendBroadcastIntent(intent);
        context.sendBroadcast(intent,receiverPermission);
    }

    @Override
    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String receiverPermission, @Nullable BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        intent = injectSendBroadcastIntent(intent);
        context.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        context.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        context.unregisterComponentCallbacks(callback);
    }

    public void disable(){
        isEnable = false;
        Logger.i(TAG,"disable enter , packageName = " + proxyPackageName);
    }

    public void enable(){
        isEnable = true;
        Logger.i(TAG,"enable enter , packageName = " + proxyPackageName);
    }

    @Override
    public Context getApplicationContext() {
        if(isEnable){
            if(proxyApplication == null){
                proxyApplication = new ProxyApplication(this);
            }
            return proxyApplication;
        }
        return context.getApplicationContext();
    }

}
