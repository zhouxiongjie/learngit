package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class Collect implements Serializable {


    /**
     * id : 93
     * title : 悲情采珠丁
     * des :
     * cover : http://sl-cms.static.slradio.cn/merchants/1/imges/sc5DkjJEXF6mBSBy3xbKkJHizB6Zb9th1552544914897.jpg
     * type : 3
     * author_id : 8
     * status : 4
     * city : 4310
     * post_collection : {"id":50,"user_id":19,"post_id":93,"created_at":"2019-04-15 11:23:42","updated_at":"2019-06-12 11:30:40","type":3}
     * author_info : {"id":8,"merchant_id":6,"staff_name":"李阳","user_id":12,"merchant":{"id":6,"name":"浏阳法院","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/WT0M00Sfw5BHjfxMZpz0S7eBWK8p2MXi1552275020098.jpg","type":11,"parent_id":4}}
     * article : {"id":22,"post_id":93,"count":-1,"covers":["http://sl-cms.static.slradio.cn/merchants/1/imges/sc5DkjJEXF6mBSBy3xbKkJHizB6Zb9th1552544914897.jpg"],"type":1}
     * gallerie : {"id":7,"post_id":1555,"count":3,"covers":["http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/SSCDMB5N4ScBGR7e.jpg","http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/SSCDMB5N4ScBGR7e.jpg","http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/jX2JpyDKttKizc6W.jpg"],"type":2}
     * special : {"id":68,"post_id":1324,"subtitle":"专题副标题","module":1}
     * albums : {"id":237,"post_id":1060,"count":0,"subscribe":0,"status":0}
     * video : {"post_id":1366,"duration":316.8583,"size":20955212,"url":"http://vod.slradio.cn/sv/58242a3c-169fff78f4f/58242a3c-169fff78f4f.mp4","format":"mp4","width":720,"height":400,"bitrate":"711:400","rate":"711:400","video_id":"36d5363b01714ffcb5e932ab3305bbde","source_id":125}
     */

    private int id;
    private String title;
    private String des;
    private String cover;
    private int type;
    private int author_id;
    private int status;
    private String city;
    private PostCollectionBean post_collection;
    private AuthorInfoBean author_info;
    private ArticleBean article;
    private GallerieBean gallerie;
    private SpecialBean special;
    private AlbumsBean albums;
    private VideoBean video;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public PostCollectionBean getPost_collection() {
        return post_collection;
    }

    public void setPost_collection(PostCollectionBean post_collection) {
        this.post_collection = post_collection;
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

    public static class PostCollectionBean {
        /**
         * id : 50
         * user_id : 19
         * post_id : 93
         * created_at : 2019-04-15 11:23:42
         * updated_at : 2019-06-12 11:30:40
         * type : 3
         */

        private int id;
        private int user_id;
        private int post_id;
        private String created_at;
        private String updated_at;
        private int type;

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

        public int getPost_id() {
            return post_id;
        }

        public void setPost_id(int post_id) {
            this.post_id = post_id;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class AuthorInfoBean {
        /**
         * id : 8
         * merchant_id : 6
         * staff_name : 李阳
         * user_id : 12
         * merchant : {"id":6,"name":"浏阳法院","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/WT0M00Sfw5BHjfxMZpz0S7eBWK8p2MXi1552275020098.jpg","type":11,"parent_id":4}
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
             * logo : http://sl-ucenter.static.slradio.cn/merchants/4/imges/WT0M00Sfw5BHjfxMZpz0S7eBWK8p2MXi1552275020098.jpg
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

    public static class ArticleBean {
        /**
         * id : 22
         * post_id : 93
         * count : -1
         * covers : ["http://sl-cms.static.slradio.cn/merchants/1/imges/sc5DkjJEXF6mBSBy3xbKkJHizB6Zb9th1552544914897.jpg"]
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

    public static class GallerieBean {
        /**
         * id : 7
         * post_id : 1555
         * count : 3
         * covers : ["http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/SSCDMB5N4ScBGR7e.jpg","http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/SSCDMB5N4ScBGR7e.jpg","http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/jX2JpyDKttKizc6W.jpg"]
         * type : 2
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

    public static class SpecialBean {
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

    public static class AlbumsBean {
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

    public static class VideoBean {
        /**
         * post_id : 1366
         * duration : 316.8583
         * size : 20955212
         * url : http://vod.slradio.cn/sv/58242a3c-169fff78f4f/58242a3c-169fff78f4f.mp4
         * format : mp4
         * width : 720
         * height : 400
         * bitrate : 711:400
         * rate : 711:400
         * video_id : 36d5363b01714ffcb5e932ab3305bbde
         * source_id : 125
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
}
