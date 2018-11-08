package com.thdtek.acs.terminal.bean;

import java.util.List;

/**
 * Time:2018/10/12
 * User:lizhen
 * Description:
 */

public class ADBean {

    /**
     * msg : 成功
     * code : 0
     * data : [{"rotation_seconds":5,"adv_id":13,"adv_play_time":null,"video_urls":"[{\"video_first_image\": \"6018/adv/file/13_2018-10-12161919186000_QQ20181011-175238-HDmp4_first.jpeg\", \"video_url\": \"6018/adv/file/13_2018-10-12161919.186000_QQ20181011-175238-HD.mp4\"}]","urls":"6018/adv/file/13_2018-10-12161919186000_QQ20181011-175238-HDmp4_first.jpeg","type":1,"id":13},{"rotation_seconds":3,"adv_id":14,"adv_play_time":null,"video_urls":null,"urls":"6018/adv/file/14_2018-10-12162105.049000_0.jpg;6018/adv/file/14_2018-10-12162030.869000_sb.jpg","type":2,"id":14}]
     */

    private String msg;
    private int code;
    private List<DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * rotation_seconds : 5
         * adv_id : 13
         * adv_play_time : null
         * video_urls : [{"video_first_image": "6018/adv/file/13_2018-10-12161919186000_QQ20181011-175238-HDmp4_first.jpeg", "video_url": "6018/adv/file/13_2018-10-12161919.186000_QQ20181011-175238-HD.mp4"}]
         * urls : 6018/adv/file/13_2018-10-12161919186000_QQ20181011-175238-HDmp4_first.jpeg
         * type : 1
         * id : 13
         */

        private int rotation_seconds;
        private int adv_id;
        private Object adv_play_time;
        private String video_urls;
        private String urls;
        private int type;
        private int id;

        public int getRotation_seconds() {
            return rotation_seconds;
        }

        public void setRotation_seconds(int rotation_seconds) {
            this.rotation_seconds = rotation_seconds;
        }

        public int getAdv_id() {
            return adv_id;
        }

        public void setAdv_id(int adv_id) {
            this.adv_id = adv_id;
        }

        public Object getAdv_play_time() {
            return adv_play_time;
        }

        public void setAdv_play_time(Object adv_play_time) {
            this.adv_play_time = adv_play_time;
        }

        public String getVideo_urls() {
            return video_urls;
        }

        public void setVideo_urls(String video_urls) {
            this.video_urls = video_urls;
        }

        public String getUrls() {
            return urls;
        }

        public void setUrls(String urls) {
            this.urls = urls;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "rotation_seconds=" + rotation_seconds +
                    ", adv_id=" + adv_id +
                    ", adv_play_time=" + adv_play_time +
                    ", video_urls='" + video_urls + '\'' +
                    ", urls='" + urls + '\'' +
                    ", type=" + type +
                    ", id=" + id +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ADBean{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
