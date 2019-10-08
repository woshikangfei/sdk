package com.analytics.sdk.service.ad.entity;

import java.util.List;

/**
 * Created by kf on 2018/9/17.
 */

public class ConfigBeans {
    private String adType;
    private String appId;
    private String appName;
    private int height;
    private int width;
    private int joinType;
    //如果是百度的广告源，判断kpg和当前的包名是否一致，如果一致走正常，否则破解
    private String pkg;
    private int priority;
    private int slotFill; //填充类型，1=模板填充，2=自渲染填充
    private String slotId;
    //0=信息流填充广告,1=开屏填充广告
    private int slotType;
    private String token;
    private String version;
    private int source;
    //1.大图 2.三图 3.左图右文 4.左文右图
    private int xxlStyle;

    private int configid; //后端服务器数据库的主键ID

    //填充方式，只有_SDK并且slot_type为插屏情况下生效。1.自渲染2.模版填充3.混合
    private int fillMode;

    /**
     *模版填充app id
     */
    private String tempAppId;
    /**
     *模版填充广告位id
     */
    private String tempSlotId;

    /**
     *混合填充，自渲染多少次后，进行模版填充
     */
    private int drawtimes;

    /**
     * 推广文本
     */
    private String spreadText;
    /**
     * 推广url
     */
    private String spreadUrl;

    /**
     * 推广次数
     */
    private int spreadTimes;


    // 单日拉起次数，不可拉起android版本，不可拉起手机型号
    public int dayPulltimes;
    public String model;
    public String androidversion;
    public List<Items> items;


    public int getXxlStyle() {
        return xxlStyle;
    }

    public void setXxlStyle(int xxlStyle) {
        this.xxlStyle = xxlStyle;
    }

    public int getDayPulltimes() {
        return dayPulltimes;
    }

    public void setDayPulltimes(int dayPulltimes) {
        this.dayPulltimes = dayPulltimes;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAndroidversion() {
        return androidversion;
    }

    public void setAndroidversion(String androidversion) {
        this.androidversion = androidversion;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public String getSpreadText() {
        return spreadText;
    }

    public void setSpreadText(String spreadText) {
        this.spreadText = spreadText;
    }

    public String getSpreadUrl() {
        return spreadUrl;
    }

    public void setSpreadUrl(String spreadUrl) {
        this.spreadUrl = spreadUrl;
    }

    public int getSpreadTimes() {
        return spreadTimes;
    }

    public void setSpreadTimes(int spreadTimes) {
        this.spreadTimes = spreadTimes;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getJoinType() {
        return joinType;
    }

    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    public int getSlotFill() {
        return slotFill;
    }

    public void setSlotFill(int slotFill) {
        this.slotFill = slotFill;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTempAppId() {
        return tempAppId;
    }

    public void setTempAppId(String tempAppId) {
        this.tempAppId = tempAppId;
    }

    public String getTempSlotId() {
        return tempSlotId;
    }

    public void setTempSlotId(String tempSlotId) {
        this.tempSlotId = tempSlotId;
    }

    public int getDrawtimes() {
        return drawtimes;
    }

    public void setDrawtimes(int drawtimes) {
        this.drawtimes = drawtimes;
    }



    public int getFillMode() {
        return fillMode;
    }

    public void setFillMode(int fillMode) {
        this.fillMode = fillMode;
    }


    public int getConfigid() {
        return configid;
    }

    public void setConfigid(int configid) {
        this.configid = configid;
    }


    public int getSlotType() {
        return slotType;
    }

    public void setSlotType(int slotType) {
        this.slotType = slotType;
    }



    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }



    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }


    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }


    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }



    @Override
    public String toString() {
        return "ConfigBeans{" +
                "appId='" + appId + '\'' +
                ", appPackage='" + pkg + '\'' +
                ", appName='" + appName + '\'' +
                ", appVersion='" + version + '\'' +
                ", slotId='" + slotId + '\'' +
                ", slotW=" + width +
                ", slotH=" + height +
                ", adType='" + adType + '\'' +
                ", source='" + source + '\'' +
                ", priority='" + priority + '\'' +
                ", slotType=" + slotType +
                '}';
    }
}
