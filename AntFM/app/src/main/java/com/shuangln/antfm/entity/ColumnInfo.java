package com.shuangln.antfm.entity;

import java.io.Serializable;
import java.util.List;

public class ColumnInfo implements Serializable {


    /**
     * code : 100000
     * data : [{"id":7,"name":"热门","sort":1,"type":1,"post_type":0},{"id":3,"name":"脱口秀","sort":4,"type":0,"post_type":0},{"id":4,"name":"汽车","sort":5,"type":0,"post_type":0},{"id":6,"name":"搞笑","sort":6,"type":0,"post_type":0},{"id":5,"name":"校园","sort":7,"type":0,"post_type":0},{"id":2,"name":"资讯","sort":8,"type":0,"post_type":0},{"id":1,"name":"休闲","sort":9,"type":0,"post_type":0},{"id":8,"name":"同城","sort":10,"type":2,"post_type":0},{"id":9,"name":"视频","sort":11,"type":3,"post_type":4}]
     * msg : 查询成功
     */

    private int code;
    private String msg;
    private List<Column> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Column> getData() {
        return data;
    }

    public void setData(List<Column> data) {
        this.data = data;
    }

    public static class Column {
        /**
         * id : 7
         * name : 热门
         * sort : 1
         * type : 1
         * post_type : 0
         */

        private int id;
        private String name;
        private int sort;
        private int type;
        private int post_type;



        private List<ColumnContent.Content> content;

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

        public int getPost_type() {
            return post_type;
        }

        public void setPost_type(int post_type) {
            this.post_type = post_type;
        }

        public List<ColumnContent.Content> getContent() {
            return content;
        }

        public void setContent(List<ColumnContent.Content> content) {
            this.content = content;
        }
    }
}
