package com.shuangling.software.entity;
import java.util.List;
public class FeedBackInfo {
/**
     * id : 56
     * user_id : 827
     * phone : 15111141073
     * opinion : 太冷了家里
     * enclosure : ["https://sl-cdn.slradio.cn/cms/user/494142e41c657401169bd6b3d7bfbe70.jpg"]
     * is_reply : 0
     * remarks : null
     * is_user_read : 0
     * reply_time : null
     * created_at : 2020-11-20 14:09:17
     * user : {"id":827,"username":"00001_sl_opkDiwvbcMASAz6-ucn8Rp-6gkRs","nickname":"双菱运维","phone":"","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/BwXicCJN0XHPIzfILjDvXeMxFHG6F08QnIiaadsoq2nlAAwp1F4ia9GgibRN46e6gskaQ1ibWMNT4Z0qYfkUXU3pfrA/132"}
     */
private int id;
    private int user_id;
    private String phone;
    private String opinion;
    private int is_reply;
    private Object remarks;
    private int is_user_read;
    private Object reply_time;
    private String created_at;
    private UserBean user;
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
public Object getRemarks() {
        return remarks;
    }
public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }
public int getIs_user_read() {
        return is_user_read;
    }
public void setIs_user_read(int is_user_read) {
        this.is_user_read = is_user_read;
    }
public Object getReply_time() {
        return reply_time;
    }
public void setReply_time(Object reply_time) {
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
public List<String> getEnclosure() {
        return enclosure;
    }
public void setEnclosure(List<String> enclosure) {
        this.enclosure = enclosure;
    }
public static class UserBean {
        /**
         * id : 827
         * username : 00001_sl_opkDiwvbcMASAz6-ucn8Rp-6gkRs
         * nickname : 双菱运维
         * phone :
         * avatar : http://thirdwx.qlogo.cn/mmopen/vi_32/BwXicCJN0XHPIzfILjDvXeMxFHG6F08QnIiaadsoq2nlAAwp1F4ia9GgibRN46e6gskaQ1ibWMNT4Z0qYfkUXU3pfrA/132
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
}
