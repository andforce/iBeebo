package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;
import java.util.ArrayList;

public class CardlistInfo {
	
    private String pageType;
    private String sharedText;
    private ArrayList<CardlistMenus> cardlistMenus;
    private String containerid;
    private double canShared;
    private double total;
    private String vP;
    private String cardlistTitle;
    private String desc;
    private String background;
    private String sharedTextQrcode;
    private double showStyle;
    private String titleTop;
    
    
	public CardlistInfo () {
		
	}	
        
    public CardlistInfo (JSONObject json) {
    
        this.pageType = json.optString("page_type");
        this.sharedText = json.optString("shared_text");

        this.cardlistMenus = new ArrayList<CardlistMenus>();
        JSONArray arrayCardlistMenus = json.optJSONArray("cardlist_menus");
        if (null != arrayCardlistMenus) {
            int cardlistMenusLength = arrayCardlistMenus.length();
            for (int i = 0; i < cardlistMenusLength; i++) {
                JSONObject item = arrayCardlistMenus.optJSONObject(i);
                if (null != item) {
                    this.cardlistMenus.add(new CardlistMenus(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("cardlist_menus");
            if (null != item) {
                this.cardlistMenus.add(new CardlistMenus(item));
            }
        }

        this.containerid = json.optString("containerid");
        this.canShared = json.optDouble("can_shared");
        this.total = json.optDouble("total");
        this.vP = json.optString("v_p");
        this.cardlistTitle = json.optString("cardlist_title");
        this.desc = json.optString("desc");
        this.background = json.optString("background");
        this.sharedTextQrcode = json.optString("shared_text_qrcode");
        this.showStyle = json.optDouble("show_style");
        this.titleTop = json.optString("title_top");

    }
    
    public String getPageType() {
        return this.pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getSharedText() {
        return this.sharedText;
    }

    public void setSharedText(String sharedText) {
        this.sharedText = sharedText;
    }

    public ArrayList<CardlistMenus> getCardlistMenus() {
        return this.cardlistMenus;
    }

    public void setCardlistMenus(ArrayList<CardlistMenus> cardlistMenus) {
        this.cardlistMenus = cardlistMenus;
    }

    public String getContainerid() {
        return this.containerid;
    }

    public void setContainerid(String containerid) {
        this.containerid = containerid;
    }

    public double getCanShared() {
        return this.canShared;
    }

    public void setCanShared(double canShared) {
        this.canShared = canShared;
    }

    public double getTotal() {
        return this.total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getVP() {
        return this.vP;
    }

    public void setVP(String vP) {
        this.vP = vP;
    }

    public String getCardlistTitle() {
        return this.cardlistTitle;
    }

    public void setCardlistTitle(String cardlistTitle) {
        this.cardlistTitle = cardlistTitle;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getSharedTextQrcode() {
        return this.sharedTextQrcode;
    }

    public void setSharedTextQrcode(String sharedTextQrcode) {
        this.sharedTextQrcode = sharedTextQrcode;
    }

    public double getShowStyle() {
        return this.showStyle;
    }

    public void setShowStyle(double showStyle) {
        this.showStyle = showStyle;
    }

    public String getTitleTop() {
        return this.titleTop;
    }

    public void setTitleTop(String titleTop) {
        this.titleTop = titleTop;
    }


    
}
