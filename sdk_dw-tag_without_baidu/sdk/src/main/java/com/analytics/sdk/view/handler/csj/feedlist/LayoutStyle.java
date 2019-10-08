package com.analytics.sdk.view.handler.csj.feedlist;

public enum LayoutStyle {

    //1.大图 2.三图 3.左图右文 4.左文右图

    /**
     * 大图
     */
    BIG_IMAGE(1),
    /**
     * 三图
     */
    THREE_IMAGE(2),
    /**
     * 左图右文
     */
    LEFT_IMAGE(3),
    /**
     * 左文右图
     */
    RIGHT_IMAGE(4),
    /**
     * 未知的类型
     */
    UNKNOWN(-1);

    private final int valueInt;

    LayoutStyle(int valueInt) {
        this.valueInt = valueInt;
    }

    public int getIntValue() {
        return valueInt;
    }

    public static LayoutStyle to(int valueInt){
        if(BIG_IMAGE.valueInt == valueInt){
            return BIG_IMAGE;
        } else if(THREE_IMAGE.valueInt == valueInt){
            return THREE_IMAGE;
        } else if(LEFT_IMAGE.valueInt == valueInt) {
            return LEFT_IMAGE;
        } else if(RIGHT_IMAGE.valueInt == valueInt) {
            return RIGHT_IMAGE;
        } else {
            return UNKNOWN;
        }
    }

}
