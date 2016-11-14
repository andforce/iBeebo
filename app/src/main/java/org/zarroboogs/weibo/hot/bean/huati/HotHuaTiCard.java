package org.zarroboogs.weibo.hot.bean.huati;

import org.json.*;

import java.util.ArrayList;

public class HotHuaTiCard {

    private double autoFlow;
    private double col;
    private double displayArrow;
    private String buttontitle;
    private String openurl;
    private double isAsyn;
    private String title;
    private String cardTypeName;
    private double flowGap;
    private double cardType;
    private String scheme;
    private String itemid;
    private ArrayList<Group> group;
    private ArrayList<HotHuaTiCardGroup> card_group;
    private ArrayList<PicItems> picItems;


    public HotHuaTiCard() {

    }


    public ArrayList<HotHuaTiCardGroup> getCard_group() {
        return card_group;
    }


    public void setCard_group(ArrayList<HotHuaTiCardGroup> card_group) {
        this.card_group = card_group;
    }


    public double getAutoFlow() {
        return this.autoFlow;
    }

    public void setAutoFlow(double autoFlow) {
        this.autoFlow = autoFlow;
    }

    public double getCol() {
        return this.col;
    }

    public void setCol(double col) {
        this.col = col;
    }

    public double getDisplayArrow() {
        return this.displayArrow;
    }

    public void setDisplayArrow(double displayArrow) {
        this.displayArrow = displayArrow;
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

    public double getIsAsyn() {
        return this.isAsyn;
    }

    public void setIsAsyn(double isAsyn) {
        this.isAsyn = isAsyn;
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

    public double getFlowGap() {
        return this.flowGap;
    }

    public void setFlowGap(double flowGap) {
        this.flowGap = flowGap;
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


    public ArrayList<PicItems> getPicItems() {
        return this.picItems;
    }

    public void setPicItems(ArrayList<PicItems> picItems) {
        this.picItems = picItems;
    }


}
