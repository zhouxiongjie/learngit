package com.shuangling.software.entity;

import java.io.Serializable;

public class RedPacketInfo implements Serializable {


    /**
     * id : 182
     * room_id : 502
     * anchor_id : 4
     * sponsor_nm : 东鹏特饮
     * sponsor_log : http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/logo/imges/GeBDf1r0akK8BFaY1600742013605.jpg
     * bagcount : 10
     * money : 100
     * wish : 恭喜发财，大吉大利
     * bag_type : 2
     * send_type : 1
     * state : 1
     * start : 2020-09-22 10:34:00
     * stop : 2020-09-22 10:34:00
     * red_bag_count : 0
     * red_bag_money : 0
     * room_name : 产品创建的直播间
     * id_asc : 158
     * state_end : 0
     */

    private int id;
    private int room_id;
    private int anchor_id;
    private String sponsor_nm;
    private String sponsor_log;
    private int bagcount;
    private int money;
    private String wish;
    private int bag_type;
    private int send_type;
    private int state;
    private String start;
    private String stop;
    private int red_bag_count;
    private int red_bag_money;
    private String room_name;
    private int id_asc;
    private int state_end;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getAnchor_id() {
        return anchor_id;
    }

    public void setAnchor_id(int anchor_id) {
        this.anchor_id = anchor_id;
    }

    public String getSponsor_nm() {
        return sponsor_nm;
    }

    public void setSponsor_nm(String sponsor_nm) {
        this.sponsor_nm = sponsor_nm;
    }

    public String getSponsor_log() {
        return sponsor_log;
    }

    public void setSponsor_log(String sponsor_log) {
        this.sponsor_log = sponsor_log;
    }

    public int getBagcount() {
        return bagcount;
    }

    public void setBagcount(int bagcount) {
        this.bagcount = bagcount;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getWish() {
        return wish;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }

    public int getBag_type() {
        return bag_type;
    }

    public void setBag_type(int bag_type) {
        this.bag_type = bag_type;
    }

    public int getSend_type() {
        return send_type;
    }

    public void setSend_type(int send_type) {
        this.send_type = send_type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public int getRed_bag_count() {
        return red_bag_count;
    }

    public void setRed_bag_count(int red_bag_count) {
        this.red_bag_count = red_bag_count;
    }

    public int getRed_bag_money() {
        return red_bag_money;
    }

    public void setRed_bag_money(int red_bag_money) {
        this.red_bag_money = red_bag_money;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public int getId_asc() {
        return id_asc;
    }

    public void setId_asc(int id_asc) {
        this.id_asc = id_asc;
    }

    public int getState_end() {
        return state_end;
    }

    public void setState_end(int state_end) {
        this.state_end = state_end;
    }
}
