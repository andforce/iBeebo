package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Icons {

    private String url;


    public Icons() {

    }

    public Icons(JSONObject json) {

        this.url = json.optString("url");

    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
