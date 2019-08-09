package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class RadioDetail implements Serializable {


    /**
     * channel : {"id":19,"merchant_id":{"id":18,"name":"景德镇美食广播电台"},"type":1,"type_id":5,"region_type":1,"province":36,"city":3602,"county":"","name":"FM99.9 私家车美食广播","logo":"http://sl-cms.static.slradio.cn/platform/imges/z5eh3dPiPXcAxTGZTzA7jNZzRPeddXGe1553496028457.jpg","des":"FM99.9 私家车美食广播，倡导美食新生活。","stream":"https://lhttp.qingting.fm/live/20211687/64k.mp3","status":1,"created_by":{"id":7,"username":"sl_13786102122"},"view":187,"collection":0,"deleted_at":null,"created_at":"2019-03-25 14:38:08","updated_at":"2019-06-17 15:44:00"}
     * program_list : [[{"id":83008,"schedule_id":3611,"program_id":84,"start_time":"07:00:00","end_time":"08:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"聆听早晨","programs":{"id":84,"name":"聆听早晨"}},{"id":83009,"schedule_id":3611,"program_id":85,"start_time":"08:00:00","end_time":"10:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"新闻麻辣烫","programs":{"id":85,"name":"新闻麻辣烫"}},{"id":83010,"schedule_id":3611,"program_id":86,"start_time":"10:00:00","end_time":"11:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"美食商城","programs":{"id":86,"name":"美食商城"}},{"id":83011,"schedule_id":3611,"program_id":87,"start_time":"11:00:00","end_time":"12:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"大胃王","programs":{"id":87,"name":"大胃王"}},{"id":83012,"schedule_id":3611,"program_id":88,"start_time":"12:00:00","end_time":"13:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"浮梁新闻+美食视界","programs":{"id":88,"name":"浮梁新闻+美食视界"}},{"id":83013,"schedule_id":3611,"program_id":89,"start_time":"13:00:00","end_time":"15:00:00","anchor_id":0,"anchor_name":"南城","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"美食商城+美食故事","programs":{"id":89,"name":"美食商城+美食故事"}},{"id":83014,"schedule_id":3611,"program_id":90,"start_time":"15:00:00","end_time":"17:00:00","anchor_id":0,"anchor_name":"洪静","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"大厨在线+味道中国","programs":{"id":90,"name":"大厨在线+味道中国"}},{"id":83015,"schedule_id":3611,"program_id":91,"start_time":"17:00:00","end_time":"19:00:00","anchor_id":0,"anchor_name":"大璇","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"吃香喝辣","programs":{"id":91,"name":"吃香喝辣"}},{"id":83016,"schedule_id":3611,"program_id":92,"start_time":"19:00:00","end_time":"20:00:00","anchor_id":0,"anchor_name":"洪静","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"浮梁新闻+美食幸运星","programs":{"id":92,"name":"浮梁新闻+美食幸运星"}},{"id":83017,"schedule_id":3611,"program_id":93,"start_time":"20:00:00","end_time":"23:59:59","anchor_id":0,"anchor_name":"","created_at":"2019-06-15 23:00:03","updated_at":"2019-06-15 23:00:03","name":"美食视界+悠闲时光","programs":{"id":93,"name":"美食视界+悠闲时光"}}],[{"id":83679,"schedule_id":3643,"program_id":84,"start_time":"07:00:00","end_time":"08:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"聆听早晨","programs":{"id":84,"name":"聆听早晨"}},{"id":83680,"schedule_id":3643,"program_id":85,"start_time":"08:00:00","end_time":"10:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"新闻麻辣烫","programs":{"id":85,"name":"新闻麻辣烫"}},{"id":83681,"schedule_id":3643,"program_id":86,"start_time":"10:00:00","end_time":"11:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"美食商城","programs":{"id":86,"name":"美食商城"}},{"id":83682,"schedule_id":3643,"program_id":87,"start_time":"11:00:00","end_time":"12:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"大胃王","programs":{"id":87,"name":"大胃王"}},{"id":83683,"schedule_id":3643,"program_id":88,"start_time":"12:00:00","end_time":"13:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"浮梁新闻+美食视界","programs":{"id":88,"name":"浮梁新闻+美食视界"}},{"id":83684,"schedule_id":3643,"program_id":89,"start_time":"13:00:00","end_time":"15:00:00","anchor_id":0,"anchor_name":"南城","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"美食商城+美食故事","programs":{"id":89,"name":"美食商城+美食故事"}},{"id":83685,"schedule_id":3643,"program_id":90,"start_time":"15:00:00","end_time":"17:00:00","anchor_id":0,"anchor_name":"洪静","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"大厨在线+味道中国","programs":{"id":90,"name":"大厨在线+味道中国"}},{"id":83686,"schedule_id":3643,"program_id":91,"start_time":"17:00:00","end_time":"19:00:00","anchor_id":0,"anchor_name":"大璇","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"吃香喝辣","programs":{"id":91,"name":"吃香喝辣"}},{"id":83687,"schedule_id":3643,"program_id":92,"start_time":"19:00:00","end_time":"20:00:00","anchor_id":0,"anchor_name":"洪静","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"浮梁新闻+美食幸运星","programs":{"id":92,"name":"浮梁新闻+美食幸运星"}},{"id":83688,"schedule_id":3643,"program_id":93,"start_time":"20:00:00","end_time":"23:59:59","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"美食视界+悠闲时光","programs":{"id":93,"name":"美食视界+悠闲时光"}}],[{"id":84010,"schedule_id":3659,"program_id":84,"start_time":"07:00:00","end_time":"08:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:30:02","updated_at":"2019-06-16 23:30:02","name":"聆听早晨","programs":{"id":84,"name":"聆听早晨"}},{"id":84011,"schedule_id":3659,"program_id":85,"start_time":"08:00:00","end_time":"10:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"新闻麻辣烫","programs":{"id":85,"name":"新闻麻辣烫"}},{"id":84012,"schedule_id":3659,"program_id":86,"start_time":"10:00:00","end_time":"11:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"美食商城","programs":{"id":86,"name":"美食商城"}},{"id":84013,"schedule_id":3659,"program_id":87,"start_time":"11:00:00","end_time":"12:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"大胃王","programs":{"id":87,"name":"大胃王"}},{"id":84014,"schedule_id":3659,"program_id":88,"start_time":"12:00:00","end_time":"13:00:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"浮梁新闻+美食视界","programs":{"id":88,"name":"浮梁新闻+美食视界"}},{"id":84015,"schedule_id":3659,"program_id":89,"start_time":"13:00:00","end_time":"15:00:00","anchor_id":0,"anchor_name":"南城","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"美食商城+美食故事","programs":{"id":89,"name":"美食商城+美食故事"}},{"id":84016,"schedule_id":3659,"program_id":90,"start_time":"15:00:00","end_time":"17:00:00","anchor_id":0,"anchor_name":"洪静","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"大厨在线+味道中国","programs":{"id":90,"name":"大厨在线+味道中国"}},{"id":84017,"schedule_id":3659,"program_id":91,"start_time":"17:00:00","end_time":"19:00:00","anchor_id":0,"anchor_name":"大璇","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"吃香喝辣","programs":{"id":91,"name":"吃香喝辣"}},{"id":84018,"schedule_id":3659,"program_id":92,"start_time":"19:00:00","end_time":"20:00:00","anchor_id":0,"anchor_name":"洪静","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"浮梁新闻+美食幸运星","programs":{"id":92,"name":"浮梁新闻+美食幸运星"}},{"id":84019,"schedule_id":3659,"program_id":93,"start_time":"20:00:00","end_time":"23:59:59","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:30:03","updated_at":"2019-06-16 23:30:03","name":"美食视界+悠闲时光","programs":{"id":93,"name":"美食视界+悠闲时光"}}]]
     * in_play : {"id":25127,"schedule_id":987,"program_id":92,"start_time":"19:00:00","end_time":"20:00:00","anchor_id":0,"anchor_name":"洪静","created_at":"2019-03-31 23:00:01","updated_at":"2019-03-31 23:00:01","name":"浮梁新闻+美食幸运星","programs":{"id":92,"name":"浮梁新闻+美食幸运星"}}
     * collection : 1
     */

    private ChannelBean channel;
    private InPlayBean in_play;
    private int collection;
    private List<List<ProgramListBean>> program_list;

    public ChannelBean getChannel() {
        return channel;
    }

    public void setChannel(ChannelBean channel) {
        this.channel = channel;
    }

    public InPlayBean getIn_play() {
        return in_play;
    }

    public void setIn_play(InPlayBean in_play) {
        this.in_play = in_play;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    public List<List<ProgramListBean>> getProgram_list() {
        return program_list;
    }

    public void setProgram_list(List<List<ProgramListBean>> program_list) {
        this.program_list = program_list;
    }

    public static class ChannelBean {
        /**
         * id : 19
         * merchant_id : {"id":18,"name":"景德镇美食广播电台"}
         * type : 1
         * type_id : 5
         * region_type : 1
         * province : 36
         * city : 3602
         * county :
         * name : FM99.9 私家车美食广播
         * logo : http://sl-cms.static.slradio.cn/platform/imges/z5eh3dPiPXcAxTGZTzA7jNZzRPeddXGe1553496028457.jpg
         * des : FM99.9 私家车美食广播，倡导美食新生活。
         * stream : https://lhttp.qingting.fm/live/20211687/64k.mp3
         * status : 1
         * created_by : {"id":7,"username":"sl_13786102122"}
         * view : 187
         * collection : 0
         * deleted_at : null
         * created_at : 2019-03-25 14:38:08
         * updated_at : 2019-06-17 15:44:00
         */

        private int id;
        private MerchantIdBean merchant_id;
        private int type;
        private int type_id;
        private int region_type;
        private int province;
        private int city;
        private String county;
        private String name;
        private String logo;
        private String des;
        private String stream;
        private int status;
        private CreatedByBean created_by;
        private int view;
        private int collection;
        private Object deleted_at;
        private String created_at;
        private String updated_at;

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

        public int getProvince() {
            return province;
        }

        public void setProvince(int province) {
            this.province = province;
        }

        public int getCity() {
            return city;
        }

        public void setCity(int city) {
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

        public CreatedByBean getCreated_by() {
            return created_by;
        }

        public void setCreated_by(CreatedByBean created_by) {
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

        public static class MerchantIdBean {
            /**
             * id : 18
             * name : 景德镇美食广播电台
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

        public static class CreatedByBean {
            /**
             * id : 7
             * username : sl_13786102122
             */

            private int id;
            private String username;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }
    }

    public static class InPlayBean {
        /**
         * id : 25127
         * schedule_id : 987
         * program_id : 92
         * start_time : 19:00:00
         * end_time : 20:00:00
         * anchor_id : 0
         * anchor_name : 洪静
         * created_at : 2019-03-31 23:00:01
         * updated_at : 2019-03-31 23:00:01
         * name : 浮梁新闻+美食幸运星
         * programs : {"id":92,"name":"浮梁新闻+美食幸运星"}
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
        private ProgramsBean programs;

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

        public ProgramsBean getPrograms() {
            return programs;
        }

        public void setPrograms(ProgramsBean programs) {
            this.programs = programs;
        }

        public static class ProgramsBean {
            /**
             * id : 92
             * name : 浮梁新闻+美食幸运星
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
    }

    public static class ProgramListBean {
        /**
         * id : 83008
         * schedule_id : 3611
         * program_id : 84
         * start_time : 07:00:00
         * end_time : 08:00:00
         * anchor_id : 0
         * anchor_name :
         * created_at : 2019-06-15 23:00:03
         * updated_at : 2019-06-15 23:00:03
         * name : 聆听早晨
         * programs : {"id":84,"name":"聆听早晨"}
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
        private ProgramsBeanX programs;

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

        public ProgramsBeanX getPrograms() {
            return programs;
        }

        public void setPrograms(ProgramsBeanX programs) {
            this.programs = programs;
        }

        public static class ProgramsBeanX {
            /**
             * id : 84
             * name : 聆听早晨
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
    }
}
