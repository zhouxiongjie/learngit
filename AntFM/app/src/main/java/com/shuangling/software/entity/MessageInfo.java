package com.shuangling.software.entity;

import java.io.Serializable;

public class MessageInfo implements Serializable {


    /**
     * id : 1333
     * message_id : 808
     * is_read : 0
     * created_at : 2019-07-09 17:13:14
     * message : {"id":808,"message_type_id":2,"content":{"icon":null,"title":"系统提醒","description":"活动提醒：您参与的活动\"活动测试22222\"需要您签到参与，签到时间：2019-04-25 15:00:00至2019-04-25 15:00:00，详情https://www-asc-c.review.slradio.cn/activity","behavior_title":"查看详情","jump_url":"https://www-asc-c.review.slradio.cn/activity","img_url":null},"message_type":{"id":2,"name":"通知","parent_id":0,"ext_fields":""}}
     */

    private int id;
    private int message_id;
    private int is_read;
    private String created_at;
    private MessageBean message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public MessageBean getMessage() {
        return message;
    }

    public void setMessage(MessageBean message) {
        this.message = message;
    }

    public static class MessageBean {
        /**
         * id : 808
         * message_type_id : 2
         * content : {"icon":null,"title":"系统提醒","description":"活动提醒：您参与的活动\"活动测试22222\"需要您签到参与，签到时间：2019-04-25 15:00:00至2019-04-25 15:00:00，详情https://www-asc-c.review.slradio.cn/activity","behavior_title":"查看详情","jump_url":"https://www-asc-c.review.slradio.cn/activity","img_url":null}
         * message_type : {"id":2,"name":"通知","parent_id":0,"ext_fields":""}
         */

        private int id;
        private int message_type_id;
        private ContentBean content;
        private MessageTypeBean message_type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getMessage_type_id() {
            return message_type_id;
        }

        public void setMessage_type_id(int message_type_id) {
            this.message_type_id = message_type_id;
        }

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public MessageTypeBean getMessage_type() {
            return message_type;
        }

        public void setMessage_type(MessageTypeBean message_type) {
            this.message_type = message_type;
        }

        public static class ContentBean {
            /**
             * icon : null
             * title : 系统提醒
             * description : 活动提醒：您参与的活动"活动测试22222"需要您签到参与，签到时间：2019-04-25 15:00:00至2019-04-25 15:00:00，详情https://www-asc-c.review.slradio.cn/activity
             * behavior_title : 查看详情
             * jump_url : https://www-asc-c.review.slradio.cn/activity
             * img_url : null
             */

            private Object icon;
            private String title;
            private String description;
            private String behavior_title;
            private String jump_url;
            private Object img_url;

            public Object getIcon() {
                return icon;
            }

            public void setIcon(Object icon) {
                this.icon = icon;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getBehavior_title() {
                return behavior_title;
            }

            public void setBehavior_title(String behavior_title) {
                this.behavior_title = behavior_title;
            }

            public String getJump_url() {
                return jump_url;
            }

            public void setJump_url(String jump_url) {
                this.jump_url = jump_url;
            }

            public Object getImg_url() {
                return img_url;
            }

            public void setImg_url(Object img_url) {
                this.img_url = img_url;
            }
        }

        public static class MessageTypeBean {
            /**
             * id : 2
             * name : 通知
             * parent_id : 0
             * ext_fields :
             */

            private int id;
            private String name;
            private int parent_id;
            private String ext_fields;

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

            public int getParent_id() {
                return parent_id;
            }

            public void setParent_id(int parent_id) {
                this.parent_id = parent_id;
            }

            public String getExt_fields() {
                return ext_fields;
            }

            public void setExt_fields(String ext_fields) {
                this.ext_fields = ext_fields;
            }
        }
    }
}
