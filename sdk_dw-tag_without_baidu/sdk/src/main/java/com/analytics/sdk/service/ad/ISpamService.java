package com.analytics.sdk.service.ad;

import com.analytics.sdk.client.AdType;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.IService;

import org.json.JSONException;

/**
 * 反作弊服务
 */
public interface ISpamService extends IService {

    interface SpamState {
        int STATE_NOTING = 0;
        /**
         * 当前设备为灰色,需要游戏确认
         */
        int STATE_GARY = 2;
        /**
         * 当前设备为黑,作弊用户
         */
        int STATE_BLACK = 1;
    }

    /**
     * 是否是root设备
     */
    boolean isRootedDevice();

    /**
     * 是否是4.4以下设备
     */
    boolean isLowDevice();

    /**
     * 是否是安全的包名
     */
    boolean isSafePackage();

    /**
     * 获取黑白名单
     */
    boolean getBWPackages() throws JSONException;
    /**
     * 上报安装包
     */
    boolean reportInstalledPackages();
    /**
     * 上报异常的IMEI
     * @param code 0: 失败， 1：成功
     */
    boolean reportExcpIMEI(int code);
    /**
     * 是否被命中,如果命中则所有广告不在继续请求
     */
    boolean isHitBlack();
    /**
     *
     */
    boolean isHitGray();

    /**
     * 今天是否已经超过最大曝光数
     */
    boolean isGtExposureMaxCount(String codeId);

    /**
     * 递增曝光数
     */
    boolean increateExposureCount(String codeId);

    /**
     * 是否支持反作弊
     */
    boolean isSupportSpam();

    /**
     * 某个广告类型是否可点击
     * @param adType 广告类型
     */
    boolean canClick(AdType adType);
    /**
     * 当前是否为灰色设备
     */
    boolean isGrayStateFromServer();
    /**
     * 是否为黑色设备
     */
    boolean isBlackStateFromServer() ;


}
