package com.analytics.sdk.service;

import com.analytics.sdk.common.runtime.event.EventActionList;

public interface AdEventActions {

    EventActionList GLOBAL = EventActionList.create()
                                    .addAction(AdEventActions.ACTION_AD_HOTFIX)
                                    .addAction(AdEventActions.ACTION_AD_HACK);

    EventActionList BASE_CLIENT = EventActionList.create()
                                    .addAction(AdEventActions.ACTION_AD_CLICK)
                                    .addAction(AdEventActions.ACTION_AD_ERROR)
                                    .addAction(AdEventActions.ACTION_AD_SHOW)
                                    .addAction(AdEventActions.ACTION_AD_EXPOSURE)
                                    .addAction(AdEventActions.ACTION_AD_TICK_TIMEOVER)
                                    .addAction(AdEventActions.ACTION_AD_DISMISS);

    /**
     * 基础的AdHandler相关的action
     */
    EventActionList BASE_HANDLER = BASE_CLIENT.clone()
                                    .addAction(AdEventActions.ACTION_AD_REQUEST)
                                    .addAction(AdEventActions.ACTION_AD_RANDOM_CLICK)
                                    .addAction(AdEventActions.ACTION_AD_RANDOM_CLICK_B)
                                    .addAction(AdEventActions.ACTION_AD_BLOCK_MAINACTIVITY_C);
    /**
     * 下载相关
     */
    EventActionList BASE_DOWNLOAD = EventActionList.create()
                                    .addAction(AdEventActions.Download.ACTION_AD_ACTIVE)
                                    .addAction(AdEventActions.Download.ACTION_AD_COMPLETED)
                                    .addAction(AdEventActions.Download.ACTION_AD_ERROR)
                                    .addAction(AdEventActions.Download.ACTION_AD_PAUSE)
                                    .addAction(AdEventActions.Download.ACTION_AD_INSTALLED);

    /**
     * 激励视频相关
     */
    EventActionList BASE_REWARD_VIDEO = EventActionList.create()
                                    .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_COMPLETED)
                                    .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_REWARD)
                                    .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_CACHED)
                                    .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_SKIPPED)
                                    .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_LOADED);

    /**
     * 激励视频相关
     */
    EventActionList BASE_FEEDLIST = EventActionList.create()
                                    .addAction(FeedList.ACTION_AD_LOADED)
                                    .addAction(FeedList.ACTION_AD_RENDER_FAIL)
                                    .addAction(FeedList.ACTION_AD_RENDER_SUCCESS);

    /**
     * 插屏
     */
    EventActionList BASE_INTERSTITIAL = EventActionList.create()
            .addAction(Interstitial.ACTION_AD_OPENED)
            .addAction(Interstitial.ACTOIN_AD_RECEIVE)
            .addAction(Interstitial.ACTION_AD_LEFT_APPLICATION);

    /**
     * 横幅
     */
    EventActionList BASE_BANNER = EventActionList.create()
            .addAction(Banner.ACTOIN_AD_RECEIVE)
            .addAction(Banner.ACTION_AD_CLOSE_OVERLAY)
            .addAction(Banner.ACTION_AD_OPEN_OVERLAY)
            .addAction(Banner.ACTION_AD_LEFT_APPLICATION);

    /**
     * 当服务器没有ResponseData返回时,或者解析ResponseData失败等情况
     */
    String ACTION_UNKNOW = "unknow";
    /**
     * 广告请求
     */
    String ACTION_AD_REQUEST = "request";
    /**
     * 展现
     */
    String ACTION_AD_SHOW = "show";
    /**
     * 曝光
     */
    String ACTION_AD_EXPOSURE = "exposure";
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
     * 下载完成
     */
    String ACTION_AD_DOWNLOAD_COMPLETED = "dl_completed";
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
     * 倒计时结束后,阻塞主界面跳转
     */
    String ACTION_AD_BLOCK_MAINACTIVITY_C = "c";
    /**
     * 信息流模拟点击
     */
    String ACTION_AD_INFORMATION_RANDOM_CLICK_D = "d";
    /**
     * 不反馈给开发者点击事件
     */
    String ACTION_AD_CLICK_EVENT_E = "e";
    /**
     * 信息流的点击
     */
    String ACTION_AD_F = "f";
    /**
     * 热修复
     */
    String ACTION_AD_HOTFIX = "dcd_hotfix";
    /**
     * INJECT | HOOK
     */
    String ACTION_AD_HACK = "dcd_hack";

    /**
     * 激励视频
     */
    interface RewardVideo {
        /**
         * 视频播放完成
         */
        String ACTION_AD_VIDEO_COMPLETED = "video_completed";
        /**
         * 视频加载成功
         */
        String ACTION_AD_VIDEO_LOADED = "video_loaded";
        /**
         * 激励视频广告激励发放
         */
        String ACTION_AD_VIDEO_REWARD = "video_reward";
        /**
         * 视频素材缓存成功，可在此回调后进行广告展示
         */
        String ACTION_AD_VIDEO_CACHED = "video_cached";
        /**
         * 视频跳过
         */
        String ACTION_AD_VIDEO_SKIPPED = "video_skipped";
    }

    interface Download {
        /**
         * 下载中
         */
        String ACTION_AD_ACTIVE = "dl_active";
        String ACTION_AD_PAUSE = "dl_paused";
        /**
         * 下载失败
         */
        String ACTION_AD_ERROR = "dl_error";
        String ACTION_AD_COMPLETED = "dl_completed";
        /**
         * 安装完成，点击下载区域打开
         */
        String ACTION_AD_INSTALLED = "dl_installed";
    }

    interface FeedList {
        /**
         * 数据返回
         */
        String ACTION_AD_LOADED = "loaded";
        String ACTION_AD_RENDER_FAIL = "render_fail";
        String ACTION_AD_RENDER_SUCCESS = "render_success";
    }

    interface Interstitial {
        String ACTOIN_AD_RECEIVE = "inter_receive";
        String ACTION_AD_OPENED = "inter_opened";
        String ACTION_AD_LEFT_APPLICATION = "inter_left_application";
    }

    interface Banner {
        String ACTOIN_AD_RECEIVE = "banner_receive";
        String ACTION_AD_LEFT_APPLICATION = "banner_left_application";
        String ACTION_AD_OPEN_OVERLAY = "banner_open_overylay";
        String ACTION_AD_CLOSE_OVERLAY = "banner_close_overylay";
    }

    interface Spam {
        String ACTION_AD_SPAM = "spam";
    }

}
