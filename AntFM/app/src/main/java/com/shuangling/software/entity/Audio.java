package com.shuangling.software.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Audio implements Parcelable {


    /**
     * id : 3125
     * post_album_id : 2975
     * post_audio_id : 2980
     * sort : 1
     * audios : [{"id":2980,"title":"0318热点新闻评论派01","putaway":1,"status":4,"publish_at":"2019-03-27 15:02:19","audio":{"post_id":2980,"duration":126.0874,"size":2188752,"url":"http://vod.slradio.cn/sv/47af9f2d-169bdf2899c/47af9f2d-169bdf2899c.mp3"}}]
     * album : [{"id":2975,"title":"热点新闻评论派","putaway":1,"status":4,"publish_at":"2019-04-18 14:03:02","cover":"http://sl-cms.static.slradio.cn/merchants/1/imges/NT7W7YAWQW2cn6cyAb4QQcsd8AFT5T6Y1553562186061.jpg"}]
     */

    private int id;
    private int post_album_id;
    private int post_audio_id;
    private int sort;
    private List<AudiosBean> audios;
    private List<AlbumBean> album;

    public Audio(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_album_id() {
        return post_album_id;
    }

    public void setPost_album_id(int post_album_id) {
        this.post_album_id = post_album_id;
    }

    public int getPost_audio_id() {
        return post_audio_id;
    }

    public void setPost_audio_id(int post_audio_id) {
        this.post_audio_id = post_audio_id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<AudiosBean> getAudios() {
        return audios;
    }

    public void setAudios(List<AudiosBean> audios) {
        this.audios = audios;
    }

    public List<AlbumBean> getAlbum() {
        return album;
    }

    public void setAlbum(List<AlbumBean> album) {
        this.album = album;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(post_album_id);
        out.writeInt(post_audio_id);
        out.writeInt(sort);
        out.writeTypedList(audios);
        out.writeTypedList(album);

    }


    public static final Parcelable.Creator<Audio> CREATOR = new Parcelable.Creator<Audio>() {
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    private Audio(Parcel in) {

        audios=new ArrayList<>();
        album=new ArrayList<>();
        id=in.readInt();
        post_album_id=in.readInt();
        post_audio_id=in.readInt();
        sort=in.readInt();
        in.readTypedList(audios,AudiosBean.CREATOR);
        in.readTypedList(album,AlbumBean.CREATOR);

    }






    public static class AudiosBean implements Parcelable{
        /**
         * id : 2980
         * title : 0318热点新闻评论派01
         * putaway : 1
         * status : 4
         * publish_at : 2019-03-27 15:02:19
         * audio : {"post_id":2980,"duration":126.0874,"size":2188752,"url":"http://vod.slradio.cn/sv/47af9f2d-169bdf2899c/47af9f2d-169bdf2899c.mp3"}
         */

        private int id;
        private String title;
        private int putaway;
        private int status;
        private String publish_at;
        private AudioBean audio;

        public AudiosBean(){

        }
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPutaway() {
            return putaway;
        }

        public void setPutaway(int putaway) {
            this.putaway = putaway;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPublish_at() {
            return publish_at;
        }

        public void setPublish_at(String publish_at) {
            this.publish_at = publish_at;
        }

        public AudioBean getAudio() {
            return audio;
        }

        public void setAudio(AudioBean audio) {
            this.audio = audio;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(id);
            out.writeString(title);
            out.writeInt(putaway);
            out.writeInt(status);
            out.writeString(publish_at);
            out.writeParcelable(audio,0);
        }


        public static final Parcelable.Creator<AudiosBean> CREATOR = new Parcelable.Creator<AudiosBean>() {
            public AudiosBean createFromParcel(Parcel in) {
                return new AudiosBean(in);
            }

            public AudiosBean[] newArray(int size) {
                return new AudiosBean[size];
            }
        };

        private AudiosBean(Parcel in) {


            id=in.readInt();
            title=in.readString();
            putaway=in.readInt();
            status=in.readInt();
            publish_at=in.readString();
            audio=in.readParcelable(Thread.currentThread().getContextClassLoader());

        }





        public static class AudioBean implements Parcelable{
            /**
             * post_id : 2980
             * duration : 126.0874
             * size : 2188752
             * url : http://vod.slradio.cn/sv/47af9f2d-169bdf2899c/47af9f2d-169bdf2899c.mp3
             */

            private int post_id;
            private double duration;
            private int size;
            private String url;

            public AudioBean(){

            }

            public int getPost_id() {
                return post_id;
            }

            public void setPost_id(int post_id) {
                this.post_id = post_id;
            }

            public double getDuration() {
                return duration;
            }

            public void setDuration(double duration) {
                this.duration = duration;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel out, int flags) {

                out.writeInt(post_id);
                out.writeDouble(duration);
                out.writeInt(size);
                out.writeString(url);

            }


            public static final Parcelable.Creator<AudioBean> CREATOR = new Parcelable.Creator<AudioBean>() {
                public AudioBean createFromParcel(Parcel in) {
                    return new AudioBean(in);
                }

                public AudioBean[] newArray(int size) {
                    return new AudioBean[size];
                }
            };

            private AudioBean(Parcel in) {


                post_id=in.readInt();
                duration=in.readDouble();
                size=in.readInt();
                url=in.readString();

            }


        }
    }

    public static class AlbumBean implements Parcelable{
        /**
         * id : 2975
         * title : 热点新闻评论派
         * putaway : 1
         * status : 4
         * publish_at : 2019-04-18 14:03:02
         * cover : http://sl-cms.static.slradio.cn/merchants/1/imges/NT7W7YAWQW2cn6cyAb4QQcsd8AFT5T6Y1553562186061.jpg
         */

        private int id;
        private String title;
        private int putaway;
        private int status;
        private String publish_at;
        private String cover;

        public AlbumBean(){

        }
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPutaway() {
            return putaway;
        }

        public void setPutaway(int putaway) {
            this.putaway = putaway;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPublish_at() {
            return publish_at;
        }

        public void setPublish_at(String publish_at) {
            this.publish_at = publish_at;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {

            out.writeInt(id);
            out.writeInt(putaway);
            out.writeInt(status);
            out.writeString(publish_at);
            out.writeString(cover);

        }


        public static final Parcelable.Creator<AlbumBean> CREATOR = new Parcelable.Creator<AlbumBean>() {
            public AlbumBean createFromParcel(Parcel in) {
                return new AlbumBean(in);
            }

            public AlbumBean[] newArray(int size) {
                return new AlbumBean[size];
            }
        };

        private AlbumBean(Parcel in) {

            id=in.readInt();
            title=in.readString();
            putaway=in.readInt();
            status=in.readInt();
            publish_at=in.readString();
            cover=in.readString();
        }
    }
}
