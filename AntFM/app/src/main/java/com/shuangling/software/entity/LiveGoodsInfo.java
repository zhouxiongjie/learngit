package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class LiveGoodsInfo implements Serializable {


    /**
     * current_page : 1
     * data : [{"id":287,"merchant_goods_id":225,"scene_id":87,"click_num":0,"sort":3,"in_explanation":1,"deleted_at":null,"created_at":"2021-01-26 08:56:44","updated_at":"2021-02-01 09:41:29","merchant_goods":{"id":225,"source":1,"goods_id":636383402327,"goods_name":"正宗湖南豆腐乳特产麻香辣霉豆腐四川特辣下饭菜坛装农家手工自制","goods_desc":"","pict_url":"https://sl-cdn.slradio.cn/vms/merchants/2021/20210126/1611622603/4115ac6d389d9cd0acd18e6cc5ce42fe","shop_title":"宇顺食品专营店","price":18.9,"promotion_price":15.9,"after_coupon_price":15.9,"coupon_amount":0,"redirect_url":"http://api-shop.review.slradio.cn/v1/redirect?to=%2F%2Fs.click.taobao.com%2Ft%3Fe%3Dm%253D2%2526s%253DFuziH%252FDoyXBw4vFB6t2Z2ueEDrYVVa64Dne87AjQPk9yINtkUhsv0BQwyvvvqGA3%252F67zSlRHtIYZ%252BDiQh0tKkBBnUQFs1lHU3L4xI%252BFf%252BHKq3j5PRC5sPQ5fRUs14VI%252FUyNpxLfgKr0jWpzpm6nEC7ExG%252F9TpPHe%252Fd4BQH1Qzsxcfm37xb4PJVCmG7wGNcNzyCat6TEqkvmdRvuPMhzebHEqY%252Bakgpmw%26scm%3D1007.15348.115058.0_28026%26pvid%3D23b6a2bc-45fc-4a0e-9ec4-2f281160af8f%26app_pvid%3D59590_33.44.58.46_596_1611622595922%26ptl%3DfloorId%3A28026%3BoriginalFloorId%3A28026%3Bpvid%3A23b6a2bc-45fc-4a0e-9ec4-2f281160af8f%3Bapp_pvid%3A59590_33.44.58.46_596_1611622595922%26union_lens%3DlensId%253AMAPI%25401611622595%254023b6a2bc-45fc-4a0e-9ec4-2f281160af8f_636383402327%25401&item_id=225"}},{"id":291,"merchant_goods_id":229,"scene_id":87,"click_num":0,"sort":1,"in_explanation":0,"deleted_at":null,"created_at":"2021-02-01 09:41:29","updated_at":"2021-02-01 09:41:29","merchant_goods":{"id":229,"source":1,"goods_id":628725338585,"goods_name":"加压淋浴花洒喷头洗澡淋雨沐浴软管套装家用超强增压莲蓬头热水器","goods_desc":"","pict_url":"https://sl-cdn.slradio.cn/vms/merchants/2021/20210201/1612143689/6450fcafb6a5136b7fcc926d8bd5c8bf","shop_title":"天天特卖工厂店","price":5.9,"promotion_price":3.9,"after_coupon_price":1.9,"coupon_amount":2,"redirect_url":"http://api-shop.review.slradio.cn/v1/redirect?to=%2F%2Fuland.taobao.com%2Fcoupon%2Fedetail%3Fe%3DKMecvnFCi7MNfLV8niU3R5TgU2jJNKOfNNtsjZw%252F%252FoLYD%252FOVDiMGxRU86nx4dRAVvFyy3aoz7Mc35Lk0sqK5pMuRTiT9oEhVZV8pr6FWc0NYSKygbVp7RDnZivLDkkjamMHpNfYdHdA1fzFTENrBvKZugt3GMol2G%252FbessqWxJFnaMHHi45dSvoQccC%252B76%252FQjCyTQ%252FA6sH9lBRYM90QVRw%253D%253D%26%26app_pvid%3D59590_11.10.224.181_691_1612143679166%26ptl%3DfloorId%3A28026%3Bapp_pvid%3A59590_11.10.224.181_691_1612143679166%3Btpp_pvid%3A755f779d-d9f0-4817-9838-a01cd2342fa0%26union_lens%3DlensId%253AMAPI%25401612143679%2540755f779d-d9f0-4817-9838-a01cd2342fa0_628725338585%25401&item_id=229"}},{"id":290,"merchant_goods_id":228,"scene_id":87,"click_num":0,"sort":2,"in_explanation":0,"deleted_at":null,"created_at":"2021-02-01 09:41:17","updated_at":"2021-02-01 09:41:29","merchant_goods":{"id":228,"source":1,"goods_id":631801716790,"goods_name":"n95口罩kn95韩国一次性3D立体鱼嘴型防护男女神时尚KF透气94防尘","goods_desc":"","pict_url":"https://sl-cdn.slradio.cn/vms/merchants/2021/20210201/1612143676/f76209474d304506b1fe362e74fb1e73","shop_title":"pissa旗舰店","price":15,"promotion_price":3.19,"after_coupon_price":2.19,"coupon_amount":1,"redirect_url":"http://api-shop.review.slradio.cn/v1/redirect?to=%2F%2Fuland.taobao.com%2Fcoupon%2Fedetail%3Fe%3Dvw%252BJiw%252Fndz4NfLV8niU3R5TgU2jJNKOfNNtsjZw%252F%252FoKiQFIMAOBXp%252BO0uMJFtKTtVOsPw1SjxsGfDdQspvnUHumFKyIN1bVX65OH1WfUm95Uf2TiFOebe4IYm1yX2mC4mupviBlxhIpk3qypIGt1zohtRswGFzH%252BtldWFq0xJ0%252F9qR%252BmCCg1IcuJIJoRLyeIP9GBUqVt0pddGG4UyTXVWw%253D%253D%26%26app_pvid%3D59590_11.20.249.226_717_1612143666202%26ptl%3DfloorId%3A28026%3Bapp_pvid%3A59590_11.20.249.226_717_1612143666202%3Btpp_pvid%3A6c99aea7-7c3f-44cb-b74b-7fca735a69ca%26union_lens%3DlensId%253AMAPI%25401612143666%25406c99aea7-7c3f-44cb-b74b-7fca735a69ca_631801716790%25401&item_id=228"}}]
     * first_page_url : http://api-shop.review.slradio.cn/v1/original_scenes/1450/c_goods?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://api-shop.review.slradio.cn/v1/original_scenes/1450/c_goods?page=1
     * next_page_url : null
     * path : http://api-shop.review.slradio.cn/v1/original_scenes/1450/c_goods
     * per_page : 10
     * prev_page_url : null
     * to : 3
     * total : 3
     */

    private int current_page;
    private String first_page_url;
    private int from;
    private int last_page;
    private String last_page_url;
    private Object next_page_url;
    private String path;
    private int per_page;
    private Object prev_page_url;
    private int to;
    private int total;
    private List<DataBean> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public String getFirst_page_url() {
        return first_page_url;
    }

    public void setFirst_page_url(String first_page_url) {
        this.first_page_url = first_page_url;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public String getLast_page_url() {
        return last_page_url;
    }

    public void setLast_page_url(String last_page_url) {
        this.last_page_url = last_page_url;
    }

    public Object getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(Object next_page_url) {
        this.next_page_url = next_page_url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public Object getPrev_page_url() {
        return prev_page_url;
    }

    public void setPrev_page_url(Object prev_page_url) {
        this.prev_page_url = prev_page_url;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 287
         * merchant_goods_id : 225
         * scene_id : 87
         * click_num : 0
         * sort : 3
         * in_explanation : 1
         * deleted_at : null
         * created_at : 2021-01-26 08:56:44
         * updated_at : 2021-02-01 09:41:29
         * merchant_goods : {"id":225,"source":1,"goods_id":636383402327,"goods_name":"正宗湖南豆腐乳特产麻香辣霉豆腐四川特辣下饭菜坛装农家手工自制","goods_desc":"","pict_url":"https://sl-cdn.slradio.cn/vms/merchants/2021/20210126/1611622603/4115ac6d389d9cd0acd18e6cc5ce42fe","shop_title":"宇顺食品专营店","price":18.9,"promotion_price":15.9,"after_coupon_price":15.9,"coupon_amount":0,"redirect_url":"http://api-shop.review.slradio.cn/v1/redirect?to=%2F%2Fs.click.taobao.com%2Ft%3Fe%3Dm%253D2%2526s%253DFuziH%252FDoyXBw4vFB6t2Z2ueEDrYVVa64Dne87AjQPk9yINtkUhsv0BQwyvvvqGA3%252F67zSlRHtIYZ%252BDiQh0tKkBBnUQFs1lHU3L4xI%252BFf%252BHKq3j5PRC5sPQ5fRUs14VI%252FUyNpxLfgKr0jWpzpm6nEC7ExG%252F9TpPHe%252Fd4BQH1Qzsxcfm37xb4PJVCmG7wGNcNzyCat6TEqkvmdRvuPMhzebHEqY%252Bakgpmw%26scm%3D1007.15348.115058.0_28026%26pvid%3D23b6a2bc-45fc-4a0e-9ec4-2f281160af8f%26app_pvid%3D59590_33.44.58.46_596_1611622595922%26ptl%3DfloorId%3A28026%3BoriginalFloorId%3A28026%3Bpvid%3A23b6a2bc-45fc-4a0e-9ec4-2f281160af8f%3Bapp_pvid%3A59590_33.44.58.46_596_1611622595922%26union_lens%3DlensId%253AMAPI%25401611622595%254023b6a2bc-45fc-4a0e-9ec4-2f281160af8f_636383402327%25401&item_id=225"}
         */

        private int id;
        private int merchant_goods_id;
        private int scene_id;
        private int click_num;
        private int sort;
        private int in_explanation;
        private Object deleted_at;
        private String created_at;
        private String updated_at;
        private MerchantGoodsBean merchant_goods;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getMerchant_goods_id() {
            return merchant_goods_id;
        }

        public void setMerchant_goods_id(int merchant_goods_id) {
            this.merchant_goods_id = merchant_goods_id;
        }

        public int getScene_id() {
            return scene_id;
        }

        public void setScene_id(int scene_id) {
            this.scene_id = scene_id;
        }

        public int getClick_num() {
            return click_num;
        }

        public void setClick_num(int click_num) {
            this.click_num = click_num;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getIn_explanation() {
            return in_explanation;
        }

        public void setIn_explanation(int in_explanation) {
            this.in_explanation = in_explanation;
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

        public MerchantGoodsBean getMerchant_goods() {
            return merchant_goods;
        }

        public void setMerchant_goods(MerchantGoodsBean merchant_goods) {
            this.merchant_goods = merchant_goods;
        }

        public static class MerchantGoodsBean {
            /**
             * id : 225
             * source : 1
             * goods_id : 636383402327
             * goods_name : 正宗湖南豆腐乳特产麻香辣霉豆腐四川特辣下饭菜坛装农家手工自制
             * goods_desc :
             * pict_url : https://sl-cdn.slradio.cn/vms/merchants/2021/20210126/1611622603/4115ac6d389d9cd0acd18e6cc5ce42fe
             * shop_title : 宇顺食品专营店
             * price : 18.9
             * promotion_price : 15.9
             * after_coupon_price : 15.9
             * coupon_amount : 0
             * redirect_url : http://api-shop.review.slradio.cn/v1/redirect?to=%2F%2Fs.click.taobao.com%2Ft%3Fe%3Dm%253D2%2526s%253DFuziH%252FDoyXBw4vFB6t2Z2ueEDrYVVa64Dne87AjQPk9yINtkUhsv0BQwyvvvqGA3%252F67zSlRHtIYZ%252BDiQh0tKkBBnUQFs1lHU3L4xI%252BFf%252BHKq3j5PRC5sPQ5fRUs14VI%252FUyNpxLfgKr0jWpzpm6nEC7ExG%252F9TpPHe%252Fd4BQH1Qzsxcfm37xb4PJVCmG7wGNcNzyCat6TEqkvmdRvuPMhzebHEqY%252Bakgpmw%26scm%3D1007.15348.115058.0_28026%26pvid%3D23b6a2bc-45fc-4a0e-9ec4-2f281160af8f%26app_pvid%3D59590_33.44.58.46_596_1611622595922%26ptl%3DfloorId%3A28026%3BoriginalFloorId%3A28026%3Bpvid%3A23b6a2bc-45fc-4a0e-9ec4-2f281160af8f%3Bapp_pvid%3A59590_33.44.58.46_596_1611622595922%26union_lens%3DlensId%253AMAPI%25401611622595%254023b6a2bc-45fc-4a0e-9ec4-2f281160af8f_636383402327%25401&item_id=225
             */

            private int id;
            private int source;
            private long goods_id;
            private String goods_name;
            private String goods_desc;
            private String pict_url;
            private String shop_title;
            private double price;
            private double promotion_price;
            private double after_coupon_price;
            private int coupon_amount;
            private String redirect_url;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getSource() {
                return source;
            }

            public void setSource(int source) {
                this.source = source;
            }

            public long getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(long goods_id) {
                this.goods_id = goods_id;
            }

            public String getGoods_name() {
                return goods_name;
            }

            public void setGoods_name(String goods_name) {
                this.goods_name = goods_name;
            }

            public String getGoods_desc() {
                return goods_desc;
            }

            public void setGoods_desc(String goods_desc) {
                this.goods_desc = goods_desc;
            }

            public String getPict_url() {
                return pict_url;
            }

            public void setPict_url(String pict_url) {
                this.pict_url = pict_url;
            }

            public String getShop_title() {
                return shop_title;
            }

            public void setShop_title(String shop_title) {
                this.shop_title = shop_title;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public double getPromotion_price() {
                return promotion_price;
            }

            public void setPromotion_price(double promotion_price) {
                this.promotion_price = promotion_price;
            }

            public double getAfter_coupon_price() {
                return after_coupon_price;
            }

            public void setAfter_coupon_price(double after_coupon_price) {
                this.after_coupon_price = after_coupon_price;
            }

            public int getCoupon_amount() {
                return coupon_amount;
            }

            public void setCoupon_amount(int coupon_amount) {
                this.coupon_amount = coupon_amount;
            }

            public String getRedirect_url() {
                return redirect_url;
            }

            public void setRedirect_url(String redirect_url) {
                this.redirect_url = redirect_url;
            }
        }
    }
}
