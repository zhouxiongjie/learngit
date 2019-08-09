package com.shuangling.software.entity;

public class Organization {


    /**
     * id : 4
     * name : 黄庚的机构勿删勿改
     * logo : http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png
     * type : 1
     * parent_id : 0
     * des : 黄庚的机构账户
     * is_follow : 0
     * merchant : null
     * others : {"id":3,"merchant_id":4,"menu_status":0,"created_at":"2019-03-11 11:08:49","updated_at":"2019-06-12 16:11:58","follows":0,"count":418,"likes":11,"collections":15,"views":20,"comments":45,"reprints":0}
     */

    private int id;
    private String name;
    private String logo;
    private int type;
    private int parent_id;
    private String des;
    private int is_follow;
    private Object merchant;



    private int follows;

    private Others others;

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

    public Object getMerchant() {
        return merchant;
    }

    public void setMerchant(Object merchant) {
        this.merchant = merchant;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public Others getOthers() {
        return others;
    }

    public void setOthers(Others others) {
        this.others = others;
    }

    public static class Others {
        /**
         * id : 3
         * merchant_id : 4
         * menu_status : 0
         * created_at : 2019-03-11 11:08:49
         * updated_at : 2019-06-12 16:11:58
         * follows : 0
         * count : 418
         * likes : 11
         * collections : 15
         * views : 20
         * comments : 45
         * reprints : 0
         */

        private int id;
        private int merchant_id;
        private int menu_status;
        private String created_at;
        private String updated_at;
        private int follows;
        private int count;
        private int likes;
        private int collections;
        private int views;
        private int comments;
        private int reprints;

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

        public int getMenu_status() {
            return menu_status;
        }

        public void setMenu_status(int menu_status) {
            this.menu_status = menu_status;
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

        public int getFollows() {
            return follows;
        }

        public void setFollows(int follows) {
            this.follows = follows;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getCollections() {
            return collections;
        }

        public void setCollections(int collections) {
            this.collections = collections;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public int getReprints() {
            return reprints;
        }

        public void setReprints(int reprints) {
            this.reprints = reprints;
        }
    }
}
