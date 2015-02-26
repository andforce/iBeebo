package org.zarroboogs.weibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotButtonsBean implements Parcelable {

	private String type = "follow";
	private String name = "关注";
	private String pic = "http://u1.sinaimg.cn/upload/2013/07/02/timeline_card_small_button_icon_add.png";
	private HotParamsBean params = null;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public HotParamsBean getParams() {
		return params;
	}

	public void setParams(HotParamsBean params) {
		this.params = params;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
