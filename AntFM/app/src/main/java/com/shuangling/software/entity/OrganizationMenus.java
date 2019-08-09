package com.shuangling.software.entity;

import java.util.List;

public class OrganizationMenus {


    /**
     * id : 14
     * merchant_id : 115
     * created_at : 2019-03-21 15:38:28
     * status : 3
     * putaway : 1
     * reviewer_id : 1
     * content : [{"action":1,"childs":[{"action":2,"childs":[],"level":2,"name":"体育","url":"http://www.baidu.com"}],"level":1,"name":"音乐","url":null}]
     * merchant : {"id":115,"name":"双菱融媒","logo":"http://sl-ucenter.static.slradio.cn/platform/pFw5dTF7pECeMJrMbRsYHwWaHyaSbGQb1553150581280.jpg","type":1,"tel":"","address":"","phone":"15361884540","email":"774913431@qq.com","remark":"","status":1,"expired_at":null,"deleted_at":null,"created_at":"2019-03-21 14:51:54","updated_at":"2019-07-24 09:15:48","from_id":4,"parent_id":0,"des":"呵呵呵呵呵呵呵"}
     * other_merchant : {"id":18,"merchant_id":115,"menu_status":1,"created_at":"2019-03-21 15:38:28","updated_at":"2019-08-03 14:25:34","follows":11,"count":191,"likes":2,"collections":4,"views":10,"comments":4,"reprints":0}
     */

    private int id;
    private int merchant_id;
    private String created_at;
    private int status;
    private int putaway;
    private int reviewer_id;
    private MerchantBean merchant;
    private OtherMerchantBean other_merchant;
    private List<ContentBean> content;

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPutaway() {
        return putaway;
    }

    public void setPutaway(int putaway) {
        this.putaway = putaway;
    }

    public int getReviewer_id() {
        return reviewer_id;
    }

    public void setReviewer_id(int reviewer_id) {
        this.reviewer_id = reviewer_id;
    }

    public MerchantBean getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantBean merchant) {
        this.merchant = merchant;
    }

    public OtherMerchantBean getOther_merchant() {
        return other_merchant;
    }

    public void setOther_merchant(OtherMerchantBean other_merchant) {
        this.other_merchant = other_merchant;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class MerchantBean {
        /**
         * id : 115
         * name : 双菱融媒
         * logo : http://sl-ucenter.static.slradio.cn/platform/pFw5dTF7pECeMJrMbRsYHwWaHyaSbGQb1553150581280.jpg
         * type : 1
         * tel :
         * address :
         * phone : 15361884540
         * email : 774913431@qq.com
         * remark :
         * status : 1
         * expired_at : null
         * deleted_at : null
         * created_at : 2019-03-21 14:51:54
         * updated_at : 2019-07-24 09:15:48
         * from_id : 4
         * parent_id : 0
         * des : 呵呵呵呵呵呵呵
         */

        private int id;
        private String name;
        private String logo;
        private int type;
        private String tel;
        private String address;
        private String phone;
        private String email;
        private String remark;
        private int status;
        private Object expired_at;
        private Object deleted_at;
        private String created_at;
        private String updated_at;
        private int from_id;
        private int parent_id;
        private String des;

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

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getExpired_at() {
            return expired_at;
        }

        public void setExpired_at(Object expired_at) {
            this.expired_at = expired_at;
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

        public int getFrom_id() {
            return from_id;
        }

        public void setFrom_id(int from_id) {
            this.from_id = from_id;
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
    }

    public static class OtherMerchantBean {
        /**
         * id : 18
         * merchant_id : 115
         * menu_status : 1
         * created_at : 2019-03-21 15:38:28
         * updated_at : 2019-08-03 14:25:34
         * follows : 11
         * count : 191
         * likes : 2
         * collections : 4
         * views : 10
         * comments : 4
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

    public static class ContentBean {
        /**
         * action : 1
         * childs : [{"action":2,"childs":[],"level":2,"name":"体育","url":"http://www.baidu.com"}]
         * level : 1
         * name : 音乐
         * url : null
         */

        private int action;
        private int level;
        private String name;
        private String url;
        private List<ChildsBean> childs;

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<ChildsBean> getChilds() {
            return childs;
        }

        public void setChilds(List<ChildsBean> childs) {
            this.childs = childs;
        }

        public static class ChildsBean {
            /**
             * action : 2
             * childs : []
             * level : 2
             * name : 体育
             * url : http://www.baidu.com
             */

            private int action;
            private int level;
            private String name;
            private String url;
            private List<?> childs;

            public int getAction() {
                return action;
            }

            public void setAction(int action) {
                this.action = action;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public List<?> getChilds() {
                return childs;
            }

            public void setChilds(List<?> childs) {
                this.childs = childs;
            }
        }
    }
}
