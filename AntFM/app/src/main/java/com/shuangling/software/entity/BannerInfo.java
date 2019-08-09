package com.shuangling.software.entity;

import com.shuangling.software.customview.BannerView;
import java.io.Serializable;


public class BannerInfo extends BannerView.Banner implements Serializable {

    String logo;
    String title;
    String url;





    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getLogo() {
        return logo;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        return url;
    }





}
