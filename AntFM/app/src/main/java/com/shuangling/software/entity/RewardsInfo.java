package com.shuangling.software.entity;

import java.util.List;

public class RewardsInfo {


    /**
     * id : 11
     * room_id : 656
     * ask_id : 190
     * des :
     * keyword : 1112222222
     * state : 2
     * open_at : 2021-01-19 00:00:00
     * stop_at : 2021-01-20 00:00:00
     * deleted_at : null
     * created_at : 2021-01-18 14:32:23
     * updated_at : 2021-01-18 14:42:01
     * awards : [{"id":7,"ask_id":190,"batch":1,"user_id":1,"nickname":"昵称一","avatar":"http://234.jpg","money":26,"open_at":"2020-01-15 09:22:15","deleted_at":null,"created_at":"2021-01-18 11:29:19","updated_at":"2021-01-18 11:29:19"},{"id":8,"ask_id":190,"batch":1,"user_id":2,"nickname":"昵称二","avatar":"http://234.jpg","money":34,"open_at":"2020-01-15 09:22:18","deleted_at":null,"created_at":"2021-01-18 11:29:19","updated_at":"2021-01-18 11:29:19"}]
     */

    private int id;
    private int room_id;
    private int ask_id;
    private String des;
    private String keyword;
    private int state;
    private String open_at;
    private String stop_at;
    private Object deleted_at;
    private String created_at;
    private String updated_at;
    private List<AwardsBean> awards;

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

    public int getAsk_id() {
        return ask_id;
    }

    public void setAsk_id(int ask_id) {
        this.ask_id = ask_id;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOpen_at() {
        return open_at;
    }

    public void setOpen_at(String open_at) {
        this.open_at = open_at;
    }

    public String getStop_at() {
        return stop_at;
    }

    public void setStop_at(String stop_at) {
        this.stop_at = stop_at;
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

    public List<AwardsBean> getAwards() {
        return awards;
    }

    public void setAwards(List<AwardsBean> awards) {
        this.awards = awards;
    }

    public static class AwardsBean {
        /**
         * id : 7
         * ask_id : 190
         * batch : 1
         * user_id : 1
         * nickname : 昵称一
         * avatar : http://234.jpg
         * money : 26
         * open_at : 2020-01-15 09:22:15
         * deleted_at : null
         * created_at : 2021-01-18 11:29:19
         * updated_at : 2021-01-18 11:29:19
         */

        private int id;
        private int ask_id;
        private int batch;
        private int user_id;
        private String nickname;
        private String avatar;
        private int money;
        private String open_at;
        private Object deleted_at;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAsk_id() {
            return ask_id;
        }

        public void setAsk_id(int ask_id) {
            this.ask_id = ask_id;
        }

        public int getBatch() {
            return batch;
        }

        public void setBatch(int batch) {
            this.batch = batch;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public String getOpen_at() {
            return open_at;
        }

        public void setOpen_at(String open_at) {
            this.open_at = open_at;
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
    }
}
