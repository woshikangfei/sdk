package com.analytics.sdk.service.ad.entity;

import java.io.Serializable;

/**
 * Created by kf on 2018/9/26.
 */

public class ClickLoction implements Serializable {
    private int adWidth;
    private int adHeight;
    private int finalwidth;
    private int finalHeight;
    private int DOWN_X;
    private int DOWN_Y;
    private int UP_X;
    private int UP_Y;
    private int ACTION_ID;
    private String CLICK_ID;
    private long downTime;
    private long upTime;

    public long getDownTime() {
        return downTime;
    }

    public void setDownTime(long downTime) {
        this.downTime = downTime;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public int getACTION_ID() {
        return ACTION_ID;
    }

    public void setACTION_ID(int ACTION_ID) {
        this.ACTION_ID = ACTION_ID;
    }

    public String getCLICK_ID() {
        return CLICK_ID;
    }

    public void setCLICK_ID(String CLICK_ID) {
        this.CLICK_ID = CLICK_ID;
    }


    public int getAdWidth() {
        return adWidth;
    }

    public void setAdWidth(int adWidth) {
        this.adWidth = adWidth;
    }

    public int getAdHeight() {
        return adHeight;
    }

    public void setAdHeight(int adHeight) {
        this.adHeight = adHeight;
    }

    public int getFinalwidth() {
        return finalwidth;
    }

    public void setFinalwidth(int finalwidth) {
        this.finalwidth = finalwidth;
    }

    public int getFinalHeight() {
        return finalHeight;
    }

    public void setFinalHeight(int finalHeight) {
        this.finalHeight = finalHeight;
    }

    public int getDOWN_X() {
        return DOWN_X;
    }

    public void setDOWN_X(int DOWN_X) {
        this.DOWN_X = DOWN_X;
    }

    public int getDOWN_Y() {
        return DOWN_Y;
    }

    public void setDOWN_Y(int DOWN_Y) {
        this.DOWN_Y = DOWN_Y;
    }

    public int getUP_X() {
        return UP_X;
    }

    public void setUP_X(int UP_X) {
        this.UP_X = UP_X;
    }

    public int getUP_Y() {
        return UP_Y;
    }

    public void setUP_Y(int UP_Y) {
        this.UP_Y = UP_Y;
    }

}
