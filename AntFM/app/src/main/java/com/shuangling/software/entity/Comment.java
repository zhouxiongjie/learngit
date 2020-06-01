package com.shuangling.software.entity;


import java.io.Serializable;
import java.util.List;

public class Comment implements Serializable {


    /**
     * id : 1190
     * post_id : 17293
     * type : 1
     * like_count : 0
     * comment_count : 5
     * parent_id : 0
     * base_comment_id : 0
     * user_id : 149
     * green_status : 3
     * status : 1
     * created_at : 2020-05-11 08:57:43
     * model : 0
     * sort_weight : 70
     * delete : 0
     * fabulous : 0
     * time_length : 昨天
     * text : {"post_comment_id":1190,"content":"好样的"}
     * user : {"id":149,"nickname":"18874751342","avatar":""}
     * parent_comment : [{"id":1191,"post_id":17293,"type":1,"like_count":0,"comment_count":0,"parent_id":1190,"base_comment_id":1190,"user_id":149,"green_status":3,"status":1,"created_at":"2020-05-11 08:57:59","model":0,"sort_weight":0,"delete":0,"fabulous":0,"time_length":"昨天","text":{"post_comment_id":1191,"content":"非常棒"},"user":{"id":149,"nickname":"18874751342","avatar":""}},{"id":1195,"post_id":17293,"type":1,"like_count":0,"comment_count":0,"parent_id":1190,"base_comment_id":1190,"user_id":1,"green_status":3,"status":1,"created_at":"2020-05-11 10:57:39","model":0,"sort_weight":0,"delete":0,"fabulous":0,"time_length":"昨天","text":{"post_comment_id":1195,"content":"哎呀"},"user":{"id":1,"nickname":"手动阀","avatar":"https://sl-cdn.slradio.cn/ucenter/avatar/imges/kr137bdceP01yipw1584610030811.png"}}]
     */

    private int id;
    private int post_id;
    private int type;
    private int like_count;
    private int comment_count;
    private int parent_id;
    private int base_comment_id;
    private int user_id;
    private int green_status;
    private int status;
    private String created_at;
    private int model;
    private int sort_weight;
    private int delete;
    private int fabulous;
    private String time_length;
    private TextBean text;
    private UserBean user;
    private boolean expand;
    private List<Comment> parent_comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getBase_comment_id() {
        return base_comment_id;
    }

    public void setBase_comment_id(int base_comment_id) {
        this.base_comment_id = base_comment_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getGreen_status() {
        return green_status;
    }

    public void setGreen_status(int green_status) {
        this.green_status = green_status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getSort_weight() {
        return sort_weight;
    }

    public void setSort_weight(int sort_weight) {
        this.sort_weight = sort_weight;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    public int getFabulous() {
        return fabulous;
    }

    public void setFabulous(int fabulous) {
        this.fabulous = fabulous;
    }

    public String getTime_length() {
        return time_length;
    }

    public void setTime_length(String time_length) {
        this.time_length = time_length;
    }

    public TextBean getText() {
        return text;
    }

    public void setText(TextBean text) {
        this.text = text;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }



    public static class TextBean {
        /**
         * post_comment_id : 1190
         * content : 好样的
         */

        private int post_comment_id;
        private String content;

        public int getPost_comment_id() {
            return post_comment_id;
        }

        public void setPost_comment_id(int post_comment_id) {
            this.post_comment_id = post_comment_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class UserBean {
        /**
         * id : 149
         * nickname : 18874751342
         * avatar :
         */

        private int id;
        private String nickname;
        private String avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }


    public List<Comment> getParent_comment() {
        return parent_comment;
    }

    public void setParent_comment(List<Comment> parent_comment) {
        this.parent_comment = parent_comment;
    }


}