package com.shuangling.software.entity;

import java.io.Serializable;

public class AudioOrVideoAuthInfo implements Serializable {


    /**
     * RequestId : 028BC1FC-6260-49FD-8035-BE27BDB6E400
     * VideoMeta : {"CoverURL":"http://vms.slradio.cn/29de532928744c4c9fb8578b50fb56a2/snapshots/5b13575143cf4b9bba9eec88fb2f7cc0-00001.jpg","Status":"Normal","VideoId":"29de532928744c4c9fb8578b50fb56a2","Duration":290.7690124511719,"Title":"34c27737dac2f795864758c9c2d6efb5.mp3"}
     * PlayAuth : eyJTZWN1cml0eVRva2VuIjoiQ0FJUzN3SjFxNkZ0NUIyeWZTaklyNG5HQ1BEMW5hbG14WmlFUjJMYXB6VU5mTnBlMll2WWxqejJJSGxQZTNGaEFPb2V2L2svbVc5VTdmb2Nsck1xRnNVY0doT2JQWnNzdGNVT29GNzdKcExGc3QySjZyOEpqc1ZueGVCMjlWdXBzdlhKYXNEVkVma3VFNVhFTWlJNS8wMGU2TC8rY2lyWVhEN0JHSmFWaUpsaFE4MEtWdzJqRjFSdkQ4dFhJUTBRazYxOUszemRaOW1nTGlidWkzdnhDa1J2MkhCaWptOHR4cW1qL015UTV4MzFpMXYweStCM3dZSHRPY3FjYThCOU1ZMVdUc3Uxdm9oemFyR1Q2Q3BaK2psTStxQVU2cWxZNG1YcnM5cUhFa0ZOd0JpWFNaMjJsT2RpTndoa2ZLTTNOcmRacGZ6bjc1MUN0L2ZVaXA3OHhtUW1YNGdYY1Z5R0d0RHhrWk9aUXJ6emJZNWhLK2lnQVJtWGpJRFRiS3VTbWhnL2ZIY1dPRGxOZjljY01YSnFBWFF1TUdxRGNmRC9xUW1RT2xiK0cvWGFqUHBxajRBSjVsSHA3TWVNR1YrRGVMeVF5aDBFSWFVN2EwNDRxTDZvYnQ4WG1zUWFnQUVUV3hoZHlKeWN4cWZYMFMrZEdOckt1RGRhRkk5a0dZcUd2MDRpVTRSeDV2RmlXbDhhRHRBaC8wTUZQU1g0UGdFSUxmNUVXNEJzWk85eFVLUHlnOHVqa1VaNUhKUDlEOGxCMWdvZU5xODAyMU1leHVRSXVzTWxSWkxzNEFjNHlTSVlkVHB4cUxBbWNFVXhLcGNidFEya21Nd2k1bHdnbEVTR1BYdU91SXhSU3c9PSIsIkF1dGhJbmZvIjoie1wiQ2FsbGVyXCI6XCI1VnZKRHZxL3dWZjk2ZTZPMXN0dGUzYmgzNXBJWGhmeE1SSW5PRVIwZ3ZBPVxcclxcblwiLFwiRXhwaXJlVGltZVwiOlwiMjAxOS0wMy0wOVQwODoxMjoyOVpcIixcIk1lZGlhSWRcIjpcIjI5ZGU1MzI5Mjg3NDRjNGM5ZmI4NTc4YjUwZmI1NmEyXCIsXCJQbGF5RG9tYWluXCI6XCJ2bXMuc2xyYWRpby5jblwiLFwiU2lnbmF0dXJlXCI6XCIydUFDTnkzRnFsTHo4RjdlK2ZlOE95OFdkL2M9XCJ9IiwiVmlkZW9NZXRhIjp7IlN0YXR1cyI6Ik5vcm1hbCIsIlZpZGVvSWQiOiIyOWRlNTMyOTI4NzQ0YzRjOWZiODU3OGI1MGZiNTZhMiIsIlRpdGxlIjoiMzRjMjc3MzdkYWMyZjc5NTg2NDc1OGM5YzJkNmVmYjUubXAzIiwiQ292ZXJVUkwiOiJodHRwOi8vdm1zLnNscmFkaW8uY24vMjlkZTUzMjkyODc0NGM0YzlmYjg1NzhiNTBmYjU2YTIvc25hcHNob3RzLzViMTM1NzUxNDNjZjRiOWJiYTllZWM4OGZiMmY3Y2MwLTAwMDAxLmpwZyIsIkR1cmF0aW9uIjoyOTAuNzY5fSwiQWNjZXNzS2V5SWQiOiJTVFMuTkpzQ0pBcHZHclpvRURrQzVYcFVyNk5zdCIsIlBsYXlEb21haW4iOiJ2bXMuc2xyYWRpby5jbiIsIkFjY2Vzc0tleVNlY3JldCI6IkVTYVZ4cThBWWU1N3VKTkJ6Mm1kWndnNTlwanFDb3ZNZGtwTmVSU2lqejk2IiwiUmVnaW9uIjoiY24tc2hhbmdoYWkiLCJDdXN0b21lcklkIjoxNTIxNzc3ODg0MzkxOTUyfQ==
     */

    private String RequestId;
    private VideoMetaBean VideoMeta;
    private String PlayAuth;

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String RequestId) {
        this.RequestId = RequestId;
    }

    public VideoMetaBean getVideoMeta() {
        return VideoMeta;
    }

    public void setVideoMeta(VideoMetaBean VideoMeta) {
        this.VideoMeta = VideoMeta;
    }

    public String getPlayAuth() {
        return PlayAuth;
    }

    public void setPlayAuth(String PlayAuth) {
        this.PlayAuth = PlayAuth;
    }

    public static class VideoMetaBean {
        /**
         * CoverURL : http://vms.slradio.cn/29de532928744c4c9fb8578b50fb56a2/snapshots/5b13575143cf4b9bba9eec88fb2f7cc0-00001.jpg
         * Status : Normal
         * VideoId : 29de532928744c4c9fb8578b50fb56a2
         * Duration : 290.7690124511719
         * Title : 34c27737dac2f795864758c9c2d6efb5.mp3
         */

        private String CoverURL;
        private String Status;
        private String VideoId;
        private double Duration;
        private String Title;

        public String getCoverURL() {
            return CoverURL;
        }

        public void setCoverURL(String CoverURL) {
            this.CoverURL = CoverURL;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String Status) {
            this.Status = Status;
        }

        public String getVideoId() {
            return VideoId;
        }

        public void setVideoId(String VideoId) {
            this.VideoId = VideoId;
        }

        public double getDuration() {
            return Duration;
        }

        public void setDuration(double Duration) {
            this.Duration = Duration;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }
    }
}
