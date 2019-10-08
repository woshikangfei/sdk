package com.analytics.sdk.common.runtime.event;
/**
 * 事件拦截
 * @author wei.deng
 */
public interface EventInterceptor {
	boolean onIntercept(Event e);
}
