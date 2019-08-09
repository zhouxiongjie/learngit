package com.shuangling.software.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RadioSet implements Parcelable {


    /**
     * label : 排行榜
     * code : 0
     * list : [{"id":56,"province":"43","city":"4301","county":"430104","name":"法院之声","logo":"http://sl-cms.static.slradio.cn/platform/imges/wZtn1tF00pifY4W730zAWSDwKXYTjEiF1553493032102.jpg","des":"法院之声法院之声法院之声","stream":"http://www","view":63,"collection":5,"merchant_id":4,"schedule":{"id":4587,"channel_id":56,"name":"法院之声","type":1,"weekday":1,"date":"2019-06-17","created_at":"2019-06-16 23:00:01","updated_at":"2019-06-16 23:00:01","program":{"id":7317,"schedule_id":4587,"program_id":120,"start_time":"06:00","end_time":"23:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"今日说法"}}}]
     */

    public RadioSet(){

    }

    private String label;
    private int code;
    private List<Radio> list;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Radio> getList() {
        return list;
    }

    public void setList(List<Radio> list) {
        this.list = list;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(label);
        out.writeInt(code);
        out.writeTypedList(list);

    }


    public static final Parcelable.Creator<RadioSet> CREATOR = new Parcelable.Creator<RadioSet>() {
        public RadioSet createFromParcel(Parcel in) {
            return new RadioSet(in);
        }

        public RadioSet[] newArray(int size) {
            return new RadioSet[size];
        }
    };

    private RadioSet(Parcel in) {

        list=new ArrayList<>();
        label=in.readString();
        code=in.readInt();
        in.readTypedList(list,RadioSet.Radio.CREATOR);

    }

    public static class Radio implements Parcelable{
        /**
         * id : 56
         * province : 43
         * city : 4301
         * county : 430104
         * name : 法院之声
         * logo : http://sl-cms.static.slradio.cn/platform/imges/wZtn1tF00pifY4W730zAWSDwKXYTjEiF1553493032102.jpg
         * des : 法院之声法院之声法院之声
         * stream : http://www
         * view : 63
         * collection : 5
         * merchant_id : 4
         * schedule : {"id":4587,"channel_id":56,"name":"法院之声","type":1,"weekday":1,"date":"2019-06-17","created_at":"2019-06-16 23:00:01","updated_at":"2019-06-16 23:00:01","program":{"id":7317,"schedule_id":4587,"program_id":120,"start_time":"06:00","end_time":"23:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"今日说法"}}
         */

        private int id;
        private String province;
        private String city;
        private String type;
        private String county;
        private String name;
        private String logo;
        private String des;
        private String stream;
        private int view;
        private int collection;
        private int merchant_id;
        private Schedule schedule;

        public Radio(){

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
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

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }

        public int getCollection() {
            return collection;
        }

        public void setCollection(int collection) {
            this.collection = collection;
        }

        public int getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(int merchant_id) {
            this.merchant_id = merchant_id;
        }

        public Schedule getSchedule() {
            return schedule;
        }

        public void setSchedule(Schedule schedule) {
            this.schedule = schedule;
        }



        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {

            out.writeInt(id);
            out.writeString(province);
            out.writeString(city);
            out.writeString(type);
            out.writeString(county)
            ;
            out.writeString(name);
            out.writeString(logo);
            out.writeString(des);
            out.writeString(stream);

            out.writeInt(view);
            out.writeInt(collection);
            out.writeInt(merchant_id);

            out.writeParcelable(schedule,0);

        }


        public static final Parcelable.Creator<Radio> CREATOR = new Parcelable.Creator<Radio>() {
            public Radio createFromParcel(Parcel in) {
                return new Radio(in);
            }

            public Radio[] newArray(int size) {
                return new Radio[size];
            }
        };

        private Radio(Parcel in) {


            id=in.readInt();
            province=in.readString();
            city=in.readString();
            type=in.readString();
            county=in.readString();

            name=in.readString();
            logo=in.readString();
            des=in.readString();
            stream=in.readString();

            view=in.readInt();
            collection=in.readInt();
            merchant_id=in.readInt();

            schedule=in.readParcelable(Thread.currentThread().getContextClassLoader());
        }


        public static class Schedule implements Parcelable{
            /**
             * id : 4587
             * channel_id : 56
             * name : 法院之声
             * type : 1
             * weekday : 1
             * date : 2019-06-17
             * created_at : 2019-06-16 23:00:01
             * updated_at : 2019-06-16 23:00:01
             * program : {"id":7317,"schedule_id":4587,"program_id":120,"start_time":"06:00","end_time":"23:00","anchor_id":0,"anchor_name":"","created_at":"2019-06-16 23:00:02","updated_at":"2019-06-16 23:00:02","name":"今日说法"}
             */

            private int id;
            private int channel_id;
            private String name;
            private int type;
            private int weekday;
            private String date;
            private String created_at;
            private String updated_at;
            private Program program;


            public Schedule(){

            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getChannel_id() {
                return channel_id;
            }

            public void setChannel_id(int channel_id) {
                this.channel_id = channel_id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getWeekday() {
                return weekday;
            }

            public void setWeekday(int weekday) {
                this.weekday = weekday;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
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

            public Program getProgram() {
                return program;
            }

            public void setProgram(Program program) {
                this.program = program;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel out, int flags) {

                out.writeInt(id);
                out.writeInt(channel_id);
                out.writeString(name);
                out.writeInt(type)
                ;
                out.writeInt(weekday);
                out.writeString(date);
                out.writeString(created_at);
                out.writeString(updated_at);


                out.writeParcelable(program,0);

            }


            public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
                public Schedule createFromParcel(Parcel in) {
                    return new Schedule(in);
                }

                public Schedule[] newArray(int size) {
                    return new Schedule[size];
                }
            };

            private Schedule(Parcel in) {


                id=in.readInt();
                channel_id=in.readInt();
                name=in.readString();
                type=in.readInt();

                weekday=in.readInt();
                date=in.readString();
                created_at=in.readString();
                updated_at=in.readString();

                program=in.readParcelable(Thread.currentThread().getContextClassLoader());
            }

            public static class Program implements Parcelable{
                /**
                 * id : 7317
                 * schedule_id : 4587
                 * program_id : 120
                 * start_time : 06:00
                 * end_time : 23:00
                 * anchor_id : 0
                 * anchor_name :
                 * created_at : 2019-06-16 23:00:02
                 * updated_at : 2019-06-16 23:00:02
                 * name : 今日说法
                 */

                private int id;
                private int schedule_id;
                private int program_id;
                private String start_time;
                private String end_time;
                private int anchor_id;
                private String anchor_name;
                private String created_at;
                private String updated_at;
                private String name;

                public Program(){

                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getSchedule_id() {
                    return schedule_id;
                }

                public void setSchedule_id(int schedule_id) {
                    this.schedule_id = schedule_id;
                }

                public int getProgram_id() {
                    return program_id;
                }

                public void setProgram_id(int program_id) {
                    this.program_id = program_id;
                }

                public String getStart_time() {
                    return start_time;
                }

                public void setStart_time(String start_time) {
                    this.start_time = start_time;
                }

                public String getEnd_time() {
                    return end_time;
                }

                public void setEnd_time(String end_time) {
                    this.end_time = end_time;
                }

                public int getAnchor_id() {
                    return anchor_id;
                }

                public void setAnchor_id(int anchor_id) {
                    this.anchor_id = anchor_id;
                }

                public String getAnchor_name() {
                    return anchor_name;
                }

                public void setAnchor_name(String anchor_name) {
                    this.anchor_name = anchor_name;
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

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel out, int flags) {

                    out.writeInt(id);
                    out.writeInt(schedule_id);
                    out.writeInt(program_id);

                    out.writeString(start_time);
                    out.writeString(end_time);
                    out.writeInt(anchor_id);

                    out.writeString(anchor_name);
                    out.writeString(created_at);
                    out.writeString(updated_at);
                    out.writeString(name);

                }


                public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>() {
                    public Program createFromParcel(Parcel in) {
                        return new Program(in);
                    }

                    public Program[] newArray(int size) {
                        return new Program[size];
                    }
                };

                private Program(Parcel in) {


                    id=in.readInt();
                    schedule_id=in.readInt();
                    program_id=in.readInt();
                    start_time=in.readString();

                    end_time=in.readString();
                    anchor_id=in.readInt();
                    anchor_name=in.readString();
                    created_at=in.readString();
                    updated_at=in.readString();
                    name=in.readString();

                }
            }
        }
    }
}
