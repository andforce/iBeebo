package org.zarroboogs.weibo.hot.bean.huati;

import org.json.*;

import java.util.ArrayList;

public class HotHuaTiCardGroup {

    private double displayArrow;
    private String title_sub;
    private String desc1;
    private ArrayList<Buttons> buttons;
    private String openurl;
    private String pic;
    private String title;
    private String desc2;
    private String cardTypeName;
    private double cardType;
    private String scheme;
    private String itemid;
    private double attitudesStatus;


    public HotHuaTiCardGroup() {

    }


    public double getDisplayArrow() {
        return this.displayArrow;
    }

    public void setDisplayArrow(double displayArrow) {
        this.displayArrow = displayArrow;
    }


    public String getTitle_sub() {
        return title_sub;
    }


    public void setTitle_sub(String title_sub) {
        this.title_sub = title_sub;
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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc2() {
        return this.desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getCardTypeName() {
        return this.cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
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

    public double getAttitudesStatus() {
        return this.attitudesStatus;
    }

    public void setAttitudesStatus(double attitudesStatus) {
        this.attitudesStatus = attitudesStatus;
    }


}
