package com.shuangling.software.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class BottomMenu implements Serializable {


    /**
     * id : 2
     * type : 3
     * name : 媒体矩阵
     * default : 0
     * sort : 2
     * display : 1
     * source_id :
     * created_at : 2019-10-23 17:25:46
     * updated_at : 2019-11-01 16:17:50
     * <p>
     * source_type: 1
     * show_type:2
     * url: "https:\/\/www.baidu.com"
     * logo: "https:\/\/sl-cdn.slradio.cn\/cms\/logo\/imges\/YemTAKNSkx7MPbDjT2HRdn4jc2QRhT1Y1610694242383.jpg"
     */
    private int id;
    private int type;//1首页,2个人中心,3媒体矩阵,4建言资政,5办事指南,6便民服务,7活动中心,8电视,9电台,10资讯分类
    private String name;//菜单名称
    @JSONField(name = "default")
    private int defaultX;//1默认不能关，0自定义
    private int sort;//排序
    private int display;//1显示，0删除
    private String source_id; //分类id，字符串
    private String created_at;
    private String updated_at;

    private int source_type;//数据源类型 1站内资源 2链接地址
    private int show_type; //显示效果 1图标+标题 2大图
    private String url; //链接地址
    private String logo;//图标


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefaultX() {
        return defaultX;
    }

    public void setDefaultX(int defaultX) {
        this.defaultX = defaultX;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
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

    public int getSource_type() {
        return source_type;
    }

    public void setSource_type(int source_type) {
        this.source_type = source_type;
    }

    public int getShow_type() {
        return show_type;
    }

    public void setShow_type(int show_type) {
        this.show_type = show_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
