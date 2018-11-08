package com.thdtek.acs.terminal.bean;

import java.util.List;

/**
 * Time:2018/10/12
 * User:lizhen
 * Description:
 */

public class VideoBean {

    /**
     * video_first_image : 6018/adv/file/13_2018-10-12161919186000_QQ20181011-175238-HDmp4_first.jpeg
     * video_url : 6018/adv/file/13_2018-10-12161919.186000_QQ20181011-175238-HD.mp4
     */

    private String video_first_image;
    private String video_url;

    public String getVideo_first_image() {
        return video_first_image;
    }

    public void setVideo_first_image(String video_first_image) {
        this.video_first_image = video_first_image;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
