package com.shuangling.software.entity;

import java.util.List;

public class ServerCategory {


    /**
     * id : 24
     * title : 第三方公益
     * status : 1
     * created_at : 2019-07-03 13:46:34
     * updated_at : 2019-07-03 13:46:34
     * service : [{"id":39,"title":"蚂蚁森林","categorie_id":24,"type":2,"cover":"https://sl-cms.static.slradio.cn/merchants/115/imges/7C2Sfpyy1JrQNtWhkhdkhxzd0RmQGyT61562132876966.jpg","des":"","status":1,"link_url":"http://www.mayisenlin.com","publish_at":"2019-12-05 09:31:35","author_id":18,"province":"43","city":"4301","county":"430102","created_at":"2019-07-03 13:48:10","updated_at":"2019-12-11 14:36:02","clicks":31}]
     */

    private int id;
    private String title;
    private int status;
    private String created_at;
    private String updated_at;
    private List<Service> service;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public List<Service> getService() {
        return service;
    }

    public void setService(List<Service> service) {
        this.service = service;
    }

    public static class Service {
        /**
         * id : 39
         * title : 蚂蚁森林
         * categorie_id : 24
         * type : 2
         * cover : https://sl-cms.static.slradio.cn/merchants/115/imges/7C2Sfpyy1JrQNtWhkhdkhxzd0RmQGyT61562132876966.jpg
         * des :
         * status : 1
         * link_url : http://www.mayisenlin.com
         * publish_at : 2019-12-05 09:31:35
         * author_id : 18
         * province : 43
         * city : 4301
         * county : 430102
         * created_at : 2019-07-03 13:48:10
         * updated_at : 2019-12-11 14:36:02
         * clicks : 31
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
        private int clicks;

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

        public int getClicks() {
            return clicks;
        }

        public void setClicks(int clicks) {
            this.clicks = clicks;
        }
    }
}
