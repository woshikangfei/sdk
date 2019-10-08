package com.analytics.sdk.service;

/**
 * 数据来源(SDK | API)
 */
public interface DataSource {
    /**
     * 未知
     */
    int UNKNOW = -1;
    /**
     * 百度SDK
     */
    int SDK_BAIDU = 1;
    /**
     * 广点通SDK
     */
    int SDK_GDT = 100;
    /**
     * 穿山甲SDK
     */
    int SDK_CSJ = 101;

    /**
     * 百度API
     */
    int API_BAIDU = 1;
    /**
     * 穿山甲API
     */
    int API_CSJ = 2;
    /**
     * 广点通API
     */
    int API_GDT = 21;

    int API = 22;

}
