package com.shuangling.software.entity;

import java.io.Serializable;

public class Subscribe implements Serializable {


    /**
     * id : 90
     * type : 2
     * author_id : 8
     * title : 杂记
     * des : 杂记杂记杂记
     * cover : http://sl-cms.static.slradio.cn/merchants/1/imges/0zBjDrWfhEfhhjwGj7YYbnfMN6TbEcMF1552544670398.jpg
     * created_at : 2019-07-05 11:07:32
     * albums : {"id":30,"post_id":90,"count":5,"subscribe":2,"created_at":"2019-03-14 14:24:42","updated_at":"2019-07-05 11:07:32","status":0}
     * author_info : {"id":8,"merchant_id":6,"staff_name":"李阳","user_id":12,"merchant":{"id":6,"name":"浏阳法院","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/WT0M00Sfw5BHjfxMZpz0S7eBWK8p2MXi1552275020098.jpg","type":11,"parent_id":4}}
     */

    private int id;
    private int type;
    private int author_id;
    private String title;
    private String des;
    private String cover;
    private String created_at;
    private AlbumsBean albums;
    private AuthorInfoBean author_info;

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

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public AlbumsBean getAlbums() {
        return albums;
    }

    public void setAlbums(AlbumsBean albums) {
        this.albums = albums;
    }

    public AuthorInfoBean getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(AuthorInfoBean author_info) {
        this.author_info = author_info;
    }

    public static class AlbumsBean {
        /**
         * id : 30
         * post_id : 90
         * count : 5
         * subscribe : 2
         * created_at : 2019-03-14 14:24:42
         * updated_at : 2019-07-05 11:07:32
         * status : 0
         */

        private int id;
        private int post_id;
        private int count;
        private int subscribe;
        private String created_at;
        private String updated_at;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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
}
