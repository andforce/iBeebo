package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class Thumbnail {
	
    private String url;
    private double cutType;
    private double width;
    private String type;
    private double height;
    
    
	public Thumbnail () {
		
	}	
        
    public Thumbnail (JSONObject json) {
    
        this.url = json.optString("url");
        this.cutType = json.optDouble("cut_type");
        this.width = json.optDouble("width");
        this.type = json.optString("type");
        this.height = json.optDouble("height");

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

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }


    
}
