package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class Params {

    private String scheme;
    private String uid;


    public Params() {

    }

    public Params(JSONObject json) {

        this.scheme = json.optString("scheme");
        this.uid = json.optString("uid");

    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
