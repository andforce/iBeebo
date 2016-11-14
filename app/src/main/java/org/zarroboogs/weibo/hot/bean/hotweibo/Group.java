package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class Group {

    private String scheme;
    private String pic;
    private String titleSub;


    public Group() {

    }

    public Group(JSONObject json) {

        this.scheme = json.optString("scheme");
        this.pic = json.optString("pic");
        this.titleSub = json.optString("title_sub");

    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitleSub() {
        return this.titleSub;
    }

    public void setTitleSub(String titleSub) {
        this.titleSub = titleSub;
    }


}
