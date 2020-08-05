package com.shuangling.software.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LiveMenu implements Serializable {


    /**
     * id : 1569
     * room_id : 359
     * showtype : 1
     * menu_name : 聊天
     * using : 1
     * deleted_at : null
     * created_at : 2020-07-27 00:00:00
     * updated_at : null
     * sort : 1
     * class : 1
     */

    private int id;
    private int room_id;
    private int showtype;
    private String menu_name;
    private int using;
    private Object deleted_at;
    private String created_at;
    private Object updated_at;
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

    public Object getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Object updated_at) {
        this.updated_at = updated_at;
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
