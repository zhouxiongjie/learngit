package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class ColumnContent implements Serializable {
    /**
     * id : 1658
     * title : SL_吕梁人民广播电台_2
     * des : SL_吕梁人民广播电台_2
     * cover : https://vod.slradio.cn/36ecf4069045420da7b837613c82c7f6/snapshots/fd668edef47b41bf8fd500eb467cfc76-00003.jpg
     * type : 4
     * author_id : 5
     * update_time : 2019-06-12 15:02:06
     * view : 3
     * comment : 0
     * publish_at : 2019-06-12 14:54:31
     * categories : [{"category_id":8,"name":"相声","icon":"http://sl-cms.static.slradio.cn/platform/PXEpZdjSs4eh4eGbxjDH338wTadBH8AE1552447766797.jpg","display":1,"type":0,"sort":4,"post_type":0,"parent_id":0,"pivot":{"post_id":1658,"category_id":8},"parent":null}]
     * author_info : {"id":5,"merchant_id":4,"staff_name":"黄庚","user_id":1,"merchant":{"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"parent_id":0}}
     * article : {"id":550,"post_id":1373,"count":0,"covers":"","type":0}
     * gallerie : {"id":9,"post_id":1568,"count":1,"covers":["http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/jX2JpyDKttKizc6W.jpg"],"type":1}
     * special : {"id":68,"post_id":1324,"subtitle":"专题副标题","module":1}
     * albums : {"id":237,"post_id":1060,"count":0,"subscribe":0,"status":0}
     * video : {"post_id":1658,"duration":"42.518","size":4975406,"url":"https://vod.slradio.cn/sv/5aeafda2-16b4a466078/5aeafda2-16b4a466078.mp4","format":"mp4","width":432,"height":320,"bitrate":"27:20","rate":"27:20","video_id":"36ecf4069045420da7b837613c82c7f6","source_id":800}
     */
    private int id;
    private String title;
    private String des;
    private String cover;
    private int type;
    private int author_id;
    private String update_time;
    private int view;
    private int comment;
    private String publish_at;
    private AuthorInfoBean author_info;
    private ArticleBean article;
    private GallerieBean gallerie;
    private SpecialBean special;
    private AlbumsBean albums;
    private VideoBean video;
    private ActivitiyBean activitiy;
    private LiveBean live;
    private List<CategoriesBean> categories;
    private TopBean top;
    private int is_like;
    private int is_user_report;

    public int getIs_user_report() {
        return is_user_report;
    }

    public void setIs_user_report(int is_user_report) {
        this.is_user_report = is_user_report;
    }

    public int getIs_like() {
        return is_like;
    }

    public void setIs_like(int is_like) {
        this.is_like = is_like;
    }

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
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

    public String getPublish_at() {
        return publish_at;
    }

    public void setPublish_at(String publish_at) {
        this.publish_at = publish_at;
    }

    public AuthorInfoBean getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(AuthorInfoBean author_info) {
        this.author_info = author_info;
    }

    public ArticleBean getArticle() {
        return article;
    }

    public void setArticle(ArticleBean article) {
        this.article = article;
    }

    public GallerieBean getGallerie() {
        return gallerie;
    }

    public void setGallerie(GallerieBean gallerie) {
        this.gallerie = gallerie;
    }

    public SpecialBean getSpecial() {
        return special;
    }

    public void setSpecial(SpecialBean special) {
        this.special = special;
    }

    public AlbumsBean getAlbums() {
        return albums;
    }

    public void setAlbums(AlbumsBean albums) {
        this.albums = albums;
    }

    public VideoBean getVideo() {
        return video;
    }

    public void setVideo(VideoBean video) {
        this.video = video;
    }

    public ActivitiyBean getActivitiy() {
        return activitiy;
    }

    public void setActivitiy(ActivitiyBean activitiy) {
        this.activitiy = activitiy;
    }

    public LiveBean getLive() {
        return live;
    }

    public void setLive(LiveBean live) {
        this.live = live;
    }

    public List<CategoriesBean> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoriesBean> categories) {
        this.categories = categories;
    }

    public TopBean getTop() {
        return top;
    }

    public void setTop(TopBean top) {
        this.top = top;
    }

    public static class AuthorInfoBean implements Serializable {
        /**
         * id : 5
         * merchant_id : 4
         * staff_name : 黄庚
         * user_id : 1
         * merchant : {"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"parent_id":0}
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

        public static class MerchantBean implements Serializable {
            /**
             * id : 4
             * name : 黄庚的机构勿删勿改
             * logo : http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png
             * type : 1
             * parent_id : 0
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

    public static class ArticleBean implements Serializable {
        /**
         * id : 550
         * post_id : 1373
         * count : 0
         * covers :
         * type : 0
         */
        private int id;
        private int post_id;
        private int count;
        private List<String> covers;
        private int type;
        private String link_address;

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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<String> getCovers() {
            return covers;
        }

        public void setCovers(List<String> covers) {
            this.covers = covers;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getLink_address() {
            return link_address;
        }

        public void setLink_address(String link_address) {
            this.link_address = link_address;
        }
    }

    public static class GallerieBean implements Serializable {
        /**
         * id : 9
         * post_id : 1568
         * count : 1
         * covers : ["http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/jX2JpyDKttKizc6W.jpg"]
         * type : 1
         */
        private int id;
        private int post_id;
        private int count;
        private int type;
        private List<String> covers;

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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<String> getCovers() {
            return covers;
        }

        public void setCovers(List<String> covers) {
            this.covers = covers;
        }
    }

    public static class SpecialBean implements Serializable {
        /**
         * id : 68
         * post_id : 1324
         * subtitle : 专题副标题
         * module : 1
         */
        private int id;
        private int post_id;
        private String subtitle;
        private int module;

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

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public int getModule() {
            return module;
        }

        public void setModule(int module) {
            this.module = module;
        }
    }

    public static class TopBean implements Serializable {
        private int id;
        private int post_id;
        private int position;
        private int status;

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

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class AlbumsBean implements Serializable {
        /**
         * id : 237
         * post_id : 1060
         * count : 0
         * subscribe : 0
         * status : 0
         */
        private int id;
        private int post_id;
        private int count;
        private int subscribe;
        private int status;

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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(int subscribe) {
            this.subscribe = subscribe;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class VideoBean implements Serializable {
        /**
         * post_id : 1658
         * duration : 42.518
         * size : 4975406
         * url : https://vod.slradio.cn/sv/5aeafda2-16b4a466078/5aeafda2-16b4a466078.mp4
         * format : mp4
         * width : 432
         * height : 320
         * bitrate : 27:20
         * rate : 27:20
         * video_id : 36ecf4069045420da7b837613c82c7f6
         * source_id : 800
         */
        private int post_id;
        private String duration;
        private int size;
        private String url;
        private String format;
        private int width;
        private int height;
        private String bitrate;
        private String rate;
        private String video_id;
        private int source_id;

        public int getPost_id() {
            return post_id;
        }

        public void setPost_id(int post_id) {
            this.post_id = post_id;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getBitrate() {
            return bitrate;
        }

        public void setBitrate(String bitrate) {
            this.bitrate = bitrate;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public int getSource_id() {
            return source_id;
        }

        public void setSource_id(int source_id) {
            this.source_id = source_id;
        }
    }

    public static class ActivitiyBean implements Serializable {
        /**
         * id : 21
         * post_id : 12362
         * activitiy_id : 1053
         * type : 2
         * qr_code : http://api-asc.review.slradio.cn/storage/activities/qrCode/1053.png
         * url : http://www-asc-c.review.slradio.cn/signup/1053
         * start_status : 1
         * join_number : 0
         * join_count : 0
         * view_count : 0
         */
        private int id;
        private int post_id;
        private int activitiy_id;
        private int type;
        private String qr_code;
        private String url;
        private int start_status;
        private int join_number;
        private int join_count;
        private int view_count;

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

        public int getActivitiy_id() {
            return activitiy_id;
        }

        public void setActivitiy_id(int activitiy_id) {
            this.activitiy_id = activitiy_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getQr_code() {
            return qr_code;
        }

        public void setQr_code(String qr_code) {
            this.qr_code = qr_code;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getStart_status() {
            return start_status;
        }

        public void setStart_status(int start_status) {
            this.start_status = start_status;
        }

        public int getJoin_number() {
            return join_number;
        }

        public void setJoin_number(int join_number) {
            this.join_number = join_number;
        }

        public int getJoin_count() {
            return join_count;
        }

        public void setJoin_count(int join_count) {
            this.join_count = join_count;
        }

        public int getView_count() {
            return view_count;
        }

        public void setView_count(int view_count) {
            this.view_count = view_count;
        }
    }

    public static class LiveBean implements Serializable {
        /**
         * id : 111
         * post_id : 25946
         * room_id : 1347
         * type : 1
         * push_url : rtmp://txpush.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B
         * pull_url :
         * use_stream : 1
         * rtmp_play_url : rtmp://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B
         * flv_play_url : https://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B.flv
         * hls_play_url : https://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B.m3u8
         * status : 3
         * popularity : 0
         * url : http://live-c.review.slradio.cn/index?stream_name=B8F4EDE41E20BFE957AAF4E040F7304B
         * created_at : 2021-01-21 16:18:37
         * updated_at : 2021-01-21 16:18:37
         * cover_url : http://sl-cdn.slradio.cn/live/logo/imges/hCzG3easQ0xaRPbr1611216908570.jpg
         * is_record : 0
         * stream_name : B8F4EDE41E20BFE957AAF4E040F7304B
         * text :
         * is_rtslive : 2
         * rts_push_url :
         * rts_pull_url : webrtc://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B
         * show_cover : 1
         * public_code :
         * estimate_play_time : 2021-01-21 16:15:13
         * entry_mode : 1
         * entry_password :
         * sms_subscribe : 0
         * extra : {"id":1347,"studio_id":0,"num":1,"merchant_id":152,"anchor_id":5937,"name":"app开发测试","cover_url":"http://sl-cdn.slradio.cn/live/logo/imges/hCzG3easQ0xaRPbr1611216908570.jpg","des":"","cms_section_id":12,"cms_section_name":"","type":1,"invite_rewards":0,"push_url":"rtmp://txpush.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B","pull_url":"","using_url":1,"RtmpPlayUrl":"rtmp://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B","FlvPlayUrl":"https://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B.flv","HLSPlayUrl":"https://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B.m3u8","is_record":0,"audit_result":1,"chat":1,"stream_name":"B8F4EDE41E20BFE957AAF4E040F7304B","state":3,"stream_state":0,"text":"","post_id":0,"deleted_at":null,"created_at":"2021-01-21 16:15:21","updated_at":"2021-01-21 16:16:27","audit":0,"is_rtslive":2,"rts_push_url":"","rts_pull_url":"webrtc://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B","cover_url_vertical":"http://sl-cdn.slradio.cn/live/logo/imges/hCzG3easQ0xaRPbr1611216908570.jpg","public_code":"","ai_audit":1,"estimate_play_time":"2021-01-21 16:15:13","show_cover":1,"entry_mode":1,"entry_password":null,"sms_subscribe":0,"entry_login":0,"entry_phone":0,"show_model":1,"live_driver":"tencent"}
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
        private String created_at;
        private String updated_at;
        private String cover_url;
        private int is_record;
        private String stream_name;
        private String text;
        private int is_rtslive;
        private String rts_push_url;
        private String rts_pull_url;
        private int show_cover;
        private String public_code;
        private String estimate_play_time;
        private int entry_mode;
        private String entry_password;
        private int sms_subscribe;
        private ExtraBean extra;

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

        public String getCover_url() {
            return cover_url;
        }

        public void setCover_url(String cover_url) {
            this.cover_url = cover_url;
        }

        public int getIs_record() {
            return is_record;
        }

        public void setIs_record(int is_record) {
            this.is_record = is_record;
        }

        public String getStream_name() {
            return stream_name;
        }

        public void setStream_name(String stream_name) {
            this.stream_name = stream_name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getIs_rtslive() {
            return is_rtslive;
        }

        public void setIs_rtslive(int is_rtslive) {
            this.is_rtslive = is_rtslive;
        }

        public String getRts_push_url() {
            return rts_push_url;
        }

        public void setRts_push_url(String rts_push_url) {
            this.rts_push_url = rts_push_url;
        }

        public String getRts_pull_url() {
            return rts_pull_url;
        }

        public void setRts_pull_url(String rts_pull_url) {
            this.rts_pull_url = rts_pull_url;
        }

        public int getShow_cover() {
            return show_cover;
        }

        public void setShow_cover(int show_cover) {
            this.show_cover = show_cover;
        }

        public String getPublic_code() {
            return public_code;
        }

        public void setPublic_code(String public_code) {
            this.public_code = public_code;
        }

        public String getEstimate_play_time() {
            return estimate_play_time;
        }

        public void setEstimate_play_time(String estimate_play_time) {
            this.estimate_play_time = estimate_play_time;
        }

        public int getEntry_mode() {
            return entry_mode;
        }

        public void setEntry_mode(int entry_mode) {
            this.entry_mode = entry_mode;
        }

        public String getEntry_password() {
            return entry_password;
        }

        public void setEntry_password(String entry_password) {
            this.entry_password = entry_password;
        }

        public int getSms_subscribe() {
            return sms_subscribe;
        }

        public void setSms_subscribe(int sms_subscribe) {
            this.sms_subscribe = sms_subscribe;
        }

        public ExtraBean getExtra() {
            return extra;
        }

        public void setExtra(ExtraBean extra) {
            this.extra = extra;
        }

        public static class ExtraBean {
            /**
             * id : 1347
             * studio_id : 0
             * num : 1
             * merchant_id : 152
             * anchor_id : 5937
             * name : app开发测试
             * cover_url : http://sl-cdn.slradio.cn/live/logo/imges/hCzG3easQ0xaRPbr1611216908570.jpg
             * des :
             * cms_section_id : 12
             * cms_section_name :
             * type : 1
             * invite_rewards : 0
             * push_url : rtmp://txpush.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B
             * pull_url :
             * using_url : 1
             * RtmpPlayUrl : rtmp://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B
             * FlvPlayUrl : https://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B.flv
             * HLSPlayUrl : https://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B.m3u8
             * is_record : 0
             * audit_result : 1
             * chat : 1
             * stream_name : B8F4EDE41E20BFE957AAF4E040F7304B
             * state : 3
             * stream_state : 0
             * text :
             * post_id : 0
             * deleted_at : null
             * created_at : 2021-01-21 16:15:21
             * updated_at : 2021-01-21 16:16:27
             * audit : 0
             * is_rtslive : 2
             * rts_push_url :
             * rts_pull_url : webrtc://txlive.slradio.cn/shuanglnlive/B8F4EDE41E20BFE957AAF4E040F7304B
             * cover_url_vertical : http://sl-cdn.slradio.cn/live/logo/imges/hCzG3easQ0xaRPbr1611216908570.jpg
             * public_code :
             * ai_audit : 1
             * estimate_play_time : 2021-01-21 16:15:13
             * show_cover : 1
             * entry_mode : 1
             * entry_password : null
             * sms_subscribe : 0
             * entry_login : 0
             * entry_phone : 0
             * show_model : 1
             * live_driver : tencent
             */

            private int id;
            private int studio_id;
            private int num;
            private int merchant_id;
            private int anchor_id;
            private String name;
            private String cover_url;
            private String des;
            private int cms_section_id;
            private String cms_section_name;
            private int type;
            private int invite_rewards;
            private String push_url;
            private String pull_url;
            private int using_url;
            private String RtmpPlayUrl;
            private String FlvPlayUrl;
            private String HLSPlayUrl;
            private int is_record;
            private int audit_result;
            private int chat;
            private String stream_name;
            private int state;
            private int stream_state;
            private String text;
            private int post_id;
            private Object deleted_at;
            private String created_at;
            private String updated_at;
            private int audit;
            private int is_rtslive;
            private String rts_push_url;
            private String rts_pull_url;
            private String cover_url_vertical;
            private String public_code;
            private int ai_audit;
            private String estimate_play_time;
            private int show_cover;
            private int entry_mode;
            private Object entry_password;
            private int sms_subscribe;
            private int entry_login;
            private int entry_phone;
            private int show_model;
            private String live_driver;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getStudio_id() {
                return studio_id;
            }

            public void setStudio_id(int studio_id) {
                this.studio_id = studio_id;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
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

            public int getCms_section_id() {
                return cms_section_id;
            }

            public void setCms_section_id(int cms_section_id) {
                this.cms_section_id = cms_section_id;
            }

            public String getCms_section_name() {
                return cms_section_name;
            }

            public void setCms_section_name(String cms_section_name) {
                this.cms_section_name = cms_section_name;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getInvite_rewards() {
                return invite_rewards;
            }

            public void setInvite_rewards(int invite_rewards) {
                this.invite_rewards = invite_rewards;
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

            public int getUsing_url() {
                return using_url;
            }

            public void setUsing_url(int using_url) {
                this.using_url = using_url;
            }

            public String getRtmpPlayUrl() {
                return RtmpPlayUrl;
            }

            public void setRtmpPlayUrl(String RtmpPlayUrl) {
                this.RtmpPlayUrl = RtmpPlayUrl;
            }

            public String getFlvPlayUrl() {
                return FlvPlayUrl;
            }

            public void setFlvPlayUrl(String FlvPlayUrl) {
                this.FlvPlayUrl = FlvPlayUrl;
            }

            public String getHLSPlayUrl() {
                return HLSPlayUrl;
            }

            public void setHLSPlayUrl(String HLSPlayUrl) {
                this.HLSPlayUrl = HLSPlayUrl;
            }

            public int getIs_record() {
                return is_record;
            }

            public void setIs_record(int is_record) {
                this.is_record = is_record;
            }

            public int getAudit_result() {
                return audit_result;
            }

            public void setAudit_result(int audit_result) {
                this.audit_result = audit_result;
            }

            public int getChat() {
                return chat;
            }

            public void setChat(int chat) {
                this.chat = chat;
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

            public int getStream_state() {
                return stream_state;
            }

            public void setStream_state(int stream_state) {
                this.stream_state = stream_state;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public int getPost_id() {
                return post_id;
            }

            public void setPost_id(int post_id) {
                this.post_id = post_id;
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

            public int getAudit() {
                return audit;
            }

            public void setAudit(int audit) {
                this.audit = audit;
            }

            public int getIs_rtslive() {
                return is_rtslive;
            }

            public void setIs_rtslive(int is_rtslive) {
                this.is_rtslive = is_rtslive;
            }

            public String getRts_push_url() {
                return rts_push_url;
            }

            public void setRts_push_url(String rts_push_url) {
                this.rts_push_url = rts_push_url;
            }

            public String getRts_pull_url() {
                return rts_pull_url;
            }

            public void setRts_pull_url(String rts_pull_url) {
                this.rts_pull_url = rts_pull_url;
            }

            public String getCover_url_vertical() {
                return cover_url_vertical;
            }

            public void setCover_url_vertical(String cover_url_vertical) {
                this.cover_url_vertical = cover_url_vertical;
            }

            public String getPublic_code() {
                return public_code;
            }

            public void setPublic_code(String public_code) {
                this.public_code = public_code;
            }

            public int getAi_audit() {
                return ai_audit;
            }

            public void setAi_audit(int ai_audit) {
                this.ai_audit = ai_audit;
            }

            public String getEstimate_play_time() {
                return estimate_play_time;
            }

            public void setEstimate_play_time(String estimate_play_time) {
                this.estimate_play_time = estimate_play_time;
            }

            public int getShow_cover() {
                return show_cover;
            }

            public void setShow_cover(int show_cover) {
                this.show_cover = show_cover;
            }

            public int getEntry_mode() {
                return entry_mode;
            }

            public void setEntry_mode(int entry_mode) {
                this.entry_mode = entry_mode;
            }

            public Object getEntry_password() {
                return entry_password;
            }

            public void setEntry_password(Object entry_password) {
                this.entry_password = entry_password;
            }

            public int getSms_subscribe() {
                return sms_subscribe;
            }

            public void setSms_subscribe(int sms_subscribe) {
                this.sms_subscribe = sms_subscribe;
            }

            public int getEntry_login() {
                return entry_login;
            }

            public void setEntry_login(int entry_login) {
                this.entry_login = entry_login;
            }

            public int getEntry_phone() {
                return entry_phone;
            }

            public void setEntry_phone(int entry_phone) {
                this.entry_phone = entry_phone;
            }

            public int getShow_model() {
                return show_model;
            }

            public void setShow_model(int show_model) {
                this.show_model = show_model;
            }

            public String getLive_driver() {
                return live_driver;
            }

            public void setLive_driver(String live_driver) {
                this.live_driver = live_driver;
            }
        }
        /**
         * id : 2
         * post_id : 12467
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

        


//        private int id;
//        private int post_id;
//        private int room_id;
//        private int type;
//        private String push_url;
//        private String pull_url;
//        private int use_stream;
//        private String rtmp_play_url;
//        private String flv_play_url;
//        private String hls_play_url;
//        private int status;
//        private int popularity;
//        private String url;
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public int getPost_id() {
//            return post_id;
//        }
//
//        public void setPost_id(int post_id) {
//            this.post_id = post_id;
//        }
//
//        public int getRoom_id() {
//            return room_id;
//        }
//
//        public void setRoom_id(int room_id) {
//            this.room_id = room_id;
//        }
//
//        public int getType() {
//            return type;
//        }
//
//        public void setType(int type) {
//            this.type = type;
//        }
//
//        public String getPush_url() {
//            return push_url;
//        }
//
//        public void setPush_url(String push_url) {
//            this.push_url = push_url;
//        }
//
//        public String getPull_url() {
//            return pull_url;
//        }
//
//        public void setPull_url(String pull_url) {
//            this.pull_url = pull_url;
//        }
//
//        public int getUse_stream() {
//            return use_stream;
//        }
//
//        public void setUse_stream(int use_stream) {
//            this.use_stream = use_stream;
//        }
//
//        public String getRtmp_play_url() {
//            return rtmp_play_url;
//        }
//
//        public void setRtmp_play_url(String rtmp_play_url) {
//            this.rtmp_play_url = rtmp_play_url;
//        }
//
//        public String getFlv_play_url() {
//            return flv_play_url;
//        }
//
//        public void setFlv_play_url(String flv_play_url) {
//            this.flv_play_url = flv_play_url;
//        }
//
//        public String getHls_play_url() {
//            return hls_play_url;
//        }
//
//        public void setHls_play_url(String hls_play_url) {
//            this.hls_play_url = hls_play_url;
//        }
//
//        public int getStatus() {
//            return status;
//        }
//
//        public void setStatus(int status) {
//            this.status = status;
//        }
//
//        public int getPopularity() {
//            return popularity;
//        }
//
//        public void setPopularity(int popularity) {
//            this.popularity = popularity;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public void setUrl(String url) {
//            this.url = url;
//        }
    }

    public static class CategoriesBean implements Serializable {
        /**
         * category_id : 8
         * name : 相声
         * icon : http://sl-cms.static.slradio.cn/platform/PXEpZdjSs4eh4eGbxjDH338wTadBH8AE1552447766797.jpg
         * display : 1
         * type : 0
         * sort : 4
         * post_type : 0
         * parent_id : 0
         * pivot : {"post_id":1658,"category_id":8}
         * parent : null
         */
        private int category_id;
        private String name;
        private String icon;
        private int display;
        private int type;
        private int sort;
        private int post_type;
        private int parent_id;
        private PivotBean pivot;
        private Object parent;

        public int getCategory_id() {
            return category_id;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getDisplay() {
            return display;
        }

        public void setDisplay(int display) {
            this.display = display;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getPost_type() {
            return post_type;
        }

        public void setPost_type(int post_type) {
            this.post_type = post_type;
        }

        public int getParent_id() {
            return parent_id;
        }

        public void setParent_id(int parent_id) {
            this.parent_id = parent_id;
        }

        public PivotBean getPivot() {
            return pivot;
        }

        public void setPivot(PivotBean pivot) {
            this.pivot = pivot;
        }

        public Object getParent() {
            return parent;
        }

        public void setParent(Object parent) {
            this.parent = parent;
        }

        public static class PivotBean {
            /**
             * post_id : 1658
             * category_id : 8
             */
            private int post_id;
            private int category_id;

            public int getPost_id() {
                return post_id;
            }

            public void setPost_id(int post_id) {
                this.post_id = post_id;
            }

            public int getCategory_id() {
                return category_id;
            }

            public void setCategory_id(int category_id) {
                this.category_id = category_id;
            }
        }
    }
}
