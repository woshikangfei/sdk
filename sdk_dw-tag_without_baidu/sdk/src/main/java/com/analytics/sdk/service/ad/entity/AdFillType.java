package com.analytics.sdk.service.ad.entity;

/**
 * 广告填充类型
 */
public enum AdFillType {

    /**
     * 开屏
     */
    TEMPLATE(1),
    /**
     * 信息流
     */
    SELF_RENDER(2),
    /**
     * 未知的类型
     */
    UNKNOWN(-1);

    private final int value;

    AdFillType(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }

    public static AdFillType to(int adFillType){
        if(adFillType == TEMPLATE.intValue()) {
            return TEMPLATE;
        } else if(adFillType == SELF_RENDER.intValue()) {
            return SELF_RENDER;
        } else {
            return UNKNOWN;
        }
    }

}
