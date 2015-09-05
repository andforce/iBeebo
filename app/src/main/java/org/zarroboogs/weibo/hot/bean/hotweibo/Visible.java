package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class Visible {
	
    private double type;
    private double listId;
    
    
	public Visible () {
		
	}	
        
    public Visible (JSONObject json) {
    
        this.type = json.optDouble("type");
        this.listId = json.optDouble("list_id");

    }
    
    public double getType() {
        return this.type;
    }

    public void setType(double type) {
        this.type = type;
    }

    public double getListId() {
        return this.listId;
    }

    public void setListId(double listId) {
        this.listId = listId;
    }


    
}
