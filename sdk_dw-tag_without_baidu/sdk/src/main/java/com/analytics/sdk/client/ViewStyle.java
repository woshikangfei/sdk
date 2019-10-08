package com.analytics.sdk.client;

public final class ViewStyle {
    public static final String STYLE_TITLE = "title";
    public static final String STYLE_DESC = "desc";

    /**
     * 字体大小
     */
    private float textSize = -1;
    /**
     * 字体颜色
     */
    private int textColor = -1;
    /**
     * 背景颜色
     */
    private int bgColor = -1;

    private ViewStyle(){}

    private ViewStyle(ViewStyle viewStyle){
        this.textColor = viewStyle.getTextColor();
        this.textSize = viewStyle.getTextSize();
        this.bgColor = viewStyle.getBgColor();
    }

    public static ViewStyle obtain(){
        return new ViewStyle();
    }

    public static ViewStyle obtain(ViewStyle viewStyle){
        return new ViewStyle(viewStyle);
    }

    public float getTextSize() {
        return textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public ViewStyle setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
    }

    public boolean hasBgColor(){
        return bgColor != -1;
    }

    public boolean hasTextSize(){
        return textSize != -1;
    }

    public boolean hasTextColor(){
        return textColor != -1;
    }

    public ViewStyle setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public ViewStyle setBgColor(int bgColor) {
        this.bgColor = bgColor;
        return this;
    }
}
