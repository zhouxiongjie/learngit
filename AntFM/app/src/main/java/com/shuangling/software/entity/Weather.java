package com.shuangling.software.entity;

import java.io.Serializable;

public class Weather implements Serializable {

    /**
     * weather : {"province":"湖南","city":"长沙市","adcode":"430100","weather":"阴","temperature":"13","winddirection":"西","windpower":"≤3","humidity":"94","reporttime":"2021-03-18 16:02:06"}
     */

    private WeatherBean weather;

    public WeatherBean getWeather() {
        return weather;
    }

    public void setWeather(WeatherBean weather) {
        this.weather = weather;
    }

    public static class WeatherBean {
        /**
         * province : 湖南
         * city : 长沙市
         * adcode : 430100
         * weather : 阴
         * temperature : 13
         * winddirection : 西
         * windpower : ≤3
         * humidity : 94
         * reporttime : 2021-03-18 16:02:06
         */

        private String province;
        private String city;
        private String adcode;
        private String weather;
        private String temperature;
        private String winddirection;
        private String windpower;
        private String humidity;
        private String reporttime;

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

        public String getAdcode() {
            return adcode;
        }

        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getWinddirection() {
            return winddirection;
        }

        public void setWinddirection(String winddirection) {
            this.winddirection = winddirection;
        }

        public String getWindpower() {
            return windpower;
        }

        public void setWindpower(String windpower) {
            this.windpower = windpower;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public String getReporttime() {
            return reporttime;
        }

        public void setReporttime(String reporttime) {
            this.reporttime = reporttime;
        }
    }
}