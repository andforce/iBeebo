package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class Annotations {
	
    private String clientMblogid;
    
    
	public Annotations () {
		
	}	
        
    public Annotations (JSONObject json) {
    
        this.clientMblogid = json.optString("client_mblogid");

    }
    
    public String getClientMblogid() {
        return this.clientMblogid;
    }

    public void setClientMblogid(String clientMblogid) {
        this.clientMblogid = clientMblogid;
    }


    
}
