package com.analytics.sdk.service.ad;

import android.content.Context;

import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.config.CodeIdConfig;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.IService;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.config.ServerInitConfig;
import com.analytics.sdk.view.handler.AdHandler;

import org.json.JSONException;

public interface IAdService extends IService {

    /**
     * 1.先尝试走缓存
     * 2.如果缓存中不存在则走默认，同时去请求服务器
     */
    void loadAdData(AdRequest adClientRequest,Listener<AdResponse,String> listener);

    /**
     * 从缓存中获取数据
     */
    ResponseData loadAdDataFromCache(AdRequest adRequest);

    /**
     * 强制请求服务器来更新数据
     */
    void requestServerAdData(AdRequest adClientRequest, Listener<AdResponse,String> listener);

    /**
     * 根据广告位ID获取配置
     */
    CodeIdConfig getCodeIdConfig(String codeId);

    /**
     * 获取对应的广告处理器(比如：如果adResponse中是广点通的则返回广点通的处理器)
     */
    AdHandler getAdHandler(AdResponse adResponse);

    /**
     * 获取默认的配置
     * 1. 目前只针对开屏
     */
    ResponseData getDefaultResponseData(AdRequest adClientRequest);
    /**
     * 跳转WebView
     */
     int JMP = 1;
    /**
     * 下载
     */
     int DOWNLOAD = 2;

    /**
     * 初始化广告配置
     */
    void initAdConfig(final Context context) throws JSONException;

    /**
     * 广告处理
     */
    AdHandler handleApiAd(AdResponse adResponse, AdListeneable adListeneable) throws AdSdkException;

}
