package com.analytics.sdk.common.runtime.event;

import com.analytics.sdk.view.handler.IRecycler;

import java.io.Serializable;
/**
 * @author wei.deng
 */
public abstract class Event implements Schedulable , Serializable , IRecycler {
	
	private static final long serialVersionUID = 1L;
	
	private String action;
	private Object arg1;
	private Object arg2;
	private long timestamp;
	
	private Event(){
	}

	public static Event obtain(String action){
		return obtain(action,null,null);
	}

	public static Event obtain(String action,Object arg1) {
		return obtain(action,arg1,null);
	}

	public static Event obtain(String action,Object arg1,Object arg2){
		Event event = new SchedulableEvent();
		event.setAction(action);
		event.setArg1(arg1);
		event.setArg2(arg2);
		return event;
	}

	public <T> T getArg1() {
		return (T)arg1;
	}

	public void setArg1(Object arg1) {
		this.arg1 = arg1;
	}

	public <T> T getArg2() {
		return (T)arg2;
	}

	public void setArg2(Object arg2) {
		this.arg2 = arg2;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public boolean equals(Object obj) {
		
		Event e = (Event) obj;
		
		String action = e.getAction();

		if(this.action == action) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.action.hashCode();
	}

	@Override
	public String toString() {
		return "Event{" +
				"action='" + action + '\'' +
				", arg1=" + arg1 +
				", timestamp=" + timestamp +
				'}';
	}

	@Override
	public boolean recycle() {
		arg1 = null;
		arg2 = null;
		return true;
	}

	static final class SchedulableEvent extends Event {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
	
}
