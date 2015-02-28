package org.zarroboogs.weibo.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class HotHuaTiCardGroupBean implements Parcelable {

	private int card_type = 8;
	private String card_type_name = "岁岁平安";
	private String itemid = "100803_-_card_home_hottopic_more";
	private String title = "";
	private String scheme = "";
	private String title_sub = "#岁岁平安#";
	private String pic = "http://ww3.sinaimg.cn/thumbnail/8aca58f7jw1ep5451e1q6j2050050my3.jpg";
	private int display_arrow = 0;
	private String desc1 = "";
	private String desc2 = "3亿阅读";
	private int attitudes_status = 0;

	private List<HotHuaTiButtons> buttons = new ArrayList<HotHuaTiButtons>();
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

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getTitle_sub() {
		return title_sub;
	}

	public void setTitle_sub(String title_sub) {
		this.title_sub = title_sub;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public int getDisplay_arrow() {
		return display_arrow;
	}

	public void setDisplay_arrow(int display_arrow) {
		this.display_arrow = display_arrow;
	}

	public String getDesc1() {
		return desc1;
	}

	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}

	public String getDesc2() {
		return desc2;
	}

	public void setDesc2(String desc2) {
		this.desc2 = desc2;
	}

	public int getAttitudes_status() {
		return attitudes_status;
	}

	public void setAttitudes_status(int attitudes_status) {
		this.attitudes_status = attitudes_status;
	}

	public List<HotHuaTiButtons> getButtons() {
		return buttons;
	}

	public void setButtons(List<HotHuaTiButtons> buttons) {
		this.buttons = buttons;
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
