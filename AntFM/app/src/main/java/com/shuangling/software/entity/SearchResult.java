package com.shuangling.software.entity;

public class SearchResult {


    /**
     * id : 110
     * type : 1
     * title : 歌曲1
     * name : 歌曲1
     * des :
     * cover :
     * search_type : 1
     * duration : 240.0130
     */

    private int id;
    private int type;
    private String title;
    private String name;
    private String des;
    private String cover;
    private int search_type;
    private String duration;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getSearch_type() {
        return search_type;
    }

    public void setSearch_type(int search_type) {
        this.search_type = search_type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
