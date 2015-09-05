package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Params {
	
    private String uid;
    private String type;
    private String scheme;
    
    
	public Params () {
		
	}	
        
    public Params (JSONObject json) {
    
        this.uid = json.optString("uid");
        this.type = json.optString("type");
        this.scheme = json.optString("scheme");

    }
    
    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
