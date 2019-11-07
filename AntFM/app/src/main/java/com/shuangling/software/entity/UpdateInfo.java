package com.shuangling.software.entity;


import java.io.Serializable;

public class UpdateInfo implements Serializable {


    /**
     * support : true
     * new_version : {"id":2,"version":"V3.0.0","content":"DEEE","type":"android","support":1,"created_at":"2019-08-28 17:11:41","updated_at":"2019-08-28 17:11:41","created_by":1}
     */

    private boolean support;
    private NewVersionBean new_version;

    public boolean isSupport() {
        return support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }

    public NewVersionBean getNew_version() {
        return new_version;
    }

    public void setNew_version(NewVersionBean new_version) {
        this.new_version = new_version;
    }

    public static class NewVersionBean {
        /**
         * id : 2
         * version : V3.0.0
         * content : DEEE
         * type : android
         * support : 1
         * created_at : 2019-08-28 17:11:41
         * updated_at : 2019-08-28 17:11:41
         * created_by : 1
         */

        private int id;
        private String version;
        private String content;
        private String type;
        private int support;
        private String created_at;
        private String updated_at;
        private int created_by;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getSupport() {
            return support;
        }

        public void setSupport(int support) {
            this.support = support;
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

        public int getCreated_by() {
            return created_by;
        }

        public void setCreated_by(int created_by) {
            this.created_by = created_by;
        }
    }
}