package com.shuangling.software.entity;

import java.io.Serializable;

public class ZhifubaoAccountInfo implements Serializable {


    /**
     * id : 1
     * user_id : 115
     * name : 姜佳利
     * account : 18163605388
     * created_at : 2020-01-06 10:15:29
     * updated_at : 2020-01-06 10:15:29
     */

    private int id;
    private int user_id;
    private String name;
    private String account;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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
