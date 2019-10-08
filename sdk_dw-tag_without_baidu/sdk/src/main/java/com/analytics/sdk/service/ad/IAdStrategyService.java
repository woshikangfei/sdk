package com.analytics.sdk.service.ad;

import android.view.ViewGroup;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.service.IService;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdStragegyWorkArgs;

import java.util.Map;

public interface IAdStrategyService extends IService {

    enum CallResult {
        /**
         * 调用SUPER
         */
        CALL_SUPER,
        /**
         * 继续递归
         */
        CALL_RECURSION,
        /**
         * 事件被消耗了
         */
        CALL_RETURN_TRUE,
        CALL_RESULT_FALSE,
    }

    CallResult dispatchTouchEvent(AdStragegyWorkArgs adStragegyWorkArgs);

    /**
     * 信息流
     */
    CallResult dispatchTouchEventWithFeedlist2(AdStragegyWorkArgs adStragegyWorkArgs);

    /**
     * 开屏策略
     */
    ViewGroup applyStrategy(AdRequest adRequest);

    /**
     * 当点击被命中
     */
    void onRandomClickHit(AdResponse adResponse);

    /**
     * 发送模拟点击事件
     */
    boolean sendSimulateEvent(final ViewGroup adContainer);

    /**
     * 请求热力图
     */
    void requestClickMap(AdRequest adRequest);

    /**
     * 是否可以点击
     */
    boolean canClick(AdResponse adResponse);
    /**
     * 获取热力图
     */
    ClickMap getClickMap(AdRequest adRequest);
    /**
     * 获取所有的热力图
     */
    Map<String,ClickMap> getClickMapContainer();

}
