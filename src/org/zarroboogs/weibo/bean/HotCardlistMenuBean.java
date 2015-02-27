package org.zarroboogs.weibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotCardlistMenuBean implements Parcelable {

	private String type = "gohome";
	private String name = "返回首页";

	private HotSchemeBean params = null;

	// =====================================================//

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

	public HotSchemeBean getParams() {
		return params;
	}

	public void setParams(HotSchemeBean params) {
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
