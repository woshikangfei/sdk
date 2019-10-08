package com.analytics.sdk.service.ad.entity;

/**
 * Created by kf on 2018/10/12.
 */

public class Items {

    private String url;
    private String id;
    private String title;
    private String appPkgname;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppPkgname() {
        return appPkgname;
    }

    public void setAppPkgname(String appPkgname) {
        this.appPkgname = appPkgname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
