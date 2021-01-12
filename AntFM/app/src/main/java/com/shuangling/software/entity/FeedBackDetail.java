package com.shuangling.software.entity;
import java.util.List;
public class FeedBackDetail {
/**
     * id : 47
     * user_id : 1
     * phone : 18207406897
     * opinion : 这个是意见反馈的内容
     * enclosure : []
     * is_reply : 1
     * remarks : 回复意见
     * is_user_read : 0
     * reply_id : 1
     * reply_time : 2020-11-19 15:23:51
     * created_at : 2020-11-19 13:10:57
     * user : {"id":1,"username":"00001_sl_18207406897","nickname":"手动阀","phone":"18207406897","avatar":"https://sl-cdn.slradio.cn/ucenter/avatar/imges/kr137bdceP01yipw1584610030811.png"}
     * reply_user : {"id":1,"user_id":1,"staff_name":"小桂子"}
     */
private int id;
    private int user_id;
    private String phone;
    private String opinion;
    private int is_reply;
    private String remarks;
    private int is_user_read;
    private int reply_id;
    private String reply_time;
    private String created_at;
    private UserBean user;
    private ReplyUserBean reply_user;
    private List<String> enclosure;
public int getId() {
        return id;
    }
public void setId(int id) {
        this.id = id;
    }
public int getUser_id() {
        return user_id;
    }
public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
public String getPhone() {
        return phone;
    }
public void setPhone(String phone) {
        this.phone = phone;
    }
public String getOpinion() {
        return opinion;
    }
public void setOpinion(String opinion) {
        this.opinion = opinion;
    }
public int getIs_reply() {
        return is_reply;
    }
public void setIs_reply(int is_reply) {
        this.is_reply = is_reply;
    }
public String getRemarks() {
        return remarks;
    }
public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
public int getIs_user_read() {
        return is_user_read;
    }
public void setIs_user_read(int is_user_read) {
        this.is_user_read = is_user_read;
    }
public int getReply_id() {
        return reply_id;
    }
public void setReply_id(int reply_id) {
        this.reply_id = reply_id;
    }
public String getReply_time() {
        return reply_time;
    }
public void setReply_time(String reply_time) {
        this.reply_time = reply_time;
    }
public String getCreated_at() {
        return created_at;
    }
public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
public UserBean getUser() {
        return user;
    }
public void setUser(UserBean user) {
        this.user = user;
    }
public ReplyUserBean getReply_user() {
        return reply_user;
    }
public void setReply_user(ReplyUserBean reply_user) {
        this.reply_user = reply_user;
    }
public List<String> getEnclosure() {
        return enclosure;
    }
public void setEnclosure(List<String> enclosure) {
        this.enclosure = enclosure;
    }
public static class UserBean {
        /**
         * id : 1
         * username : 00001_sl_18207406897
         * nickname : 手动阀
         * phone : 18207406897
         * avatar : https://sl-cdn.slradio.cn/ucenter/avatar/imges/kr137bdceP01yipw1584610030811.png
         */
private int id;
        private String username;
        private String nickname;
        private String phone;
        private String avatar;
public int getId() {
            return id;
        }
public void setId(int id) {
            this.id = id;
        }
public String getUsername() {
            return username;
        }
public void setUsername(String username) {
            this.username = username;
        }
public String getNickname() {
            return nickname;
        }
public void setNickname(String nickname) {
            this.nickname = nickname;
        }
public String getPhone() {
            return phone;
        }
public void setPhone(String phone) {
            this.phone = phone;
        }
public String getAvatar() {
            return avatar;
        }
public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
public static class ReplyUserBean {
        /**
         * id : 1
         * user_id : 1
         * staff_name : 小桂子
         */
private int id;
        private int user_id;
        private String staff_name;
public int getId() {
            return id;
        }
public void setId(int id) {
            this.id = id;
        }
public int getUser_id() {
            return user_id;
        }
public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
public String getStaff_name() {
            return staff_name;
        }
public void setStaff_name(String staff_name) {
            this.staff_name = staff_name;
        }
    }
}
