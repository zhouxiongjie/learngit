package com.shuangling.software.entity;

import java.util.List;

public class UserThirdPlatformModel {

    /**
     * code : 100000
     * data : [{"id":83,"user_id":3580,"unionid":"opkDiwgAdxyTmXh-hRsNdBMqdZGg","app_type":0,"nickname":"空","headimgurl":"http://thirdwx.qlogo.cn/mmopen/vi_32/RI38XdTN6eib4l6uhrtODQB4DPLSF2T4HI3ShTUwqeoX4xgCDibYY7sZQ8Q5AlqfiaPx5qcdUiaO4sU1G6hyPEKlQA/132","created_at":"2020-03-04 08:53:46","updated_at":"2020-03-04 08:53:46"}]
     * msg : 请求成功
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 83
         * user_id : 3580
         * unionid : opkDiwgAdxyTmXh-hRsNdBMqdZGg
         * app_type : 0
         * nickname : 空
         * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/RI38XdTN6eib4l6uhrtODQB4DPLSF2T4HI3ShTUwqeoX4xgCDibYY7sZQ8Q5AlqfiaPx5qcdUiaO4sU1G6hyPEKlQA/132
         * created_at : 2020-03-04 08:53:46
         * updated_at : 2020-03-04 08:53:46
         */

        private int id;
        private int user_id;
        private String unionid;
        private int app_type;
        private String nickname;
        private String headimgurl;
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

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public int getApp_type() {
            return app_type;
        }

        public void setApp_type(int app_type) {
            this.app_type = app_type;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
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
