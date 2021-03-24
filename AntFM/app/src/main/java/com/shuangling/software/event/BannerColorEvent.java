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

    private int dominantColor;
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


    public String getEventName() {
        return EventName;
    }


    public BannerColorEvent() {
    }

    public int getdominantColor() {
        return dominantColor;
    }

    public void setdominantColor(int dominantColor) {
        this.dominantColor = dominantColor;
    }

    public String getIsBannerColorChange() {
        return isBannerColorChange;
    }

    public void setIsBannerColorChange(String isBannerColorChange) {
        this.isBannerColorChange = isBannerColorChange;
    }
}
