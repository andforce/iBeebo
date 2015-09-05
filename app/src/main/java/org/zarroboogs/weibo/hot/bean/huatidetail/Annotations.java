package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Annotations {
	
    private String clientMblogid;
    private double shooting;
    
    
	public Annotations () {
		
	}	
        
    public Annotations (JSONObject json) {
    
        this.clientMblogid = json.optString("client_mblogid");
        this.shooting = json.optDouble("shooting");

    }
    
    public String getClientMblogid() {
        return this.clientMblogid;
    }

    public void setClientMblogid(String clientMblogid) {
        this.clientMblogid = clientMblogid;
    }

    public double getShooting() {
        return this.shooting;
    }

    public void setShooting(double shooting) {
        this.shooting = shooting;
    }


    
}
