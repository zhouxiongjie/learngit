package com.shuangling.software.entity;

import java.util.List;

public class GuidePage {


    /**
     * id : 4
     * title :
     * version : V2.4
     * content_number : 2
     * created_at : 2019-06-12 15:35:28
     * updated_at : 2019-06-12 15:35:28
     * contents : [{"id":8,"guide_id":4,"sort":1,"title":"第一张图片","image":"https://sl-cms.static.slradio.cn/platform/imges/ki84CHreycQBnkzYDYS5fkrTHbehW9Ma1560324902081.jpg","created_at":"2019-06-12 15:35:28","updated_at":"2019-06-12 15:35:28"},{"id":9,"guide_id":4,"sort":2,"title":"第二张图片","image":"https://sl-cms.static.slradio.cn/platform/imges/csJJxENm83QXr3y1JczeRfQ9ANRbyP8G1560324919307.jpg","created_at":"2019-06-12 15:35:28","updated_at":"2019-06-12 15:35:28"}]
     */

    private int id;
    private String title;
    private String version;
    private int content_number;
    private String created_at;
    private String updated_at;
    private List<ContentsBean> contents;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getContent_number() {
        return content_number;
    }

    public void setContent_number(int content_number) {
        this.content_number = content_number;
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

    public static class ContentsBean {
        /**
         * id : 8
         * guide_id : 4
         * sort : 1
         * title : 第一张图片
         * image : https://sl-cms.static.slradio.cn/platform/imges/ki84CHreycQBnkzYDYS5fkrTHbehW9Ma1560324902081.jpg
         * created_at : 2019-06-12 15:35:28
         * updated_at : 2019-06-12 15:35:28
         */

        private int id;
        private int guide_id;
        private int sort;
        private String title;
        private String image;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getGuide_id() {
            return guide_id;
        }

        public void setGuide_id(int guide_id) {
            this.guide_id = guide_id;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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
