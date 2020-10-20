package com.shuangling.software.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AudioDetail implements Parcelable {


    /**
     * id : 1675
     * title : Sleep Away-测试1223
     * author_id : 6
     * view : 39
     * is_comment : 1
     * merchant_id : 4
     * type : 1
     * album : [{"id":1812,"type":2,"merchant_id":4,"artist_id":0,"author_id":5,"province":"43","city":"4301","county":"430104","title":"111111111111","des":"鞍山市所所所所","cover":"http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/5.jpg","update_time":"2019-06-13 19:29:13","publish_time":null,"publish_at":"2019-06-13 19:29:13","putaway":1,"status":4,"collection":0,"like":0,"view":60,"comment":9,"reprint":0,"deleted_at":null,"created_at":"2019-06-13 19:29:13","updated_at":"2019-06-20 17:41:43","is_comment":0,"is_sub":0,"pivot":{"post_audio_id":1675,"post_album_id":1812},"albums":{"id":265,"post_id":1812,"count":3,"subscribe":0,"created_at":"2019-06-13 19:29:13","updated_at":"2019-06-15 16:12:11","status":0},"author_info":{"id":5,"merchant_id":4,"staff_name":"黄庚","user_id":1},"merchant":{"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"tel":"","address":"","phone":"18207406897","email":"","remark":"","status":1,"expired_at":null,"deleted_at":null,"created_at":"2019-03-09 11:15:44","updated_at":"2019-04-19 10:45:19","from_id":0,"parent_id":0,"des":"黄庚的机构账户"}}]
     * audio : {"post_id":1675,"duration":"200.5735","size":4842585,"url":"https://vod.slradio.cn/sv/583d405a-16ab52fa0a2/583d405a-16ab52fa0a2.mp3","video_id":"09fa092198c2416eafb114ef0702a6aa","channel":2,"source_id":307,"sample_freq":44100,"bit_depth":1,"bitrate":193}
     * author_info : {"id":6,"merchant_id":4,"staff_name":"weiyi","user_id":9,"is_follow":0,"attention":1,"merchant":{"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"parent_id":0}}
     */

    private int id;
    private String title;
    private int author_id;
    private int view;
    private int is_comment;
    private int merchant_id;
    private int type;
    private AudioBean audio;
    private AuthorInfoBean author_info;
    private List<AlbumBean> album;
    private int is_user_report;

    public AudioDetail(){

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

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getIs_comment() {
        return is_comment;
    }

    public void setIs_comment(int is_comment) {
        this.is_comment = is_comment;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public AudioBean getAudio() {
        return audio;
    }

    public void setAudio(AudioBean audio) {
        this.audio = audio;
    }

    public AuthorInfoBean getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(AuthorInfoBean author_info) {
        this.author_info = author_info;
    }

    public List<AlbumBean> getAlbum() {
        return album;
    }

    public void setAlbum(List<AlbumBean> album) {
        this.album = album;
    }

    public int getIs_user_report() {
        return is_user_report;
    }

    public void setIs_user_report(int is_user_report) {
        this.is_user_report = is_user_report;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {



        out.writeInt(id);
        out.writeString(title);
        out.writeInt(author_id);
        out.writeInt(view);
        out.writeInt(is_comment);
        out.writeInt(merchant_id);
        out.writeInt(type);
        out.writeParcelable(audio,0);
        out.writeParcelable(author_info,0);
        out.writeTypedList(album);


    }


    public static final Parcelable.Creator<AudioDetail> CREATOR = new Parcelable.Creator<AudioDetail>() {
        public AudioDetail createFromParcel(Parcel in) {
            return new AudioDetail(in);
        }

        public AudioDetail[] newArray(int size) {
            return new AudioDetail[size];
        }
    };

    private AudioDetail(Parcel in) {



        album=new ArrayList<>();
        id=in.readInt();
        title=in.readString();
        author_id=in.readInt();
        view=in.readInt();
        is_comment=in.readInt();
        merchant_id=in.readInt();
        type=in.readInt();
        audio=in.readParcelable(Thread.currentThread().getContextClassLoader());
        author_info=in.readParcelable(Thread.currentThread().getContextClassLoader());
        in.readTypedList(album,AudioDetail.AlbumBean.CREATOR);

    }


    public static class AudioBean implements Parcelable{
        /**
         * post_id : 1675
         * duration : 200.5735
         * size : 4842585
         * url : https://vod.slradio.cn/sv/583d405a-16ab52fa0a2/583d405a-16ab52fa0a2.mp3
         * video_id : 09fa092198c2416eafb114ef0702a6aa
         * channel : 2
         * source_id : 307
         * sample_freq : 44100
         * bit_depth : 1
         * bitrate : 193
         */

        private int post_id;
        private String duration;
        private int size;
        private String url;
        private String video_id;
        private int channel;
        private int source_id;
        private int sample_freq;
        private int bit_depth;
        private String bitrate;

        public AudioBean(){

        }

        public int getPost_id() {
            return post_id;
        }

        public void setPost_id(int post_id) {
            this.post_id = post_id;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
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

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public int getSource_id() {
            return source_id;
        }

        public void setSource_id(int source_id) {
            this.source_id = source_id;
        }

        public int getSample_freq() {
            return sample_freq;
        }

        public void setSample_freq(int sample_freq) {
            this.sample_freq = sample_freq;
        }

        public int getBit_depth() {
            return bit_depth;
        }

        public void setBit_depth(int bit_depth) {
            this.bit_depth = bit_depth;
        }

        public String getBitrate() {
            return bitrate;
        }

        public void setBitrate(String bitrate) {
            this.bitrate = bitrate;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {


            out.writeInt(post_id);
            out.writeString(duration);
            out.writeInt(size);
            out.writeString(url);
            out.writeString(video_id);
            out.writeInt(channel);
            out.writeInt(source_id);
            out.writeInt(sample_freq);
            out.writeInt(bit_depth);
            out.writeString(bitrate);


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
            duration=in.readString();
            size=in.readInt();
            url=in.readString();
            video_id=in.readString();
            channel=in.readInt();
            source_id=in.readInt();
            sample_freq=in.readInt();
            bit_depth=in.readInt();
            bitrate=in.readString();

        }

    }

    public static class AuthorInfoBean implements Parcelable{
        /**
         * id : 6
         * merchant_id : 4
         * staff_name : weiyi
         * user_id : 9
         * is_follow : 0
         * attention : 1
         * merchant : {"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"parent_id":0}
         */

        private int id;
        private int merchant_id;
        private String staff_name;
        private int user_id;
        private int is_follow;
        private int attention;
        private MerchantBean merchant;

        public AuthorInfoBean(){

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(int merchant_id) {
            this.merchant_id = merchant_id;
        }

        public String getStaff_name() {
            return staff_name;
        }

        public void setStaff_name(String staff_name) {
            this.staff_name = staff_name;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(int is_follow) {
            this.is_follow = is_follow;
        }

        public int getAttention() {
            return attention;
        }

        public void setAttention(int attention) {
            this.attention = attention;
        }

        public MerchantBean getMerchant() {
            return merchant;
        }

        public void setMerchant(MerchantBean merchant) {
            this.merchant = merchant;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {



            out.writeInt(id);
            out.writeInt(merchant_id);
            out.writeString(staff_name);
            out.writeInt(user_id);
            out.writeInt(is_follow);
            out.writeInt(attention);
            out.writeParcelable(merchant,0);



        }


        public static final Parcelable.Creator<AuthorInfoBean> CREATOR = new Parcelable.Creator<AuthorInfoBean>() {
            public AuthorInfoBean createFromParcel(Parcel in) {
                return new AuthorInfoBean(in);
            }

            public AuthorInfoBean[] newArray(int size) {
                return new AuthorInfoBean[size];
            }
        };

        private AuthorInfoBean(Parcel in) {



            id=in.readInt();
            merchant_id=in.readInt();
            staff_name=in.readString();
            user_id=in.readInt();
            is_follow=in.readInt();
            attention=in.readInt();
            merchant=in.readParcelable(Thread.currentThread().getContextClassLoader());


        }

        public static class MerchantBean implements Parcelable{
            /**
             * id : 4
             * name : 黄庚的机构勿删勿改
             * logo : http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png
             * type : 1
             * parent_id : 0
             */

            private int id;
            private String name;
            private String logo;
            private int type;
            private int parent_id;

            public MerchantBean(){

            }

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

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getParent_id() {
                return parent_id;
            }

            public void setParent_id(int parent_id) {
                this.parent_id = parent_id;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel out, int flags) {



                out.writeInt(id);
                out.writeString(name);
                out.writeString(logo);
                out.writeInt(type);
                out.writeInt(parent_id);




            }


            public static final Parcelable.Creator<MerchantBean> CREATOR = new Parcelable.Creator<MerchantBean>() {
                public MerchantBean createFromParcel(Parcel in) {
                    return new MerchantBean(in);
                }

                public MerchantBean[] newArray(int size) {
                    return new MerchantBean[size];
                }
            };

            private MerchantBean(Parcel in) {

                id=in.readInt();
                name=in.readString();
                logo=in.readString();
                type=in.readInt();
                parent_id=in.readInt();



            }
        }
    }

    public static class AlbumBean implements Parcelable{
        /**
         * id : 1812
         * type : 2
         * merchant_id : 4
         * artist_id : 0
         * author_id : 5
         * province : 43
         * city : 4301
         * county : 430104
         * title : 111111111111
         * des : 鞍山市所所所所
         * cover : http://sl0703.oss-cn-shenzhen.aliyuncs.com/Image/5.jpg
         * update_time : 2019-06-13 19:29:13
         * publish_time : null
         * publish_at : 2019-06-13 19:29:13
         * putaway : 1
         * status : 4
         * collection : 0
         * like : 0
         * view : 60
         * comment : 9
         * reprint : 0
         * deleted_at : null
         * created_at : 2019-06-13 19:29:13
         * updated_at : 2019-06-20 17:41:43
         * is_comment : 0
         * is_sub : 0
         * pivot : {"post_audio_id":1675,"post_album_id":1812}
         * albums : {"id":265,"post_id":1812,"count":3,"subscribe":0,"created_at":"2019-06-13 19:29:13","updated_at":"2019-06-15 16:12:11","status":0}
         * author_info : {"id":5,"merchant_id":4,"staff_name":"黄庚","user_id":1}
         * merchant : {"id":4,"name":"黄庚的机构勿删勿改","logo":"http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png","type":1,"tel":"","address":"","phone":"18207406897","email":"","remark":"","status":1,"expired_at":null,"deleted_at":null,"created_at":"2019-03-09 11:15:44","updated_at":"2019-04-19 10:45:19","from_id":0,"parent_id":0,"des":"黄庚的机构账户"}
         */

        private int id;
        private int type;
        private int merchant_id;
        private int artist_id;
        private int author_id;
        private String province;
        private String city;
        private String county;
        private String title;
        private String des;
        private String cover;
        private String update_time;
        private String publish_time;
        private String publish_at;
        private int putaway;
        private int status;
        private int collection;
        private int like;
        private int view;
        private int comment;
        private int reprint;
        private String deleted_at;
        private String created_at;
        private String updated_at;
        private int is_comment;
        private int is_sub;
        private PivotBean pivot;
        private AlbumsBean albums;
        private AuthorInfoBeanX author_info;
        private MerchantBeanX merchant;

        public AlbumBean(){

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(int merchant_id) {
            this.merchant_id = merchant_id;
        }

        public int getArtist_id() {
            return artist_id;
        }

        public void setArtist_id(int artist_id) {
            this.artist_id = artist_id;
        }

        public int getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(int author_id) {
            this.author_id = author_id;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }

        public String getPublish_at() {
            return publish_at;
        }

        public void setPublish_at(String publish_at) {
            this.publish_at = publish_at;
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

        public int getCollection() {
            return collection;
        }

        public void setCollection(int collection) {
            this.collection = collection;
        }

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }

        public int getComment() {
            return comment;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getReprint() {
            return reprint;
        }

        public void setReprint(int reprint) {
            this.reprint = reprint;
        }

        public String getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(String deleted_at) {
            this.deleted_at = deleted_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public int getIs_comment() {
            return is_comment;
        }

        public void setIs_comment(int is_comment) {
            this.is_comment = is_comment;
        }

        public int getIs_sub() {
            return is_sub;
        }

        public void setIs_sub(int is_sub) {
            this.is_sub = is_sub;
        }

        public PivotBean getPivot() {
            return pivot;
        }

        public void setPivot(PivotBean pivot) {
            this.pivot = pivot;
        }

        public AlbumsBean getAlbums() {
            return albums;
        }

        public void setAlbums(AlbumsBean albums) {
            this.albums = albums;
        }

        public AuthorInfoBeanX getAuthor_info() {
            return author_info;
        }

        public void setAuthor_info(AuthorInfoBeanX author_info) {
            this.author_info = author_info;
        }

        public MerchantBeanX getMerchant() {
            return merchant;
        }

        public void setMerchant(MerchantBeanX merchant) {
            this.merchant = merchant;
        }



        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {


            out.writeInt(id);
            out.writeInt(type);
            out.writeInt(merchant_id);
            out.writeInt(artist_id);
            out.writeInt(author_id);

            out.writeString(province);
            out.writeString(city);
            out.writeString(county);
            out.writeString(title);
            out.writeString(des);
            out.writeString(cover);

            out.writeString(update_time);
            out.writeString(publish_time);
            out.writeString(publish_at);
            out.writeInt(putaway);
            out.writeInt(status);
            out.writeInt(collection);

            out.writeInt(like);
            out.writeInt(view);
            out.writeInt(comment);
            out.writeInt(reprint);
            out.writeString(deleted_at);
            out.writeString(created_at);
            out.writeString(updated_at);
            out.writeInt(is_comment);
            out.writeInt(is_sub);

            out.writeParcelable(pivot,0);

            out.writeParcelable(albums,0);
            out.writeParcelable(author_info,0);
            out.writeParcelable(merchant,0);



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
            type=in.readInt();
            merchant_id=in.readInt();
            artist_id=in.readInt();
            author_id=in.readInt();

            province=in.readString();
            city=in.readString();
            county=in.readString();
            title=in.readString();
            des=in.readString();
            cover=in.readString();


            update_time=in.readString();
            publish_time=in.readString();
            publish_at=in.readString();
            putaway=in.readInt();
            status=in.readInt();
            collection=in.readInt();

            like=in.readInt();
            view=in.readInt();
            comment=in.readInt();
            reprint=in.readInt();
            deleted_at=in.readString();
            created_at=in.readString();
            updated_at=in.readString();
            is_comment=in.readInt();
            is_sub=in.readInt();

            pivot=in.readParcelable(Thread.currentThread().getContextClassLoader());
            albums=in.readParcelable(Thread.currentThread().getContextClassLoader());
            author_info=in.readParcelable(Thread.currentThread().getContextClassLoader());
            merchant=in.readParcelable(Thread.currentThread().getContextClassLoader());

        }


        public static class PivotBean implements Parcelable{
            /**
             * post_audio_id : 1675
             * post_album_id : 1812
             */

            private int post_audio_id;
            private int post_album_id;

            public PivotBean(){

            }

            public int getPost_audio_id() {
                return post_audio_id;
            }

            public void setPost_audio_id(int post_audio_id) {
                this.post_audio_id = post_audio_id;
            }

            public int getPost_album_id() {
                return post_album_id;
            }

            public void setPost_album_id(int post_album_id) {
                this.post_album_id = post_album_id;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel out, int flags) {



                out.writeInt(post_audio_id);
                out.writeInt(post_album_id);




            }


            public static final Parcelable.Creator<PivotBean> CREATOR = new Parcelable.Creator<PivotBean>() {
                public PivotBean createFromParcel(Parcel in) {
                    return new PivotBean(in);
                }

                public PivotBean[] newArray(int size) {
                    return new PivotBean[size];
                }
            };

            private PivotBean(Parcel in) {

                post_audio_id=in.readInt();
                post_album_id=in.readInt();




            }
        }

        public static class AlbumsBean implements Parcelable{
            /**
             * id : 265
             * post_id : 1812
             * count : 3
             * subscribe : 0
             * created_at : 2019-06-13 19:29:13
             * updated_at : 2019-06-15 16:12:11
             * status : 0
             */

            private int id;
            private int post_id;
            private int count;
            private int subscribe;
            private String created_at;
            private String updated_at;
            private int status;

            public AlbumsBean(){

            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPost_id() {
                return post_id;
            }

            public void setPost_id(int post_id) {
                this.post_id = post_id;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public int getSubscribe() {
                return subscribe;
            }

            public void setSubscribe(int subscribe) {
                this.subscribe = subscribe;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel out, int flags) {


                out.writeInt(id);
                out.writeInt(post_id);
                out.writeInt(count);
                out.writeInt(subscribe);
                out.writeString(created_at);
                out.writeString(updated_at);
                out.writeInt(status);



            }


            public static final Parcelable.Creator<AlbumsBean> CREATOR = new Parcelable.Creator<AlbumsBean>() {
                public AlbumsBean createFromParcel(Parcel in) {
                    return new AlbumsBean(in);
                }

                public AlbumsBean[] newArray(int size) {
                    return new AlbumsBean[size];
                }
            };

            private AlbumsBean(Parcel in) {


                id=in.readInt();
                post_id=in.readInt();
                count=in.readInt();
                subscribe=in.readInt();
                created_at=in.readString();
                updated_at=in.readString();
                status=in.readInt();



            }
        }

        public static class AuthorInfoBeanX implements Parcelable {
            /**
             * id : 5
             * merchant_id : 4
             * staff_name : 黄庚
             * user_id : 1
             */

            private int id;
            private int merchant_id;
            private String staff_name;
            private int user_id;

            public AuthorInfoBeanX(){

            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getMerchant_id() {
                return merchant_id;
            }

            public void setMerchant_id(int merchant_id) {
                this.merchant_id = merchant_id;
            }

            public String getStaff_name() {
                return staff_name;
            }

            public void setStaff_name(String staff_name) {
                this.staff_name = staff_name;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel out, int flags) {

                out.writeInt(id);
                out.writeInt(merchant_id);
                out.writeString(staff_name);
                out.writeInt(user_id);




            }


            public static final Parcelable.Creator<AuthorInfoBeanX> CREATOR = new Parcelable.Creator<AuthorInfoBeanX>() {
                public AuthorInfoBeanX createFromParcel(Parcel in) {
                    return new AuthorInfoBeanX(in);
                }

                public AuthorInfoBeanX[] newArray(int size) {
                    return new AuthorInfoBeanX[size];
                }
            };

            private AuthorInfoBeanX(Parcel in) {

                id=in.readInt();
                merchant_id=in.readInt();
                staff_name=in.readString();
                user_id=in.readInt();

            }
        }

        public static class MerchantBeanX implements Parcelable{
            /**
             * id : 4
             * name : 黄庚的机构勿删勿改
             * logo : http://sl-ucenter.static.slradio.cn/merchants/4/imges/s3neE6NZzbQ7Cc6kD7J9mFk64QAaT8kC1555641918048.png
             * type : 1
             * tel :
             * address :
             * phone : 18207406897
             * email :
             * remark :
             * status : 1
             * expired_at : null
             * deleted_at : null
             * created_at : 2019-03-09 11:15:44
             * updated_at : 2019-04-19 10:45:19
             * from_id : 0
             * parent_id : 0
             * des : 黄庚的机构账户
             */

            private int id;
            private String name;
            private String logo;
            private int type;
            private String tel;
            private String address;
            private String phone;
            private String email;
            private String remark;
            private int status;
            private String expired_at;
            private String deleted_at;
            private String created_at;
            private String updated_at;
            private int from_id;
            private int parent_id;
            private String des;


            public MerchantBeanX(){

            }
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

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getTel() {
                return tel;
            }

            public void setTel(String tel) {
                this.tel = tel;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getExpired_at() {
                return expired_at;
            }

            public void setExpired_at(String expired_at) {
                this.expired_at = expired_at;
            }

            public String getDeleted_at() {
                return deleted_at;
            }

            public void setDeleted_at(String deleted_at) {
                this.deleted_at = deleted_at;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }

            public int getFrom_id() {
                return from_id;
            }

            public void setFrom_id(int from_id) {
                this.from_id = from_id;
            }

            public int getParent_id() {
                return parent_id;
            }

            public void setParent_id(int parent_id) {
                this.parent_id = parent_id;
            }

            public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel out, int flags) {



                out.writeInt(id);
                out.writeString(name);
                out.writeString(logo);
                out.writeInt(type);

                out.writeString(tel);
                out.writeString(address);
                out.writeString(phone);
                out.writeString(email);
                out.writeString(remark);
                out.writeInt(status);
                out.writeString(expired_at);
                out.writeString(deleted_at);

                out.writeString(created_at);
                out.writeString(updated_at);
                out.writeInt(from_id);
                out.writeInt(parent_id);
                out.writeString(des);


            }


            public static final Parcelable.Creator<MerchantBeanX> CREATOR = new Parcelable.Creator<MerchantBeanX>() {
                public MerchantBeanX createFromParcel(Parcel in) {
                    return new MerchantBeanX(in);
                }

                public MerchantBeanX[] newArray(int size) {
                    return new MerchantBeanX[size];
                }
            };

            private MerchantBeanX(Parcel in) {

                id=in.readInt();
                name=in.readString();
                logo=in.readString();
                type=in.readInt();

                tel=in.readString();
                address=in.readString();
                phone=in.readString();
                email=in.readString();

                remark=in.readString();
                status=in.readInt();
                expired_at=in.readString();
                deleted_at=in.readString();

                created_at=in.readString();
                updated_at=in.readString();
                from_id=in.readInt();
                parent_id=in.readInt();
                des=in.readString();



            }
        }
    }
}
