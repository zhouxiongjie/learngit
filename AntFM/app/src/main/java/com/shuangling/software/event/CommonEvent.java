package com.shuangling.software.event;

/**
 * Created by Administrator on 2018-06-25.
 */

public class CommonEvent {
    private String eventName;

    public CommonEvent(String eventName){
        this.eventName=eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


}
