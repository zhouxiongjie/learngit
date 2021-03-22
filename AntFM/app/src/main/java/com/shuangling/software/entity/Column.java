package com.shuangling.software.entity;

import java.io.Serializable;
import java.util.List;

public class Column implements Serializable {
    /**
     * id : 76
     * name : 通知公告 //名称
     * icon : https://sl-cdn.slradio.cn/cms/logo/dzirGyRpSbtjb4YS1577673352137.png //图标
     * display : 1 //是否现实 1 显示 2 不显示
     * type : 0 //1热门 2同城 3特殊的分类如音频、视频分类   0 自定义的分类，6首页
     * sort : 12 //排序
     * post_type : 0 //type为3时为内容类型（1音频 2专辑 3资讯 4视频 5专题 7图集），其他为0
     * parent_id : 0 //父级id
     * show_mode : 2 //展现方式 1小图模式 2大图模式
     * range_mode : 1 //排列方式 1单行模式 2换行模式
     * display_effect_type : 0 //显示效果：0标题加图片，1：标题+角标,2:大图
     * display_effect : null //显示效果内容
     * effect_type : null ,//1：hot,2:new
     * is_link : 0 /是否开启链接地址1是，0否
     * link_address : null //链接地址
     * is_top_color : 0 //是否开启顶部颜色1是，0否
     * top_color : null //栏目顶部颜色
     * children : [{"id":77,"name":"学术讲座","icon":"https://sl-cdn.slradio.cn/cms/logo/iCffQMGrKCnyAGJE1577674792292.png","display":1,"type":0,"sort":0,"post_type":0,"parent_id":76},{"id":115,"name":"院内通知","icon":"","display":1,"type":0,"sort":1,"post_type":0,"parent_id":76},{"id":116,"name":"招标公告","icon":"","display":1,"type":0,"sort":2,"post_type":0,"parent_id":76}] //子集数据
     */

    private int id;
    private String name;
    private String icon;
    private int display;
    private int type;
    private int sort;
    private int post_type;
    private int parent_id;
    private int show_mode;
    private int range_mode;
    private int display_effect_type;
    private String display_effect;
    private String effect_type;
    private int is_link;
    private String link_address;
    private int is_top_color;
    private String top_color;
    private List<Column> children;
    private boolean isFresh;

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

    public int getShow_mode() {
        return show_mode;
    }

    public void setShow_mode(int show_mode) {
        this.show_mode = show_mode;
    }

    public int getRange_mode() {
        return range_mode;
    }

    public void setRange_mode(int range_mode) {
        this.range_mode = range_mode;
    }

    public int getDisplay_effect_type() {
        return display_effect_type;
    }

    public void setDisplay_effect_type(int display_effect_type) {
        this.display_effect_type = display_effect_type;
    }

    public String getDisplay_effect() {
        return display_effect;
    }

    public void setDisplay_effect(String display_effect) {
        this.display_effect = display_effect;
    }

    public String getEffect_type() {
        return effect_type;
    }

    public void setEffect_type(String effect_type) {
        this.effect_type = effect_type;
    }

    public int getIs_link() {
        return is_link;
    }

    public void setIs_link(int is_link) {
        this.is_link = is_link;
    }

    public String getLink_address() {
        return link_address;
    }

    public void setLink_address(String link_address) {
        this.link_address = link_address;
    }

    public int getIs_top_color() {
        return is_top_color;
    }

    public void setIs_top_color(int is_top_color) {
        this.is_top_color = is_top_color;
    }

    public String getTop_color() {
        return top_color;
    }

    public void setTop_color(String top_color) {
        this.top_color = top_color;
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
//    private int id;
//    private String name;
//    private String icon;
//    private int display;
//    private int type;
//    private int sort;
//    private int post_type;
//    private int parent_id;
//    private int show_mode;
//    private int range_mode;
//    private List<Column> children;
//    private boolean isFresh;
//
//    //private List<ColumnContent> content;
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getIcon() {
//        return icon;
//    }
//
//    public void setIcon(String icon) {
//        this.icon = icon;
//    }
//
//    public int getDisplay() {
//        return display;
//    }
//
//    public void setDisplay(int display) {
//        this.display = display;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public int getSort() {
//        return sort;
//    }
//
//    public void setSort(int sort) {
//        this.sort = sort;
//    }
//
//    public int getPost_type() {
//        return post_type;
//    }
//
//    public void setPost_type(int post_type) {
//        this.post_type = post_type;
//    }
//
//    public int getParent_id() {
//        return parent_id;
//    }
//
//    public void setParent_id(int parent_id) {
//        this.parent_id = parent_id;
//    }
//
//    public int getShow_mode() {
//        return show_mode;
//    }
//
//    public void setShow_mode(int show_mode) {
//        this.show_mode = show_mode;
//    }
//
//    public int getRange_mode() {
//        return range_mode;
//    }
//
//    public void setRange_mode(int range_mode) {
//        this.range_mode = range_mode;
//    }
//
//    public List<Column> getChildren() {
//        return children;
//    }
//
//    public void setChildren(List<Column> children) {
//        this.children = children;
//    }
//
//



}
