package org.zarroboogs.weibo.hot.bean.model;

import org.json.*;

import java.util.ArrayList;

public class HotModelCards {

    private String itemid;
    private ArrayList<HotModelCardGroup> card_group;
    private String scheme;
    private double cardType;
    private double displayArrow;
    private String title;
    private String buttontitle;
    private String openurl;


    public HotModelCards() {

    }

    public String getExtparam() {
        String str = "extparam=";
        int extparamStart = getScheme().indexOf(str, str.length() + 5);
        String ext = getScheme().substring(extparamStart + str.length(), extparamStart + str.length() + 5);
        return ext;
    }

    public HotModelCards(JSONObject json) {

        this.itemid = json.optString("itemid");

        this.card_group = new ArrayList<HotModelCardGroup>();
        JSONArray arrayCardGroup = json.optJSONArray("card_group");
        if (null != arrayCardGroup) {
            int cardGroupLength = arrayCardGroup.length();
            for (int i = 0; i < cardGroupLength; i++) {
                JSONObject item = arrayCardGroup.optJSONObject(i);
                if (null != item) {
                    this.card_group.add(new HotModelCardGroup(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("card_group");
            if (null != item) {
                this.card_group.add(new HotModelCardGroup(item));
            }
        }

        this.scheme = json.optString("scheme");
        this.cardType = json.optDouble("card_type");
        this.displayArrow = json.optDouble("display_arrow");
        this.title = json.optString("title");
        this.buttontitle = json.optString("buttontitle");
        this.openurl = json.optString("openurl");

    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }


    public ArrayList<HotModelCardGroup> getCard_group() {
        return card_group;
    }

    public void setCard_group(ArrayList<HotModelCardGroup> card_group) {
        this.card_group = card_group;
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

    public double getDisplayArrow() {
        return this.displayArrow;
    }

    public void setDisplayArrow(double displayArrow) {
        this.displayArrow = displayArrow;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButtontitle() {
        return this.buttontitle;
    }

    public void setButtontitle(String buttontitle) {
        this.buttontitle = buttontitle;
    }

    public String getOpenurl() {
        return this.openurl;
    }

    public void setOpenurl(String openurl) {
        this.openurl = openurl;
    }


}
