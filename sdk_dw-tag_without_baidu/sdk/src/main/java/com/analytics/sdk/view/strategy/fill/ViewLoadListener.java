package com.analytics.sdk.view.strategy.fill;

import android.view.View;

import com.analytics.sdk.client.AdListeneable;

public interface ViewLoadListener extends AdListeneable {

    boolean onViewLoaded(View view);
    boolean onlyGetViewData(Object adData);

}
