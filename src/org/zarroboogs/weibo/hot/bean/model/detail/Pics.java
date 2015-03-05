package org.zarroboogs.weibo.hot.bean.model.detail;

import org.json.*;


public class Pics {
	
    private String desc1;
    private String picBig;
    private String picSmall;
    private String scheme;
    
    
	public Pics () {
		
	}	
        
    public Pics (JSONObject json) {
    
        this.desc1 = json.optString("desc1");
        this.picBig = json.optString("pic_big");
        this.picSmall = json.optString("pic_small");
        this.scheme = json.optString("scheme");

    }
    
    public String getDesc1() {
        return this.desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getPicBig() {
        return this.picBig;
    }

    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getPicSmall() {
        return this.picSmall;
    }

    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    
}
