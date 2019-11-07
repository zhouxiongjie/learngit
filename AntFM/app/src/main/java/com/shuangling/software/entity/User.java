package com.shuangling.software.entity;

public class User {




    private static User sUser;
    /**
     * id : 781
     * username : sl_151111410731
     * username_checked : 0
     * nickname : 151****1073
     * password_checked : 0
     * email :
     * email_checked : 0
     * phone : 15111141073
     * phone_checked : 0
     * avatar : http://sl-cdn.slradio.cn/cms/user/head.jpg
     * status : 0
     * reg_from_app_id : 0
     * created_at : 2019-07-10 09:39:11
     * is_talk : 0
     * Authorization : b6f867d752e2c4ec544b2001f8641c39
     * refresh_token : 11fcbce99bd97d8165377772a903bf1f
     */

    private int id;
    private String username;
    private int username_checked;
    private String nickname;
    private int password_checked;
    private String email;
    private int email_checked;
    private String phone;
    private int phone_checked;
    private String avatar;
    private int status;
    private int reg_from_app_id;
    private String created_at;
    private int is_talk;
    private String Authorization;
    private String refresh_token;

    public static User getInstance(){
//        if(sUser==null){
//            synchronized (User.class){
//                if (sUser == null) {
//                    sUser = new User();
//                }
//            }
//        }
        return sUser;
    }

    public static void setInstance(User user){
        sUser=user;
    }


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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getIs_talk() {
        return is_talk;
    }

    public void setIs_talk(int is_talk) {
        this.is_talk = is_talk;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public void setAuthorization(String Authorization) {
        this.Authorization = Authorization;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
