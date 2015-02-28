package org.zarroboogs.weibo.hot.hean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotExtendBean implements Parcelable{

//	"extend": {
//    "privacy": {
//        "mobile": 0
//    },
//    "mbprivilege": "0000000000000000000000000000000000000000000000000000000000010000"
//},
//	
	private HotPrivacyBean privacy = null;
	private String mbprivilege = "";
	
	
	public HotPrivacyBean getPrivacy() {
		return privacy;
	}

	public void setPrivacy(HotPrivacyBean privacy) {
		this.privacy = privacy;
	}

	public String getMbprivilege() {
		return mbprivilege;
	}

	public void setMbprivilege(String mbprivilege) {
		this.mbprivilege = mbprivilege;
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
