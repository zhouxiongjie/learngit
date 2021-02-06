package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class LiveOnlineViewer implements Serializable {


    /**
     * room_id : 438
     * msg : {"online_count":1,"total_count":1,"avatar_count":1,"avatar_list":[{"user_id":349,"nick_name":"15111111111","avatar":""}]}
     * socket : null
     */

    private int room_id;
    private MsgBean msg;
    private Object socket;

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public Object getSocket() {
        return socket;
    }

    public void setSocket(Object socket) {
        this.socket = socket;
    }

    public static class MsgBean {
        /**
         * online_count : 1
         * total_count : 1
         * avatar_count : 1
         * avatar_list : [{"user_id":349,"nick_name":"15111111111","avatar":""}]
         */

        private int online_count;
        private int total_count;
        private int avatar_count;
        private List<AvatarListBean> avatar_list;

        public int getOnline_count() {
            return online_count;
        }

        public void setOnline_count(int online_count) {
            this.online_count = online_count;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public int getAvatar_count() {
            return avatar_count;
        }

        public void setAvatar_count(int avatar_count) {
            this.avatar_count = avatar_count;
        }

        public List<AvatarListBean> getAvatar_list() {
            return avatar_list;
        }

        public void setAvatar_list(List<AvatarListBean> avatar_list) {
            this.avatar_list = avatar_list;
        }

        public static class AvatarListBean {
            /**
             * user_id : 349
             * nick_name : 15111111111
             * avatar :
             */

            private int user_id;
            private String nick_name;
            private String avatar;

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public String getNick_name() {
                return nick_name;
            }

            public void setNick_name(String nick_name) {
                this.nick_name = nick_name;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}
