package com.shuangling.software.entity;


import java.io.Serializable;

public class Comment implements Serializable {


    /**
     * id : 349
     * post_id : 1858
     * type : 1
     * like_count : 0
     * comment_count : 0
     * parent_id : 348
     * base_comment_id : 268
     * user_id : 325
     * green_status : 3
     * status : 1
     * created_at : 2019-06-26 09:57:30
     * delete : 0
     * fabulous : 0
     * time_length : 2小时前
     * text : {"post_comment_id":349,"content":"敌法很叼？"}
     * user : {"id":325,"nickname":"151****1111","avatar":""}
     * parent : {"id":348,"parent_id":347,"user_id":325,"created_at":"2019-06-26 09:56:36","time_length":"2小时前","text":{"post_comment_id":348,"content":"敌法敌法敌法"},"user":{"id":325,"nickname":"151****1111","avatar":""}}
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
    private int delete;
    private int fabulous;
    private String time_length;
    private TextBean text;
    private UserBean user;
    private ParentBean parent;

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

    public ParentBean getParent() {
        return parent;
    }

    public void setParent(ParentBean parent) {
        this.parent = parent;
    }

    public static class TextBean {
        /**
         * post_comment_id : 349
         * content : 敌法很叼？
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
         * id : 325
         * nickname : 151****1111
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

    public static class ParentBean {
        /**
         * id : 348
         * parent_id : 347
         * user_id : 325
         * created_at : 2019-06-26 09:56:36
         * time_length : 2小时前
         * text : {"post_comment_id":348,"content":"敌法敌法敌法"}
         * user : {"id":325,"nickname":"151****1111","avatar":""}
         */

        private int id;
        private int parent_id;
        private int user_id;
        private String created_at;
        private String time_length;
        private TextBean text;
        private UserBean user;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getParent_id() {
            return parent_id;
        }

        public void setParent_id(int parent_id) {
            this.parent_id = parent_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
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

    }
}