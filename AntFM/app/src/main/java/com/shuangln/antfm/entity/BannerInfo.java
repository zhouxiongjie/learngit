package com.shuangln.antfm.entity;

import com.shuangln.antfm.customview.BannerView;
import java.io.Serializable;


public class BannerInfo extends BannerView.Banner implements Serializable {

    String logo;
    String title;





    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getLogo() {
        return logo;
    }

    @Override
    public String getTitle() {
        return title;
    }



}
