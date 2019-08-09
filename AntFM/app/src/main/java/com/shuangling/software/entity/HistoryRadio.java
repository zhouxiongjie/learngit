package com.shuangling.software.entity;

import java.io.Serializable;

public class HistoryRadio implements Serializable {


    /**
     * id : 487
     * user_id : 325
     * channel_id : 74
     * created_at : 2019-06-27 14:32:59
     * updated_at : 2019-06-27 14:32:59
     * radio : {"id":74,"merchant_id":4,"province":"43","city":"4301","county":"430104","name":"sl卫视","logo":"http://sl-cms.static.slradio.cn/merchants/1/imges/1R0YrT0cx4Ks7nXsWYmG3x8ZbzD18MdF1554342992728.jpg","des":"一个电视台呃呃呃呃额","stream":"http://live.xn--kprp9vt3co1csxbnw2bdmep13c3h9a.xn--55qw42g.cn/live/zlt.m3u8","view":327,"collection":4}
     */

    private int id;
    private int user_id;
    private int channel_id;
    private String created_at;
    private String updated_at;
    private RadioBean radio;

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

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
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

    public RadioBean getRadio() {
        return radio;
    }

    public void setRadio(RadioBean radio) {
        this.radio = radio;
    }

    public static class RadioBean {
        /**
         * id : 74
         * merchant_id : 4
         * province : 43
         * city : 4301
         * county : 430104
         * name : sl卫视
         * logo : http://sl-cms.static.slradio.cn/merchants/1/imges/1R0YrT0cx4Ks7nXsWYmG3x8ZbzD18MdF1554342992728.jpg
         * des : 一个电视台呃呃呃呃额
         * stream : http://live.xn--kprp9vt3co1csxbnw2bdmep13c3h9a.xn--55qw42g.cn/live/zlt.m3u8
         * view : 327
         * collection : 4
         */

        private int id;
        private int merchant_id;
        private String province;
        private String city;
        private String county;
        private String name;
        private String logo;
        private String des;
        private String stream;
        private int view;
        private int collection;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }

        public int getCollection() {
            return collection;
        }

        public void setCollection(int collection) {
            this.collection = collection;
        }
    }
}
