package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class History implements Serializable {


    /**
     * id : 5654
     * post_id : 78
     * user_id : 325
     * created_at : 2019-06-28 09:23:13
     * posts : {"id":78,"title":"许美静-城里的月光.mp3","type":1,"des":"","cover":"","author_info":null,"article":{"id":550,"post_id":1373,"count":0,"covers":"","type":0},"gallerie":{"id":9,"post_id":1568,"count":1,"covers":["http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/jX2JpyDKttKizc6W.jpg"],"type":1},"special":{"id":68,"post_id":1324,"subtitle":"专题副标题","module":1},"albums":{"id":237,"post_id":1060,"count":0,"subscribe":0,"status":0},"video":{"post_id":1366,"duration":316.8583,"size":20955212,"url":"http://vod.slradio.cn/sv/58242a3c-169fff78f4f/58242a3c-169fff78f4f.mp4","format":"mp4","width":720,"height":400,"bitrate":"711:400","rate":"711:400","video_id":"36d5363b01714ffcb5e932ab3305bbde","source_id":125},"audio":{"post_id":78,"duration":"317.3616","size":5079324,"url":"http://vod.slradio.cn/sv/3c61055f-1697a1eb90e/3c61055f-1697a1eb90e.mp3","video_id":"ffed0c8eb8494f7a87f3258a935cc775","channel":0,"source_id":0,"sample_freq":0,"bit_depth":0,"bitrate":0}}
     */

    private int id;
    private int post_id;
    private int user_id;
    private String created_at;
    private PostsBean posts;

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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public PostsBean getPosts() {
        return posts;
    }

    public void setPosts(PostsBean posts) {
        this.posts = posts;
    }

    public static class PostsBean {
        /**
         * id : 78
         * title : 许美静-城里的月光.mp3
         * type : 1
         * des :
         * cover :
         * author_info : null
         * article : {"id":550,"post_id":1373,"count":0,"covers":"","type":0}
         * gallerie : {"id":9,"post_id":1568,"count":1,"covers":["http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/jX2JpyDKttKizc6W.jpg"],"type":1}
         * special : {"id":68,"post_id":1324,"subtitle":"专题副标题","module":1}
         * albums : {"id":237,"post_id":1060,"count":0,"subscribe":0,"status":0}
         * video : {"post_id":1366,"duration":316.8583,"size":20955212,"url":"http://vod.slradio.cn/sv/58242a3c-169fff78f4f/58242a3c-169fff78f4f.mp4","format":"mp4","width":720,"height":400,"bitrate":"711:400","rate":"711:400","video_id":"36d5363b01714ffcb5e932ab3305bbde","source_id":125}
         * audio : {"post_id":78,"duration":"317.3616","size":5079324,"url":"http://vod.slradio.cn/sv/3c61055f-1697a1eb90e/3c61055f-1697a1eb90e.mp3","video_id":"ffed0c8eb8494f7a87f3258a935cc775","channel":0,"source_id":0,"sample_freq":0,"bit_depth":0,"bitrate":0}
         */

        private int id;
        private String title;
        private int type;
        private String des;
        private String cover;
        private Object author_info;
        private ArticleBean article;
        private GallerieBean gallerie;
        private SpecialBean special;
        private AlbumsBean albums;
        private VideoBean video;
        private AudioBean audio;

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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
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

        public Object getAuthor_info() {
            return author_info;
        }

        public void setAuthor_info(Object author_info) {
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

        public AudioBean getAudio() {
            return audio;
        }

        public void setAudio(AudioBean audio) {
            this.audio = audio;
        }

        public static class ArticleBean {
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
            private String covers;
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

            public String getCovers() {
                return covers;
            }

            public void setCovers(String covers) {
                this.covers = covers;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }

        public static class GallerieBean {
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

        public static class AudioBean {
            /**
             * post_id : 78
             * duration : 317.3616
             * size : 5079324
             * url : http://vod.slradio.cn/sv/3c61055f-1697a1eb90e/3c61055f-1697a1eb90e.mp3
             * video_id : ffed0c8eb8494f7a87f3258a935cc775
             * channel : 0
             * source_id : 0
             * sample_freq : 0
             * bit_depth : 0
             * bitrate : 0
             */

            private int post_id;
            private String duration;
            private int size;
            private String url;
            private String video_id;
            private String channel;
            private int source_id;
            private String sample_freq;
            private String bit_depth;
            private String bitrate;

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

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }

            public String getChannel() {
                return channel;
            }

            public void setChannel(String channel) {
                this.channel = channel;
            }

            public int getSource_id() {
                return source_id;
            }

            public void setSource_id(int source_id) {
                this.source_id = source_id;
            }

            public String getSample_freq() {
                return sample_freq;
            }

            public void setSample_freq(String sample_freq) {
                this.sample_freq = sample_freq;
            }

            public String getBit_depth() {
                return bit_depth;
            }

            public void setBit_depth(String bit_depth) {
                this.bit_depth = bit_depth;
            }

            public String getBitrate() {
                return bitrate;
            }

            public void setBitrate(String bitrate) {
                this.bitrate = bitrate;
            }
        }
    }
}
