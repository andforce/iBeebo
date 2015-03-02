package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Privacy {
	
    private double mobile;
    
    
	public Privacy () {
		
	}	
        
    public Privacy (JSONObject json) {
    
        this.mobile = json.optDouble("mobile");

    }
    
    public double getMobile() {
        return this.mobile;
    }

    public void setMobile(double mobile) {
        this.mobile = mobile;
    }


    
}
