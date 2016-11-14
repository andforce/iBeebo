package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;

import java.util.ArrayList;

public class CardGroup {

    private Mblog mblog;
    private String titleSub;
    private String desc1;
    private ArrayList<Buttons> buttons;
    private String openurl;
    private String pic;
    private String cardTypeName;
    private String desc2;
    private String scheme;
    private double cardType;
    private String itemid;
    private double showType;


    public CardGroup() {

    }

    public CardGroup(JSONObject json) {

        this.mblog = new Mblog(json.optJSONObject("mblog"));
        this.titleSub = json.optString("title_sub");
        this.desc1 = json.optString("desc1");

        this.buttons = new ArrayList<Buttons>();
        JSONArray arrayButtons = json.optJSONArray("buttons");
        if (null != arrayButtons) {
            int buttonsLength = arrayButtons.length();
            for (int i = 0; i < buttonsLength; i++) {
                JSONObject item = arrayButtons.optJSONObject(i);
                if (null != item) {
                    this.buttons.add(new Buttons(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("buttons");
            if (null != item) {
                this.buttons.add(new Buttons(item));
            }
        }

        this.openurl = json.optString("openurl");
        this.pic = json.optString("pic");
        this.cardTypeName = json.optString("card_type_name");
        this.desc2 = json.optString("desc2");
        this.scheme = json.optString("scheme");
        this.cardType = json.optDouble("card_type");
        this.itemid = json.optString("itemid");
        this.showType = json.optDouble("show_type");

    }

    public Mblog getMblog() {
        return this.mblog;
    }

    public void setMblog(Mblog mblog) {
        this.mblog = mblog;
    }

    public String getTitleSub() {
        return this.titleSub;
    }

    public void setTitleSub(String titleSub) {
        this.titleSub = titleSub;
    }

    public String getDesc1() {
        return this.desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public ArrayList<Buttons> getButtons() {
        return this.buttons;
    }

    public void setButtons(ArrayList<Buttons> buttons) {
        this.buttons = buttons;
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

    public String getCardTypeName() {
        return this.cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public String getDesc2() {
        return this.desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
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

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public double getShowType() {
        return this.showType;
    }

    public void setShowType(double showType) {
        this.showType = showType;
    }


}
