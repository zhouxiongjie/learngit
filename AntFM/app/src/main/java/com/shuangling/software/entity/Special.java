package com.shuangling.software.entity;


import java.io.Serializable;
import java.util.List;

public class Special implements Serializable {


    /**
     * id : 1263
     * type : 5
     * title : 手动阀
     * des : 发送广寒深宫萨菲
     * cover : http://sl-cms.static.slradio.cn/merchants/1/imges/QGPK2JD9t0sanX9CARmMdY93CKf1Q8Kc1556505455878.png
     * status : 4
     * publish_at : 2019-04-29 10:37:57
     * created_at : 2019-04-29 10:37:57
     * updated_at : 2019-07-10 13:52:09
     * is_comment : 1
     * putaway : 1
     * collection : 2
     * like : 1
     * view : 137
     * reprint : 0
     * comment : 14
     * author_id : 5
     * merchant_id : 4
     * is_follow : 0
     * is_likes : 0
     * is_collection : 0
     * special : {"id":53,"post_id":1263,"subtitle":"专题副标题","module":1,"created_at":"2019-04-29 10:37:57","updated_at":"2019-04-29 10:37:57","events":[{"year":2019,"list":[{"id":102,"special_id":1263,"event_date":"2019-04-17","post_id":1264,"url":"http://www-cms-c.review.slradio.cn/audio-player/1","longitude":"113.0430000","latitude":"28.1880000","locale":"湖南省长沙市雨花区","created_at":"2019-04-29 10:37:57","updated_at":"2019-04-29 10:37:57","original_id":1093,"type":1,"posts":{"id":1264,"type":6,"merchant_id":4,"artist_id":0,"author_id":5,"province":"43","city":"4301","county":"430104","title":"发生大的","des":"暗杀的风很大个","cover":"http://sl-cms.static.slradio.cn/merchants/1/imges/3JFGaYkiA1cfGRtXM5p8iJckap5PityC1556505463000.png","update_time":"2019-04-29 10:37:57","publish_time":null,"publish_at":"2019-04-29 10:37:57","putaway":1,"status":4,"collection":0,"like":0,"view":0,"comment":0,"reprint":0,"deleted_at":null,"created_at":"2019-04-29 10:37:57","updated_at":"2019-06-18 09:07:15","is_comment":1},"medias":[{"id":136,"event_id":1264,"type":3,"url":"http://sl-cms.static.slradio.cn/merchants/1/imges/CKJ79F1P8fByQi5FAkZebANwR6ie3kMm1556505473349.jpg","title":"avatar_3.jpg","format":"image/jpeg","size":136077,"duration":"","source_id":0,"video_id":"0","created_at":"2019-04-29 10:37:57","updated_at":"2019-04-29 10:37:57","cover":""}]}]}]}
     * categories : []
     * author_info : {"id":5,"merchant_id":4,"staff_name":"黄庚","user_id":1,"merchant":{"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"parent_id":0}}
     */

    private int id;
    private int type;
    private String title;
    private String des;
    private String cover;
    private int status;
    private String publish_at;
    private String created_at;
    private String updated_at;
    private int is_comment;
    private int putaway;
    private int collection;
    private int like;
    private int view;
    private int reprint;
    private int comment;
    private int author_id;
    private int merchant_id;
    private int is_follow;
    private int is_likes;
    private int is_collection;
    private SpecialBean special;
    private AuthorInfoBean author_info;
    private List<?> categories;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPublish_at() {
        return publish_at;
    }

    public void setPublish_at(String publish_at) {
        this.publish_at = publish_at;
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

    public int getPutaway() {
        return putaway;
    }

    public void setPutaway(int putaway) {
        this.putaway = putaway;
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

    public int getReprint() {
        return reprint;
    }

    public void setReprint(int reprint) {
        this.reprint = reprint;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public int getIs_likes() {
        return is_likes;
    }

    public void setIs_likes(int is_likes) {
        this.is_likes = is_likes;
    }

    public int getIs_collection() {
        return is_collection;
    }

    public void setIs_collection(int is_collection) {
        this.is_collection = is_collection;
    }

    public SpecialBean getSpecial() {
        return special;
    }

    public void setSpecial(SpecialBean special) {
        this.special = special;
    }

    public AuthorInfoBean getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(AuthorInfoBean author_info) {
        this.author_info = author_info;
    }

    public List<?> getCategories() {
        return categories;
    }

    public void setCategories(List<?> categories) {
        this.categories = categories;
    }

    public static class SpecialBean {
        /**
         * id : 53
         * post_id : 1263
         * subtitle : 专题副标题
         * module : 1
         * created_at : 2019-04-29 10:37:57
         * updated_at : 2019-04-29 10:37:57
         * events : [{"year":2019,"list":[{"id":102,"special_id":1263,"event_date":"2019-04-17","post_id":1264,"url":"http://www-cms-c.review.slradio.cn/audio-player/1","longitude":"113.0430000","latitude":"28.1880000","locale":"湖南省长沙市雨花区","created_at":"2019-04-29 10:37:57","updated_at":"2019-04-29 10:37:57","original_id":1093,"type":1,"posts":{"id":1264,"type":6,"merchant_id":4,"artist_id":0,"author_id":5,"province":"43","city":"4301","county":"430104","title":"发生大的","des":"暗杀的风很大个","cover":"http://sl-cms.static.slradio.cn/merchants/1/imges/3JFGaYkiA1cfGRtXM5p8iJckap5PityC1556505463000.png","update_time":"2019-04-29 10:37:57","publish_time":null,"publish_at":"2019-04-29 10:37:57","putaway":1,"status":4,"collection":0,"like":0,"view":0,"comment":0,"reprint":0,"deleted_at":null,"created_at":"2019-04-29 10:37:57","updated_at":"2019-06-18 09:07:15","is_comment":1},"medias":[{"id":136,"event_id":1264,"type":3,"url":"http://sl-cms.static.slradio.cn/merchants/1/imges/CKJ79F1P8fByQi5FAkZebANwR6ie3kMm1556505473349.jpg","title":"avatar_3.jpg","format":"image/jpeg","size":136077,"duration":"","source_id":0,"video_id":"0","created_at":"2019-04-29 10:37:57","updated_at":"2019-04-29 10:37:57","cover":""}]}]}]
         */

        private int id;
        private int post_id;
        private String subtitle;
        private int module;
        private String created_at;
        private String updated_at;
        private List<EventsBean> events;

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

        public List<EventsBean> getEvents() {
            return events;
        }

        public void setEvents(List<EventsBean> events) {
            this.events = events;
        }

        public static class EventsBean {
            /**
             * year : 2019
             * list : [{"id":102,"special_id":1263,"event_date":"2019-04-17","post_id":1264,"url":"http://www-cms-c.review.slradio.cn/audio-player/1","longitude":"113.0430000","latitude":"28.1880000","locale":"湖南省长沙市雨花区","created_at":"2019-04-29 10:37:57","updated_at":"2019-04-29 10:37:57","original_id":1093,"type":1,"posts":{"id":1264,"type":6,"merchant_id":4,"artist_id":0,"author_id":5,"province":"43","city":"4301","county":"430104","title":"发生大的","des":"暗杀的风很大个","cover":"http://sl-cms.static.slradio.cn/merchants/1/imges/3JFGaYkiA1cfGRtXM5p8iJckap5PityC1556505463000.png","update_time":"2019-04-29 10:37:57","publish_time":null,"publish_at":"2019-04-29 10:37:57","putaway":1,"status":4,"collection":0,"like":0,"view":0,"comment":0,"reprint":0,"deleted_at":null,"created_at":"2019-04-29 10:37:57","updated_at":"2019-06-18 09:07:15","is_comment":1},"medias":[{"id":136,"event_id":1264,"type":3,"url":"http://sl-cms.static.slradio.cn/merchants/1/imges/CKJ79F1P8fByQi5FAkZebANwR6ie3kMm1556505473349.jpg","title":"avatar_3.jpg","format":"image/jpeg","size":136077,"duration":"","source_id":0,"video_id":"0","created_at":"2019-04-29 10:37:57","updated_at":"2019-04-29 10:37:57","cover":""}]}]
             */

            private int year;
            private List<ListBean> list;

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean {
                /**
                 * id : 102
                 * special_id : 1263
                 * event_date : 2019-04-17
                 * post_id : 1264
                 * url : http://www-cms-c.review.slradio.cn/audio-player/1
                 * longitude : 113.0430000
                 * latitude : 28.1880000
                 * locale : 湖南省长沙市雨花区
                 * created_at : 2019-04-29 10:37:57
                 * updated_at : 2019-04-29 10:37:57
                 * original_id : 1093
                 * type : 1
                 * posts : {"id":1264,"type":6,"merchant_id":4,"artist_id":0,"author_id":5,"province":"43","city":"4301","county":"430104","title":"发生大的","des":"暗杀的风很大个","cover":"http://sl-cms.static.slradio.cn/merchants/1/imges/3JFGaYkiA1cfGRtXM5p8iJckap5PityC1556505463000.png","update_time":"2019-04-29 10:37:57","publish_time":null,"publish_at":"2019-04-29 10:37:57","putaway":1,"status":4,"collection":0,"like":0,"view":0,"comment":0,"reprint":0,"deleted_at":null,"created_at":"2019-04-29 10:37:57","updated_at":"2019-06-18 09:07:15","is_comment":1}
                 * medias : [{"id":136,"event_id":1264,"type":3,"url":"http://sl-cms.static.slradio.cn/merchants/1/imges/CKJ79F1P8fByQi5FAkZebANwR6ie3kMm1556505473349.jpg","title":"avatar_3.jpg","format":"image/jpeg","size":136077,"duration":"","source_id":0,"video_id":"0","created_at":"2019-04-29 10:37:57","updated_at":"2019-04-29 10:37:57","cover":""}]
                 */

                private int id;
                private int special_id;
                private String event_date;
                private int post_id;
                private String url;
                private String longitude;
                private String latitude;
                private String locale;
                private String created_at;
                private String updated_at;
                private int original_id;
                private int type;
                private PostsBean posts;
                private List<MediasBean> medias;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getSpecial_id() {
                    return special_id;
                }

                public void setSpecial_id(int special_id) {
                    this.special_id = special_id;
                }

                public String getEvent_date() {
                    return event_date;
                }

                public void setEvent_date(String event_date) {
                    this.event_date = event_date;
                }

                public int getPost_id() {
                    return post_id;
                }

                public void setPost_id(int post_id) {
                    this.post_id = post_id;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getLongitude() {
                    return longitude;
                }

                public void setLongitude(String longitude) {
                    this.longitude = longitude;
                }

                public String getLatitude() {
                    return latitude;
                }

                public void setLatitude(String latitude) {
                    this.latitude = latitude;
                }

                public String getLocale() {
                    return locale;
                }

                public void setLocale(String locale) {
                    this.locale = locale;
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

                public int getOriginal_id() {
                    return original_id;
                }

                public void setOriginal_id(int original_id) {
                    this.original_id = original_id;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public PostsBean getPosts() {
                    return posts;
                }

                public void setPosts(PostsBean posts) {
                    this.posts = posts;
                }

                public List<MediasBean> getMedias() {
                    return medias;
                }

                public void setMedias(List<MediasBean> medias) {
                    this.medias = medias;
                }

                public static class PostsBean {
                    /**
                     * id : 1264
                     * type : 6
                     * merchant_id : 4
                     * artist_id : 0
                     * author_id : 5
                     * province : 43
                     * city : 4301
                     * county : 430104
                     * title : 发生大的
                     * des : 暗杀的风很大个
                     * cover : http://sl-cms.static.slradio.cn/merchants/1/imges/3JFGaYkiA1cfGRtXM5p8iJckap5PityC1556505463000.png
                     * update_time : 2019-04-29 10:37:57
                     * publish_time : null
                     * publish_at : 2019-04-29 10:37:57
                     * putaway : 1
                     * status : 4
                     * collection : 0
                     * like : 0
                     * view : 0
                     * comment : 0
                     * reprint : 0
                     * deleted_at : null
                     * created_at : 2019-04-29 10:37:57
                     * updated_at : 2019-06-18 09:07:15
                     * is_comment : 1
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
                }

                public static class MediasBean {
                    /**
                     * id : 136
                     * event_id : 1264
                     * type : 3
                     * url : http://sl-cms.static.slradio.cn/merchants/1/imges/CKJ79F1P8fByQi5FAkZebANwR6ie3kMm1556505473349.jpg
                     * title : avatar_3.jpg
                     * format : image/jpeg
                     * size : 136077
                     * duration :
                     * source_id : 0
                     * video_id : 0
                     * created_at : 2019-04-29 10:37:57
                     * updated_at : 2019-04-29 10:37:57
                     * cover :
                     */

                    private int id;
                    private int event_id;
                    private int type;
                    private String url;
                    private String title;
                    private String format;
                    private int size;
                    private String duration;
                    private int source_id;
                    private String video_id;
                    private String created_at;
                    private String updated_at;
                    private String cover;

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public int getEvent_id() {
                        return event_id;
                    }

                    public void setEvent_id(int event_id) {
                        this.event_id = event_id;
                    }

                    public int getType() {
                        return type;
                    }

                    public void setType(int type) {
                        this.type = type;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public String getTitle() {
                        return title;
                    }

                    public void setTitle(String title) {
                        this.title = title;
                    }

                    public String getFormat() {
                        return format;
                    }

                    public void setFormat(String format) {
                        this.format = format;
                    }

                    public int getSize() {
                        return size;
                    }

                    public void setSize(int size) {
                        this.size = size;
                    }

                    public String getDuration() {
                        return duration;
                    }

                    public void setDuration(String duration) {
                        this.duration = duration;
                    }

                    public int getSource_id() {
                        return source_id;
                    }

                    public void setSource_id(int source_id) {
                        this.source_id = source_id;
                    }

                    public String getVideo_id() {
                        return video_id;
                    }

                    public void setVideo_id(String video_id) {
                        this.video_id = video_id;
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

                    public String getCover() {
                        return cover;
                    }

                    public void setCover(String cover) {
                        this.cover = cover;
                    }
                }
            }
        }
    }

    public static class AuthorInfoBean {
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

        public static class MerchantBean {
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
}