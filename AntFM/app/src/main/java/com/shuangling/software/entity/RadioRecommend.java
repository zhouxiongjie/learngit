package com.shuangling.software.entity;

import java.io.Serializable;

public class RadioRecommend implements Serializable {


    /**
     * id : 58
     * merchant_id : {"id":4,"name":"黄庚的机构勿删勿改"}
     * type : 1
     * type_id : 6
     * region_type : 1
     * province : 43
     * city : 4301
     * county : 430104
     * name : 测试一台
     * logo : http://sl-cms.static.slradio.cn/platform/imges/9HhXkfi4s2zKfWfseMRxfQ7XfkyethHR1553494936012.jpg
     * des : 测试一台测试一台测试一台
     * stream : http://www
     * status : 1
     * created_by : 1
     * view : 2
     * collection : 0
     * deleted_at : null
     * created_at : 2019-03-25 14:22:33
     * updated_at : 2019-04-16 16:35:52
     * schedule : {"id":4627,"channel_id":58,"name":"测试一台","type":1,"weekday":2,"date":"2019-06-18","created_at":"2019-06-17 23:00:02","updated_at":"2019-06-17 23:00:02","program":{"id":7369,"schedule_id":4627,"program_id":122,"start_time":"06:00","end_time":"23:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-17 23:00:02","updated_at":"2019-06-17 23:00:02","name":"今日测试"}}
     */

    private int id;
    private MerchantIdBean merchant_id;
    private int type;
    private int type_id;
    private int region_type;
    private String province;
    private String city;
    private String county;
    private String name;
    private String logo;
    private String des;
    private String stream;
    private int status;
    private int created_by;
    private int view;
    private int collection;
    private Object deleted_at;
    private String created_at;
    private String updated_at;
    private ScheduleBean schedule;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MerchantIdBean getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(MerchantIdBean merchant_id) {
        this.merchant_id = merchant_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public int getRegion_type() {
        return region_type;
    }

    public void setRegion_type(int region_type) {
        this.region_type = region_type;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
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

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
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

    public ScheduleBean getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleBean schedule) {
        this.schedule = schedule;
    }

    public static class MerchantIdBean {
        /**
         * id : 4
         * name : 黄庚的机构勿删勿改
         */

        private int id;
        private String name;

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
    }

    public static class ScheduleBean {
        /**
         * id : 4627
         * channel_id : 58
         * name : 测试一台
         * type : 1
         * weekday : 2
         * date : 2019-06-18
         * created_at : 2019-06-17 23:00:02
         * updated_at : 2019-06-17 23:00:02
         * program : {"id":7369,"schedule_id":4627,"program_id":122,"start_time":"06:00","end_time":"23:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-17 23:00:02","updated_at":"2019-06-17 23:00:02","name":"今日测试"}
         */

        private int id;
        private int channel_id;
        private String name;
        private int type;
        private int weekday;
        private String date;
        private String created_at;
        private String updated_at;
        private ProgramBean program;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getChannel_id() {
            return channel_id;
        }

        public void setChannel_id(int channel_id) {
            this.channel_id = channel_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getWeekday() {
            return weekday;
        }

        public void setWeekday(int weekday) {
            this.weekday = weekday;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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

        public ProgramBean getProgram() {
            return program;
        }

        public void setProgram(ProgramBean program) {
            this.program = program;
        }

        public static class ProgramBean {
            /**
             * id : 7369
             * schedule_id : 4627
             * program_id : 122
             * start_time : 06:00
             * end_time : 23:00
             * anchor_id : 0
             * anchor_name :
             * created_at : 2019-06-17 23:00:02
             * updated_at : 2019-06-17 23:00:02
             * name : 今日测试
             */

            private int id;
            private int schedule_id;
            private int program_id;
            private String start_time;
            private String end_time;
            private int anchor_id;
            private String anchor_name;
            private String created_at;
            private String updated_at;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getSchedule_id() {
                return schedule_id;
            }

            public void setSchedule_id(int schedule_id) {
                this.schedule_id = schedule_id;
            }

            public int getProgram_id() {
                return program_id;
            }

            public void setProgram_id(int program_id) {
                this.program_id = program_id;
            }

            public String getStart_time() {
                return start_time;
            }

            public void setStart_time(String start_time) {
                this.start_time = start_time;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }

            public int getAnchor_id() {
                return anchor_id;
            }

            public void setAnchor_id(int anchor_id) {
                this.anchor_id = anchor_id;
            }

            public String getAnchor_name() {
                return anchor_name;
            }

            public void setAnchor_name(String anchor_name) {
                this.anchor_name = anchor_name;
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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
