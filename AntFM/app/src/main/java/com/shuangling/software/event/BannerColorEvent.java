package com.shuangling.software.event;

/**
 * 项目名称：AntFM
 * 创建人：YoungBean
 * 创建时间：2021/3/8 16:37
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BannerColorEvent {
    public final static String INDEX_FRAGMENT_COLOR = "INDEX_FRAGMENT_COLOR";
    public final static String CONTENTHOT_FRAGMENT_COLOR = "CONTENTHOT_FRAGMENT_COLOR";
    public final static String CONTENT_FRAGMENT_COLOR = "CONTENT_FRAGMENT_COLOR";

    private int vibrantColor;
    private String mColumnName;
    private String isBannerColorChange;
    private String EventName;
    private int mColumnId;

    public int getmColumnId() {
        return mColumnId;
    }

    public void setmColumnId(int mColumnId) {
        this.mColumnId = mColumnId;
    }

    public BannerColorEvent(String eventName) {
        EventName = eventName;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public BannerColorEvent() {
    }

    public String getmColumnName() {
        return mColumnName;
    }

    public void setmColumnName(String mColumnName) {
        this.mColumnName = mColumnName;
    }

    public int getVibrantColor() {
        return vibrantColor;
    }

    public void setVibrantColor(int vibrantColor) {
        this.vibrantColor = vibrantColor;
    }

    public String getIsBannerColorChange() {
        return isBannerColorChange;
    }

    public void setIsBannerColorChange(String isBannerColorChange) {
        this.isBannerColorChange = isBannerColorChange;
    }
}
