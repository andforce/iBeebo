package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Thumbnail {
	
    private double height;
    private String type;
    private String url;
    private double width;
    private double cutType;
    
    
	public Thumbnail () {
		
	}	
        
    public Thumbnail (JSONObject json) {
    
        this.height = json.optDouble("height");
        this.type = json.optString("type");
        this.url = json.optString("url");
        this.width = json.optDouble("width");
        this.cutType = json.optDouble("cut_type");

    }
    
    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getCutType() {
        return this.cutType;
    }

    public void setCutType(double cutType) {
        this.cutType = cutType;
    }


    
}
