package com.shuangling.software.entity;

public class LiveRoomDetail {


    /**
     * id : 276
     * merchant_id : 4
     * anchor_id : 4
     * name : 测试新版3.1
     * cover_url : http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/logo/imges/tj5zHkR1M95a3JpN1585726180634.png
     * des :
     * cms_section_id : 12
     * cms_section_name :
     * type : 1
     * push_url : rtmp://tmp-live-push.slradio.cn/4/733539EF5599D13C1A5F8132A044F63E
     * pull_url :
     * using_url : 1
     * rtmp_play_url : rtmp://tmp-live.slradio.cn/4/733539EF5599D13C1A5F8132A044F63E
     * flv_play_url : http://tmp-live.slradio.cn/4/733539EF5599D13C1A5F8132A044F63E.flv
     * hls_play_url : http://tmp-live.slradio.cn/4/733539EF5599D13C1A5F8132A044F63E.m3u8
     * is_record : 0
     * audit_result : 1
     * chat : 1
     * stream_name : 733539EF5599D13C1A5F8132A044F63E
     * state : 1
     * text :
     * rts_push_url :
     * rts_pull_url : http://rts-live.slradio.cn/4/733539EF5599D13C1A5F8132A044F63E
     * open_at :
     * is_rtslive : 2
     * audit : 0
     * room_menus_answer_type : 1
     * cover_url_vertical :
     * public_code : null
     * ai_audit : 0
     * estimate_play_time : null
     */

    private int id;
    private int merchant_id;
    private int anchor_id;
    private String name;
    private String cover_url;
    private String des;
    private int cms_section_id;
    private String cms_section_name;
    private int type;
    private String push_url;
    private String pull_url;
    private int using_url;
    private String rtmp_play_url;
    private String flv_play_url;
    private String hls_play_url;
    private int is_record;
    private int audit_result;
    private int chat;
    private String stream_name;
    private int state;
    private String text;
    private String rts_push_url;
    private String rts_pull_url;
    private String open_at;
    private int is_rtslive;
    private int audit;   //观众人数
    private int room_menus_answer_type;
    private String cover_url_vertical;
    private Object public_code;
    private int ai_audit;
    private Object estimate_play_time;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getOpen_at() {
        return open_at;
    }

    public void setOpen_at(String open_at) {
        this.open_at = open_at;
    }

    public int getIs_rtslive() {
        return is_rtslive;
    }

    public void setIs_rtslive(int is_rtslive) {
        this.is_rtslive = is_rtslive;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public int getRoom_menus_answer_type() {
        return room_menus_answer_type;
    }

    public void setRoom_menus_answer_type(int room_menus_answer_type) {
        this.room_menus_answer_type = room_menus_answer_type;
    }

    public String getCover_url_vertical() {
        return cover_url_vertical;
    }

    public void setCover_url_vertical(String cover_url_vertical) {
        this.cover_url_vertical = cover_url_vertical;
    }

    public Object getPublic_code() {
        return public_code;
    }

    public void setPublic_code(Object public_code) {
        this.public_code = public_code;
    }

    public int getAi_audit() {
        return ai_audit;
    }

    public void setAi_audit(int ai_audit) {
        this.ai_audit = ai_audit;
    }

    public Object getEstimate_play_time() {
        return estimate_play_time;
    }

    public void setEstimate_play_time(Object estimate_play_time) {
        this.estimate_play_time = estimate_play_time;
    }
}
