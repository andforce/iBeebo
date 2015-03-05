package org.zarroboogs.weibo.hot.bean.model.detail;

import org.json.*;
import java.util.ArrayList;

public class Cards {
	
    private ArrayList<Pics> pics;
    private String openurl;
    private double cols;
    private double cardType;
    private String itemid;
    
    
	public Cards () {
		
	}	
        
    public Cards (JSONObject json) {
    

        this.pics = new ArrayList<Pics>();
        JSONArray arrayPics = json.optJSONArray("pics");
        if (null != arrayPics) {
            int picsLength = arrayPics.length();
            for (int i = 0; i < picsLength; i++) {
                JSONObject item = arrayPics.optJSONObject(i);
                if (null != item) {
                    this.pics.add(new Pics(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("pics");
            if (null != item) {
                this.pics.add(new Pics(item));
            }
        }

        this.openurl = json.optString("openurl");
        this.cols = json.optDouble("cols");
        this.cardType = json.optDouble("card_type");
        this.itemid = json.optString("itemid");

    }
    
    public ArrayList<Pics> getPics() {
        return this.pics;
    }

    public void setPics(ArrayList<Pics> pics) {
        this.pics = pics;
    }

    public String getOpenurl() {
        return this.openurl;
    }

    public void setOpenurl(String openurl) {
        this.openurl = openurl;
    }

    public double getCols() {
        return this.cols;
    }

    public void setCols(double cols) {
        this.cols = cols;
    }

    public double getCardType() {
        return this.cardType;
    }

    public void setCardType(double cardType) {
        this.cardType = cardType;
    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }


    
}
