package com.analytics.sdk.common.runtime.event;


import android.text.TextUtils;
import android.util.Log;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.view.handler.IRecycler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * <p>@author wei.deng</p>
 *
 * <p>事件调度器
 * 1.支持拦截所有事件
 * 2.支持事件优先级
 * </p>
 *
 * <p>举例：
 * <p>
 * <pre class="prettyprint">
 * EventSchedulerImpl eventScheduler = EventSchedulerImpl.factory();
 * eventScheduler.addEventListener(Event.obtain(5, EventType.DATA),new PriorityEventListener() {
 public boolean handle(Event event) {
 //handle this event
 return false;
 }
 public int getPriority() {
 return PriorityEventListener.MAX_PRIORITY;
 }
 });</p>
 eventScheduler.addEventListener(Event.obtain(6, EventType.SERVICE),new EventListener() {
 public boolean handle(Event event) {
 //handle this event
 return false;
 }
 });
 <p>
 try {
 eventScheduler.schedul(Event.obtain(5, EventType.DATA));
 eventScheduler.schedul(Event.obtain(6, EventType.SERVICE));
 } catch (EventSchedulerException e) {
 e.printStackTrace(); //handle this
 }
 </pre>
 *
 */
final class EventSchedulerImpl {

	static final String TAG = "EventSchedulerImpl";

	private ConcurrentHashMap<String, EventNotifier> mSchedulers = new ConcurrentHashMap<>();

	private EventInterceptor mEventInterceptor;

	private final Executor mDefaultExecutor = new Executor() {

		@Override
		public void execute(Runnable command) {
			command.run();
		}
	};

	private Executor mExecutor = mDefaultExecutor;

	private static final EventSchedulerImpl sDefault = new EventSchedulerImpl();

	public static EventSchedulerImpl getDefault(){
		return sDefault;
	}

	private void check(Event event){
		String action = event.getAction();

		if(TextUtils.isEmpty(action)){
			throw new EventSchedulerRuntimeException("event.action must not be null");
		}
	}

	public void setEventInterceptor(EventInterceptor eventInterceptor) {
		this.mEventInterceptor = eventInterceptor;
	}

	public void setEventExecutor(Executor executor) {
		mExecutor = executor;
	}

	public void addEventListener(EventActionList eventActions,EventListener listener) {

		if(eventActions == null || eventActions.isEmpty()){
			return;
		}

		if(listener == null){
			throw new EventSchedulerRuntimeException("listener is null");
		}

		for(int i = 0;i < eventActions.size();i++){
			String eventAction = eventActions.getItem(i);
			EventNotifier schedulerImpl = mSchedulers.get(eventAction);

			if(schedulerImpl==null){
				schedulerImpl = new EventNotifier();
				mSchedulers.put(eventAction,schedulerImpl);
			}
			schedulerImpl.addEventListener(listener);
		}

	}

	public void deleteEventListener(EventActionList eventActions,EventListener listener) {

		if(eventActions == null || eventActions.isEmpty()){
			return;
		}

		List<String> willRemoveScheduler = new ArrayList<>();

		for(int i = 0;i < eventActions.size();i++){
			String eventAction = eventActions.getItem(i);
			EventNotifier scheduler = mSchedulers.get(eventAction);
			if(scheduler!=null){
				scheduler.deleteEventListener(listener);

				if(scheduler.isEmtpy()){
					willRemoveScheduler.add(eventAction);
				}

			}
		}
		if(willRemoveScheduler.size() > 0) {
			for (int i = 0;i < willRemoveScheduler.size();i++){
				EventNotifier eventNotifier = mSchedulers.remove(willRemoveScheduler.get(i));
				if(eventNotifier != null){
					eventNotifier.recycle();
				}
			}
		}

	}

	public void deleteEventListener(EventActionList eventActions) {
		if(eventActions == null || eventActions.isEmpty()){
			return;
		}
		for(int i = 0;i < eventActions.size();i++){
			String eventAction = eventActions.getItem(i);
			EventNotifier scheduler = mSchedulers.get(eventAction);
			if(scheduler!=null){
				scheduler.deleteEventListeners();
			}
		}
	}

	public boolean dispatch(Event event) throws EventSchedulerException {

		check(event);

		try {
			if(interceptEventBeforeSchedul(event)){
				return false;
			}
			EventNotifier scheduler = mSchedulers.get(event.getAction());

			if(scheduler == null){
				Logger.i(TAG,"EventNotifier is null");
				return false;
			}

			if(mExecutor == null){
				mExecutor = mDefaultExecutor;
			}

			mExecutor.execute(scheduler.build(event));

			return true;
		} catch(Exception e){
			throw new EventSchedulerException(" scheduler error " + event.toString() ,e);
		}
	}

	private boolean interceptEventBeforeSchedul(Event event){

		if(mEventInterceptor!=null){
			synchronized (mEventInterceptor) {
				return mEventInterceptor.onIntercept(event);
			}
		}
		return false;
	}

    public int listenerSize() {
		return mSchedulers.size();
    }

    final class EventNotifier implements Comparator<EventListener> , Runnable , IRecycler {

		private boolean changed = false;
		private Vector<EventListener> obs;
		private Event target;

		public EventNotifier() {
			obs = new Vector<EventListener>();
		}

		public Runnable build(Event event) {
			this.target = event;
			return this;
		}

		public int size(){
			return obs.size();
		}

		public void addEventListener(EventListener o) {
			if (o == null)
				throw new NullPointerException();

			if (!obs.contains(o)) {
				obs.addElement(o);
				Collections.sort(obs, this);
			}
		}

		public void deleteEventListener(EventListener o) {
			obs.removeElement(o);
		}

		public void notifyEventListeners(Event event) {

			setChanged();

			Object[] arrLocal;

			synchronized (this) {

				if (!changed)
					return;
				arrLocal = obs.toArray();
				clearChanged();

			}

			for (int i = 0;i <= arrLocal.length - 1; i++) {
				try {
					((EventListener) arrLocal[i]).handle(event);
				} catch (Throwable e){
					e.printStackTrace();
				}
			}

		}

		public void deleteEventListeners() {
			obs.removeAllElements();
		}

		protected synchronized void setChanged() {
			changed = true;
		}

		protected void clearChanged() {
			changed = false;
		}

		public boolean hasChanged() {
			return changed;
		}


		public int countListeners() {
			return obs.size();
		}

		@Override
		public int compare(EventListener o1, EventListener o2) {

			int e1Priority = 0;
			int e2Priority = 0;

			if(o1 instanceof PriorityEventListener) {
				e1Priority = ((PriorityEventListener) o1).getPriority();
			}

			if(o2 instanceof PriorityEventListener) {
				e2Priority = ((PriorityEventListener) o2).getPriority();
			}

			if(e1Priority < e2Priority) {
				return 1;
			} else if(e1Priority == e2Priority) {
				return 0;
			} else {
				return -1;
			}
		}

		@Override
		public void run() {
			notifyEventListeners(target);
		}


		public boolean isEmtpy() {
			return (countListeners() == 0);
		}

		@Override
		public boolean recycle() {

			if(obs != null){
				obs.clear();
			}

			if(target != null){
				target.recycle();
				target = null;
			}
			return true;
		}
	}

}

