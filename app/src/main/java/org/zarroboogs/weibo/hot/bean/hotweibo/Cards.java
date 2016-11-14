package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;

import java.util.ArrayList;

public class Cards {

    private double displayArrow;
    private Mblog mblog;
    private String openurl;
    private String pic;
    private String title;
    private String cardTypeName;
    private ArrayList<CardGroup> cardGroup;
    private double cardType;
    private String scheme;
    private String itemid;
    private ArrayList<Group> group;
    private String desc;
    private double showType;
    private String weiboNeed;
    private double col;


    public Cards() {

    }

    public Cards(JSONObject json) {

        this.displayArrow = json.optDouble("display_arrow");
        this.mblog = new Mblog(json.optJSONObject("mblog"));
        this.openurl = json.optString("openurl");
        this.pic = json.optString("pic");
        this.title = json.optString("title");
        this.cardTypeName = json.optString("card_type_name");

        this.cardGroup = new ArrayList<CardGroup>();
        JSONArray arrayCardGroup = json.optJSONArray("card_group");
        if (null != arrayCardGroup) {
            int cardGroupLength = arrayCardGroup.length();
            for (int i = 0; i < cardGroupLength; i++) {
                JSONObject item = arrayCardGroup.optJSONObject(i);
                if (null != item) {
                    this.cardGroup.add(new CardGroup(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("card_group");
            if (null != item) {
                this.cardGroup.add(new CardGroup(item));
            }
        }

        this.cardType = json.optDouble("card_type");
        this.scheme = json.optString("scheme");
        this.itemid = json.optString("itemid");

        this.group = new ArrayList<Group>();
        JSONArray arrayGroup = json.optJSONArray("group");
        if (null != arrayGroup) {
            int groupLength = arrayGroup.length();
            for (int i = 0; i < groupLength; i++) {
                JSONObject item = arrayGroup.optJSONObject(i);
                if (null != item) {
                    this.group.add(new Group(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("group");
            if (null != item) {
                this.group.add(new Group(item));
            }
        }

        this.desc = json.optString("desc");
        this.showType = json.optDouble("show_type");
        this.weiboNeed = json.optString("weibo_need");
        this.col = json.optDouble("col");

    }

    public double getDisplayArrow() {
        return this.displayArrow;
    }

    public void setDisplayArrow(double displayArrow) {
        this.displayArrow = displayArrow;
    }

    public Mblog getMblog() {
        return this.mblog;
    }

    public void setMblog(Mblog mblog) {
        this.mblog = mblog;
    }

    public String getOpenurl() {
        return this.openurl;
    }

    public void setOpenurl(String openurl) {
        this.openurl = openurl;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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

    public ArrayList<CardGroup> getCardGroup() {
        return this.cardGroup;
    }

    public void setCardGroup(ArrayList<CardGroup> cardGroup) {
        this.cardGroup = cardGroup;
    }

    public double getCardType() {
        return this.cardType;
    }

    public void setCardType(double cardType) {
        this.cardType = cardType;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public ArrayList<Group> getGroup() {
        return this.group;
    }

    public void setGroup(ArrayList<Group> group) {
        this.group = group;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getShowType() {
        return this.showType;
    }

    public void setShowType(double showType) {
        this.showType = showType;
    }

    public String getWeiboNeed() {
        return this.weiboNeed;
    }

    public void setWeiboNeed(String weiboNeed) {
        this.weiboNeed = weiboNeed;
    }

    public double getCol() {
        return this.col;
    }

    public void setCol(double col) {
        this.col = col;
    }


}
