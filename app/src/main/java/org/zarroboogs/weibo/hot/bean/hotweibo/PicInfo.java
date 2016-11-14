package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class PicInfo {

    private PicBig picBig;
    private PicMiddle picMiddle;
    private PicSmall picSmall;


    public PicInfo() {

    }

    public PicInfo(JSONObject json) {

        this.picBig = new PicBig(json.optJSONObject("pic_big"));
        this.picMiddle = new PicMiddle(json.optJSONObject("pic_middle"));
        this.picSmall = new PicSmall(json.optJSONObject("pic_small"));

    }

    public PicBig getPicBig() {
        return this.picBig;
    }

    public void setPicBig(PicBig picBig) {
        this.picBig = picBig;
    }

    public PicMiddle getPicMiddle() {
        return this.picMiddle;
    }

    public void setPicMiddle(PicMiddle picMiddle) {
        this.picMiddle = picMiddle;
    }

    public PicSmall getPicSmall() {
        return this.picSmall;
    }

    public void setPicSmall(PicSmall picSmall) {
        this.picSmall = picSmall;
    }


}
