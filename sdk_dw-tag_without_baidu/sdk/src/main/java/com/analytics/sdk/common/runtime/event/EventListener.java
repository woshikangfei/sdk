package com.analytics.sdk.common.runtime.event;

/**
 * 处理事件
 * @author wei.deng
 */
public interface EventListener {

	EventListener EMPTY = new EventListener() {
		@Override
		public boolean handle(Event event) {
			return false;
		}
	};

	boolean handle(Event event);
	
}
