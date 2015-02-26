package org.zarroboogs.weibo.bean;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class HotWeiboBean implements Parcelable{

	private CardlistInfoBean cardlistInfo = null;
	private List<CardBean> cards = null;
	
	
	
	public CardlistInfoBean getCardlistInfo() {
		return cardlistInfo;
	}

	public void setCardlistInfo(CardlistInfoBean cardlistInfo) {
		this.cardlistInfo = cardlistInfo;
	}

	public List<CardBean> getCards() {
		return cards;
	}

	public void setCards(List<CardBean> cards) {
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
