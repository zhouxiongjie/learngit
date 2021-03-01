package com.shuangling.software.entity;
import java.io.Serializable;
import java.util.List;

public class RedPacketInfo implements Serializable {

    /**
     * id : 182
     * room_id : 1450
     * anchor_id : 4
     * sponsor_nm :
     * sponsor_log :
     * bagcount : 10
     * money : 100
     * wish : 恭喜发财，恭喜发财！
     * bag_type : 2
     * send_type : 1
     * state : 1
     * start : 2021-01-27 09:06:30
     * stop : 2021-01-27 09:06:30
     * created_by : 1
     * deleted_at : null
     * created_at : 2021-01-27 09:06:30
     * updated_at : 2021-01-27 09:06:30
     * one_money : 0
     * id_asc : 331
     * bill_no_asc : 202101273000331
     * state_end : 0
     * auto_type : 1
     * red_bag_money : 16
     * red_bag_count : 1
     * user : [{"id":123,"redbag_id":182,"user_id":6311,"envelope_id":331,"money":16,"created_at":"2021-01-27 09:06:48","updated_at":"2021-01-27 09:06:48"}]
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
    private int created_by;
    private Object deleted_at;
    private String created_at;
    private String updated_at;
    private int one_money;
    private int id_asc;
    private String bill_no_asc;
    private int state_end;
    private int auto_type;
    private int red_bag_money;
    private int red_bag_count;
    private List<UserBean> user;

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

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public Object getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Object deleted_at) {
        this.deleted_at = deleted_at;
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

    public int getOne_money() {
        return one_money;
    }

    public void setOne_money(int one_money) {
        this.one_money = one_money;
    }

    public int getId_asc() {
        return id_asc;
    }

    public void setId_asc(int id_asc) {
        this.id_asc = id_asc;
    }

    public String getBill_no_asc() {
        return bill_no_asc;
    }

    public void setBill_no_asc(String bill_no_asc) {
        this.bill_no_asc = bill_no_asc;
    }

    public int getState_end() {
        return state_end;
    }

    public void setState_end(int state_end) {
        this.state_end = state_end;
    }

    public int getAuto_type() {
        return auto_type;
    }

    public void setAuto_type(int auto_type) {
        this.auto_type = auto_type;
    }

    public int getRed_bag_money() {
        return red_bag_money;
    }

    public void setRed_bag_money(int red_bag_money) {
        this.red_bag_money = red_bag_money;
    }

    public int getRed_bag_count() {
        return red_bag_count;
    }

    public void setRed_bag_count(int red_bag_count) {
        this.red_bag_count = red_bag_count;
    }

    public List<UserBean> getUser() {
        return user;
    }

    public void setUser(List<UserBean> user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * id : 123
         * redbag_id : 182
         * user_id : 6311
         * envelope_id : 331
         * money : 16
         * created_at : 2021-01-27 09:06:48
         * updated_at : 2021-01-27 09:06:48
         */

        private int id;
        private int redbag_id;
        private int user_id;
        private int envelope_id;
        private int money;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRedbag_id() {
            return redbag_id;
        }

        public void setRedbag_id(int redbag_id) {
            this.redbag_id = redbag_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getEnvelope_id() {
            return envelope_id;
        }

        public void setEnvelope_id(int envelope_id) {
            this.envelope_id = envelope_id;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
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
}
