package com.shuangling.software.entity;

public class ChatMessage {


    /**
     * messageType : 1
     * userId : 1
     * msg : https://sl-cdn.slradio.cn/cms/live_c_chat/cAFWJS7jcTP7rtaP1589184175714.jpg
     * streamName : DDC04C31EB2650C08C613E66D2445340
     * nickName : 18874867191
     * type : 2
     * chatsId : 1835486
     * userLog : https://sl-cdn.slradio.cn/qrs/user/2020_02_12_11_48_58.jpeg
     * sendTime : 2020-05-11 16:02:58
     * audit : 2
     * aiAudit : 2
     * contentType : 2
     * parentMsgInfo :
     */

    private int messageType;
    private String userId;
    private String msg;
    private String streamName;
    private String nickName;
    private int type;
    private int chatsId;
    private String userLog;
    private String sendTime;
    private int audit;
    private int aiAudit;
    private int contentType;
    private ChatMessage parentMsgInfo;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChatsId() {
        return chatsId;
    }

    public void setChatsId(int chatsId) {
        this.chatsId = chatsId;
    }

    public String getUserLog() {
        return userLog;
    }

    public void setUserLog(String userLog) {
        this.userLog = userLog;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public int getAiAudit() {
        return aiAudit;
    }

    public void setAiAudit(int aiAudit) {
        this.aiAudit = aiAudit;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public ChatMessage getParentMsgInfo() {
        return parentMsgInfo;
    }

    public void setParentMsgInfo(ChatMessage parentMsgInfo) {
        this.parentMsgInfo = parentMsgInfo;
    }
}
