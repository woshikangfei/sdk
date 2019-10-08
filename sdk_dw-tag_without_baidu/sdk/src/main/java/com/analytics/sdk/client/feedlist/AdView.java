package com.analytics.sdk.client.feedlist;

import android.view.View;

public interface AdView {

    View getView();

    /**
     * 渲染
     */
    void render();

    /**
     * 释放
     */
    boolean recycle();

}
