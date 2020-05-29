package com.shuangling.software.entity;

import com.aliyun.apsara.alivclittlevideo.view.video.BaseVideoSourceModel;
import com.aliyun.apsara.alivclittlevideo.view.video.videolist.VideoSourceType;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidSts;

import static com.aliyun.apsara.alivclittlevideo.view.video.videolist.VideoSourceType.TYPE_STS;

public class SmallVideoBean extends BaseVideoSourceModel {



    private VidSts mVidSts;
    private String sourceId;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setmVidSts(VidSts mVidSts) {
        this.mVidSts = mVidSts;
    }

    @Override
    public VideoSourceType getSourceType() {
        return TYPE_STS;
    }

    @Override
    public String getUUID() {
        return sourceId;
    }


    @Override
    public VidSts getVidStsSource() {
        return mVidSts;
    }
}
