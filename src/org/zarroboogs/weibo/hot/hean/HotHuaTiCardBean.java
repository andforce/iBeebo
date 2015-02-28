package org.zarroboogs.weibo.hot.hean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class HotHuaTiCardBean implements Parcelable {

	private int card_type = 11;
	private String card_type_name = "trendtop_hot_more";
	private String title = "";

	private List<HotHuaTiCardGroupBean> card_group = new ArrayList<HotHuaTiCardGroupBean>();

	private String buttontitle = "";
	private int display_arrow = 1;
	private String scheme = "";
	private String itemid = "100803_-_card_home_hottopic_more";
	private String openurl = "";

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<HotHuaTiCardGroupBean> getCard_group() {
		return card_group;
	}

	public void setCard_group(List<HotHuaTiCardGroupBean> card_group) {
		this.card_group = card_group;
	}

	public String getButtontitle() {
		return buttontitle;
	}

	public void setButtontitle(String buttontitle) {
		this.buttontitle = buttontitle;
	}

	public int getDisplay_arrow() {
		return display_arrow;
	}

	public void setDisplay_arrow(int display_arrow) {
		this.display_arrow = display_arrow;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getOpenurl() {
		return openurl;
	}

	public void setOpenurl(String openurl) {
		this.openurl = openurl;
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
