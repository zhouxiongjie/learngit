package com.shuangling.software.entity;


import java.io.Serializable;

public class City  implements Serializable {


    /**
     * code : 4310
     * name : 郴州市
     * initials : C
     */

    private int code;
    private String name;
    private String initials;

    public City(){

    }

    public City(int code,String name,String initials){
        this.code=code;
        this.name=name;
        this.initials=initials;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }
}