package com.analytics.sdk.view.handler.common;

import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.service.AdEventActions;

public abstract class SplashBasicHandler extends BasicAdHandler {

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_HANDLER;
    }
}
