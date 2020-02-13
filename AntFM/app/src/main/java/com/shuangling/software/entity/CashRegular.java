package com.shuangling.software.entity;

import java.io.Serializable;

public class CashRegular implements Serializable {


    /**
     * id : 1
     * day_count : 5
     * min_money : 100
     * max_money : 1000
     * transfer_day : 5
     * fail_msg : 22
     * created_at : 2020-01-06 10:13:24
     * updated_at : 2020-01-13 16:31:46
     * rule : 1、每天限制提现5笔；\n2、单笔提现最低金额1元，最高金额10元；\n3、预计在5工作日内到账；
     */

    private int id;
    private int day_count;
    private int min_money;
    private int max_money;
    private int transfer_day;
    private String fail_msg;
    private String created_at;
    private String updated_at;
    private String rule;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay_count() {
        return day_count;
    }

    public void setDay_count(int day_count) {
        this.day_count = day_count;
    }

    public int getMin_money() {
        return min_money;
    }

    public void setMin_money(int min_money) {
        this.min_money = min_money;
    }

    public int getMax_money() {
        return max_money;
    }

    public void setMax_money(int max_money) {
        this.max_money = max_money;
    }

    public int getTransfer_day() {
        return transfer_day;
    }

    public void setTransfer_day(int transfer_day) {
        this.transfer_day = transfer_day;
    }

    public String getFail_msg() {
        return fail_msg;
    }

    public void setFail_msg(String fail_msg) {
        this.fail_msg = fail_msg;
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

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
