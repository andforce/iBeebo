package org.zarroboogs.weibo.hot.bean.model.detail;

import org.json.*;


public class Pics {
	
    private String desc1;
    private String pic_big;
    private String pic_small;
    private String scheme;
    
    
	public Pics () {
		
	}	
        
    public Pics (JSONObject json) {
    
        this.desc1 = json.optString("desc1");
        this.pic_big = json.optString("pic_big");
        this.pic_small = json.optString("pic_small");
        this.scheme = json.optString("scheme");

    }
    
    public String getDesc1() {
        return this.desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getPic_big() {
		return pic_big;
	}

	public void setPic_big(String pic_big) {
		this.pic_big = pic_big;
	}

	public String getPic_small() {
		return pic_small;
	}

	public void setPic_small(String pic_small) {
		this.pic_small = pic_small;
	}

	public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    
}
