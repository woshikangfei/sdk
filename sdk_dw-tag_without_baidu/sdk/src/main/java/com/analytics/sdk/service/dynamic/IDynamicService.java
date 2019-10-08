package com.analytics.sdk.service.dynamic;

import com.analytics.sdk.service.IService;


public interface IDynamicService extends IService{
    /**
     * 执行新动态包的抓取、热修复、动态执行。
     */
    void c();

    /**
     * 定时执行c任务
     */
    void ct();

    void tryC();
}
