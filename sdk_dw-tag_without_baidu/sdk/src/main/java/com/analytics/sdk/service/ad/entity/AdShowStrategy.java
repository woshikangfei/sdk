package com.analytics.sdk.service.ad.entity;

import java.util.Arrays;

public class AdShowStrategy {
    private String click_url;
    private String[] imgs;
    private int interaction_type;
    private String title;
    private String action;
    private float probability;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public AdShowStrategy() {

    }

    public AdShowStrategy(String click_url, String[] imgs, int interaction_type, String title) {
        this.click_url = click_url;
        this.imgs = imgs;
        this.interaction_type = interaction_type;
        this.title = title;
    }

    public String getClick_url() {
        return click_url;
    }

    public void setClick_url(String click_url) {
        this.click_url = click_url;
    }

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    public int getInteraction_type() {
        return interaction_type;
    }

    public void setInteraction_type(int interaction_type) {
        this.interaction_type = interaction_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "AdShowStrategy{" +
                "click_url='" + click_url + '\'' +
                ", imgs=" + Arrays.toString(imgs) +
                ", interaction_type=" + interaction_type +
                ", title='" + title + '\'' +
                ", action='" + action + '\'' +
                ", probability=" + probability +
                '}';
    }
}
