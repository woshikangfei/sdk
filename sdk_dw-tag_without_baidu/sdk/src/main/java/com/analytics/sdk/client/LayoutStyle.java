package com.analytics.sdk.client;

import java.util.HashMap;

public final class LayoutStyle {

    public static final LayoutStyle EMPTY = new LayoutStyle();

    private boolean isHiddenClose = false;
    private int bgColor = -1;
    private HashMap<String,ViewStyle> viewStyleMap = new HashMap<>();

    private LayoutStyle(){}

    public static LayoutStyle obtain(){
        return new LayoutStyle();
    }

    public boolean isHiddenClose() {
        return isHiddenClose;
    }

    public int getBgColor() {
        return bgColor;
    }

    public boolean hasBgColor(){
        return bgColor != -1;
    }

    public LayoutStyle setBgColor(int bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public LayoutStyle setHiddenClose(boolean isHiddenClose){
        this.isHiddenClose = isHiddenClose;
        return this;
    }

    public HashMap<String, ViewStyle> getViewStyleMap() {
        return viewStyleMap;
    }

    public boolean isEmpty(){
        return this == EMPTY;
    }

    public ViewStyle getViewStyle(String filedName){
        return viewStyleMap.get(filedName);
    }

    public LayoutStyle addViewStyle(String fieldName, ViewStyle viewStyle) {
        this.viewStyleMap.put(fieldName,viewStyle);
        return this;
    }

}
