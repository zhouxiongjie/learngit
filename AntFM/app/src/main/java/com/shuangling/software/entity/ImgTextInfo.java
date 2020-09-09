package com.shuangling.software.entity;

import java.util.List;

public class ImgTextInfo {


    /**
     * chatsId : 198
     * msg : {"contents":"哈哈哈哈","imgArray":["https://sl-cdn.slradio.cn/vms/Image/7xdDTrnNs4Y6awmG.jpg?width=728&height=509"]}
     * type : 1
     * user_id : 1
     * nickName : 黄庚
     * userLog : https://sl-cdn.slradio.cn/ucenter/avatar/imges/kr137bdceP01yipw1584610030811.png
     * sendTime : 2020-08-21 14:39:12
     * audit : 2
     * parent_id : 0
     * ai_audit : 2
     * content_type : 3
     * parentMsgInfo :
     */

    private int chatsId;
    private String msg;
    private int type;
    private int user_id;
    private String nickName;
    private String userLog;
    private String sendTime;
    private int audit;
    private int parent_id;
    private int ai_audit;
    private int content_type;
    private String parentMsgInfo;
    private String contents;
    private List<String> imgArray;



    public int getChatsId() {
        return chatsId;
    }

    public void setChatsId(int chatsId) {
        this.chatsId = chatsId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getAi_audit() {
        return ai_audit;
    }

    public void setAi_audit(int ai_audit) {
        this.ai_audit = ai_audit;
    }

    public int getContent_type() {
        return content_type;
    }

    public void setContent_type(int content_type) {
        this.content_type = content_type;
    }

    public String getParentMsgInfo() {
        return parentMsgInfo;
    }

    public void setParentMsgInfo(String parentMsgInfo) {
        this.parentMsgInfo = parentMsgInfo;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public List<String> getImgArray() {
        return imgArray;
    }

    public void setImgArray(List<String> imgArray) {
        this.imgArray = imgArray;
    }
}
