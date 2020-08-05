package com.shuangling.software.event;

/**
 * Created by Administrator on 2018-06-25.
 */

public class MessageEvent {
    private String eventName;
    private String messageBody;

    public MessageEvent(String eventName, String messageBody){
        this.eventName=eventName;
        this.messageBody=messageBody;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
