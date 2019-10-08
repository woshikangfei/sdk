package com.analytics.sdk.config;

import com.analytics.sdk.BuildConfig;

public class ServerEnvConfig {

    static final int RELEASE_EVN = 0;
    static final int TEST_EVN = 1;
    static final int DEV_ENV = 2;

    public interface HttpConfig {
        /**
         * 广告路径
         */
        String PATH_ADS = "sdk/ads2";
        /**
         * 埋点上报路径
         */
        String PATH_LOG = "sdklogV2";
        /**
         * 埋点上报路径
         */
        String PATH_LOG_V3 = "sdklogV3";
        /**
         * 热修复路径
         */
        String PATH_HOTFIX = "getAppVersion";
        /**
         * 初始化
         */
        String PATH_INIT = "sdk/init2";
        /**
         * 点击热力图
         */
        String PATH_CLICK_MAP = "sdk/clickmap";
        /**
         * /sdk/package_list
         * 黑白名单
         */
        String PATH_WB_PACKAGE_LIST = "sdk/package_list";
        /**
         * 终端设备信息上报
         */
        String PATH_DEVICE_INFO_REPORT = "package_report";
        /**
         * 上报异常的IMEI
         */
        String PATH_EXCP_IMEI = "imei_report";
    }

    /**
     * 是否是正式环境 , 默认为正式环境
     */
    private int sdkServerEnv = BuildConfig.RELEASE_EVN;

    /**
     * 正式服务器地址
     */
    private String releaseServerUrl = BuildConfig.RELEASE_SERVER_URL;
    /**
     * 测试服务器地址
     */
    private String testServerUrl = BuildConfig.TEST_SERVER_URL;
    /**
     * 调试服务器地址
     */
    private String devServerUrl = BuildConfig.DEV_SERVER_URL;

    public String getReleaseServerUrl() {
        return releaseServerUrl;
    }

    public String getDevServerUrl() {
        return devServerUrl;
    }

    public void setDevServerUrl(String devServerUrl) {
        this.devServerUrl = devServerUrl;
    }

    public int getSdkServerEnv() {
        return sdkServerEnv;
    }

    public void setSdkServerEnv(int sdkServerEnv) {
        this.sdkServerEnv = sdkServerEnv;
    }

    public void setReleaseServerUrl(String releaseServerUrl) {
        this.releaseServerUrl = releaseServerUrl;
    }

    public String getTestServerUrl() {
        return testServerUrl;
    }

    public void setTestServerUrl(String testServerUrl) {
        this.testServerUrl = testServerUrl;
    }

    public String getCurrentServerUrl(){

        if(RELEASE_EVN == sdkServerEnv){
            return getReleaseServerUrl();
        } else if(TEST_EVN == sdkServerEnv){
            return getTestServerUrl();
        } else if(DEV_ENV == sdkServerEnv){
            return getDevServerUrl();
        }
        return getReleaseServerUrl();
    }

    public String getCurrentServerUrl2(){

        if(RELEASE_EVN == sdkServerEnv){
            return BuildConfig.RELEASE_QUERY_SERVER_URL;
        } else if(TEST_EVN == sdkServerEnv){
            return getTestServerUrl();
        } else if(DEV_ENV == sdkServerEnv){
            return getDevServerUrl();
        }

        return BuildConfig.RELEASE_QUERY_SERVER_URL;
    }

    public String getLogUrlV2(){
        return getCurrentServerUrl() + HttpConfig.PATH_LOG;
    }

    public String getLogUrlV3(){
        return getCurrentServerUrl() + HttpConfig.PATH_LOG_V3;
    }

    public String getAdsUrl(){
        return getCurrentServerUrl2() + HttpConfig.PATH_ADS;
    }

    public String getHotfixUrl(){
        return getCurrentServerUrl2() + HttpConfig.PATH_HOTFIX;
    }

    public String getInitUrl(){
        return getCurrentServerUrl2() + HttpConfig.PATH_INIT;
    }

    public String getClickMapUrl(){
        return getCurrentServerUrl2() + HttpConfig.PATH_CLICK_MAP;
    }

    public String getDeviceReportUrl(){
        return getCurrentServerUrl() + HttpConfig.PATH_DEVICE_INFO_REPORT;
    }

    public String getExcpIMEIReportUrl(){
        return getCurrentServerUrl() + HttpConfig.PATH_EXCP_IMEI;
    }

    public String getWBPackageListUrl(){
        return getCurrentServerUrl2() + HttpConfig.PATH_WB_PACKAGE_LIST;
    }

    @Override
    public String toString() {
        return "ServerEnvConfig{" +
                "\n"+
                ", sdkServerEnv=" + sdkServerEnv +
                "\n"+
                ", releaseServerUrl='" + releaseServerUrl + '\'' +
                "\n"+
                ", testServerUrl='" + testServerUrl + '\'' +
                "\n"+
                ", devServerUrl='" + devServerUrl + '\'' +
                '}' + "\n";
    }
}
