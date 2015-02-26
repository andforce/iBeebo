package org.zarroboogs.weibo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotPrivacyBean implements Parcelable {

//  "privacy": {
//  "mobile": 0
//},
	private int mobile = 0;
	
	
	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		this.mobile = mobile;
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
