package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class PicBig {
	
    private String url;
    private String width;
    private String height;
    
    
	public PicBig () {
		
	}	
        
    public PicBig (JSONObject json) {
    
        this.url = json.optString("url");
        this.width = json.optString("width");
        this.height = json.optString("height");

    }
    
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }


    
}
