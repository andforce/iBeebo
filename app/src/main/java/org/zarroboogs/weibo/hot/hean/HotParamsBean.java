package org.zarroboogs.weibo.hot.hean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotParamsBean implements Parcelable {

	private String uid = "5187664653";

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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
