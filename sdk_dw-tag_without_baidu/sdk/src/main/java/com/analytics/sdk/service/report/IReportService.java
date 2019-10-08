package com.analytics.sdk.service.report;

import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.service.IService;
import com.analytics.sdk.service.report.entity.ReportData;

/**
 * 数据上报
 */
public interface IReportService extends IService {

    String ACTION_REPORT_SUCCESS = "report.success";
    String ACTION_REPORT_ERROR = "report.error";

    boolean report(ReportData reportData, Listener<String,String> listener);

    /**
     * 上传本地缓存数据
     */
    boolean startBatchReportLocal();

    /**
     * 获取当天的错误
     */
    int getErrorCountToday(String action);

    @Deprecated
    interface Action {
        /**
         * 当服务器没有ResponseData返回时,或者解析ResponseData失败等情况
         */
        String ACTION_UNKNOW = "unknow";
        /**
         * 广告请求
         */
        String ACTION_AD_REQUEST = "request";
        /**
         * 视频播放完成
         */
        String ACTION_AD_VIDEOCOMPLETED = "onAdVideoCompleted";
        /**
         * 展现
         */
        String ACTION_AD_SHOW = "show";
        /**
         * 曝光
         */
        String ACTION_AD_EXPOSURE = "exposure";
        /**
         * 视频加载
         */
        String ACTION_AD_VIDEOLOAD = "onVideoLoad";
        /**
         * 视频暂停
         */
        String ACTION_AD_VIDEOPAUSE = "onVideoPause";
        /**
         * 视频开始播放
         */
        String ACTION_AD_VIDEOSTART = "onVideoStart";
        /**
         * 被关闭或者被跳过
         */
        String ACTION_AD_DISMISS = "dismiss";
        /**
         * 倒计时
         */
        String ACTION_AD_TICK_TIMEOVER = "tick_timeover";
        /**
         * 点击
         */
        String ACTION_AD_CLICK = "click";
        /**
         * 下载
         */
        String DOWNLOAD_ACTION = "download";
        /**
         * 错误
         */
        String ACTION_AD_ERROR = "error";
        /**
         * 广点通无广告
         */
        String ACTION_AD_GDT_NOTFOUND = "gdt_noad";
        /**
         * 误点上报
         */
        String ACTION_AD_RANDOM_CLICK = "a";
        /**
         * 开屏4-5S间隔时误点
         */
        String ACTION_AD_RANDOM_CLICK_B = "b";
        /**
         * 热修复
         */
        String ACTION_HOTFIX = "hotfix";

    }

    /**
     * 上报的数据类型
     */
    interface Type {
        String TYPE_API = "api";
        String TYPE_SDK = "sdk";
    }

}
