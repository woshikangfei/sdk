package com.analytics.sdk.common.runtime.event;

import java.util.ArrayList;
import java.util.List;

public final class EventActionList {

    final List<String> actions = new ArrayList<>();

    private EventActionList(){}

    public static EventActionList create(){
        EventActionList eventActionList = new EventActionList();
        return eventActionList;
    }

    public EventActionList clone(){
        EventActionList eventActionList = new EventActionList();
        eventActionList.getList().addAll(this.getList());
        return eventActionList;
    }

    public boolean isEmpty(){
        return actions.isEmpty();
    }

    public EventActionList addAction(String action){
        actions.add(action);
        return this;
    }
    
    public boolean contains(String action){
        return actions.contains(action);
    }

    public EventActionList addActionList(EventActionList eventActionList){
        actions.addAll(eventActionList.getList());
        return this;
    }

    public EventActionList removeAction(String action){
        actions.remove(action);
        return this;
    }

    public int size(){
        return actions.size();
    }

    public String getItem(int index){
        return actions.get(index);
    }

    public List<String> getList(){
        return actions;
    }

}
