package com.shuangling.software.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class Advert {
    /**
     * id : 2
     * name : 广告二
     * type : 1
     * default : 0
     * content : https://sl-cdn.slradio.cn/cms/adv/imges/KmsrP30CK1yWrRfChBE30B9jG2YRS8041574124247921.jpg
     * url : http://www.baidu.com
     * skip : 1
     * length : 4
     * merchant : 湖南双菱
     * expires_time : 2019-11-21 00:00:00
     * status : 1
     * start : 08:44
     * end : 13:44
     * view_count : 41
     * click_count : 0
     * deleted_at : null
     * created_at : 2019-11-19 08:44:58
     * updated_at : 2019-11-19 11:49:16
     */
    private int id;
    private String name;
    private int type;
    @JSONField(name = "default")
    private int defaultX;
    private String content;
    private String url;
    private int skip;
    private int length;
    private String merchant;
    private String expires_time;
    private int status;
    private String start;
    private String end;
    private int view_count;
    private int click_count;
    private Object deleted_at;
    private String created_at;
    private String updated_at;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDefaultX() {
        return defaultX;
    }

    public void setDefaultX(int defaultX) {
        this.defaultX = defaultX;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getExpires_time() {
        return expires_time;
    }

    public void setExpires_time(String expires_time) {
        this.expires_time = expires_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
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
}
