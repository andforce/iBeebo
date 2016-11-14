package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Largest {

    private String height;
    private String type;
    private String url;
    private String width;
    private double cutType;


    public Largest() {

    }

    public Largest(JSONObject json) {

        this.height = json.optString("height");
        this.type = json.optString("type");
        this.url = json.optString("url");
        this.width = json.optString("width");
        this.cutType = json.optDouble("cut_type");

    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public double getCutType() {
        return this.cutType;
    }

    public void setCutType(double cutType) {
        this.cutType = cutType;
    }


}
