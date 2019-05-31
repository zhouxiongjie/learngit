package com.shuangln.antfm.entity;

/**
 * Created by Administrator on 2018-06-25.
 */

public class PlayerEvent {
    private String eventName;
    private Object eventObject;

    public PlayerEvent(String eventName, Object eventObject){
        this.eventName=eventName;
        this.eventObject=eventObject;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object getEventObject() {
        return eventObject;
    }

    public void setEventObject(Object eventObject) {
        this.eventObject = eventObject;
    }
}
