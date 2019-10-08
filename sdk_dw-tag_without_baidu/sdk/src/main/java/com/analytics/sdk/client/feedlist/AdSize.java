package com.analytics.sdk.client.feedlist;

public class AdSize {
    public static final int FULL_WIDTH = -1;
    public static final int AUTO_HEIGHT = -2;
    private int adWith;
    private int adHeight;
    public AdSize(int adWith, int adHeight){
        this.adWith=adWith;
        this.adHeight=adHeight;
    }

    public int getAdWith() {
        return adWith;
    }

    public int getAdHeight() {
        return adHeight;
    }

}
