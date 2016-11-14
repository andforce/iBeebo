package org.zarroboogs.weibo.hot.bean.model;

import org.json.*;

import java.util.ArrayList;

public class CardlistInfo {

    private ArrayList<String> button;
    private double showStyle;
    private ArrayList<String> filterGroup;
    private ArrayList<CardlistMenus> cardlistMenus;
    private double vP;
    private String containerid;
    private double total;
    private String pageType;
    private String background;
    private String titleTop;


    public CardlistInfo() {

    }

    public CardlistInfo(JSONObject json) {


        this.button = new ArrayList<String>();
        JSONArray arrayButton = json.optJSONArray("button");
        if (null != arrayButton) {
            int buttonLength = arrayButton.length();
            for (int i = 0; i < buttonLength; i++) {
                String item = arrayButton.optString(i);
                if (null != item) {
                    this.button.add(item);
                }
            }
        } else {
            String item = json.optString("button");
            if (null != item) {
                this.button.add(item);
            }
        }

        this.showStyle = json.optDouble("show_style");

        this.filterGroup = new ArrayList<String>();
        JSONArray arrayFilterGroup = json.optJSONArray("filter_group");
        if (null != arrayFilterGroup) {
            int filterGroupLength = arrayFilterGroup.length();
            for (int i = 0; i < filterGroupLength; i++) {
                String item = arrayFilterGroup.optString(i);
                if (null != item) {
                    this.filterGroup.add(item);
                }
            }
        } else {
            String item = json.optString("filter_group");
            if (null != item) {
                this.filterGroup.add(item);
            }
        }


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
        } else {
            JSONObject item = json.optJSONObject("cardlist_menus");
            if (null != item) {
                this.cardlistMenus.add(new CardlistMenus(item));
            }
        }

        this.vP = json.optDouble("v_p");
        this.containerid = json.optString("containerid");
        this.total = json.optDouble("total");
        this.pageType = json.optString("page_type");
        this.background = json.optString("background");
        this.titleTop = json.optString("title_top");

    }

    public ArrayList<String> getButton() {
        return this.button;
    }

    public void setButton(ArrayList<String> button) {
        this.button = button;
    }

    public double getShowStyle() {
        return this.showStyle;
    }

    public void setShowStyle(double showStyle) {
        this.showStyle = showStyle;
    }

    public ArrayList<String> getFilterGroup() {
        return this.filterGroup;
    }

    public void setFilterGroup(ArrayList<String> filterGroup) {
        this.filterGroup = filterGroup;
    }

    public ArrayList<CardlistMenus> getCardlistMenus() {
        return this.cardlistMenus;
    }

    public void setCardlistMenus(ArrayList<CardlistMenus> cardlistMenus) {
        this.cardlistMenus = cardlistMenus;
    }

    public double getVP() {
        return this.vP;
    }

    public void setVP(double vP) {
        this.vP = vP;
    }

    public String getContainerid() {
        return this.containerid;
    }

    public void setContainerid(String containerid) {
        this.containerid = containerid;
    }

    public double getTotal() {
        return this.total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPageType() {
        return this.pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getTitleTop() {
        return this.titleTop;
    }

    public void setTitleTop(String titleTop) {
        this.titleTop = titleTop;
    }


}
