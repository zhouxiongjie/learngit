package com.shuangling.software.entity;


import java.io.Serializable;
import java.util.List;

public class Weather implements Serializable {


    /**
     * weather : {"HeWeather6":[{"basic":{"cid":"CN101250101","location":"长沙","parent_city":"长沙","admin_area":"湖南","cnty":"中国","lat":"28.19408989","lon":"112.98227692","tz":"+8.00"},"update":{"loc":"2019-07-01 11:27","utc":"2019-07-01 03:27"},"status":"ok","now":{"cloud":"91","cond_code":"101","cond_txt":"多云","fl":"32","hum":"79","pcpn":"0.0","pres":"997","tmp":"29","vis":"13","wind_deg":"307","wind_dir":"西北风","wind_sc":"2","wind_spd":"8"}}]}
     * air : {"HeWeather6":[{"basic":{"cid":"CN101250101","location":"长沙","parent_city":"长沙","admin_area":"湖南","cnty":"中国","lat":"28.19408989","lon":"112.98227692","tz":"+8.00"},"update":{"loc":"2019-07-01 11:15","utc":"2019-07-01 03:15"},"status":"ok","air_now_city":{"aqi":"49","qlty":"优","main":"-","pm25":"32","pm10":"49","no2":"15","so2":"7","co":"0.9","o3":"89","pub_time":"2019-07-01 10:00"},"air_now_station":[{"air_sta":"经开区环保局","aqi":"38","asid":"CNA1335","co":"0.5","lat":"28.2325","lon":"113.0833","main":"-","no2":"15","o3":"101","pm10":"38","pm25":"22","pub_time":"2019-07-01 10:00","qlty":"优","so2":"8"},{"air_sta":"高开区环保局","aqi":"64","asid":"CNA1336","co":"0.7","lat":"28.2189","lon":"112.8872","main":"PM2.5","no2":"10","o3":"75","pm10":"71","pm25":"46","pub_time":"2019-07-01 10:00","qlty":"良","so2":"7"},{"air_sta":"马坡岭","aqi":"44","asid":"CNA1337","co":"0.8","lat":"28.2053","lon":"113.0792","main":"-","no2":"16","o3":"103","pm10":"44","pm25":"25","pub_time":"2019-07-01 10:00","qlty":"优","so2":"7"},{"air_sta":"湖南师范大学","aqi":"64","asid":"CNA1338","co":"0.9","lat":"28.19","lon":"112.9394","main":"PM2.5","no2":"9","o3":"86","pm10":"8","pm25":"46","pub_time":"2019-07-01 10:00","qlty":"良","so2":"6"},{"air_sta":"雨花区环保局","aqi":"50","asid":"CNA1339","co":"1.0","lat":"28.1442","lon":"112.9956","main":"-","no2":"16","o3":"98","pm10":"50","pm25":"23","pub_time":"2019-07-01 10:00","qlty":"优","so2":"7"},{"air_sta":"伍家岭","aqi":"57","asid":"CNA1340","co":"1.2","lat":"28.2597","lon":"112.9792","main":"PM10","no2":"18","o3":"76","pm10":"64","pm25":"33","pub_time":"2019-07-01 10:00","qlty":"良","so2":"6"},{"air_sta":"火车新站","aqi":"58","asid":"CNA1341","co":"1.0","lat":"28.1944","lon":"113.0014","main":"PM2.5","no2":"20","o3":"74","pm10":"53","pm25":"41","pub_time":"2019-07-01 10:00","qlty":"良","so2":"9"},{"air_sta":"天心区环保局","aqi":"41","asid":"CNA1342","co":"0.6","lat":"28.1178","lon":"112.9844","main":"-","no2":"20","o3":"96","pm10":"41","pm25":"24","pub_time":"2019-07-01 10:00","qlty":"优","so2":"5"},{"air_sta":"湖南中医药大学","aqi":"55","asid":"CNA1343","co":"0.9","lat":"28.1308","lon":"112.8908","main":"PM2.5","no2":"13","o3":"89","pm10":"57","pm25":"39","pub_time":"2019-07-01 10:00","qlty":"良","so2":"6"},{"air_sta":"沙坪","aqi":"56","asid":"CNA1344","co":"0.8","lat":"28.3586","lon":"112.9958","main":"PM10","no2":"5","o3":"88","pm10":"62","pm25":"18","pub_time":"2019-07-01 10:00","qlty":"良","so2":"8"}]}]}
     */

    private WeatherBean weather;
    private AirBean air;

    public WeatherBean getWeather() {
        return weather;
    }

    public void setWeather(WeatherBean weather) {
        this.weather = weather;
    }

    public AirBean getAir() {
        return air;
    }

    public void setAir(AirBean air) {
        this.air = air;
    }

    public static class WeatherBean {
        private List<HeWeather6Bean> HeWeather6;

        public List<HeWeather6Bean> getHeWeather6() {
            return HeWeather6;
        }

        public void setHeWeather6(List<HeWeather6Bean> HeWeather6) {
            this.HeWeather6 = HeWeather6;
        }

        public static class HeWeather6Bean {
            /**
             * basic : {"cid":"CN101250101","location":"长沙","parent_city":"长沙","admin_area":"湖南","cnty":"中国","lat":"28.19408989","lon":"112.98227692","tz":"+8.00"}
             * update : {"loc":"2019-07-01 11:27","utc":"2019-07-01 03:27"}
             * status : ok
             * now : {"cloud":"91","cond_code":"101","cond_txt":"多云","fl":"32","hum":"79","pcpn":"0.0","pres":"997","tmp":"29","vis":"13","wind_deg":"307","wind_dir":"西北风","wind_sc":"2","wind_spd":"8"}
             */

            private BasicBean basic;
            private UpdateBean update;
            private String status;
            private NowBean now;

            public BasicBean getBasic() {
                return basic;
            }

            public void setBasic(BasicBean basic) {
                this.basic = basic;
            }

            public UpdateBean getUpdate() {
                return update;
            }

            public void setUpdate(UpdateBean update) {
                this.update = update;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public NowBean getNow() {
                return now;
            }

            public void setNow(NowBean now) {
                this.now = now;
            }

            public static class BasicBean {
                /**
                 * cid : CN101250101
                 * location : 长沙
                 * parent_city : 长沙
                 * admin_area : 湖南
                 * cnty : 中国
                 * lat : 28.19408989
                 * lon : 112.98227692
                 * tz : +8.00
                 */

                private String cid;
                private String location;
                private String parent_city;
                private String admin_area;
                private String cnty;
                private String lat;
                private String lon;
                private String tz;

                public String getCid() {
                    return cid;
                }

                public void setCid(String cid) {
                    this.cid = cid;
                }

                public String getLocation() {
                    return location;
                }

                public void setLocation(String location) {
                    this.location = location;
                }

                public String getParent_city() {
                    return parent_city;
                }

                public void setParent_city(String parent_city) {
                    this.parent_city = parent_city;
                }

                public String getAdmin_area() {
                    return admin_area;
                }

                public void setAdmin_area(String admin_area) {
                    this.admin_area = admin_area;
                }

                public String getCnty() {
                    return cnty;
                }

                public void setCnty(String cnty) {
                    this.cnty = cnty;
                }

                public String getLat() {
                    return lat;
                }

                public void setLat(String lat) {
                    this.lat = lat;
                }

                public String getLon() {
                    return lon;
                }

                public void setLon(String lon) {
                    this.lon = lon;
                }

                public String getTz() {
                    return tz;
                }

                public void setTz(String tz) {
                    this.tz = tz;
                }
            }

            public static class UpdateBean {
                /**
                 * loc : 2019-07-01 11:27
                 * utc : 2019-07-01 03:27
                 */

                private String loc;
                private String utc;

                public String getLoc() {
                    return loc;
                }

                public void setLoc(String loc) {
                    this.loc = loc;
                }

                public String getUtc() {
                    return utc;
                }

                public void setUtc(String utc) {
                    this.utc = utc;
                }
            }

            public static class NowBean {
                /**
                 * cloud : 91
                 * cond_code : 101
                 * cond_txt : 多云
                 * fl : 32
                 * hum : 79
                 * pcpn : 0.0
                 * pres : 997
                 * tmp : 29
                 * vis : 13
                 * wind_deg : 307
                 * wind_dir : 西北风
                 * wind_sc : 2
                 * wind_spd : 8
                 */

                private String cloud;
                private String cond_code;
                private String cond_txt;
                private String fl;
                private String hum;
                private String pcpn;
                private String pres;
                private String tmp;
                private String vis;
                private String wind_deg;
                private String wind_dir;
                private String wind_sc;
                private String wind_spd;

                public String getCloud() {
                    return cloud;
                }

                public void setCloud(String cloud) {
                    this.cloud = cloud;
                }

                public String getCond_code() {
                    return cond_code;
                }

                public void setCond_code(String cond_code) {
                    this.cond_code = cond_code;
                }

                public String getCond_txt() {
                    return cond_txt;
                }

                public void setCond_txt(String cond_txt) {
                    this.cond_txt = cond_txt;
                }

                public String getFl() {
                    return fl;
                }

                public void setFl(String fl) {
                    this.fl = fl;
                }

                public String getHum() {
                    return hum;
                }

                public void setHum(String hum) {
                    this.hum = hum;
                }

                public String getPcpn() {
                    return pcpn;
                }

                public void setPcpn(String pcpn) {
                    this.pcpn = pcpn;
                }

                public String getPres() {
                    return pres;
                }

                public void setPres(String pres) {
                    this.pres = pres;
                }

                public String getTmp() {
                    return tmp;
                }

                public void setTmp(String tmp) {
                    this.tmp = tmp;
                }

                public String getVis() {
                    return vis;
                }

                public void setVis(String vis) {
                    this.vis = vis;
                }

                public String getWind_deg() {
                    return wind_deg;
                }

                public void setWind_deg(String wind_deg) {
                    this.wind_deg = wind_deg;
                }

                public String getWind_dir() {
                    return wind_dir;
                }

                public void setWind_dir(String wind_dir) {
                    this.wind_dir = wind_dir;
                }

                public String getWind_sc() {
                    return wind_sc;
                }

                public void setWind_sc(String wind_sc) {
                    this.wind_sc = wind_sc;
                }

                public String getWind_spd() {
                    return wind_spd;
                }

                public void setWind_spd(String wind_spd) {
                    this.wind_spd = wind_spd;
                }
            }
        }
    }

    public static class AirBean {
        private List<HeWeather6BeanX> HeWeather6;

        public List<HeWeather6BeanX> getHeWeather6() {
            return HeWeather6;
        }

        public void setHeWeather6(List<HeWeather6BeanX> HeWeather6) {
            this.HeWeather6 = HeWeather6;
        }

        public static class HeWeather6BeanX {
            /**
             * basic : {"cid":"CN101250101","location":"长沙","parent_city":"长沙","admin_area":"湖南","cnty":"中国","lat":"28.19408989","lon":"112.98227692","tz":"+8.00"}
             * update : {"loc":"2019-07-01 11:15","utc":"2019-07-01 03:15"}
             * status : ok
             * air_now_city : {"aqi":"49","qlty":"优","main":"-","pm25":"32","pm10":"49","no2":"15","so2":"7","co":"0.9","o3":"89","pub_time":"2019-07-01 10:00"}
             * air_now_station : [{"air_sta":"经开区环保局","aqi":"38","asid":"CNA1335","co":"0.5","lat":"28.2325","lon":"113.0833","main":"-","no2":"15","o3":"101","pm10":"38","pm25":"22","pub_time":"2019-07-01 10:00","qlty":"优","so2":"8"},{"air_sta":"高开区环保局","aqi":"64","asid":"CNA1336","co":"0.7","lat":"28.2189","lon":"112.8872","main":"PM2.5","no2":"10","o3":"75","pm10":"71","pm25":"46","pub_time":"2019-07-01 10:00","qlty":"良","so2":"7"},{"air_sta":"马坡岭","aqi":"44","asid":"CNA1337","co":"0.8","lat":"28.2053","lon":"113.0792","main":"-","no2":"16","o3":"103","pm10":"44","pm25":"25","pub_time":"2019-07-01 10:00","qlty":"优","so2":"7"},{"air_sta":"湖南师范大学","aqi":"64","asid":"CNA1338","co":"0.9","lat":"28.19","lon":"112.9394","main":"PM2.5","no2":"9","o3":"86","pm10":"8","pm25":"46","pub_time":"2019-07-01 10:00","qlty":"良","so2":"6"},{"air_sta":"雨花区环保局","aqi":"50","asid":"CNA1339","co":"1.0","lat":"28.1442","lon":"112.9956","main":"-","no2":"16","o3":"98","pm10":"50","pm25":"23","pub_time":"2019-07-01 10:00","qlty":"优","so2":"7"},{"air_sta":"伍家岭","aqi":"57","asid":"CNA1340","co":"1.2","lat":"28.2597","lon":"112.9792","main":"PM10","no2":"18","o3":"76","pm10":"64","pm25":"33","pub_time":"2019-07-01 10:00","qlty":"良","so2":"6"},{"air_sta":"火车新站","aqi":"58","asid":"CNA1341","co":"1.0","lat":"28.1944","lon":"113.0014","main":"PM2.5","no2":"20","o3":"74","pm10":"53","pm25":"41","pub_time":"2019-07-01 10:00","qlty":"良","so2":"9"},{"air_sta":"天心区环保局","aqi":"41","asid":"CNA1342","co":"0.6","lat":"28.1178","lon":"112.9844","main":"-","no2":"20","o3":"96","pm10":"41","pm25":"24","pub_time":"2019-07-01 10:00","qlty":"优","so2":"5"},{"air_sta":"湖南中医药大学","aqi":"55","asid":"CNA1343","co":"0.9","lat":"28.1308","lon":"112.8908","main":"PM2.5","no2":"13","o3":"89","pm10":"57","pm25":"39","pub_time":"2019-07-01 10:00","qlty":"良","so2":"6"},{"air_sta":"沙坪","aqi":"56","asid":"CNA1344","co":"0.8","lat":"28.3586","lon":"112.9958","main":"PM10","no2":"5","o3":"88","pm10":"62","pm25":"18","pub_time":"2019-07-01 10:00","qlty":"良","so2":"8"}]
             */

            private BasicBeanX basic;
            private UpdateBeanX update;
            private String status;
            private AirNowCityBean air_now_city;
            private List<AirNowStationBean> air_now_station;

            public BasicBeanX getBasic() {
                return basic;
            }

            public void setBasic(BasicBeanX basic) {
                this.basic = basic;
            }

            public UpdateBeanX getUpdate() {
                return update;
            }

            public void setUpdate(UpdateBeanX update) {
                this.update = update;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public AirNowCityBean getAir_now_city() {
                return air_now_city;
            }

            public void setAir_now_city(AirNowCityBean air_now_city) {
                this.air_now_city = air_now_city;
            }

            public List<AirNowStationBean> getAir_now_station() {
                return air_now_station;
            }

            public void setAir_now_station(List<AirNowStationBean> air_now_station) {
                this.air_now_station = air_now_station;
            }

            public static class BasicBeanX {
                /**
                 * cid : CN101250101
                 * location : 长沙
                 * parent_city : 长沙
                 * admin_area : 湖南
                 * cnty : 中国
                 * lat : 28.19408989
                 * lon : 112.98227692
                 * tz : +8.00
                 */

                private String cid;
                private String location;
                private String parent_city;
                private String admin_area;
                private String cnty;
                private String lat;
                private String lon;
                private String tz;

                public String getCid() {
                    return cid;
                }

                public void setCid(String cid) {
                    this.cid = cid;
                }

                public String getLocation() {
                    return location;
                }

                public void setLocation(String location) {
                    this.location = location;
                }

                public String getParent_city() {
                    return parent_city;
                }

                public void setParent_city(String parent_city) {
                    this.parent_city = parent_city;
                }

                public String getAdmin_area() {
                    return admin_area;
                }

                public void setAdmin_area(String admin_area) {
                    this.admin_area = admin_area;
                }

                public String getCnty() {
                    return cnty;
                }

                public void setCnty(String cnty) {
                    this.cnty = cnty;
                }

                public String getLat() {
                    return lat;
                }

                public void setLat(String lat) {
                    this.lat = lat;
                }

                public String getLon() {
                    return lon;
                }

                public void setLon(String lon) {
                    this.lon = lon;
                }

                public String getTz() {
                    return tz;
                }

                public void setTz(String tz) {
                    this.tz = tz;
                }
            }

            public static class UpdateBeanX {
                /**
                 * loc : 2019-07-01 11:15
                 * utc : 2019-07-01 03:15
                 */

                private String loc;
                private String utc;

                public String getLoc() {
                    return loc;
                }

                public void setLoc(String loc) {
                    this.loc = loc;
                }

                public String getUtc() {
                    return utc;
                }

                public void setUtc(String utc) {
                    this.utc = utc;
                }
            }

            public static class AirNowCityBean {
                /**
                 * aqi : 49
                 * qlty : 优
                 * main : -
                 * pm25 : 32
                 * pm10 : 49
                 * no2 : 15
                 * so2 : 7
                 * co : 0.9
                 * o3 : 89
                 * pub_time : 2019-07-01 10:00
                 */

                private String aqi;
                private String qlty;
                private String main;
                private String pm25;
                private String pm10;
                private String no2;
                private String so2;
                private String co;
                private String o3;
                private String pub_time;

                public String getAqi() {
                    return aqi;
                }

                public void setAqi(String aqi) {
                    this.aqi = aqi;
                }

                public String getQlty() {
                    return qlty;
                }

                public void setQlty(String qlty) {
                    this.qlty = qlty;
                }

                public String getMain() {
                    return main;
                }

                public void setMain(String main) {
                    this.main = main;
                }

                public String getPm25() {
                    return pm25;
                }

                public void setPm25(String pm25) {
                    this.pm25 = pm25;
                }

                public String getPm10() {
                    return pm10;
                }

                public void setPm10(String pm10) {
                    this.pm10 = pm10;
                }

                public String getNo2() {
                    return no2;
                }

                public void setNo2(String no2) {
                    this.no2 = no2;
                }

                public String getSo2() {
                    return so2;
                }

                public void setSo2(String so2) {
                    this.so2 = so2;
                }

                public String getCo() {
                    return co;
                }

                public void setCo(String co) {
                    this.co = co;
                }

                public String getO3() {
                    return o3;
                }

                public void setO3(String o3) {
                    this.o3 = o3;
                }

                public String getPub_time() {
                    return pub_time;
                }

                public void setPub_time(String pub_time) {
                    this.pub_time = pub_time;
                }
            }

            public static class AirNowStationBean {
                /**
                 * air_sta : 经开区环保局
                 * aqi : 38
                 * asid : CNA1335
                 * co : 0.5
                 * lat : 28.2325
                 * lon : 113.0833
                 * main : -
                 * no2 : 15
                 * o3 : 101
                 * pm10 : 38
                 * pm25 : 22
                 * pub_time : 2019-07-01 10:00
                 * qlty : 优
                 * so2 : 8
                 */

                private String air_sta;
                private String aqi;
                private String asid;
                private String co;
                private String lat;
                private String lon;
                private String main;
                private String no2;
                private String o3;
                private String pm10;
                private String pm25;
                private String pub_time;
                private String qlty;
                private String so2;

                public String getAir_sta() {
                    return air_sta;
                }

                public void setAir_sta(String air_sta) {
                    this.air_sta = air_sta;
                }

                public String getAqi() {
                    return aqi;
                }

                public void setAqi(String aqi) {
                    this.aqi = aqi;
                }

                public String getAsid() {
                    return asid;
                }

                public void setAsid(String asid) {
                    this.asid = asid;
                }

                public String getCo() {
                    return co;
                }

                public void setCo(String co) {
                    this.co = co;
                }

                public String getLat() {
                    return lat;
                }

                public void setLat(String lat) {
                    this.lat = lat;
                }

                public String getLon() {
                    return lon;
                }

                public void setLon(String lon) {
                    this.lon = lon;
                }

                public String getMain() {
                    return main;
                }

                public void setMain(String main) {
                    this.main = main;
                }

                public String getNo2() {
                    return no2;
                }

                public void setNo2(String no2) {
                    this.no2 = no2;
                }

                public String getO3() {
                    return o3;
                }

                public void setO3(String o3) {
                    this.o3 = o3;
                }

                public String getPm10() {
                    return pm10;
                }

                public void setPm10(String pm10) {
                    this.pm10 = pm10;
                }

                public String getPm25() {
                    return pm25;
                }

                public void setPm25(String pm25) {
                    this.pm25 = pm25;
                }

                public String getPub_time() {
                    return pub_time;
                }

                public void setPub_time(String pub_time) {
                    this.pub_time = pub_time;
                }

                public String getQlty() {
                    return qlty;
                }

                public void setQlty(String qlty) {
                    this.qlty = qlty;
                }

                public String getSo2() {
                    return so2;
                }

                public void setSo2(String so2) {
                    this.so2 = so2;
                }
            }
        }
    }
}