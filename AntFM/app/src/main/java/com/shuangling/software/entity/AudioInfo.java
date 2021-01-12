package com.shuangling.software.entity;
import android.os.Parcel;
import android.os.Parcelable;
public class AudioInfo implements Parcelable {
private int id;
    private int index;
    private String title;
    private String url;
    private String duration;
    private String publish_at;
    private String logo;
    private int isRadio;    //是否是电台  0 音频  1 电台   2文章播报
    private int sourceId;
    private String video_id;
    private int articleId;
    public AudioInfo(){
}
public int getId() {
        return id;
    }
public void setId(int id) {
        this.id = id;
    }
public int getIndex() {
        return index;
    }
public void setIndex(int index) {
        this.index = index;
    }
public String getTitle() {
        return title;
    }
public void setTitle(String title) {
        this.title = title;
    }
public String getUrl() {
        return url;
    }
public void setUrl(String url) {
        this.url = url;
    }
public String getDuration() {
        return duration;
    }
public void setDuration(String duration) {
        this.duration = duration;
    }
public String getPublish_at() {
        return publish_at;
    }
public void setPublish_at(String publish_at) {
        this.publish_at = publish_at;
    }
public String getLogo() {
        return logo;
    }
public void setLogo(String logo) {
        this.logo = logo;
    }
public int getIsRadio() {
        return isRadio;
    }
public void setIsRadio(int isRadio) {
        this.isRadio = isRadio;
    }
public int getSourceId() {
        return sourceId;
    }
public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
public String getVideo_id() {
        return video_id;
    }
public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }
public int getArticleId() {
        return articleId;
    }
public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
@Override
    public int describeContents() {
        return 0;
    }
@Override
    public void writeToParcel(Parcel out, int flags) {
out.writeInt(id);
        out.writeInt(index);
out.writeString(title);
        out.writeString(url);
        out.writeString(duration);
        out.writeString(publish_at);
        out.writeString(logo);
        out.writeInt(isRadio);
        out.writeInt(sourceId);
        out.writeString(video_id);
        out.writeInt(articleId);
    }
public static final Parcelable.Creator<AudioInfo> CREATOR = new Parcelable.Creator<AudioInfo>() {
        public AudioInfo createFromParcel(Parcel in) {
            return new AudioInfo(in);
        }
public AudioInfo[] newArray(int size) {
            return new AudioInfo[size];
        }
    };
private AudioInfo(Parcel in) {
id=in.readInt();
        index=in.readInt();
        title=in.readString();
        url=in.readString();
        duration=in.readString();
        publish_at=in.readString();
        logo=in.readString();
        isRadio=in.readInt();
        sourceId=in.readInt();
        video_id=in.readString();
        articleId=in.readInt();
}
}
