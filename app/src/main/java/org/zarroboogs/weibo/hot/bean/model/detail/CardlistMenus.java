package org.zarroboogs.weibo.hot.bean.model.detail;

import org.json.*;


public class CardlistMenus {

    private String type;
    private String name;
    private Params params;


    public CardlistMenus() {

    }

    public CardlistMenus(JSONObject json) {

        this.type = json.optString("type");
        this.name = json.optString("name");
        this.params = new Params(json.optJSONObject("params"));

    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Params getParams() {
        return this.params;
    }

    public void setParams(Params params) {
        this.params = params;
    }


}
