package org.zarroboogs.weibo.hot.hean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotVisibleBean implements Parcelable {

	private int type = 0;
	private int list_id = 0;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getList_id() {
		return list_id;
	}

	public void setList_id(int list_id) {
		this.list_id = list_id;
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
