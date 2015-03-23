package org.zarroboogs.weibo.hot.bean.huati;

import org.json.*;


public class CardlistMenus {
	
    private String name;
    private String type;
    private Params params;
    
    
	public CardlistMenus () {
		
	}	
        
    public CardlistMenus (JSONObject json) {
    
        this.name = json.optString("name");
        this.type = json.optString("type");
        this.params = new Params(json.optJSONObject("params"));

    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Params getParams() {
        return this.params;
    }

    public void setParams(Params params) {
        this.params = params;
    }


    
}
