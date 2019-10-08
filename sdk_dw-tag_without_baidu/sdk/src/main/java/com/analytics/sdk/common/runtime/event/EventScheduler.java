package com.analytics.sdk.common.runtime.event;

public final class EventScheduler {

    public static void deleteEventListeners(EventActionList eventActions) {
        EventSchedulerImpl.getDefault().deleteEventListener(eventActions);
    }

    public static void addEventListener(String eventAction,EventListener listener) {
        EventSchedulerImpl.getDefault().addEventListener(EventActionList.create().addAction(eventAction),listener);
    }

    public static void deleteEventListener(String eventAction,EventListener listener) {
        EventSchedulerImpl.getDefault().deleteEventListener(EventActionList.create().addAction(eventAction),listener);
    }

    public static void addEventListener(EventActionList eventActions,EventListener listener) {
        EventSchedulerImpl.getDefault().addEventListener(eventActions,listener);
    }

    public static void deleteEventListener(EventActionList eventActions,EventListener listener) {
        EventSchedulerImpl.getDefault().deleteEventListener(eventActions,listener);
    }

    public static boolean dispatch(Event event) {
        try {
            return EventSchedulerImpl.getDefault().dispatch(event);
        } catch (EventSchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int listenerSize(){
        return EventSchedulerImpl.getDefault().listenerSize();
    }

}
