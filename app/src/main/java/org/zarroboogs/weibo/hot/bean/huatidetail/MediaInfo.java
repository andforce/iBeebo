package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class MediaInfo {

    private String h5Url;
    private String streamUrl;
    private String name;
    private String streamUrlHd;
    private double duration;


    public MediaInfo() {

    }

    public MediaInfo(JSONObject json) {

        this.h5Url = json.optString("h5_url");
        this.streamUrl = json.optString("stream_url");
        this.name = json.optString("name");
        this.streamUrlHd = json.optString("stream_url_hd");
        this.duration = json.optDouble("duration");

    }

    public String getH5Url() {
        return this.h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public String getStreamUrl() {
        return this.streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreamUrlHd() {
        return this.streamUrlHd;
    }

    public void setStreamUrlHd(String streamUrlHd) {
        this.streamUrlHd = streamUrlHd;
    }

    public double getDuration() {
        return this.duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }


}
