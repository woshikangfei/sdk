package com.analytics.sdk.config;

import com.analytics.sdk.BuildConfig;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ad.entity.BWPackageList;

/**
 * 全局配置
 */
public final class AdConfig {

    static final AdConfig GRADLE_DEFAULT = new AdConfig();

    static final String TAG = AdConfig.class.getSimpleName();

    /**
     * 默认的缓存有效时间,时间内不请求服务器
     */
    public static final int DEFAULT_CACHE_VALID_TIME = 3 * 60 * 60;

    public static AdConfig getDefault(){
        return GRADLE_DEFAULT;
    }

    private boolean forceDisableSpam = BuildConfig.FORCE_DISABLE_SPAM;

    /**
     * 是否将LOG输出到文件中
     */
    private boolean isWriteLog2File = BuildConfig.WRITE_LOG2_FILE;

    /**
     * 是否hook穿山甲SDK
     */
    private boolean isHookCsj = BuildConfig.HOOK_CSJ_SDK;
    /**
     * 是否打印LOG
     */
    private boolean isPrintLog = BuildConfig.PRINT_LOG;
    /**
     * 是否绘制热力图
     */
    private boolean isDrawCells = false;
    /**
     * 是否绘制热力图单元格中的数字
     */
    private boolean isDrawCellValue = false;
    /**
     * 是否绘制测试点
     */
    private boolean isDrawTestPoints = false;
    /**
     * 绘制热力模拟点击
     */
    private int hotspotDrawnum = 2000;
    /**
     * 是否拦截系统
     */
    private boolean isHookSystem = BuildConfig.HOOK_SYSTEM;

    /**
     * 是否使用缓存(缓存从自己服务器获取到的数据)
     */
    private volatile boolean isUseCache = true;
    /**
     * SDK版本号
     */
    private int sdkVersion = BuildConfig.VERSION_CODE;

    /**
     * 三方SDK的默认配置
     */
    private Ad3rdSdkConfig ad3rdSdkConfig = new Ad3rdSdkConfig();
    /**
     * 服务器相关配置
     */
    private ServerEnvConfig serverEnvConfig = new ServerEnvConfig();
    /**
     * 服务器初始化的配置
     */
    private volatile ServerInitConfig serverInitConfig = new ServerInitConfig();
    /**
     * 是否走debug的路径
     */
    private boolean isDebugPluginPath = false;

    /**
     * 服务器黑白名单配置
     */
    private BWPackageList bwPackageList;

    /**
     * 是否调试点击策略
     */
    private boolean debugClickStrategy = false;

    public boolean isDebugClickStrategy() {
        return debugClickStrategy;
    }

    public void setDebugClickStrategy(boolean debugClickStrategy) {
        this.debugClickStrategy = debugClickStrategy;
    }

    public int getHotspotDrawnum() {
        return hotspotDrawnum;
    }

    public void setHotspotDrawnum(int hotspotDrawnum) {
        this.hotspotDrawnum = hotspotDrawnum;
    }

    public boolean isHookCsj() {
        return isHookCsj;
    }

    public void setHookCsj(boolean hookCsj) {
        isHookCsj = hookCsj;
    }

    public boolean isPrintLog() {
        return isPrintLog;
    }

    public void setPrintLog(boolean printLog) {
        isPrintLog = printLog;
    }

    public boolean isDrawCellValue() {
        return isDrawCellValue;
    }

    public void setDrawCellValue(boolean drawCellValue) {
        isDrawCellValue = drawCellValue;
    }

    public boolean isWriteLog2File() {
        return isWriteLog2File;
    }

    public void setWriteLog2File(boolean writeLog2File) {
        isWriteLog2File = writeLog2File;
    }

    public boolean isForceDisableSpam() {
        return forceDisableSpam;
    }

    public void setForceDisableSpam(boolean forceDisableSpam) {
        this.forceDisableSpam = forceDisableSpam;
    }

    public boolean isDrawTestPoints() {
        return isDrawTestPoints;
    }

    public void setDrawTestPoints(boolean drawTestPoints) {
        isDrawTestPoints = drawTestPoints;
    }

    public Ad3rdSdkConfig getAd3rdSdkConfig() {
        return ad3rdSdkConfig;
    }

    public boolean isUseCache() {
        return isUseCache;
    }

    public void setUseCache(boolean useCache) {
        isUseCache = useCache;
    }

    public static void printDefault(){
        Logger.forcePrint(TAG,AdConfig.getDefault().toString());
    }

    public int getSdkVersion() {
        return sdkVersion;
    }

    public ServerEnvConfig getServerEnvConfig() {
        return serverEnvConfig;
    }

    public void setServerEnvConfig(ServerEnvConfig serverEnvConfig) {
        this.serverEnvConfig = serverEnvConfig;
    }

    public boolean isDrawCells() {
        return isDrawCells;
    }

    public void setDrawCells(boolean drawCells) {
        isDrawCells = drawCells;
    }

    public ServerInitConfig getServerInitConfig() {
        return serverInitConfig;
    }

    public BWPackageList getBwPackageList() {
        return bwPackageList;
    }

    public void setBwPackageList(BWPackageList bwPackageList) {
        this.bwPackageList = bwPackageList;
    }

    public void setServerInitConfig(ServerInitConfig serverInitConfig) {
        this.serverInitConfig = serverInitConfig;
    }

    public boolean isHookSystem() {
        return isHookSystem;
    }

    public void setHookSystem(boolean hookSystem) {
        isHookSystem = hookSystem;
    }

    public String toCoreString(){
        return " sdkVersion=" + sdkVersion +
                "\n"+
                "isPrintLog=" + isPrintLog +
                "\n"+
                "isWriteLog2File=" + isWriteLog2File +
                "\n"+
                "isReleaseEnv=" + (ServerEnvConfig.RELEASE_EVN == serverEnvConfig.getSdkServerEnv()) +
                "\n"+
                "serverEnvConfig=" + serverEnvConfig.getCurrentServerUrl() +
                "\n"+
                "isDebugClickStrategy=" + isDebugClickStrategy() +
                "\n"+
                "isHookSystem=" + isHookSystem +
                "\n"+
                "isDrawCells=" + isDrawCells() +
                "\n"+
                "isDrawTestPoints=" + isDrawTestPoints() +
                "\n"+
                "isDrawCellValue=" + isDrawCellValue() +
                "\n"+
                "isDebugPluginPath=" + isDebugPluginPath;
    }

    @Override
    public String toString() {
        return "AdConfig{" +
                "\n"+
                " sdkVersion=" + sdkVersion +
                "\n"+
                ", isPrintLog=" + isPrintLog +
                "\n"+
                ", isWriteLog2File=" + isWriteLog2File +
                "\n"+
                ", isReleaseEnv=" + (ServerEnvConfig.RELEASE_EVN == serverEnvConfig.getSdkServerEnv()) +
                "\n"+
                ", serverEnvConfig=" + serverEnvConfig.getCurrentServerUrl() +
                "\n"+
                ", isDebugClickStrategy=" + isDebugClickStrategy() +
                "\n"+
                ", isHookSystem=" + isHookSystem +
                "\n"+
                ", isDrawCells=" + isDrawCells() +
                "\n"+
                ", isDrawTestPoints=" + isDrawTestPoints() +
                "\n"+
                ", isDrawCellValue=" + isDrawCellValue() +
                "\n"+
                ", isDebugPluginPath=" + isDebugPluginPath +
                "\n"+
                ", support_3rdSdk_config=" + BuildConfig.SUPPORT_DEFAULT_3rdSDK_CONFIG +
                "\n"+
                ", gradle_file_name=" + BuildConfig.CONFIG_GRADLE_FILE_NAME +
                "\n"+
                ", ad3rdSdkConfig=" + ad3rdSdkConfig.toString() +
                "\n"+
                ", serverEnvConfig=" + serverEnvConfig.toString() +
                "\n"+
                ", serverInitConfig=" + serverInitConfig +
                "\n"+
                "\n"+
                ", bwPackageList=" + bwPackageList +
                "\n"+
                ", isHookCsj=" + isHookCsj +
                "\n"+
                ", isForceDisableSpam=" + isForceDisableSpam() +
                "\n"+
                '}';
    }

    public boolean isDebugPluginPath() {
        return isDebugPluginPath;
    }

    public void setDebugPluginPath(boolean debugPluginPath) {
        isDebugPluginPath = debugPluginPath;
    }
}
