package com.analytics.sdk.service.ad.entity;

import java.io.Serializable;

/**
 * Created by yangminghui on 2018/5/11.
 */

public class SlotInfo implements Serializable {
    private static final long serialVersionUID = 5243036019435440380L;
    private int slotwidth;
    private int slotheight;

    public int getSlotwidth() {
        return slotwidth;
    }

    public void setSlotwidth(int slotwidth) {
        this.slotwidth = slotwidth;
    }

    public int getSlotheight() {
        return slotheight;
    }

    public void setSlotheight(int slotheight) {
        this.slotheight = slotheight;
    }
}
