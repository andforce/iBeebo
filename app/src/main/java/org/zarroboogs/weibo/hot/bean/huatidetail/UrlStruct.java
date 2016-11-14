package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class UrlStruct {

    private boolean result;
    private String pageId;
    private String scheme;
    private String oriUrl;
    private double urlType;
    private String urlTitle;
    private String urlTypePic;
    private double hide;
    private String log;
    private String shortUrl;


    public UrlStruct() {

    }

    public UrlStruct(JSONObject json) {

        this.result = json.optBoolean("result");
        this.pageId = json.optString("page_id");
        this.scheme = json.optString("scheme");
        this.oriUrl = json.optString("ori_url");
        this.urlType = json.optDouble("url_type");
        this.urlTitle = json.optString("url_title");
        this.urlTypePic = json.optString("url_type_pic");
        this.hide = json.optDouble("hide");
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

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
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

    public double getHide() {
        return this.hide;
    }

    public void setHide(double hide) {
        this.hide = hide;
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
