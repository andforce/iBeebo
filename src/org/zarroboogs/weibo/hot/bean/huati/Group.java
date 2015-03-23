package org.zarroboogs.weibo.hot.bean.huati;

import org.json.*;


public class Group {
	
    private String titleSub;
    private String pic;
    private String newflag;
    private String scheme;
    
    
	public Group () {
		
	}	
        
    public Group (JSONObject json) {
    
        this.titleSub = json.optString("title_sub");
        this.pic = json.optString("pic");
        this.newflag = json.optString("newflag");
        this.scheme = json.optString("scheme");

    }
    
    public String getTitleSub() {
        return this.titleSub;
    }

    public void setTitleSub(String titleSub) {
        this.titleSub = titleSub;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getNewflag() {
        return this.newflag;
    }

    public void setNewflag(String newflag) {
        this.newflag = newflag;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    
}
