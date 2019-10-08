package com.analytics.sdk.common.runtime.event;
/**
 * �¼���������ʱ�쳣
 * @author wei.deng
 */
public class EventSchedulerRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EventSchedulerRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventSchedulerRuntimeException(String message) {
		super(message);
	}
	
	
	
}
