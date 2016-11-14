package org.zarroboogs.weibo.hot.bean.model.detail;

import org.json.*;


public class Params {

    private String scheme;


    public Params() {

    }

    public Params(JSONObject json) {

        this.scheme = json.optString("scheme");

    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


}
