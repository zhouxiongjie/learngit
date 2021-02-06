package com.shuangling.software.entity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LiveRoomInfo01 implements Serializable {


    /**
     * room_info : {"id":1443,"studio_id":0,"num":1,"merchant_id":4,"anchor_id":4,"name":"哈哈哈哈哈","cover_url":"http://sl-cdn.slradio.cn/live/logo/imges/2JaZiYQSZ6aABsjn1611541257278.JPG","des":"","cms_section_id":12,"cms_section_name":"","type":1,"invite_rewards":1,"push_url":"rtmp://txpush.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18","pull_url":"webrtc://5664.liveplay.myqcloud.com/live/5664_harchar1","using_url":1,"RtmpPlayUrl":"rtmp://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18","FlvPlayUrl":"https://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18.flv","HLSPlayUrl":"https://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18.m3u8","is_record":0,"audit_result":1,"chat":1,"stream_name":"4B7C99CD0E3A52052F4C2CCAAC7E6E18","state":2,"stream_state":0,"text":"","post_id":26068,"deleted_at":null,"created_at":"2021-01-25 10:21:13","updated_at":"2021-02-01 15:05:32","audit":0,"is_rtslive":2,"rts_push_url":"","rts_pull_url":"webrtc://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18","cover_url_vertical":"http://sl-cdn.slradio.cn/live/logo/imges/2JaZiYQSZ6aABsjn1611541257278.JPG","public_code":"http://sl-cdn.slradio.cn/live/avatar/hmHtDFfe4ArzpxDw1611879764946.png","ai_audit":1,"estimate_play_time":"2021-01-25 10:21:12","show_cover":1,"entry_mode":1,"entry_password":null,"sms_subscribe":0,"entry_login":0,"entry_phone":0,"show_model":3,"live_driver":"tencent","rtmp_play_url":"rtmp://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18","flv_play_url":"https://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18.flv","hls_play_url":"https://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18.m3u8","h5_index":"http://live-m.review.slradio.cn/index?stream_name=4B7C99CD0E3A52052F4C2CCAAC7E6E18","pc_index":"/index?stream_name=4B7C99CD0E3A52052F4C2CCAAC7E6E18","room_menus_answer_type":1,"heart_num":17,"hot_index":11959,"anchor":{"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png"},"flow":{"id":338,"room_stream_name":"4B7C99CD0E3A52052F4C2CCAAC7E6E18"},"menus":[{"id":4515,"room_id":1443,"showtype":1,"menu_name":"聊天","using":1,"sort":1,"class":1},{"id":4516,"room_id":1443,"showtype":2,"menu_name":"简介","using":1,"sort":2,"class":1},{"id":4517,"room_id":1443,"showtype":3,"menu_name":"点播","using":1,"sort":3,"class":1},{"id":4527,"room_id":1443,"showtype":5,"menu_name":"直播答题","using":1,"sort":5,"class":2},{"id":4528,"room_id":1443,"showtype":6,"menu_name":"拼手气红包","using":1,"sort":6,"class":2},{"id":4518,"room_id":1443,"showtype":7,"menu_name":"公众号关注","using":1,"sort":7,"class":3},{"id":4519,"room_id":1443,"showtype":8,"menu_name":"中位广告","using":1,"sort":8,"class":3},{"id":4520,"room_id":1443,"showtype":9,"menu_name":"观看人数设置","using":1,"sort":9,"class":3},{"id":4521,"room_id":1443,"showtype":10,"menu_name":"礼物打赏","using":1,"sort":10,"class":2},{"id":4522,"room_id":1443,"showtype":11,"menu_name":"图文直播","using":1,"sort":11,"class":1},{"id":4523,"room_id":1443,"showtype":13,"menu_name":"菱选好物","using":1,"sort":13,"class":2},{"id":4524,"room_id":1443,"showtype":14,"menu_name":"邀请榜","using":1,"sort":14,"class":1},{"id":4525,"room_id":1443,"showtype":15,"menu_name":"扩展互动","using":1,"sort":15,"class":2},{"id":4526,"room_id":1443,"showtype":16,"menu_name":"我问你答","using":1,"sort":16,"class":2},{"id":5509,"room_id":1443,"showtype":17,"menu_name":"热度榜","using":1,"sort":17,"class":1},{"id":5510,"room_id":1443,"showtype":18,"menu_name":"更多直播","using":1,"sort":18,"class":1}],"advertises":[],"expand_activities":[{"id":203,"room_id":1443,"type":1,"status":1,"name":"66","url":"http://asc.review.slradio.cn/lottery/14836","img_url":null,"created_at":"2021-01-28 11:22:04","updated_at":"2021-01-28 11:22:11","deleted_at":null}]}
     * props : [{"id":31,"name":"999","price":8500,"limit":63,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/3epiGGhwSe75CGHD1587733504152.jpg"},{"id":29,"name":"300","price":3000,"limit":30,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/C4s2nkT7QXKDpDrs1587733372197.JPG"},{"id":12,"name":"88888","price":1000,"limit":10,"space":3,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/sFzGwHfh2ac77DRK1586486569078.JPG"},{"id":6,"name":"111","price":800,"limit":10,"space":10,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/YFz8hsM3ZNiEsXHt1586400154040.JPG"},{"id":7,"name":"111","price":800,"limit":10,"space":10,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/YFz8hsM3ZNiEsXHt1586400154040.JPG"},{"id":19,"name":"5块钱","price":500,"limit":100,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/7KwcPKZeBx3bhCAW1587107106779.jpg"},{"id":20,"name":"6块钱","price":500,"limit":100,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/dDYfKHXBfPmARHNe1587344048344.jpg"},{"id":5,"name":"333333","price":0,"limit":0,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/FC2jQcJ63QJnyD6n1586570357581.jpg"},{"id":9,"name":"55就","price":0,"limit":55,"space":11,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/kcPYTcXYYSQrKN4m1586404156361.jpg"},{"id":10,"name":"ddd","price":0,"limit":1,"space":1,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/8xTcEJmnpSRfrPra1586486119713.jpg"},{"id":11,"name":"deeee","price":0,"limit":10,"space":1,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/KRBKBXFzfy35yZfP1586486237576.jpg"},{"id":13,"name":"ddddd","price":0,"limit":0,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/SpSWF6iXxpjQCsEm1586570143656.jpg"},{"id":14,"name":"ddd","price":0,"limit":0,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/yfxdHSwDd5Fp6MaR1586570322446.JPG"},{"id":15,"name":"dddd","price":0,"limit":0,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/AK4WnDSWDfSn8Ek41586570401823.jpg"},{"id":16,"name":"ddd","price":0,"limit":10,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/mSXpDZAz7yZ56H5m1586571223400.jpg"},{"id":17,"name":"1111","price":0,"limit":0,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/AzGFAXTJRjktimhm1586573526410.jpg"},{"id":18,"name":"dd","price":0,"limit":999999999,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/SQj7CCsnxGCmd2pN1586575166567.jpg"},{"id":21,"name":"111","price":0,"limit":10,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/C32dfSfFhPix8bKy1587437854932.jpg"},{"id":22,"name":"ddd","price":0,"limit":300,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/ZB6SkiTtF5ksa5aW1587548536892.JPG"},{"id":23,"name":"dddd","price":0,"limit":30,"space":3,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/EaZ8N5ee22EAS4yd1587548577989.jpg"},{"id":24,"name":"dd","price":0,"limit":30,"space":0,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/nyDp5pAftapf2km71587548601463.jpg"},{"id":25,"name":"10","price":0,"limit":10,"space":60,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/6cHiKGDGQHGfttpG1587733023888.jpg"},{"id":26,"name":"dddd","price":0,"limit":0,"space":4,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/hKmXmJwAZtjp7isf1587733275974.jpg"},{"id":27,"name":"89","price":0,"limit":0,"space":60,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/4PYarkd3i6mSX57G1587733312245.JPG"},{"id":28,"name":"20","price":0,"limit":0,"space":20,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/PtDPrNPZBw8RAzHX1587733353630.jpg"},{"id":30,"name":"60","price":0,"limit":88,"space":2,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/8mMpD5bzc3YHcRH81587733407456.jpg"},{"id":32,"name":"88","price":0,"limit":10,"space":2,"icon_url":"http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/kP5G4kwJb8QEMhYn1587733527836.JPG"}]
     */

    private RoomInfoBean room_info;
    private List<PropsBean> props;

    public RoomInfoBean getRoom_info() {
        return room_info;
    }

    public void setRoom_info(RoomInfoBean room_info) {
        this.room_info = room_info;
    }

    public List<PropsBean> getProps() {
        return props;
    }

    public void setProps(List<PropsBean> props) {
        this.props = props;
    }

    public static class RoomInfoBean {
        /**
         * id : 1443
         * studio_id : 0
         * num : 1
         * merchant_id : 4
         * anchor_id : 4
         * name : 哈哈哈哈哈
         * cover_url : http://sl-cdn.slradio.cn/live/logo/imges/2JaZiYQSZ6aABsjn1611541257278.JPG
         * des :
         * cms_section_id : 12
         * cms_section_name :
         * type : 1
         * invite_rewards : 1
         * push_url : rtmp://txpush.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18
         * pull_url : webrtc://5664.liveplay.myqcloud.com/live/5664_harchar1
         * using_url : 1
         * RtmpPlayUrl : rtmp://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18
         * FlvPlayUrl : https://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18.flv
         * HLSPlayUrl : https://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18.m3u8
         * is_record : 0
         * audit_result : 1
         * chat : 1
         * stream_name : 4B7C99CD0E3A52052F4C2CCAAC7E6E18
         * state : 2
         * stream_state : 0
         * text :
         * post_id : 26068
         * deleted_at : null
         * created_at : 2021-01-25 10:21:13
         * updated_at : 2021-02-01 15:05:32
         * audit : 0
         * is_rtslive : 2
         * rts_push_url :
         * rts_pull_url : webrtc://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18
         * cover_url_vertical : http://sl-cdn.slradio.cn/live/logo/imges/2JaZiYQSZ6aABsjn1611541257278.JPG
         * public_code : http://sl-cdn.slradio.cn/live/avatar/hmHtDFfe4ArzpxDw1611879764946.png
         * ai_audit : 1
         * estimate_play_time : 2021-01-25 10:21:12
         * show_cover : 1
         * entry_mode : 1
         * entry_password : null
         * sms_subscribe : 0
         * entry_login : 0
         * entry_phone : 0
         * show_model : 3
         * live_driver : tencent
         * rtmp_play_url : rtmp://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18
         * flv_play_url : https://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18.flv
         * hls_play_url : https://txlive.slradio.cn/shuanglnlive/4B7C99CD0E3A52052F4C2CCAAC7E6E18.m3u8
         * h5_index : http://live-m.review.slradio.cn/index?stream_name=4B7C99CD0E3A52052F4C2CCAAC7E6E18
         * pc_index : /index?stream_name=4B7C99CD0E3A52052F4C2CCAAC7E6E18
         * room_menus_answer_type : 1
         * heart_num : 17
         * hot_index : 11959
         * anchor : {"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png"}
         * flow : {"id":338,"room_stream_name":"4B7C99CD0E3A52052F4C2CCAAC7E6E18"}
         * menus : [{"id":4515,"room_id":1443,"showtype":1,"menu_name":"聊天","using":1,"sort":1,"class":1},{"id":4516,"room_id":1443,"showtype":2,"menu_name":"简介","using":1,"sort":2,"class":1},{"id":4517,"room_id":1443,"showtype":3,"menu_name":"点播","using":1,"sort":3,"class":1},{"id":4527,"room_id":1443,"showtype":5,"menu_name":"直播答题","using":1,"sort":5,"class":2},{"id":4528,"room_id":1443,"showtype":6,"menu_name":"拼手气红包","using":1,"sort":6,"class":2},{"id":4518,"room_id":1443,"showtype":7,"menu_name":"公众号关注","using":1,"sort":7,"class":3},{"id":4519,"room_id":1443,"showtype":8,"menu_name":"中位广告","using":1,"sort":8,"class":3},{"id":4520,"room_id":1443,"showtype":9,"menu_name":"观看人数设置","using":1,"sort":9,"class":3},{"id":4521,"room_id":1443,"showtype":10,"menu_name":"礼物打赏","using":1,"sort":10,"class":2},{"id":4522,"room_id":1443,"showtype":11,"menu_name":"图文直播","using":1,"sort":11,"class":1},{"id":4523,"room_id":1443,"showtype":13,"menu_name":"菱选好物","using":1,"sort":13,"class":2},{"id":4524,"room_id":1443,"showtype":14,"menu_name":"邀请榜","using":1,"sort":14,"class":1},{"id":4525,"room_id":1443,"showtype":15,"menu_name":"扩展互动","using":1,"sort":15,"class":2},{"id":4526,"room_id":1443,"showtype":16,"menu_name":"我问你答","using":1,"sort":16,"class":2},{"id":5509,"room_id":1443,"showtype":17,"menu_name":"热度榜","using":1,"sort":17,"class":1},{"id":5510,"room_id":1443,"showtype":18,"menu_name":"更多直播","using":1,"sort":18,"class":1}]
         * advertises : []
         * expand_activities : [{"id":203,"room_id":1443,"type":1,"status":1,"name":"66","url":"http://asc.review.slradio.cn/lottery/14836","img_url":null,"created_at":"2021-01-28 11:22:04","updated_at":"2021-01-28 11:22:11","deleted_at":null}]
         */

        private int id;
        private int studio_id;
        private int num;
        private int merchant_id;
        private int anchor_id;
        private String name;
        private String cover_url;
        private String des;
        private int cms_section_id;
        private String cms_section_name;
        private int type;
        private int invite_rewards;
        private String push_url;
        private String pull_url;
        private int using_url;
        private String RtmpPlayUrl;
        private String FlvPlayUrl;
        private String HLSPlayUrl;
        private int is_record;
        private int audit_result;
        private int chat;
        private String stream_name;
        private int state;
        private int stream_state;
        private String text;
        private int post_id;
        private Object deleted_at;
        private String created_at;
        private String updated_at;
        private int audit;
        private int is_rtslive;
        private String rts_push_url;
        private String rts_pull_url;
        private String cover_url_vertical;
        private String public_code;
        private int ai_audit;
        private String estimate_play_time;
        private int show_cover;
        private int entry_mode;
        private Object entry_password;
        private int sms_subscribe;
        private int entry_login;
        private int entry_phone;
        private int show_model;
        private String live_driver;
        private String rtmp_play_url;
        private String flv_play_url;
        private String hls_play_url;
        private String h5_index;
        private String pc_index;
        private int room_menus_answer_type;
        private int heart_num;
        private int hot_index;
        private AnchorBean anchor;
        private FlowBean flow;
        private List<MenusBean> menus;
        private List<?> advertises;
        private List<ExpandActivitiesBean> expand_activities;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStudio_id() {
            return studio_id;
        }

        public void setStudio_id(int studio_id) {
            this.studio_id = studio_id;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(int merchant_id) {
            this.merchant_id = merchant_id;
        }

        public int getAnchor_id() {
            return anchor_id;
        }

        public void setAnchor_id(int anchor_id) {
            this.anchor_id = anchor_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCover_url() {
            return cover_url;
        }

        public void setCover_url(String cover_url) {
            this.cover_url = cover_url;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public int getCms_section_id() {
            return cms_section_id;
        }

        public void setCms_section_id(int cms_section_id) {
            this.cms_section_id = cms_section_id;
        }

        public String getCms_section_name() {
            return cms_section_name;
        }

        public void setCms_section_name(String cms_section_name) {
            this.cms_section_name = cms_section_name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getInvite_rewards() {
            return invite_rewards;
        }

        public void setInvite_rewards(int invite_rewards) {
            this.invite_rewards = invite_rewards;
        }

        public String getPush_url() {
            return push_url;
        }

        public void setPush_url(String push_url) {
            this.push_url = push_url;
        }

        public String getPull_url() {
            return pull_url;
        }

        public void setPull_url(String pull_url) {
            this.pull_url = pull_url;
        }

        public int getUsing_url() {
            return using_url;
        }

        public void setUsing_url(int using_url) {
            this.using_url = using_url;
        }

        public String getRtmpPlayUrl() {
            return RtmpPlayUrl;
        }

        public void setRtmpPlayUrl(String RtmpPlayUrl) {
            this.RtmpPlayUrl = RtmpPlayUrl;
        }

        public String getFlvPlayUrl() {
            return FlvPlayUrl;
        }

        public void setFlvPlayUrl(String FlvPlayUrl) {
            this.FlvPlayUrl = FlvPlayUrl;
        }

        public String getHLSPlayUrl() {
            return HLSPlayUrl;
        }

        public void setHLSPlayUrl(String HLSPlayUrl) {
            this.HLSPlayUrl = HLSPlayUrl;
        }

        public int getIs_record() {
            return is_record;
        }

        public void setIs_record(int is_record) {
            this.is_record = is_record;
        }

        public int getAudit_result() {
            return audit_result;
        }

        public void setAudit_result(int audit_result) {
            this.audit_result = audit_result;
        }

        public int getChat() {
            return chat;
        }

        public void setChat(int chat) {
            this.chat = chat;
        }

        public String getStream_name() {
            return stream_name;
        }

        public void setStream_name(String stream_name) {
            this.stream_name = stream_name;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getStream_state() {
            return stream_state;
        }

        public void setStream_state(int stream_state) {
            this.stream_state = stream_state;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getPost_id() {
            return post_id;
        }

        public void setPost_id(int post_id) {
            this.post_id = post_id;
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

        public int getAudit() {
            return audit;
        }

        public void setAudit(int audit) {
            this.audit = audit;
        }

        public int getIs_rtslive() {
            return is_rtslive;
        }

        public void setIs_rtslive(int is_rtslive) {
            this.is_rtslive = is_rtslive;
        }

        public String getRts_push_url() {
            return rts_push_url;
        }

        public void setRts_push_url(String rts_push_url) {
            this.rts_push_url = rts_push_url;
        }

        public String getRts_pull_url() {
            return rts_pull_url;
        }

        public void setRts_pull_url(String rts_pull_url) {
            this.rts_pull_url = rts_pull_url;
        }

        public String getCover_url_vertical() {
            return cover_url_vertical;
        }

        public void setCover_url_vertical(String cover_url_vertical) {
            this.cover_url_vertical = cover_url_vertical;
        }

        public String getPublic_code() {
            return public_code;
        }

        public void setPublic_code(String public_code) {
            this.public_code = public_code;
        }

        public int getAi_audit() {
            return ai_audit;
        }

        public void setAi_audit(int ai_audit) {
            this.ai_audit = ai_audit;
        }

        public String getEstimate_play_time() {
            return estimate_play_time;
        }

        public void setEstimate_play_time(String estimate_play_time) {
            this.estimate_play_time = estimate_play_time;
        }

        public int getShow_cover() {
            return show_cover;
        }

        public void setShow_cover(int show_cover) {
            this.show_cover = show_cover;
        }

        public int getEntry_mode() {
            return entry_mode;
        }

        public void setEntry_mode(int entry_mode) {
            this.entry_mode = entry_mode;
        }

        public Object getEntry_password() {
            return entry_password;
        }

        public void setEntry_password(Object entry_password) {
            this.entry_password = entry_password;
        }

        public int getSms_subscribe() {
            return sms_subscribe;
        }

        public void setSms_subscribe(int sms_subscribe) {
            this.sms_subscribe = sms_subscribe;
        }

        public int getEntry_login() {
            return entry_login;
        }

        public void setEntry_login(int entry_login) {
            this.entry_login = entry_login;
        }

        public int getEntry_phone() {
            return entry_phone;
        }

        public void setEntry_phone(int entry_phone) {
            this.entry_phone = entry_phone;
        }

        public int getShow_model() {
            return show_model;
        }

        public void setShow_model(int show_model) {
            this.show_model = show_model;
        }

        public String getLive_driver() {
            return live_driver;
        }

        public void setLive_driver(String live_driver) {
            this.live_driver = live_driver;
        }

        public String getRtmp_play_url() {
            return rtmp_play_url;
        }

        public void setRtmp_play_url(String rtmp_play_url) {
            this.rtmp_play_url = rtmp_play_url;
        }

        public String getFlv_play_url() {
            return flv_play_url;
        }

        public void setFlv_play_url(String flv_play_url) {
            this.flv_play_url = flv_play_url;
        }

        public String getHls_play_url() {
            return hls_play_url;
        }

        public void setHls_play_url(String hls_play_url) {
            this.hls_play_url = hls_play_url;
        }

        public String getH5_index() {
            return h5_index;
        }

        public void setH5_index(String h5_index) {
            this.h5_index = h5_index;
        }

        public String getPc_index() {
            return pc_index;
        }

        public void setPc_index(String pc_index) {
            this.pc_index = pc_index;
        }

        public int getRoom_menus_answer_type() {
            return room_menus_answer_type;
        }

        public void setRoom_menus_answer_type(int room_menus_answer_type) {
            this.room_menus_answer_type = room_menus_answer_type;
        }

        public int getHeart_num() {
            return heart_num;
        }

        public void setHeart_num(int heart_num) {
            this.heart_num = heart_num;
        }

        public int getHot_index() {
            return hot_index;
        }

        public void setHot_index(int hot_index) {
            this.hot_index = hot_index;
        }

        public AnchorBean getAnchor() {
            return anchor;
        }

        public void setAnchor(AnchorBean anchor) {
            this.anchor = anchor;
        }

        public FlowBean getFlow() {
            return flow;
        }

        public void setFlow(FlowBean flow) {
            this.flow = flow;
        }

        public List<MenusBean> getMenus() {
            return menus;
        }

        public void setMenus(List<MenusBean> menus) {
            this.menus = menus;
        }

        public List<?> getAdvertises() {
            return advertises;
        }

        public void setAdvertises(List<?> advertises) {
            this.advertises = advertises;
        }

        public List<ExpandActivitiesBean> getExpand_activities() {
            return expand_activities;
        }

        public void setExpand_activities(List<ExpandActivitiesBean> expand_activities) {
            this.expand_activities = expand_activities;
        }

        public static class AnchorBean {
            /**
             * id : 4
             * name : 黄庚的机构勿删勿改
             * logo : http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png
             */

            private int id;
            private String name;
            private String logo;

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
        }

        public static class FlowBean {
            /**
             * id : 338
             * room_stream_name : 4B7C99CD0E3A52052F4C2CCAAC7E6E18
             */

            private int id;
            private String room_stream_name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getRoom_stream_name() {
                return room_stream_name;
            }

            public void setRoom_stream_name(String room_stream_name) {
                this.room_stream_name = room_stream_name;
            }
        }

        public static class MenusBean {
            /**
             * id : 4515
             * room_id : 1443
             * showtype : 1
             * menu_name : 聊天
             * using : 1
             * sort : 1
             * class : 1
             */

            private int id;
            private int room_id;
            private int showtype;
            private String menu_name;
            private int using;
            private int sort;
            @SerializedName("class")
            private int classX;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getRoom_id() {
                return room_id;
            }

            public void setRoom_id(int room_id) {
                this.room_id = room_id;
            }

            public int getShowtype() {
                return showtype;
            }

            public void setShowtype(int showtype) {
                this.showtype = showtype;
            }

            public String getMenu_name() {
                return menu_name;
            }

            public void setMenu_name(String menu_name) {
                this.menu_name = menu_name;
            }

            public int getUsing() {
                return using;
            }

            public void setUsing(int using) {
                this.using = using;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
            }

            public int getClassX() {
                return classX;
            }

            public void setClassX(int classX) {
                this.classX = classX;
            }
        }

        public static class ExpandActivitiesBean {
            /**
             * id : 203
             * room_id : 1443
             * type : 1
             * status : 1
             * name : 66
             * url : http://asc.review.slradio.cn/lottery/14836
             * img_url : null
             * created_at : 2021-01-28 11:22:04
             * updated_at : 2021-01-28 11:22:11
             * deleted_at : null
             */

            private int id;
            private int room_id;
            private int type;
            private int status;
            private String name;
            private String url;
            private Object img_url;
            private String created_at;
            private String updated_at;
            private Object deleted_at;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getRoom_id() {
                return room_id;
            }

            public void setRoom_id(int room_id) {
                this.room_id = room_id;
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

            public Object getImg_url() {
                return img_url;
            }

            public void setImg_url(Object img_url) {
                this.img_url = img_url;
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

            public Object getDeleted_at() {
                return deleted_at;
            }

            public void setDeleted_at(Object deleted_at) {
                this.deleted_at = deleted_at;
            }
        }
    }

    public static class PropsBean {
        /**
         * id : 31
         * name : 999
         * price : 8500
         * limit : 63
         * space : 0
         * icon_url : http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/avatar/3epiGGhwSe75CGHD1587733504152.jpg
         */

        private int id;
        private String name;
        private int price;
        private int limit;
        private int space;
        private String icon_url;

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

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getSpace() {
            return space;
        }

        public void setSpace(int space) {
            this.space = space;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }
    }
}
