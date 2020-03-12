package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class Column implements Serializable {


    /**
     * id : 8
     * name : 相声
     * icon : http://sl-cms.static.slradio.cn/platform/PXEpZdjSs4eh4eGbxjDH338wTadBH8AE1552447766797.jpg
     * display : 1
     * type : 0
     * sort : 4
     * post_type : 0
     * parent_id : 0
     * children : [{"id":20,"name":"相声2","icon":"http://sl-cms.static.slradio.cn/platform/TNjSttfbMyzD64KmZtQbfKFsDWsc6HaY1552027147582.jpg","display":1,"type":0,"sort":12,"post_type":0,"parent_id":8},{"id":22,"name":"一级3","icon":"","display":1,"type":0,"sort":13,"post_type":0,"parent_id":8},{"id":23,"name":"一级4","icon":"https://sl-cms.static.slradio.cn/platform/3CZktCNkQ7XHDrX887kz6DpZhGEby7XY1559628377067.jpg","display":1,"type":0,"sort":14,"post_type":0,"parent_id":8}]
     */

    private int id;
    private String name;
    private String icon;
    private int display;
    private int type;
    private int sort;
    private int post_type;
    private int parent_id;
    private List<Column> children;

    private boolean isFresh;

    //private List<ColumnContent> content;



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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getPost_type() {
        return post_type;
    }

    public void setPost_type(int post_type) {
        this.post_type = post_type;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public List<Column> getChildren() {
        return children;
    }

    public void setChildren(List<Column> children) {
        this.children = children;
    }


    public boolean isFresh() {
        return isFresh;
    }

    public void setFresh(boolean fresh) {
        isFresh = fresh;
    }

//    public List<ColumnContent> getContent() {
//        return content;
//    }
//
//    public void setContent(List<ColumnContent> content) {
//        this.content = content;
//    }

}
