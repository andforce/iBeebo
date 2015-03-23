package org.zarroboogs.weibo.hot.bean.huati;

import org.json.*;


public class PicItems {
	
    private String actionlog;
    private String picBig;
    private String pic;
    private String content1;
    private String scheme;
    
    
	public PicItems () {
		
	}	
        
    public PicItems (JSONObject json) {
    
        this.actionlog = json.optString("actionlog");
        this.picBig = json.optString("pic_big");
        this.pic = json.optString("pic");
        this.content1 = json.optString("content1");
        this.scheme = json.optString("scheme");

    }
    
    public String getActionlog() {
        return this.actionlog;
    }

    public void setActionlog(String actionlog) {
        this.actionlog = actionlog;
    }

    public String getPicBig() {
        return this.picBig;
    }

    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getContent1() {
        return this.content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    
}
