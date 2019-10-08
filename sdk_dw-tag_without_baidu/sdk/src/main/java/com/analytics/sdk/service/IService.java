package com.analytics.sdk.service;

import android.content.Context;

import com.analytics.sdk.common.data.DataProvider;

/**
 * register与unregister由 RegisterSubscibeInterceptor来完成
 */
public interface IService {
    /**
     * 初始化服务
     */
    void init(Context context);
    /**
     * 释放业务服务
     */
    void destory();
    /**
     * 为当前服务附着一个context,实现会根据自身情况转换成不同的实例(比如：转换成Activity)
     * @param context
     */
    void attach(Context context);
    /**
     * 一旦附着了某个context(如果这个context是一个activity或者view等)，一定要detach，否则会泄漏
     */
    void detach();

    /**
     * 注册服务对应的订阅者
     */
    void register(Object subscriber);
    void unregister(Object subscriber);
    void log(Class<?> clazz, String message, Object... bizObjects);
    DataProvider getDataProvider();
    boolean isSupportHotfix();
}
