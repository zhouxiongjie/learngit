package com.shuangling.software.entity;

import android.text.TextUtils;

import static android.content.Context.MODE_PRIVATE;

public class User {


    /**
     * id : 64
     * username : sl_15111141073
     * username_checked : 0
     * nickname : 15111141073
     * password_checked : 0
     * email :
     * email_checked : 0
     * phone : 15111141073
     * phone_checked : 0
     * avatar :
     * status : 0
     * reg_from_app_id : 0
     * created_at : 2019-05-21 19:26:00
     * Authorization : c4b5abb09ca20d2ee648fecc0218b2aa
     */

    private static User sUser;
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







    private int id;
    private String username;
    private int username_checked;
    private long nickname;
    private int password_checked;
    private String email;
    private int email_checked;
    private long phone;
    private int phone_checked;
    private String avatar;
    private int status;
    private int reg_from_app_id;
    private String created_at;
    private String Authorization;

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

    public long getNickname() {
        return nickname;
    }

    public void setNickname(long nickname) {
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

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
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

    public String getAuthorization() {
        return Authorization;
    }

    public void setAuthorization(String Authorization) {
        this.Authorization = Authorization;
    }
}
