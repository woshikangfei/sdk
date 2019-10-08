package com.analytics.sdk.common.runtime.event;

import java.util.concurrent.atomic.AtomicInteger;

public final class EventPriorityGetter {

    static AtomicInteger sCurrentPriority = new AtomicInteger(1);

    public static int countPriority(){
        return sCurrentPriority.getAndIncrement();
    }

}
