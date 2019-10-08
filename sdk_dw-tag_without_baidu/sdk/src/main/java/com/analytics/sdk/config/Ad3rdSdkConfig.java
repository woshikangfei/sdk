package com.analytics.sdk.config;

import com.analytics.sdk.BuildConfig;

public class Ad3rdSdkConfig {

    /**
     * 是否支持走默认集成的APP_ID和CODE_ID
     */
    private boolean support3rdSdkDefaultConfig = BuildConfig.SUPPORT_DEFAULT_3rdSDK_CONFIG;
    /**
     * 开屏默认的APP_id配置
     */
    private String splashDefualtAppId = BuildConfig.SPLASH_DEFAULT_APP_ID;
    /**
     * 开屏默认的代码位ID
     */
    private String splashDefaultSloatId = BuildConfig.SPLASH_DEFAULT_CODE_ID;
    /**
     * 默认支持的广告源(默认是广点通)
     */
    private int splashDefaultAdSource = BuildConfig.SPLASH_DEFAULT_AD_SOURCE;
    /**
     * 开屏的包名称
     */
    private String splashPackageName = BuildConfig.SPLASH_PACKAGE_NAME;

    private String splashActivityName = BuildConfig.SPLASH_ACTIVITY_NAME;
    private String mainActivityName = BuildConfig.MAIN_ACTIVITY_NAME;

    public boolean isSupport3rdSdkDefaultConfig() {
        return support3rdSdkDefaultConfig;
    }

    public void setSupport3rdSdkDefaultConfig(boolean support3rdSdkDefaultConfig) {
        this.support3rdSdkDefaultConfig = support3rdSdkDefaultConfig;
    }

    public String getSplashDefualtAppId() {
        return splashDefualtAppId;
    }

    public void setSplashDefualtAppId(String splashDefualtAppId) {
        this.splashDefualtAppId = splashDefualtAppId;
    }

    public String getSplashDefaultSloatId() {
        return splashDefaultSloatId;
    }

    public void setSplashDefaultSloatId(String splashDefaultSloatId) {
        this.splashDefaultSloatId = splashDefaultSloatId;
    }

    public int getSplashDefaultAdSource() {
        return splashDefaultAdSource;
    }

    public String getSplashPackageName() {
        return splashPackageName;
    }

    public void setSplashPackageName(String splashPackageName) {
        this.splashPackageName = splashPackageName;
    }

    public void setSplashDefaultAdSource(int splashDefaultAdSource) {
        this.splashDefaultAdSource = splashDefaultAdSource;
    }

    public String getSplashActivityName() {
        return splashActivityName;
    }

    public void setSplashActivityName(String splashActivityName) {
        this.splashActivityName = splashActivityName;
    }

    public String getMainActivityName() {
        return mainActivityName;
    }

    public void setMainActivityName(String mainActivityName) {
        this.mainActivityName = mainActivityName;
    }

    @Override
    public String toString() {
        return "Ad3rdSdkConfig{" +
                "\n"+
                "support3rdSdkDefaultConfig=" + support3rdSdkDefaultConfig +
                "\n"+
                ", splashDefualtAppId='" + splashDefualtAppId + '\'' +
                "\n"+
                ", splashDefaultSloatId='" + splashDefaultSloatId + '\'' +
                "\n"+
                ", splashDefaultAdSource=" + splashDefaultAdSource +
                "\n"+
                ", mainActivityName=" + mainActivityName +
                "\n"+
                ", splashActivityName=" + splashActivityName +
                "\n"+
                '}';
    }
}
