package com.shuangling.software.entity;

public class SearchResult {


    /**
     * id : 313
     * type : 1
     * title :
     * name : 双菱品管部
     * des :
     * cover : https://sl-ucenter.static.slradio.cn/platform/HY8XGC3A2XZyzGsXNZpBGm57Bb2zKt531560908069829.jpg
     * search_type : 10
     * duration : 0
     * is_follow : 0
     * follows : 1
     */

    private int id;
    private int type;
    private String title;
    private String name;
    private String des;
    private String cover;
    private int search_type;
    private String duration;
    private int is_follow;
    private int follows;

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

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }
}
