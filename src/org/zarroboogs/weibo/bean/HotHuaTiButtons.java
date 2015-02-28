package org.zarroboogs.weibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotHuaTiButtons implements Parcelable {

	private String type = "follow";
	private int sub_type = 0;
	private int show_loading = 1;
	private String name = "关注";

	private HotHuaTiParamsBeans params = null;

	// ===============================================//

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSub_type() {
		return sub_type;
	}

	public void setSub_type(int sub_type) {
		this.sub_type = sub_type;
	}

	public int getShow_loading() {
		return show_loading;
	}

	public void setShow_loading(int show_loading) {
		this.show_loading = show_loading;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HotHuaTiParamsBeans getParams() {
		return params;
	}

	public void setParams(HotHuaTiParamsBeans params) {
		this.params = params;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
