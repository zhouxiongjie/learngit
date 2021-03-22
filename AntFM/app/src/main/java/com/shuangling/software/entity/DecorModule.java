package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class DecorModule implements Serializable {
    /**
     * id : 263
     * layout_id : 74
     * sort : 1
     * type : 1
     * status : 1
     * title :
     * rows : 0
     * cols : 0
     * animated : 2
     * page_animated : 0
     * content_type : 1
     * content_number : 0
     * order_by : 0
     * data_source_id : 0
     * created_at : 2019-06-14 17:42:02
     * updated_at : 2019-06-14 17:42:02
     * contents : [{"id":204,"module_id":263,"sort":1,"title":"长沙本地装修测eeeeee","cover":"https://sl-cms.static.slradio.cn/platform/imges/0n3iEraY9JNGWMnBNP4GHGXxCpQJSNYz1560308913370.jpeg","source_url":"http://www.baidu.com","created_at":"2019-06-14 17:42:02","updated_at":"2019-06-14 17:42:02"},{"id":205,"module_id":263,"sort":2,"title":"长沙本地装修测试呃呃呃呃呃呃呃呃呃呃呃呃eeeeee","cover":"https://sl-cms.static.slradio.cn/platform/imges/i8GYTt37dDmQ56X4txfR7zBYQHEQBDTS1560308957632.jpeg","source_url":"http://www.baidu.com","created_at":"2019-06-14 17:42:02","updated_at":"2019-06-14 17:42:02"},{"id":206,"module_id":263,"sort":3,"title":"eeeeeeeeeeeeee","cover":"https://sl-cms.static.slradio.cn/platform/imges/frTWm8A7ZNs0a7aaYEPQbbmC8MN5QQiG1560308988250.jpeg","source_url":"http://www.baidu.com","created_at":"2019-06-14 17:42:02","updated_at":"2019-06-14 17:42:02"}]\
     * background_change(String) :栏目头部背景随轮播图颜色变化 0 否,1是
     */
    private int id;
    private int layout_id;
    private int sort;
    private int type;
    private int status;
    private String title;
    private int rows;
    private int cols;
    private int animated;
    private int page_animated;
    private int content_type;
    private int content_number;
    private int order_by;
    private String data_source_id;
    private String created_at;
    private String updated_at;
    private List<ContentsBean> contents;
    private String background_change;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLayout_id() {
        return layout_id;
    }

    public void setLayout_id(int layout_id) {
        this.layout_id = layout_id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getAnimated() {
        return animated;
    }

    public void setAnimated(int animated) {
        this.animated = animated;
    }

    public int getPage_animated() {
        return page_animated;
    }

    public void setPage_animated(int page_animated) {
        this.page_animated = page_animated;
    }

    public int getContent_type() {
        return content_type;
    }

    public void setContent_type(int content_type) {
        this.content_type = content_type;
    }

    public int getContent_number() {
        return content_number;
    }

    public void setContent_number(int content_number) {
        this.content_number = content_number;
    }

    public int getOrder_by() {
        return order_by;
    }

    public void setOrder_by(int order_by) {
        this.order_by = order_by;
    }

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
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

    public List<ContentsBean> getContents() {
        return contents;
    }

    public void setContents(List<ContentsBean> contents) {
        this.contents = contents;
    }

    public String getBackground_change() {
        return background_change;
    }

    public void setBackground_change(String background_change) {
        this.background_change = background_change;
    }

    public static class ContentsBean implements Serializable {
        /**
         * id : 204
         * module_id : 263
         * sort : 1
         * title : 长沙本地装修测eeeeee
         * cover : https://sl-cms.static.slradio.cn/platform/imges/0n3iEraY9JNGWMnBNP4GHGXxCpQJSNYz1560308913370.jpeg
         * source_url : http://www.baidu.com
         * created_at : 2019-06-14 17:42:02
         * updated_at : 2019-06-14 17:42:02
         */
        private int id;
        private int module_id;
        private int sort;
        private String title;
        private String cover;
        private String source_url;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getModule_id() {
            return module_id;
        }

        public void setModule_id(int module_id) {
            this.module_id = module_id;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getSource_url() {
            return source_url;
        }

        public void setSource_url(String source_url) {
            this.source_url = source_url;
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
    }
}
