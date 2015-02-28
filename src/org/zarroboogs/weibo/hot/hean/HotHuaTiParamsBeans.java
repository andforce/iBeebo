package org.zarroboogs.weibo.hot.hean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotHuaTiParamsBeans implements Parcelable {

	private String uid = "1022:10080800aacc07b1748abe0e744fd5af5f6982";
	private String type = "page";

	// =========================================//

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
