package com.shuangling.software.entity;

public class InviteInfo {


    /**
     * num : 4
     * id : 139
     * room_id : 631
     * inviter_user_id : 18072
     * total_bonus : 100
     * inviter : {"id":18072,"nickname":"匿名用户_A8ZSe","phone":"15343221240","avatar":""}
     */

    private int num;
    private int id;
    private int room_id;
    private int inviter_user_id;
    private int total_bonus;
    private InviterBean inviter;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

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

    public int getInviter_user_id() {
        return inviter_user_id;
    }

    public void setInviter_user_id(int inviter_user_id) {
        this.inviter_user_id = inviter_user_id;
    }

    public int getTotal_bonus() {
        return total_bonus;
    }

    public void setTotal_bonus(int total_bonus) {
        this.total_bonus = total_bonus;
    }

    public InviterBean getInviter() {
        return inviter;
    }

    public void setInviter(InviterBean inviter) {
        this.inviter = inviter;
    }

    public static class InviterBean {
        /**
         * id : 18072
         * nickname : 匿名用户_A8ZSe
         * phone : 15343221240
         * avatar :
         */

        private int id;
        private String nickname;
        private String phone;
        private String avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
