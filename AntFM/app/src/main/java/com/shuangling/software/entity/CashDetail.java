package com.shuangling.software.entity;

import java.io.Serializable;

public class CashDetail implements Serializable {


    /**
     * id : 3
     * user_id : 115
     * status : 1
     * type : 1
     * trade_no : 2020010730000003
     * money : 2
     * account : 285055073@qq.com
     * name : 周雄杰
     * audit : 1
     * remark :
     * fail_msg :
     * operator : 0
     * send_time : null
     * created_at : 2020-01-07 11:46:58
     * updated_at : 2020-01-07 11:46:58
     */

    private int id;
    private int user_id;
    private int status;
    private int type;
    private String trade_no;
    private int money;
    private String account;
    private String name;
    private int audit;
    private String remark;
    private String fail_msg;
    private int operator;
    private String send_time;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFail_msg() {
        return fail_msg;
    }

    public void setFail_msg(String fail_msg) {
        this.fail_msg = fail_msg;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
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
