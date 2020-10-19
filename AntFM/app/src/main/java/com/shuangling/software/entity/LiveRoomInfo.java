package com.shuangling.software.entity;

import java.io.Serializable;

public class LiveRoomInfo implements Serializable {


    /**
     * id : 113
     * merchant_id : 1
     * anchor_id : 1
     * name : 旅游测试直播间
     * cover_url : http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/logo/imges/nMi9p66ifZRRRJKG1600321584439.jpg
     * des :
     * cms_section_id : 122
     * cms_section_name :
     * type : 4
     * push_url : rtmp://push.on-radio.cn/shuanglnlive/47117BF18F759CA5E6E435D56B7274D3
     * pull_url :
     * using_url : 3
     * rtmp_play_url : rtmp://live.on-radio.cn/shuanglnlive/47117BF18F759CA5E6E435D56B7274D3
     * flv_play_url : https://live.on-radio.cn/shuanglnlive/47117BF18F759CA5E6E435D56B7274D3.flv
     * hls_play_url : https://live.on-radio.cn/shuanglnlive/47117BF18F759CA5E6E435D56B7274D3.m3u8
     * is_record : 0
     * audit_result : 1
     * chat : 1
     * stream_name : 47117BF18F759CA5E6E435D56B7274D3
     * state : 2
     * text :
     * rts_push_url :
     * rts_pull_url : artc://rts.on-radio.cn/shuanglnlive/47117BF18F759CA5E6E435D56B7274D3_shuanglnlive-RTS
     * open_at : 2020-09-17 13:49:31
     * is_rtslive : 3
     * audit : 0
     * room_menus_answer_type : 0
     * cover_url_vertical : http://shuanglnlive.oss-cn-beijing.aliyuncs.com/live/logo/imges/nMi9p66ifZRRRJKG1600321584439.jpg
     * public_code :
     * ai_audit : 1
     * estimate_play_time : 2020-09-17 13:46:27
     * show_cover : 1
     * entry_mode : 1
     * sms_subscribe : 0
     * entry_password : null
     * logo : https://shuangln-cdn.on-radio.cn/ucenter/avatar/xad5nsdPGzQ6Y3bw1564966345733.jpg
     * h5_index : https://live-m.on-radio.cn/index?stream_name=47117BF18F759CA5E6E435D56B7274D3
     * pc_index : https://live-pc.on-radio.cn/index?stream_name=47117BF18F759CA5E6E435D56B7274D3
     * entry_login : 0
     * entry_phone : 0
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
    private int audit;
    private int room_menus_answer_type;
    private String cover_url_vertical;
    private String public_code;
    private int ai_audit;
    private String estimate_play_time;
    private int show_cover;
    private int entry_mode;
    private int sms_subscribe;
    private String entry_password;
    private String logo;
    private String h5_index;
    private String pc_index;
    private int entry_login;
    private int entry_phone;

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

    public int getSms_subscribe() {
        return sms_subscribe;
    }

    public void setSms_subscribe(int sms_subscribe) {
        this.sms_subscribe = sms_subscribe;
    }

    public String getEntry_password() {
        return entry_password;
    }

    public void setEntry_password(String entry_password) {
        this.entry_password = entry_password;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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
}
