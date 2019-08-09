package com.shuangling.software.entity;

import java.io.Serializable;

public class UserDetail implements Serializable {


    /**
     * id : 781
     * username : sl_151111410731
     * username_checked : 0
     * nickname : 151****1073
     * password : $2y$10$YbGxrXuWvV8PmT4YwXNneuYcZmdpZ9w4vJ5eRzqTySfPfWmRF2bli
     * password_checked : 0
     * email :
     * email_checked : 0
     * phone : 15111141073
     * phone_checked : 0
     * avatar : http://sl-cdn.slradio.cn/cms/user/head.jpg
     * status : 0
     * reg_from_app_id : 0
     * deleted_at : null
     * created_at : 2019-07-10 09:39:11
     * updated_at : 2019-07-17 15:08:00
     * profile : {"id":766,"user_id":781,"en_nickname":"","sex":0,"birthdate":"","company":"","home_address":"","active_ip":"192.168.40.199","invite_user_id":0,"active_time":"2019-07-17 16:48:51","created_at":"2019-07-10 09:39:11","updated_at":"2019-07-17 16:48:51"}
     */

    private int id;
    private String username;
    private int username_checked;
    private String nickname;
    private String password;
    private int password_checked;
    private String email;
    private int email_checked;
    private String phone;
    private int phone_checked;
    private String avatar;
    private int status;
    private int reg_from_app_id;
    private Object deleted_at;
    private String created_at;
    private String updated_at;
    private ProfileBean profile;

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

    public int getUsername_checked() {
        return username_checked;
    }

    public void setUsername_checked(int username_checked) {
        this.username_checked = username_checked;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPassword_checked() {
        return password_checked;
    }

    public void setPassword_checked(int password_checked) {
        this.password_checked = password_checked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEmail_checked() {
        return email_checked;
    }

    public void setEmail_checked(int email_checked) {
        this.email_checked = email_checked;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPhone_checked() {
        return phone_checked;
    }

    public void setPhone_checked(int phone_checked) {
        this.phone_checked = phone_checked;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReg_from_app_id() {
        return reg_from_app_id;
    }

    public void setReg_from_app_id(int reg_from_app_id) {
        this.reg_from_app_id = reg_from_app_id;
    }

    public Object getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Object deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public ProfileBean getProfile() {
        return profile;
    }

    public void setProfile(ProfileBean profile) {
        this.profile = profile;
    }

    public static class ProfileBean {
        /**
         * id : 766
         * user_id : 781
         * en_nickname :
         * sex : 0
         * birthdate :
         * company :
         * home_address :
         * active_ip : 192.168.40.199
         * invite_user_id : 0
         * active_time : 2019-07-17 16:48:51
         * created_at : 2019-07-10 09:39:11
         * updated_at : 2019-07-17 16:48:51
         */

        private int id;
        private int user_id;
        private String en_nickname;
        private int sex;
        private String birthdate;
        private String company;
        private String home_address;
        private String active_ip;
        private int invite_user_id;
        private String active_time;
        private String created_at;
        private String updated_at;

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

        public String getEn_nickname() {
            return en_nickname;
        }

        public void setEn_nickname(String en_nickname) {
            this.en_nickname = en_nickname;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getHome_address() {
            return home_address;
        }

        public void setHome_address(String home_address) {
            this.home_address = home_address;
        }

        public String getActive_ip() {
            return active_ip;
        }

        public void setActive_ip(String active_ip) {
            this.active_ip = active_ip;
        }

        public int getInvite_user_id() {
            return invite_user_id;
        }

        public void setInvite_user_id(int invite_user_id) {
            this.invite_user_id = invite_user_id;
        }

        public String getActive_time() {
            return active_time;
        }

        public void setActive_time(String active_time) {
            this.active_time = active_time;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
