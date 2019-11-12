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
     */

    private int id;
    private int type;
    private String name;
    @JSONField(name="default")
    private int defaultX;
    private int sort;
    private int display;
    private String source_id;
    private String created_at;
    private String updated_at;

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
}
