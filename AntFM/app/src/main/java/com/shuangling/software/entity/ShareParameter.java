package com.shuangling.software.entity;

public class ShareParameter {

    /**
     * shareDesc : 活动专区
     * shareId : -1
     * shareLogo : https://sl-cdn.slradio.cn/cms/icon/imges/58ekYCrEbs1060ysMSjNQkjpBdNiztdF1592386745031.png
     * shareTitle : 掌上灵石
     * shareUrl : https://www-asc-c.staging.slradio.cn/activity?app=android&city=4301&multiple=1.0&from_url=https://www-asc-c.staging.slradio.cn/activity&from_user_id=$share_time=2020-6-29/17:33:12
     */

    private String shareDesc;
    private int shareId;
    private String shareLogo;
    private String shareTitle;
    private String shareUrl;

    public String getShareDesc() {
        return shareDesc;
    }

    public void setShareDesc(String shareDesc) {
        this.shareDesc = shareDesc;
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public String getShareLogo() {
        return shareLogo;
    }

    public void setShareLogo(String shareLogo) {
        this.shareLogo = shareLogo;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
