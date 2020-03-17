package com.shuangling.software.entity;

import java.util.List;

public class ArticleVoicesInfo {


    /**
     * id : 3
     * post_id : 15126
     * version : 2.0
     * voices : [{"id":10,"voice_id":3,"sound_id":1,"sound":{"id":1,"type":2,"name":"度小美"},"audio":[{"id":11,"ars_id":10,"video_id":"3b37494d50264ad683dfe5eec07301d7"},{"id":12,"ars_id":10,"video_id":"c5376b5dac704587b0859bc21b4f618e"}]},{"id":11,"voice_id":3,"sound_id":2,"sound":{"id":2,"type":1,"name":"度小宇"},"audio":[{"id":13,"ars_id":11,"video_id":"ad9e6ab4c7ff4eea87b5a57545862391"},{"id":14,"ars_id":11,"video_id":"bb8d55d66106440f884c1a57d64b159b"}]}]
     */

    private int id;
    private int post_id;
    private String version;
    private List<VoicesBean> voices;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<VoicesBean> getVoices() {
        return voices;
    }

    public void setVoices(List<VoicesBean> voices) {
        this.voices = voices;
    }

    public static class VoicesBean {
        /**
         * id : 10
         * voice_id : 3
         * sound_id : 1
         * sound : {"id":1,"type":2,"name":"度小美"}
         * audio : [{"id":11,"ars_id":10,"video_id":"3b37494d50264ad683dfe5eec07301d7"},{"id":12,"ars_id":10,"video_id":"c5376b5dac704587b0859bc21b4f618e"}]
         */

        private int id;
        private int voice_id;
        private int sound_id;
        private SoundBean sound;
        private List<AudioBean> audio;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getVoice_id() {
            return voice_id;
        }

        public void setVoice_id(int voice_id) {
            this.voice_id = voice_id;
        }

        public int getSound_id() {
            return sound_id;
        }

        public void setSound_id(int sound_id) {
            this.sound_id = sound_id;
        }

        public SoundBean getSound() {
            return sound;
        }

        public void setSound(SoundBean sound) {
            this.sound = sound;
        }

        public List<AudioBean> getAudio() {
            return audio;
        }

        public void setAudio(List<AudioBean> audio) {
            this.audio = audio;
        }

        public static class SoundBean {
            /**
             * id : 1
             * type : 2
             * name : 度小美
             */

            private int id;
            private int type;
            private String name;

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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class AudioBean {
            /**
             * id : 11
             * ars_id : 10
             * video_id : 3b37494d50264ad683dfe5eec07301d7
             */

            private int id;
            private int ars_id;
            private String video_id;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getArs_id() {
                return ars_id;
            }

            public void setArs_id(int ars_id) {
                this.ars_id = ars_id;
            }

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }
        }
    }
}
