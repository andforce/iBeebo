package org.zarroboogs.weibo.hot.hean;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class HotCardlistInfoBean implements Parcelable {

    private String v_p = "11";
    private String containerid = "102803_ctg1_8999_-_ctg1_8999";
    private int show_style = 1;
    private String title_top = "24小时";
    private int total = 0;
    private int can_shared = 1;
    private String shared_text = "热门微博24小时榜http://weibo.com/p/102803_ctg1_8999_-_ctg1_8999";
    private String shared_text_qrcode = "热门微博24小时榜http://weibo.com/p/102803_ctg1_8999_-_ctg1_8999";
    private String cardlist_title = "热门微博24小时榜";
    private String desc = "纵览全天资讯热点尽在掌握";
    private String page_type = "03";
    private String background = "";
    private List<HotCardlistMenuBean> cardlist_menus = null;
//==========================================================//


    public int getTotal() {
        return total;
    }

    public List<HotCardlistMenuBean> getCardlist_menus() {
        return cardlist_menus;
    }

    public void setCardlist_menus(List<HotCardlistMenuBean> cardlist_menus) {
        this.cardlist_menus = cardlist_menus;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getShow_style() {
        return show_style;
    }

    public void setShow_style(int show_style) {
        this.show_style = show_style;
    }

    public int getCan_shared() {
        return can_shared;
    }

    public void setCan_shared(int can_shared) {
        this.can_shared = can_shared;
    }

    public String getV_p() {
        return v_p;
    }

    public void setV_p(String v_p) {
        this.v_p = v_p;
    }

    public String getContainerid() {
        return containerid;
    }

    public void setContainerid(String containerid) {
        this.containerid = containerid;
    }

    public String getTitle_top() {
        return title_top;
    }

    public void setTitle_top(String title_top) {
        this.title_top = title_top;
    }

    public String getShared_text() {
        return shared_text;
    }

    public void setShared_text(String shared_text) {
        this.shared_text = shared_text;
    }

    public String getShared_text_qrcode() {
        return shared_text_qrcode;
    }

    public void setShared_text_qrcode(String shared_text_qrcode) {
        this.shared_text_qrcode = shared_text_qrcode;
    }

    public String getCardlist_title() {
        return cardlist_title;
    }

    public void setCardlist_title(String cardlist_title) {
        this.cardlist_title = cardlist_title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPage_type() {
        return page_type;
    }

    public void setPage_type(String page_type) {
        this.page_type = page_type;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

    }

}
