package com.shuangling.software.entity;

import java.io.Serializable;

public class AccountInfo implements Serializable {


    /**
     * user_id : 781
     * number : 200000004
     * type : 0
     * status : 0
     * balance : 0
     * free_balance : 0
     * recharge_money : 0
     * freeze_money : 0
     * sign : eyJpdiI6IkdUS2xOSWVna2p6RzVrVGNuazFYN0E9PSIsInZhbHVlIjoiVUxcL2ltMnB4dk5EOE5ZNWZXVGdTNFpQcE53N1ZCWnRza09veTh1MjJNRTA9IiwibWFjIjoiYTAxMTk5ZGY3YzVhY2ExN2ZiMmE0YThkNzY1MjRlYmEzMTU5NDc5MmRhZjUzZWVlNGY1ZDFkZDNiMzk1ODA2NyJ9
     * updated_at : 2020-01-06 13:48:19
     * created_at : 2020-01-06 13:48:19
     * id : 4
     */

    private int user_id;
    private int number;
    private int type;
    private int status;
    private int balance;
    private int free_balance;
    private int recharge_money;
    private int freeze_money;
    private String sign;
    private String updated_at;
    private String created_at;
    private int id;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getFree_balance() {
        return free_balance;
    }

    public void setFree_balance(int free_balance) {
        this.free_balance = free_balance;
    }

    public int getRecharge_money() {
        return recharge_money;
    }

    public void setRecharge_money(int recharge_money) {
        this.recharge_money = recharge_money;
    }

    public int getFreeze_money() {
        return freeze_money;
    }

    public void setFreeze_money(int freeze_money) {
        this.freeze_money = freeze_money;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
