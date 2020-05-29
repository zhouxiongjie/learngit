package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class ColumnContent  implements Serializable {
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

    public static class AuthorInfoBean implements Serializable{
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

        public static class MerchantBean implements Serializable{
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

    public static class ArticleBean implements Serializable{
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
    }

    public static class GallerieBean implements Serializable{
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

    public static class SpecialBean implements Serializable{
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

    public static class TopBean implements Serializable{
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



    public static class AlbumsBean implements Serializable{
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

    public static class VideoBean implements Serializable{
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


    public static class ActivitiyBean implements Serializable{

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

    public static class LiveBean implements Serializable{

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

    public static class CategoriesBean implements Serializable{
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
