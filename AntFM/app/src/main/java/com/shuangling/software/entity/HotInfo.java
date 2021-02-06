package com.shuangling.software.entity;

public class HotInfo {


    /**
     * id : 50
     * merchant_id : 4
     * anchor_id : 4
     * name : 电视直播1305
     * cover_url : http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/logo/imges/RxrbXGesbn4ch3iN1574312734620.png
     * des : 电台描述电台描述电台描述电台描述
     * type : 2
     * stream_name : 141CE799CEF1F06F3EC10E8271037DFF
     * state : 3
     * cover_url_vertical : null
     * entry_mode : 1
     * hot_index : 423
     * pc_url : http://live.review.slradio.cn/index?stream_name=141CE799CEF1F06F3EC10E8271037DFF
     * h5_url : http://live-m.review.slradio.cn/index?stream_name=141CE799CEF1F06F3EC10E8271037DFF
     * anchor : {"id":4,"logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","name":"黄庚的机构勿删勿改"}
     */

    private int id;
    private int merchant_id;
    private int anchor_id;
    private String name;
    private String cover_url;
    private String des;
    private int type;
    private String stream_name;
    private int state;
    private Object cover_url_vertical;
    private int entry_mode;
    private int hot_index;
    private String pc_url;
    private String h5_url;
    private AnchorBean anchor;

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

    public int getAnchor_id() {
        return anchor_id;
    }

    public void setAnchor_id(int anchor_id) {
        this.anchor_id = anchor_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStream_name() {
        return stream_name;
    }

    public void setStream_name(String stream_name) {
        this.stream_name = stream_name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Object getCover_url_vertical() {
        return cover_url_vertical;
    }

    public void setCover_url_vertical(Object cover_url_vertical) {
        this.cover_url_vertical = cover_url_vertical;
    }

    public int getEntry_mode() {
        return entry_mode;
    }

    public void setEntry_mode(int entry_mode) {
        this.entry_mode = entry_mode;
    }

    public int getHot_index() {
        return hot_index;
    }

    public void setHot_index(int hot_index) {
        this.hot_index = hot_index;
    }

    public String getPc_url() {
        return pc_url;
    }

    public void setPc_url(String pc_url) {
        this.pc_url = pc_url;
    }

    public String getH5_url() {
        return h5_url;
    }

    public void setH5_url(String h5_url) {
        this.h5_url = h5_url;
    }

    public AnchorBean getAnchor() {
        return anchor;
    }

    public void setAnchor(AnchorBean anchor) {
        this.anchor = anchor;
    }

    public static class AnchorBean {
        /**
         * id : 4
         * logo : http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png
         * name : 黄庚的机构勿删勿改
         */

        private int id;
        private String logo;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
