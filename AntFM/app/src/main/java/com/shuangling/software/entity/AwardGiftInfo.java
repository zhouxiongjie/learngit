package com.shuangling.software.entity;
import java.io.Serializable;

public class AwardGiftInfo implements Serializable {


    /**
     * id : 1735
     * prop_id : 19
     * amount : 2
     * price : 0
     * user_id : 14553
     * props : {"id":19,"merchant_id":0,"name":"干杯","price":0,"limit":0,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/khksEwTnRn2rhbkk1586933829806.png","user_id":1,"display_mode":1,"deleted_at":null,"created_at":"2020-04-15 22:57:19","updated_at":"2020-04-15 22:57:19"}
     * user : {"id":14553,"username":"00001_sl_18673108203","username_checked":0,"nickname":"小丑竟是我自己","password":"$2y$10$MBr6myM/qYT7I7uRpfidQe6OuRHk6MSC1S2V.qELVprJ.yj8DhMxy","password_checked":0,"email":"","email_checked":0,"phone":"18673108203","phone_checked":1,"avatar":"https://sl-cdn.slradio.cn/cms/avatar/3Y3Pad7nW6ndsDRN1611905519773.png","status":0,"reg_from_app_id":0,"deleted_at":null,"created_at":"2020-09-09 17:47:26","updated_at":"2021-01-29 15:32:14","is_talk":0,"from_url":"","from_user_id":0,"jump_url":"http://www-i-wyh.dev.slradio.cn/wx-bind-phone","scene_type":0,"scene_id":0,"scene_str":""}
     */

    private int id;
    private int prop_id;
    private String amount;
    private int price;
    private int user_id;
    private PropsBean props;
    private UserBean user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProp_id() {
        return prop_id;
    }

    public void setProp_id(int prop_id) {
        this.prop_id = prop_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public PropsBean getProps() {
        return props;
    }

    public void setProps(PropsBean props) {
        this.props = props;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class PropsBean {
        /**
         * id : 19
         * merchant_id : 0
         * name : 干杯
         * price : 0
         * limit : 0
         * space : 0
         * icon_url : http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/khksEwTnRn2rhbkk1586933829806.png
         * user_id : 1
         * display_mode : 1
         * deleted_at : null
         * created_at : 2020-04-15 22:57:19
         * updated_at : 2020-04-15 22:57:19
         */

        private int id;
        private int merchant_id;
        private String name;
        private int price;
        private int limit;
        private int space;
        private String icon_url;
        private int user_id;
        private int display_mode;
        private Object deleted_at;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(int merchant_id) {
            this.merchant_id = merchant_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getSpace() {
            return space;
        }

        public void setSpace(int space) {
            this.space = space;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getDisplay_mode() {
            return display_mode;
        }

        public void setDisplay_mode(int display_mode) {
            this.display_mode = display_mode;
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
         * id : 14553
         * username : 00001_sl_18673108203
         * username_checked : 0
         * nickname : 小丑竟是我自己
         * password : $2y$10$MBr6myM/qYT7I7uRpfidQe6OuRHk6MSC1S2V.qELVprJ.yj8DhMxy
         * password_checked : 0
         * email :
         * email_checked : 0
         * phone : 18673108203
         * phone_checked : 1
         * avatar : https://sl-cdn.slradio.cn/cms/avatar/3Y3Pad7nW6ndsDRN1611905519773.png
         * status : 0
         * reg_from_app_id : 0
         * deleted_at : null
         * created_at : 2020-09-09 17:47:26
         * updated_at : 2021-01-29 15:32:14
         * is_talk : 0
         * from_url :
         * from_user_id : 0
         * jump_url : http://www-i-wyh.dev.slradio.cn/wx-bind-phone
         * scene_type : 0
         * scene_id : 0
         * scene_str :
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
        private String jump_url;
        private int scene_type;
        private int scene_id;
        private String scene_str;

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

        public String getJump_url() {
            return jump_url;
        }

        public void setJump_url(String jump_url) {
            this.jump_url = jump_url;
        }

        public int getScene_type() {
            return scene_type;
        }

        public void setScene_type(int scene_type) {
            this.scene_type = scene_type;
        }

        public int getScene_id() {
            return scene_id;
        }

        public void setScene_id(int scene_id) {
            this.scene_id = scene_id;
        }

        public String getScene_str() {
            return scene_str;
        }

        public void setScene_str(String scene_str) {
            this.scene_str = scene_str;
        }
    }
}
