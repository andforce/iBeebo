package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Extend {

    private Privacy privacy;
    private String mbprivilege;


    public Extend() {

    }

    public Extend(JSONObject json) {

        this.privacy = new Privacy(json.optJSONObject("privacy"));
        this.mbprivilege = json.optString("mbprivilege");

    }

    public Privacy getPrivacy() {
        return this.privacy;
    }

    public void setPrivacy(Privacy privacy) {
        this.privacy = privacy;
    }

    public String getMbprivilege() {
        return this.mbprivilege;
    }

    public void setMbprivilege(String mbprivilege) {
        this.mbprivilege = mbprivilege;
    }


}
