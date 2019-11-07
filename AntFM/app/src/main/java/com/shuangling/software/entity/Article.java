package com.shuangling.software.entity;


import java.io.Serializable;
import java.util.List;

public class Article implements Serializable {


    /**
     * id : 3361
     * title : 这条微博，必须为中国女排转！九连胜，姑娘们好样的！继续加油，期待#中国女排总冠军#[心] ​​​
     * like : 1
     * author_id : 358
     * publish_at : 2019-09-27 16:12:59
     * view : 68
     * is_comment : 1
     * merchant_id : 328
     * type : 3
     * comment : 0
     * des : 这条微博，必须为中国女排转！九连胜，姑娘们好样的！继续加油，期待#中国女排总冠军#[心]报道来源:微博转载此文是出于传递更多的信息之目的，若有来源标注错误或者侵犯您的合法权益，请作者持权属证明与本网联
     * is_follow : 0
     * is_likes : 0
     * is_collection : 0
     * article : {"id":1679,"post_id":3361,"content":"<p style=\"text-align:center\"><img src=\"https://sl-cdn.slradio.cn/vms/merchants//weiboimg/2019/20190927/2656274875/e68c97b9e84e329d3a52a7a4a7fb3abc.jpg\"/><\/p><p>这条微博，必须为中国女排转！九连胜，姑娘们好样的！继续加油，期待#中国女排总冠军#[心]<\/p><p style=\"margin-top: 10px;color: rgba(153, 153, 153, 1)\">报道来源:微博<\/p><p style=\"color: rgba(153, 153, 153, 1);margin-top: 10px\">转载此文是出于传递更多的信息之目的，若有来源标注错误或者侵犯您的合法权益，请作者持权属证明与本网联系，我们将及时更正、删除，谢谢<\/p><p><br/><\/p>","created_at":"2019-09-27 16:12:59","updated_at":"2019-09-27 16:12:59","count":0,"website_name":"","origin_url":"http://weibo.com/2656274875/I8SCX2Oeq","originator":"","covers":[],"type":3,"source_type":3,"source_app":"scs","clue_id":0}
     * author_info : {"id":358,"merchant_id":328,"staff_name":"江平","user_id":804,"merchant":{"id":328,"name":"广播V8测试机构","logo":"http://sl-cdn.slradio.cn/ucenter/avatar/k5n3E4HSddCpcHEK1565775526093.jpg","type":1,"parent_id":0}}
     * categories : [{"id":43,"name":"实时热点","icon":"https://sl-cms.static.slradio.cn/platform/FrQTpnYyxkh4cW5exBkEHkxcwT8ZPrBk1560828435438.jpg","display":1,"type":0,"sort":1,"created_at":"2019-06-10 09:15:12","updated_at":"2019-07-23 16:15:29","post_type":0,"parent_id":12,"merchant_id":null,"pivot":{"post_id":3361,"category_id":43},"parent":{"id":12,"name":"社会热点","icon":"http://sl-cms.static.slradio.cn/platform/KPWphkh6xrAcDKAxTXKsRMCshF5JzkeS1552655015809.jpg","display":1,"type":0,"sort":11,"post_type":0,"parent_id":0}}]
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
    private String des;
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

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
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
         * id : 1679
         * post_id : 3361
         * content : <p style="text-align:center"><img src="https://sl-cdn.slradio.cn/vms/merchants//weiboimg/2019/20190927/2656274875/e68c97b9e84e329d3a52a7a4a7fb3abc.jpg"/></p><p>这条微博，必须为中国女排转！九连胜，姑娘们好样的！继续加油，期待#中国女排总冠军#[心]</p><p style="margin-top: 10px;color: rgba(153, 153, 153, 1)">报道来源:微博</p><p style="color: rgba(153, 153, 153, 1);margin-top: 10px">转载此文是出于传递更多的信息之目的，若有来源标注错误或者侵犯您的合法权益，请作者持权属证明与本网联系，我们将及时更正、删除，谢谢</p><p><br/></p>
         * created_at : 2019-09-27 16:12:59
         * updated_at : 2019-09-27 16:12:59
         * count : 0
         * website_name :
         * origin_url : http://weibo.com/2656274875/I8SCX2Oeq
         * originator :
         * covers : []
         * type : 3
         * source_type : 3
         * source_app : scs
         * clue_id : 0
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
        private String source_app;
        private int clue_id;
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

        public String getSource_app() {
            return source_app;
        }

        public void setSource_app(String source_app) {
            this.source_app = source_app;
        }

        public int getClue_id() {
            return clue_id;
        }

        public void setClue_id(int clue_id) {
            this.clue_id = clue_id;
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
         * id : 358
         * merchant_id : 328
         * staff_name : 江平
         * user_id : 804
         * merchant : {"id":328,"name":"广播V8测试机构","logo":"http://sl-cdn.slradio.cn/ucenter/avatar/k5n3E4HSddCpcHEK1565775526093.jpg","type":1,"parent_id":0}
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
             * id : 328
             * name : 广播V8测试机构
             * logo : http://sl-cdn.slradio.cn/ucenter/avatar/k5n3E4HSddCpcHEK1565775526093.jpg
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
         * id : 43
         * name : 实时热点
         * icon : https://sl-cms.static.slradio.cn/platform/FrQTpnYyxkh4cW5exBkEHkxcwT8ZPrBk1560828435438.jpg
         * display : 1
         * type : 0
         * sort : 1
         * created_at : 2019-06-10 09:15:12
         * updated_at : 2019-07-23 16:15:29
         * post_type : 0
         * parent_id : 12
         * merchant_id : null
         * pivot : {"post_id":3361,"category_id":43}
         * parent : {"id":12,"name":"社会热点","icon":"http://sl-cms.static.slradio.cn/platform/KPWphkh6xrAcDKAxTXKsRMCshF5JzkeS1552655015809.jpg","display":1,"type":0,"sort":11,"post_type":0,"parent_id":0}
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
        private ParentBean parent;

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

        public ParentBean getParent() {
            return parent;
        }

        public void setParent(ParentBean parent) {
            this.parent = parent;
        }

        public static class PivotBean {
            /**
             * post_id : 3361
             * category_id : 43
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

        public static class ParentBean {
            /**
             * id : 12
             * name : 社会热点
             * icon : http://sl-cms.static.slradio.cn/platform/KPWphkh6xrAcDKAxTXKsRMCshF5JzkeS1552655015809.jpg
             * display : 1
             * type : 0
             * sort : 11
             * post_type : 0
             * parent_id : 0
             */

            private int id;
            private String name;
            private String icon;
            private int display;
            private int type;
            private int sort;
            private int post_type;
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
        }
    }
}