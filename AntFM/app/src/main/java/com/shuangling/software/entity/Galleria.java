package com.shuangling.software.entity;


import java.io.Serializable;
import java.util.List;

public class Galleria implements Serializable {


    /**
     * id : 1840
     * type : 7
     * title : 555555555555
     * status : 4
     * publish_at : 2019-06-14 11:21:53
     * created_at : 2019-06-14 11:21:53
     * updated_at : 2019-07-08 15:51:42
     * is_comment : 1
     * putaway : 1
     * collection : 2
     * like : 0
     * view : 30
     * reprint : 0
     * comment : 17
     * author_id : 5
     * merchant_id : 4
     * images : [{"id":187,"post_id":1841,"gallerie_id":1840,"sort":1,"size":4979811,"source_id":897,"format":"jpg","width":3840,"height":2400,"created_at":"2019-06-14 11:21:53","updated_at":"2019-06-14 11:21:53","post":{"id":1841,"type":8,"title":"1111","status":4,"cover":"http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/sdaPwYsr35tyWcxp.jpg"}},{"id":188,"post_id":1842,"gallerie_id":1840,"sort":2,"size":845,"source_id":896,"format":"png","width":42,"height":42,"created_at":"2019-06-14 11:21:53","updated_at":"2019-06-14 11:21:53","post":{"id":1842,"type":8,"title":"2222","status":4,"cover":"http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/yMYim8YPSyxrNJRM.png"}},{}]
     * is_follow : 0
     * is_collection : 0
     * gallerie : {"id":29,"post_id":1840,"count":3,"covers":["http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/sdaPwYsr35tyWcxp.jpg"],"type":1,"created_at":"2019-06-14 11:21:53","updated_at":"2019-06-14 11:21:53"}
     * categories : []
     * tags : []
     * author_info : {"id":5,"merchant_id":4,"staff_name":"黄庚","user_id":1,"merchant":{"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"parent_id":0}}
     */

    private int id;
    private int type;
    private String title;
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
    private int is_collection;
    private GallerieBean gallerie;
    private AuthorInfoBean author_info;
    private List<ImagesBean> images;
    private List<?> categories;
    private List<?> tags;

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

    public int getIs_collection() {
        return is_collection;
    }

    public void setIs_collection(int is_collection) {
        this.is_collection = is_collection;
    }

    public GallerieBean getGallerie() {
        return gallerie;
    }

    public void setGallerie(GallerieBean gallerie) {
        this.gallerie = gallerie;
    }

    public AuthorInfoBean getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(AuthorInfoBean author_info) {
        this.author_info = author_info;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public List<?> getCategories() {
        return categories;
    }

    public void setCategories(List<?> categories) {
        this.categories = categories;
    }

    public List<?> getTags() {
        return tags;
    }

    public void setTags(List<?> tags) {
        this.tags = tags;
    }

    public static class GallerieBean {
        /**
         * id : 29
         * post_id : 1840
         * count : 3
         * covers : ["http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/sdaPwYsr35tyWcxp.jpg"]
         * type : 1
         * created_at : 2019-06-14 11:21:53
         * updated_at : 2019-06-14 11:21:53
         */

        private int id;
        private int post_id;
        private int count;
        private int type;
        private String created_at;
        private String updated_at;
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

        public List<String> getCovers() {
            return covers;
        }

        public void setCovers(List<String> covers) {
            this.covers = covers;
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

    public static class ImagesBean {
        /**
         * id : 187
         * post_id : 1841
         * gallerie_id : 1840
         * sort : 1
         * size : 4979811
         * source_id : 897
         * format : jpg
         * width : 3840
         * height : 2400
         * created_at : 2019-06-14 11:21:53
         * updated_at : 2019-06-14 11:21:53
         * post : {"id":1841,"type":8,"title":"1111","status":4,"cover":"http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/sdaPwYsr35tyWcxp.jpg"}
         */

        private int id;
        private int post_id;
        private int gallerie_id;
        private int sort;
        private int size;
        private int source_id;
        private String format;
        private int width;
        private int height;
        private String created_at;
        private String updated_at;
        private PostBean post;

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

        public int getGallerie_id() {
            return gallerie_id;
        }

        public void setGallerie_id(int gallerie_id) {
            this.gallerie_id = gallerie_id;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getSource_id() {
            return source_id;
        }

        public void setSource_id(int source_id) {
            this.source_id = source_id;
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

        public PostBean getPost() {
            return post;
        }

        public void setPost(PostBean post) {
            this.post = post;
        }

        public static class PostBean {
            /**
             * id : 1841
             * type : 8
             * title : 1111
             * status : 4
             * cover : http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/sdaPwYsr35tyWcxp.jpg
             */

            private int id;
            private int type;
            private String title;
            private int status;
            private String cover;

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

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
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