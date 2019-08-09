package com.shuangling.software.entity;


import java.io.Serializable;
import java.util.List;

public class Article implements Serializable {


    /**
     * id : 2011
     * title : 121212121212121212
     * like : 1
     * author_id : 5
     * publish_at : 2019-07-04 10:10:19
     * view : 28
     * is_comment : 1
     * merchant_id : 4
     * type : 3
     * comment : 0
     * is_follow : 0
     * is_likes : 0
     * is_collection : 0
     * article : {"id":786,"post_id":2011,"content":"<p>1212121212121<\/p>","created_at":"2019-07-04 10:10:20","updated_at":"2019-07-04 10:10:20","count":0,"website_name":"","origin_url":"","originator":"","covers":[],"type":3,"source_type":2}
     * author_info : {"id":5,"merchant_id":4,"staff_name":"黄庚","user_id":1,"merchant":{"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"parent_id":0}}
     * categories : [{"id":10,"name":"儿童","icon":"http://sl-cms.static.slradio.cn/platform/tQPP5jCF2tGjNsemsar4MKCJ4dc3tZYe1552638288675.jpg","display":1,"type":0,"sort":17,"created_at":"2019-03-15 16:25:05","updated_at":"2019-06-03 15:11:09","post_type":0,"parent_id":0,"merchant_id":null,"pivot":{"post_id":2011,"category_id":10},"parent":null}]
     */

    private int id;
    private String title;
    private int like;
    private int author_id;
    private String publish_at;
    private int view;
    private int is_comment;
    private int merchant_id;
    private int type;
    private int comment;
    private int is_follow;
    private int is_likes;
    private int is_collection;
    private ArticleBean article;
    private AuthorInfoBean author_info;
    private List<CategoriesBean> categories;

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

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getPublish_at() {
        return publish_at;
    }

    public void setPublish_at(String publish_at) {
        this.publish_at = publish_at;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getIs_comment() {
        return is_comment;
    }

    public void setIs_comment(int is_comment) {
        this.is_comment = is_comment;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
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

    public ArticleBean getArticle() {
        return article;
    }

    public void setArticle(ArticleBean article) {
        this.article = article;
    }

    public AuthorInfoBean getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(AuthorInfoBean author_info) {
        this.author_info = author_info;
    }

    public List<CategoriesBean> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoriesBean> categories) {
        this.categories = categories;
    }

    public static class ArticleBean {
        /**
         * id : 786
         * post_id : 2011
         * content : <p>1212121212121</p>
         * created_at : 2019-07-04 10:10:20
         * updated_at : 2019-07-04 10:10:20
         * count : 0
         * website_name :
         * origin_url :
         * originator :
         * covers : []
         * type : 3
         * source_type : 2
         */

        private int id;
        private int post_id;
        private String content;
        private String created_at;
        private String updated_at;
        private int count;
        private String website_name;
        private String origin_url;
        private String originator;
        private int type;
        private int source_type;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getWebsite_name() {
            return website_name;
        }

        public void setWebsite_name(String website_name) {
            this.website_name = website_name;
        }

        public String getOrigin_url() {
            return origin_url;
        }

        public void setOrigin_url(String origin_url) {
            this.origin_url = origin_url;
        }

        public String getOriginator() {
            return originator;
        }

        public void setOriginator(String originator) {
            this.originator = originator;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getSource_type() {
            return source_type;
        }

        public void setSource_type(int source_type) {
            this.source_type = source_type;
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

    public static class CategoriesBean {
        /**
         * id : 10
         * name : 儿童
         * icon : http://sl-cms.static.slradio.cn/platform/tQPP5jCF2tGjNsemsar4MKCJ4dc3tZYe1552638288675.jpg
         * display : 1
         * type : 0
         * sort : 17
         * created_at : 2019-03-15 16:25:05
         * updated_at : 2019-06-03 15:11:09
         * post_type : 0
         * parent_id : 0
         * merchant_id : null
         * pivot : {"post_id":2011,"category_id":10}
         * parent : null
         */

        private int id;
        private String name;
        private String icon;
        private int display;
        private int type;
        private int sort;
        private String created_at;
        private String updated_at;
        private int post_type;
        private int parent_id;
        private Object merchant_id;
        private PivotBean pivot;
        private Object parent;

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

        public Object getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(Object merchant_id) {
            this.merchant_id = merchant_id;
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
             * post_id : 2011
             * category_id : 10
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