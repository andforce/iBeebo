package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class CardGroup {
	
    private String itemid;
    private String scheme;
    private double cardType;
    private String weiboNeed;
    private Mblog mblog;
    private String title;
    private String cardTypeName;
    private double showType;
    
    
	public CardGroup () {
		
	}	
        
    public CardGroup (JSONObject json) {
    
        this.itemid = json.optString("itemid");
        this.scheme = json.optString("scheme");
        this.cardType = json.optDouble("card_type");
        this.weiboNeed = json.optString("weibo_need");
        this.mblog = new Mblog(json.optJSONObject("mblog"));
        this.title = json.optString("title");
        this.cardTypeName = json.optString("card_type_name");
        this.showType = json.optDouble("show_type");

    }
    
    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public double getCardType() {
        return this.cardType;
    }

    public void setCardType(double cardType) {
        this.cardType = cardType;
    }

    public String getWeiboNeed() {
        return this.weiboNeed;
    }

    public void setWeiboNeed(String weiboNeed) {
        this.weiboNeed = weiboNeed;
    }

    public Mblog getMblog() {
        return this.mblog;
    }

    public void setMblog(Mblog mblog) {
        this.mblog = mblog;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCardTypeName() {
        return this.cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public double getShowType() {
        return this.showType;
    }

    public void setShowType(double showType) {
        this.showType = showType;
    }


    
}
