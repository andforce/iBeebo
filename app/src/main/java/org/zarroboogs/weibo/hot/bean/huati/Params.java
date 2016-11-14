package org.zarroboogs.weibo.hot.bean.huati;

import org.json.*;


public class Params {

    private String scheme;
    private String uid;
    private String type;


    public Params() {

    }

    public Params(JSONObject json) {

        this.scheme = json.optString("scheme");
        this.uid = json.optString("uid");
        this.type = json.optString("type");

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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
