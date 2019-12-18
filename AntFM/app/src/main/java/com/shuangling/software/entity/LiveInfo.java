package com.shuangling.software.entity;

public class LiveInfo {

    /**
     * id : 12473
     * type : 10
     * merchant_id : 4
     * artist_id : 6
     * author_id : 8
     * province : 43
     * city : 4301
     * county : 430104
     * title : 电台直播0910
     * des :
     * cover : http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/logo/imges/F92Rf9NjybcJKSy31575249052228.png
     * update_time : 2019-12-02 16:20:21
     * publish_time : null
     * publish_at : 2019-12-02 16:20:21
     * putaway : 1
     * status : 4
     * collection : 0
     * like : 0
     * view : 0
     * comment : 0
     * reprint : 0
     * deleted_at : null
     * created_at : 2019-12-02 16:20:21
     * updated_at : 2019-12-02 16:20:21
     * is_comment : 1
     * perview : 0
     * audit_type : null
     * author_info : {"id":8,"merchant_id":6,"staff_name":"李阳","user_id":12,"merchant":{"id":6,"name":"浏阳法院","logo":"http://sl-ucenter.slradio.cn/merchants/4/imges/WT0M00Sfw5BHjfxMZpz0S7eBWK8p2MXi1552275020098.jpg","type":11,"parent_id":4}}
     * merchant : {"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1}
     * live : {"id":7,"post_id":12473,"room_id":73,"type":2,"push_url":"rtmp://video-center-bj.alivecdn.com/4/75FC35FCCD8B6E2579AFCE36BBF82851?vhost=slvedio-test.slradio.cn","pull_url":"","use_stream":1,"rtmp_play_url":"rtmp://slvedio-test.slradio.cn/4/75FC35FCCD8B6E2579AFCE36BBF82851","flv_play_url":"http://slvedio-test.slradio.cn/4/75FC35FCCD8B6E2579AFCE36BBF82851.flv","hls_play_url":"http://slvedio-test.slradio.cn/4/75FC35FCCD8B6E2579AFCE36BBF82851.m3u8","status":2,"popularity":0,"url":"velit ea"}
     */

    private int id;
    private int type;
    private int merchant_id;
    private int artist_id;
    private int author_id;
    private String province;
    private String city;
    private String county;
    private String title;
    private String des;
    private String cover;
    private String update_time;
    private Object publish_time;
    private String publish_at;
    private int putaway;
    private int status;
    private int collection;
    private int like;
    private int view;
    private int comment;
    private int reprint;
    private Object deleted_at;
    private String created_at;
    private String updated_at;
    private int is_comment;
    private int perview;
    private Object audit_type;
    private AuthorInfoBean author_info;
    private MerchantBeanX merchant;
    private LiveBean live;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Object getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Object publish_time) {
        this.publish_time = publish_time;
    }

    public String getPublish_at() {
        return publish_at;
    }

    public void setPublish_at(String publish_at) {
        this.publish_at = publish_at;
    }

    public int getPutaway() {
        return putaway;
    }

    public void setPutaway(int putaway) {
        this.putaway = putaway;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getReprint() {
        return reprint;
    }

    public void setReprint(int reprint) {
        this.reprint = reprint;
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

    public int getIs_comment() {
        return is_comment;
    }

    public void setIs_comment(int is_comment) {
        this.is_comment = is_comment;
    }

    public int getPerview() {
        return perview;
    }

    public void setPerview(int perview) {
        this.perview = perview;
    }

    public Object getAudit_type() {
        return audit_type;
    }

    public void setAudit_type(Object audit_type) {
        this.audit_type = audit_type;
    }

    public AuthorInfoBean getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(AuthorInfoBean author_info) {
        this.author_info = author_info;
    }

    public MerchantBeanX getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantBeanX merchant) {
        this.merchant = merchant;
    }

    public LiveBean getLive() {
        return live;
    }

    public void setLive(LiveBean live) {
        this.live = live;
    }

    public static class AuthorInfoBean {
        /**
         * id : 8
         * merchant_id : 6
         * staff_name : 李阳
         * user_id : 12
         * merchant : {"id":6,"name":"浏阳法院","logo":"http://sl-ucenter.slradio.cn/merchants/4/imges/WT0M00Sfw5BHjfxMZpz0S7eBWK8p2MXi1552275020098.jpg","type":11,"parent_id":4}
         */

        private int id;
        private int merchant_id;
        private String staff_name;
        private int user_id;
        private MerchantBean merchant;

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

        public String getStaff_name() {
            return staff_name;
        }

        public void setStaff_name(String staff_name) {
            this.staff_name = staff_name;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public MerchantBean getMerchant() {
            return merchant;
        }

        public void setMerchant(MerchantBean merchant) {
            this.merchant = merchant;
        }

        public static class MerchantBean {
            /**
             * id : 6
             * name : 浏阳法院
             * logo : http://sl-ucenter.slradio.cn/merchants/4/imges/WT0M00Sfw5BHjfxMZpz0S7eBWK8p2MXi1552275020098.jpg
             * type : 11
             * parent_id : 4
             */

            private int id;
            private String name;
            private String logo;
            private int type;
            private int parent_id;

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

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getParent_id() {
                return parent_id;
            }

            public void setParent_id(int parent_id) {
                this.parent_id = parent_id;
            }
        }
    }

    public static class MerchantBeanX {
        /**
         * id : 4
         * name : 黄庚的机构勿删勿改
         * logo : http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png
         * type : 1
         */

        private int id;
        private String name;
        private String logo;
        private int type;

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

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class LiveBean {
        /**
         * id : 7
         * post_id : 12473
         * room_id : 73
         * type : 2
         * push_url : rtmp://video-center-bj.alivecdn.com/4/75FC35FCCD8B6E2579AFCE36BBF82851?vhost=slvedio-test.slradio.cn
         * pull_url :
         * use_stream : 1
         * rtmp_play_url : rtmp://slvedio-test.slradio.cn/4/75FC35FCCD8B6E2579AFCE36BBF82851
         * flv_play_url : http://slvedio-test.slradio.cn/4/75FC35FCCD8B6E2579AFCE36BBF82851.flv
         * hls_play_url : http://slvedio-test.slradio.cn/4/75FC35FCCD8B6E2579AFCE36BBF82851.m3u8
         * status : 2
         * popularity : 0
         * url : velit ea
         */

        private int id;
        private int post_id;
        private int room_id;
        private int type;
        private String push_url;
        private String pull_url;
        private int use_stream;
        private String rtmp_play_url;
        private String flv_play_url;
        private String hls_play_url;
        private int status;
        private int popularity;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPost_id() {
            return post_id;
        }

        public void setPost_id(int post_id) {
            this.post_id = post_id;
        }

        public int getRoom_id() {
            return room_id;
        }

        public void setRoom_id(int room_id) {
            this.room_id = room_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPush_url() {
            return push_url;
        }

        public void setPush_url(String push_url) {
            this.push_url = push_url;
        }

        public String getPull_url() {
            return pull_url;
        }

        public void setPull_url(String pull_url) {
            this.pull_url = pull_url;
        }

        public int getUse_stream() {
            return use_stream;
        }

        public void setUse_stream(int use_stream) {
            this.use_stream = use_stream;
        }

        public String getRtmp_play_url() {
            return rtmp_play_url;
        }

        public void setRtmp_play_url(String rtmp_play_url) {
            this.rtmp_play_url = rtmp_play_url;
        }

        public String getFlv_play_url() {
            return flv_play_url;
        }

        public void setFlv_play_url(String flv_play_url) {
            this.flv_play_url = flv_play_url;
        }

        public String getHls_play_url() {
            return hls_play_url;
        }

        public void setHls_play_url(String hls_play_url) {
            this.hls_play_url = hls_play_url;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getPopularity() {
            return popularity;
        }

        public void setPopularity(int popularity) {
            this.popularity = popularity;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
