package org.zarroboogs.weibo.activity;

import android.os.Parcel;
import android.os.Parcelable;

public class HotGeoBean implements Parcelable {

	private String type = "";
	private double[] coordinates = { 0.0, 0.0 };

	// =============================================//

	protected HotGeoBean(Parcel in) {
		type = in.readString();
		coordinates = in.createDoubleArray();
	}

	public static final Creator<HotGeoBean> CREATOR = new Creator<HotGeoBean>() {
		@Override
		public HotGeoBean createFromParcel(Parcel in) {
			return new HotGeoBean(in);
		}

		@Override
		public HotGeoBean[] newArray(int size) {
			return new HotGeoBean[size];
		}
	};

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

		dest.writeString(type);
		dest.writeDoubleArray(coordinates);
	}

}
