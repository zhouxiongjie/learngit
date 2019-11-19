package com.shuangling.software.entity;

public class Station {


    /**
     * id : 2
     * name : 飞雪连天射白鹿
     * domain : http://www-cms-pc.review.slradio.cn
     * logo : https://sl-cms.static.slradio.cn/platform/imges/Zi8iSBYzFNbafFmZB320a6BDhT353R2a1562029126270.jpg
     * logo_gray : https://sl-cms.static.slradio.cn/platform/imges/W3f6JtBhfzRyRdbNStPzTWXBNk8P6m6X1562029336800.png
     * image : https://sl-cms.static.slradio.cn/platform/imges/XPcxWF66baaznTriQkZ6iWTe8hQWea4z1562029248895.png
     * title : 我是seo哟
     * keywords : 我是关键字哟
     * des : 我是描述哟
     * app_ios : http://www.baidu.com
     * app_android : http://www.baidu.com
     * icon1 :
     * icon2 :
     * icon3 :
     * record : 湘ICP5363546546号
     * record_url : http://www.baidu.com
     * created_at : 2019-06-28 17:18:40
     * updated_at : 2019-11-14 16:37:12
     * h5_logo : https://sl-cms.static.slradio.cn/platform/imges/FGYAnDRMa55RPssXwbGp2CMTJw2z1PSH1562723499051.jpg
     * tv_show : 0
     * tv_name : 看电视sd
     * radio_show : 1
     * radio_name : 听广播ss
     * is_league : 0
     * province : 43
     * city : 4301
     * county :
     * category : 2
     * logo1 : https://sl-cdn.slradio.cn/cms/icon/imges/p9RPMkhJkpY2bi91Rsk2cWpF6piTFr2T1573720624722.png
     * logo2 : https://sl-cdn.slradio.cn/cms/icon/imges/e8B6hrMN78d2J5nkJ06cnjSaTDQxwxDz1573699736193.png
     * city_info : {"code":"4301","name":"长沙市"}
     * province_info : {"code":"43","name":"湖南省"}
     */

    private int id;
    private String name;
    private String domain;
    private String logo;
    private String logo_gray;
    private String image;
    private String title;
    private String keywords;
    private String des;
    private String app_ios;
    private String app_android;
    private String icon1;
    private String icon2;
    private String icon3;
    private String record;
    private String record_url;
    private String created_at;
    private String updated_at;
    private String h5_logo;
    private int tv_show;
    private String tv_name;
    private int radio_show;
    private String radio_name;
    private int is_league;
    private String province;
    private String city;
    private String county;
    private int category;
    private String logo1;
    private String logo2;
    private CityInfoBean city_info;
    private ProvinceInfoBean province_info;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo_gray() {
        return logo_gray;
    }

    public void setLogo_gray(String logo_gray) {
        this.logo_gray = logo_gray;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getApp_ios() {
        return app_ios;
    }

    public void setApp_ios(String app_ios) {
        this.app_ios = app_ios;
    }

    public String getApp_android() {
        return app_android;
    }

    public void setApp_android(String app_android) {
        this.app_android = app_android;
    }

    public String getIcon1() {
        return icon1;
    }

    public void setIcon1(String icon1) {
        this.icon1 = icon1;
    }

    public String getIcon2() {
        return icon2;
    }

    public void setIcon2(String icon2) {
        this.icon2 = icon2;
    }

    public String getIcon3() {
        return icon3;
    }

    public void setIcon3(String icon3) {
        this.icon3 = icon3;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getRecord_url() {
        return record_url;
    }

    public void setRecord_url(String record_url) {
        this.record_url = record_url;
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

    public String getH5_logo() {
        return h5_logo;
    }

    public void setH5_logo(String h5_logo) {
        this.h5_logo = h5_logo;
    }

    public int getTv_show() {
        return tv_show;
    }

    public void setTv_show(int tv_show) {
        this.tv_show = tv_show;
    }

    public String getTv_name() {
        return tv_name;
    }

    public void setTv_name(String tv_name) {
        this.tv_name = tv_name;
    }

    public int getRadio_show() {
        return radio_show;
    }

    public void setRadio_show(int radio_show) {
        this.radio_show = radio_show;
    }

    public String getRadio_name() {
        return radio_name;
    }

    public void setRadio_name(String radio_name) {
        this.radio_name = radio_name;
    }

    public int getIs_league() {
        return is_league;
    }

    public void setIs_league(int is_league) {
        this.is_league = is_league;
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getLogo1() {
        return logo1;
    }

    public void setLogo1(String logo1) {
        this.logo1 = logo1;
    }

    public String getLogo2() {
        return logo2;
    }

    public void setLogo2(String logo2) {
        this.logo2 = logo2;
    }

    public CityInfoBean getCity_info() {
        return city_info;
    }

    public void setCity_info(CityInfoBean city_info) {
        this.city_info = city_info;
    }

    public ProvinceInfoBean getProvince_info() {
        return province_info;
    }

    public void setProvince_info(ProvinceInfoBean province_info) {
        this.province_info = province_info;
    }

    public static class CityInfoBean {
        /**
         * code : 4301
         * name : 长沙市
         */

        private String code;
        private String name;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ProvinceInfoBean {
        /**
         * code : 43
         * name : 湖南省
         */

        private String code;
        private String name;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
