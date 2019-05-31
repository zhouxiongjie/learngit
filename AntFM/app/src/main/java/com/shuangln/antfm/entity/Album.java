package com.shuangln.antfm.entity;

public class Album {


    /**
     * id : 2975
     * title : 热点新闻评论派
     * des : 既有热点新闻，又有地道点评。
     * cover : http://sl-cms.static.slradio.cn/merchants/1/imges/NT7W7YAWQW2cn6cyAb4QQcsd8AFT5T6Y1553562186061.jpg
     * author_id : 7
     * publish_at : 2019-04-18 14:03:02
     * view : 25
     * is_comment : 0
     * is_sub : 0
     * author_info : {"id":7,"merchant_id":7,"staff_name":"李宁","user_id":15,"merchant":{"id":7,"name":"李宁","logo":"http://sl-ucenter.static.slradio.cn/merchants/7/imges/hzkjdia6fycNSiKrpGdYjSn4kY6YRw521553671626210.jpg","type":11,"parent_id":1}}
     */

    private int id;
    private String title;
    private String des;
    private String cover;
    private int author_id;
    private String publish_at;
    private int view;
    private int is_comment;
    private int is_sub;
    private AuthorInfo author_info;

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

    public int getIs_sub() {
        return is_sub;
    }

    public void setIs_sub(int is_sub) {
        this.is_sub = is_sub;
    }

    public AuthorInfo getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(AuthorInfo author_info) {
        this.author_info = author_info;
    }

    public static class AuthorInfo {
        /**
         * id : 7
         * merchant_id : 7
         * staff_name : 李宁
         * user_id : 15
         * merchant : {"id":7,"name":"李宁","logo":"http://sl-ucenter.static.slradio.cn/merchants/7/imges/hzkjdia6fycNSiKrpGdYjSn4kY6YRw521553671626210.jpg","type":11,"parent_id":1}
         */

        private int id;
        private int merchant_id;
        private String staff_name;
        private int user_id;
        private Merchant merchant;

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

        public Merchant getMerchant() {
            return merchant;
        }

        public void setMerchant(Merchant merchant) {
            this.merchant = merchant;
        }

        public static class Merchant {
            /**
             * id : 7
             * name : 李宁
             * logo : http://sl-ucenter.static.slradio.cn/merchants/7/imges/hzkjdia6fycNSiKrpGdYjSn4kY6YRw521553671626210.jpg
             * type : 11
             * parent_id : 1
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
