package com.analytics.sdk.client;

public enum AdType {

    /**
     * 开屏
     */
    SPLASH(2, "splash"),
    /**
     * 信息流
     */
    INFORMATION_FLOW(5, "informationFlow"),
    /**
     * 横幅
     */
    BANNER(4, "banner"),
    /**
     * 插屏
     */
    INTERSTITIAL(3, "interstitial"),
    /**
     * 激励视频
     */
    REWARD_VIDEO(9, "reward_video"),
    /**
     * 未知的类型
     */
    UNKNOWN(-1, "unknow");

    private final int valueInt;
    private final String valueString;

    AdType(int valueInt, String valueString) {
        this.valueInt = valueInt;
        this.valueString = valueString;
    }

    public int getIntValue() {
        return valueInt;
    }

    public String getStringValue(){
        return valueString;
    }

}

