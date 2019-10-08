package com.analytics.sdk.view.handler.common;

public enum FeedListLayoutStyle {

    /**
     * 大图
     */
    BIG(1),
    /**
     * 左图右文
     */
    LEFT_IMAGE(3),
    /**
     * 左文右图
     */
    RIGHT_IMAGE(4),
    /**
     * 三图
     */
    THREE_IMAGE(2);

    private final int valueInt;

    FeedListLayoutStyle(int valueInt) {
        this.valueInt = valueInt;
    }

    public int intValue() {
        return valueInt;
    }

}
