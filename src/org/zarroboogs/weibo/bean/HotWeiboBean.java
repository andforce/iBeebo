package org.zarroboogs.weibo.bean;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class HotWeiboBean implements Parcelable {

	private HotCardlistInfoBean cardlistInfo = null;
	private List<HotCardBean> cards = null;

	// ===================================//

	public HotCardlistInfoBean getCardlistInfo() {
		return cardlistInfo;
	}

	public void setCardlistInfo(HotCardlistInfoBean cardlistInfo) {
		this.cardlistInfo = cardlistInfo;
	}

	public List<HotCardBean> getCards() {
		return cards;
	}

	public void setCards(List<HotCardBean> cards) {
		this.cards = cards;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
