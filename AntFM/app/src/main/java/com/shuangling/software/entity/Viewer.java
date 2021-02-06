package com.shuangling.software.entity;

public class Viewer {


    /**
     * user_id : 1
     * nick_name : 手动阀
     * avatar : https://sl-cdn.slradio.cn/ucenter/avatar/imges/kr137bdceP01yipw1584610030811.png
     */

    private String user_id;
    private String nick_name;
    private String avatar;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
