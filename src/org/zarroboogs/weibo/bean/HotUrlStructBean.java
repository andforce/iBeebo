package org.zarroboogs.weibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotUrlStructBean implements Parcelable {

	private String ori_url = "";

	public String getOri_url() {
		return ori_url;
	}

	public void setOri_url(String ori_url) {
		this.ori_url = ori_url;
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
