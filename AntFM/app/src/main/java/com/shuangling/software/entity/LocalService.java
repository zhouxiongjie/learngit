package com.shuangling.software.entity;

import java.util.List;

public class LocalService {


    /**
     * code : 100000
     * data : [{"id":12,"title":"热力图","categorie_id":5,"type":1,"cover":"https://sl-cms.static.slradio.cn/platform/imges/jYiyhiWWf8yjyjzCJS55PfBReyE08EFN1558607579298.jpg","des":"城市热力图","status":1,"link_url":"http://c.easygo.qq.com/eg_toc/map.html?origin=csfw&cityid=110000","publish_at":"2019-05-23 18:33:01","author_id":1,"province":"","city":"","county":"","created_at":"2019-05-23 18:33:01","updated_at":"2019-05-23 18:33:01"},{"id":9,"title":"挂号服务","categorie_id":6,"type":1,"cover":"https://sl-cms.static.slradio.cn/platform/imges/9EQTWF13HGdY5Ra56Yp166TQhsjJ00KX1558607108459.jpg","des":"挂号服务","status":1,"link_url":"https://weixin.91160.com/?cid=23&auto_login=true&referrer=A91160&code=033zwzF7188gnM1uWCD7130BF71zwzFE&state=91160","publish_at":"2019-05-23 18:25:09","author_id":1,"province":"","city":"","county":"","created_at":"2019-05-23 18:25:09","updated_at":"2019-05-23 18:25:09"},{"id":8,"title":"司法公证","categorie_id":4,"type":1,"cover":"https://sl-cms.static.slradio.cn/platform/imges/be6YFhmTNjetXN8hw2jzrZFYp1Wa9R3b1558606996016.jpg","des":"司法公证服务","status":1,"link_url":"http://wx.bangongzheng.com/wxlogin.htm?org_id=az&az_city=%E9%95%BF%E6%B2%99%E5%B8%82&code=0610xM8V0utvH12rfH9V04RI8V00xM83&state=az","publish_at":"2019-05-23 18:23:28","author_id":1,"province":"","city":"","county":"","created_at":"2019-05-23 18:23:28","updated_at":"2019-05-23 18:23:28"},{"id":3,"title":"天气查询","categorie_id":5,"type":1,"cover":"https://sl-cms.static.slradio.cn/platform/imges/bxdtT3Wfi1MarkT6zhj2yrB6Pee1d4tQ1558606256574.jpg","des":"本地天气查询","status":1,"link_url":"https://apip.weatherdt.com/h5.html?id=pVjoEWuj5f","publish_at":"2019-05-23 18:10:58","author_id":1,"province":"","city":"","county":"","created_at":"2019-05-23 15:55:30","updated_at":"2019-05-23 18:11:04"}]
     * msg : 查询成功
     */

    private int code;
    private String msg;
    private List<Service> data;

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

    public List<Service> getData() {
        return data;
    }

    public void setData(List<Service> data) {
        this.data = data;
    }

    public static class Service {
        /**
         * id : 12
         * title : 热力图
         * categorie_id : 5
         * type : 1
         * cover : https://sl-cms.static.slradio.cn/platform/imges/jYiyhiWWf8yjyjzCJS55PfBReyE08EFN1558607579298.jpg
         * des : 城市热力图
         * status : 1
         * link_url : http://c.easygo.qq.com/eg_toc/map.html?origin=csfw&cityid=110000
         * publish_at : 2019-05-23 18:33:01
         * author_id : 1
         * province :
         * city :
         * county :
         * created_at : 2019-05-23 18:33:01
         * updated_at : 2019-05-23 18:33:01
         */

        private int id;
        private String title;
        private int categorie_id;
        private int type;
        private String cover;
        private String des;
        private int status;
        private String link_url;
        private String publish_at;
        private int author_id;
        private String province;
        private String city;
        private String county;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCategorie_id() {
            return categorie_id;
        }

        public void setCategorie_id(int categorie_id) {
            this.categorie_id = categorie_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getLink_url() {
            return link_url;
        }

        public void setLink_url(String link_url) {
            this.link_url = link_url;
        }

        public String getPublish_at() {
            return publish_at;
        }

        public void setPublish_at(String publish_at) {
            this.publish_at = publish_at;
        }

        public int getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(int author_id) {
            this.author_id = author_id;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
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
