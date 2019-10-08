package com.analytics.sdk.common.runtime.event;
/**
 * 支持优先级的事件监听
 * @author wei.deng
 */
public interface PriorityEventListener extends EventListener{
	/**
	 * 最大优先级
	 */
	int MAX_PRIORITY = 1000;

	int getPriority();
}

