package com.adsdk.demo;

public final class GlobalConfig {

    public interface ChannelId {
        /**
         * 激励视频
         */
        String VIDEO = BuildConfig.CODEID_VIDEO;
        /**
         * 开屏
         */
        String SPLASH = BuildConfig.CODEID_SPLASH;
        /**
         * 插屏
         */
        String INTERSTITIAL = BuildConfig.CODEID_INTERSTITIAL;
        /**
         *  信息流
         */
        String FEED_LIST = BuildConfig.CODEID_FEEDLIST;

        /**
         * 横幅
         */
        String BANNER = BuildConfig.CODEID_BANNER;

    }

    public interface RConfig {

        /**  首页 **/
        int MAIN_ACTIVITY_LAYOUT_ID = R.layout.activity_main;
        int MAIN_ACTIVITY_BTN_SPLASH_ID = R.id.splash;
        int MAIN_ACTIVITY_BTN_BANNER_ID = R.id.banner;
        int MAIN_ACTIVITY_BTN_INTERSTITIAL_ID = R.id.interstitial;
        int MAIN_ACTIVITY_BTN_REWARD_VIDEO_ID = R.id.reward_video;
        int MAIN_ACTIVITY_BTN_FEEDLIST_ID = R.id.feedlist;


        /** 开发者首页 **/
        int MAIN_ACTIVITY_DEV_LAYOUT_ID = R.layout.activity_dev_main;
        int MAIN_ACTIVITY_BTN_ID = R.id.test01;

        /** 开屏 **/
        int SPLASH_ACTIVITY_LAYOUT_ID = R.layout.activity_splashv2;
        int SPLASH_ACTIVITY_LAYOUT_AD_ID = R.id.splash_layout;

        /** 激励视频 **/
        int REWARD_VIDEO_ACTIVITY_LAYOUT_ID = R.layout.activity_reward_video;

        /** 插屏 **/
        int INTERSTITIAL_ACTIVITY_LAYOUT_ID = R.layout.activity_interstitial;

        /** 横幅 **/
        int BANNER_ACTIVITY_LAYOUT_ID = R.layout.activity_banner;
        int BANNER_ACTIVITY_AD_CONTAINER = R.id.bannerContainer;

        /**  信息流 **/
        int FEEDLIST_ACTIVITY_LAYOUT_ID = R.layout.activity_feedlist;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_ID = R.id.recycler_view;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_TITLE_ID = R.id.title;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_AD_CONTAINER_ID = R.id.express_ad_container;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_ITEM_AD_ID = R.layout.layout_feedlist_item_express_ad;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_ITEM_NORMAL_ID = R.layout.layout_feedlist_item_data;
    }

}
