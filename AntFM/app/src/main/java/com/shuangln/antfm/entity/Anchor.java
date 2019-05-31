package com.shuangln.antfm.entity;

import java.util.List;

public class Anchor {


    /**
     * id : 7
     * name : 李宁
     * logo : http://sl-ucenter.static.slradio.cn/merchants/7/imges/hzkjdia6fycNSiKrpGdYjSn4kY6YRw521553671626210.jpg
     * des :
     * follows : 5
     */

    private int id;
    private String name;
    private String logo;
    private String des;
    private int follows;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }


}
