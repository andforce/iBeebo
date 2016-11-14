package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class UrlStruct {

    private boolean result;
    private String pageId;
    private double hide;
    private String oriUrl;
    private double urlType;
    private String urlTitle;
    private String urlTypePic;
    private String log;
    private String shortUrl;


    public UrlStruct() {

    }

    public UrlStruct(JSONObject json) {

        this.result = json.optBoolean("result");
        this.pageId = json.optString("page_id");
        this.hide = json.optDouble("hide");
        this.oriUrl = json.optString("ori_url");
        this.urlType = json.optDouble("url_type");
        this.urlTitle = json.optString("url_title");
        this.urlTypePic = json.optString("url_type_pic");
        this.log = json.optString("log");
        this.shortUrl = json.optString("short_url");

    }

    public boolean getResult() {
        return this.result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getPageId() {
        return this.pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public double getHide() {
        return this.hide;
    }

    public void setHide(double hide) {
        this.hide = hide;
    }

    public String getOriUrl() {
        return this.oriUrl;
    }

    public void setOriUrl(String oriUrl) {
        this.oriUrl = oriUrl;
    }

    public double getUrlType() {
        return this.urlType;
    }

    public void setUrlType(double urlType) {
        this.urlType = urlType;
    }

    public String getUrlTitle() {
        return this.urlTitle;
    }

    public void setUrlTitle(String urlTitle) {
        this.urlTitle = urlTitle;
    }

    public String getUrlTypePic() {
        return this.urlTypePic;
    }

    public void setUrlTypePic(String urlTypePic) {
        this.urlTypePic = urlTypePic;
    }

    public String getLog() {
        return this.log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }


}
