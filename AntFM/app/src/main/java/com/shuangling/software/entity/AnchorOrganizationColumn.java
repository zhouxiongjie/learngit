package com.shuangling.software.entity;

import java.io.Serializable;

public class AnchorOrganizationColumn implements Serializable {


    /**
     * type : 1
     * post_type : 3
     * name : 文章
     */

    private int type;
    private int post_type;
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPost_type() {
        return post_type;
    }

    public void setPost_type(int post_type) {
        this.post_type = post_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
