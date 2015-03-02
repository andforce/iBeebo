package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class Largest {
	
    private String url;
    private double cutType;
    private String width;
    private String type;
    private String height;
    
    
	public Largest () {
		
	}	
        
    public Largest (JSONObject json) {
    
        this.url = json.optString("url");
        this.cutType = json.optDouble("cut_type");
        this.width = json.optString("width");
        this.type = json.optString("type");
        this.height = json.optString("height");

    }
    
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getCutType() {
        return this.cutType;
    }

    public void setCutType(double cutType) {
        this.cutType = cutType;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }


    
}
