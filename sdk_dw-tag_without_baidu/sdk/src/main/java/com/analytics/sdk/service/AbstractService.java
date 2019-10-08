package com.analytics.sdk.service;

import android.content.Context;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.data.DataProvider;
import com.analytics.sdk.common.log.Logger;

/**
 * register | unregister 空实现，只是为了适配 , 所有实现在Interceptor中
 */
public abstract class AbstractService implements IService{

    Class<? extends IService> serviceClass;
    DataProvider dataProvider = null;
    protected Context context;

    public AbstractService(Class<? extends IService> serviceClass){
        this.serviceClass = serviceClass;
        dataProvider = DataProvider.newProvider(AdClientContext.getClientContext(),serviceClass.getName());
        if(serviceClass == null){
            throw new RuntimeException("serviceClass is null");
        }
    }

    @Override
    public void init(Context context) {
        log(serviceClass,"init enter");
    }

    @Override
    public void destory() {
        log(serviceClass,"destory enter");
    }

    @Override
    public void attach(Context context) {
        this.context = context;
    }

    @Override
    public void detach() {
        this.context = null;
    }

    protected boolean isAttached(){
        return (context != null);
    }

    @Override
    public void log(Class<?> clazz, String message,Object... bizObjects) {
        //default impl , main impl in interceptor
        Logger.i(clazz.getSimpleName(),message);
    }

    @Override
    public boolean isSupportHotfix() {
        return false;
    }

    @Override
    public void register(Object subscriber) {
        //do nothing , interceptor impl
    }

    @Override
    public void unregister(Object subscriber) {
        //do nothing , interceptor impl
    }

    @Override
    public DataProvider getDataProvider() {
        return dataProvider;
    }

}
