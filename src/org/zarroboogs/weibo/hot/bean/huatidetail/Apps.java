package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Apps {
	
    private String openurl;
    private String title;
    private double count;
    private String type;
    private String scheme;
    
    
	public Apps () {
		
	}	
        
    public Apps (JSONObject json) {
    
        this.openurl = json.optString("openurl");
        this.title = json.optString("title");
        this.count = json.optDouble("count");
        this.type = json.optString("type");
        this.scheme = json.optString("scheme");

    }
    
    public String getOpenurl() {
        return this.openurl;
    }

    public void setOpenurl(String openurl) {
        this.openurl = openurl;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCount() {
        return this.count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    
}
