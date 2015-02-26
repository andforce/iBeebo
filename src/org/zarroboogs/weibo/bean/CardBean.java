package org.zarroboogs.weibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CardBean implements Parcelable {

	private int card_type = 9;
	private String card_type_name = "";
	private String itemid = "102803_ctg1_8999_-_ctg1_8999_-_mbloglist_3814182711869974";
	private String scheme = "sinaweibo://detail/?mblogid=C5WXl7QsS";
	private String weibo_need = "mblog";
	private int show_type = 1;
	private String openurl="";
	private HotMblogBean mblog = null;
	
	public HotMblogBean getMblog() {
		return mblog;
	}

	public void setMblog(HotMblogBean mblog) {
		this.mblog = mblog;
	}

	public int getShow_type() {
		return show_type;
	}

	public void setShow_type(int show_type) {
		this.show_type = show_type;
	}

	public String getOpenurl() {
		return openurl;
	}

	public void setOpenurl(String openurl) {
		this.openurl = openurl;
	}

	public int getCard_type() {
		return card_type;
	}

	public void setCard_type(int card_type) {
		this.card_type = card_type;
	}

	public String getCard_type_name() {
		return card_type_name;
	}

	public void setCard_type_name(String card_type_name) {
		this.card_type_name = card_type_name;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getWeibo_need() {
		return weibo_need;
	}

	public void setWeibo_need(String weibo_need) {
		this.weibo_need = weibo_need;
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
