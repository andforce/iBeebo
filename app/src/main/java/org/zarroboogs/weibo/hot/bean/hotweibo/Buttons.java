package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class Buttons {

    private String pic;
    private String type;
    private String name;
    private Params params;


    public Buttons() {

    }

    public Buttons(JSONObject json) {

        this.pic = json.optString("pic");
        this.type = json.optString("type");
        this.name = json.optString("name");
        this.params = new Params(json.optJSONObject("params"));

    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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
