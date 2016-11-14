package org.zarroboogs.weibo.hot.bean.huati;

import org.json.*;


public class Buttons {

    private double subType;
    private double showLoading;
    private String type;
    private String name;
    private Params params;


    public Buttons() {

    }

    public Buttons(JSONObject json) {

        this.subType = json.optDouble("sub_type");
        this.showLoading = json.optDouble("show_loading");
        this.type = json.optString("type");
        this.name = json.optString("name");
        this.params = new Params(json.optJSONObject("params"));

    }

    public double getSubType() {
        return this.subType;
    }

    public void setSubType(double subType) {
        this.subType = subType;
    }

    public double getShowLoading() {
        return this.showLoading;
    }

    public void setShowLoading(double showLoading) {
        this.showLoading = showLoading;
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
