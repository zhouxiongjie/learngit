package com.shuangling.software.entity;

import java.io.Serializable;

public class TakeCashInfo implements Serializable {


    /**
     * id : 1
     * user_id : 115
     * status : 4
     * type : 1
     * trade_no :
     * money : 10
     * account : 18163605388
     * name : 姜佳利
     * audit : 0
     * remark :
     * fail_msg : 拒绝
     * operator : {"id":18,"user_id":162,"staff_no":0,"staff_name":"孙XXXXX","dept":"","position":"","title":"","status":1,"join_at":null,"is_admin":0,"privilege":1,"created_user_id":1,"deleted_at":null,"created_at":"2019-03-21 17:09:42","updated_at":"2019-03-21 17:09:42"}
     * send_time : null
     * created_at : 2020-01-06 11:02:00
     * updated_at : 2020-01-06 13:42:33
     * user : {"id":115,"username":"sl_17122334457","username_checked":0,"nickname":"17122334457","password":"$2y$10$5eKFrrqxcdMBhFk41EbpR.JrTkbDtEbM8tl/CzX.G8w0.JEUSVJRW","password_checked":1,"email":"","email_checked":0,"phone":"17122334457","phone_checked":0,"avatar":"http://sl-ucenter.static.slradio.cn/merchants/94/imges/Ra8p97yrFCYE6A0arGHmWxYieXy9CTiZ1552544629432.jpg","status":0,"reg_from_app_id":0,"deleted_at":null,"created_at":"2019-03-14 14:24:11","updated_at":"2019-03-14 14:54:17","is_talk":0,"from_url":"","from_user_id":0}
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
    private OperatorBean operator;
    private Object send_time;
    private String created_at;
    private String updated_at;
    private UserBean user;

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

    public OperatorBean getOperator() {
        return operator;
    }

    public void setOperator(OperatorBean operator) {
        this.operator = operator;
    }

    public Object getSend_time() {
        return send_time;
    }

    public void setSend_time(Object send_time) {
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

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class OperatorBean {
        /**
         * id : 18
         * user_id : 162
         * staff_no : 0
         * staff_name : 孙XXXXX
         * dept :
         * position :
         * title :
         * status : 1
         * join_at : null
         * is_admin : 0
         * privilege : 1
         * created_user_id : 1
         * deleted_at : null
         * created_at : 2019-03-21 17:09:42
         * updated_at : 2019-03-21 17:09:42
         */

        private int id;
        private int user_id;
        private int staff_no;
        private String staff_name;
        private String dept;
        private String position;
        private String title;
        private int status;
        private Object join_at;
        private int is_admin;
        private int privilege;
        private int created_user_id;
        private Object deleted_at;
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

        public int getStaff_no() {
            return staff_no;
        }

        public void setStaff_no(int staff_no) {
            this.staff_no = staff_no;
        }

        public String getStaff_name() {
            return staff_name;
        }

        public void setStaff_name(String staff_name) {
            this.staff_name = staff_name;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getJoin_at() {
            return join_at;
        }

        public void setJoin_at(Object join_at) {
            this.join_at = join_at;
        }

        public int getIs_admin() {
            return is_admin;
        }

        public void setIs_admin(int is_admin) {
            this.is_admin = is_admin;
        }

        public int getPrivilege() {
            return privilege;
        }

        public void setPrivilege(int privilege) {
            this.privilege = privilege;
        }

        public int getCreated_user_id() {
            return created_user_id;
        }

        public void setCreated_user_id(int created_user_id) {
            this.created_user_id = created_user_id;
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

    public static class UserBean {
        /**
         * id : 115
         * username : sl_17122334457
         * username_checked : 0
         * nickname : 17122334457
         * password : $2y$10$5eKFrrqxcdMBhFk41EbpR.JrTkbDtEbM8tl/CzX.G8w0.JEUSVJRW
         * password_checked : 1
         * email :
         * email_checked : 0
         * phone : 17122334457
         * phone_checked : 0
         * avatar : http://sl-ucenter.static.slradio.cn/merchants/94/imges/Ra8p97yrFCYE6A0arGHmWxYieXy9CTiZ1552544629432.jpg
         * status : 0
         * reg_from_app_id : 0
         * deleted_at : null
         * created_at : 2019-03-14 14:24:11
         * updated_at : 2019-03-14 14:54:17
         * is_talk : 0
         * from_url :
         * from_user_id : 0
         */

        private int id;
        private String username;
        private int username_checked;
        private String nickname;
        private String password;
        private int password_checked;
        private String email;
        private int email_checked;
        private String phone;
        private int phone_checked;
        private String avatar;
        private int status;
        private int reg_from_app_id;
        private Object deleted_at;
        private String created_at;
        private String updated_at;
        private int is_talk;
        private String from_url;
        private int from_user_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getUsername_checked() {
            return username_checked;
        }

        public void setUsername_checked(int username_checked) {
            this.username_checked = username_checked;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPassword_checked() {
            return password_checked;
        }

        public void setPassword_checked(int password_checked) {
            this.password_checked = password_checked;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getEmail_checked() {
            return email_checked;
        }

        public void setEmail_checked(int email_checked) {
            this.email_checked = email_checked;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getPhone_checked() {
            return phone_checked;
        }

        public void setPhone_checked(int phone_checked) {
            this.phone_checked = phone_checked;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getReg_from_app_id() {
            return reg_from_app_id;
        }

        public void setReg_from_app_id(int reg_from_app_id) {
            this.reg_from_app_id = reg_from_app_id;
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

        public int getIs_talk() {
            return is_talk;
        }

        public void setIs_talk(int is_talk) {
            this.is_talk = is_talk;
        }

        public String getFrom_url() {
            return from_url;
        }

        public void setFrom_url(String from_url) {
            this.from_url = from_url;
        }

        public int getFrom_user_id() {
            return from_user_id;
        }

        public void setFrom_user_id(int from_user_id) {
            this.from_user_id = from_user_id;
        }
    }
}
