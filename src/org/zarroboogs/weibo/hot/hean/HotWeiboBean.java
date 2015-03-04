package org.zarroboogs.weibo.hot.hean;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotWeiboBean implements Parcelable {

	private HotCardlistInfoBean cardlistInfo = null;
	private List<HotCardBean> cards = null;

	// ===================================//
	
	
	public MessageListBean getMessageListBean() {
		MessageListBean messageListBean = new MessageListBean();
		messageListBean.setStatuses(getMessageBeans());
		return messageListBean;
	}

	public List<MessageBean> getMessageBeans() {
		List<HotCardBean> cardBeans = getCards();
		List<MessageBean> hotMblogBeans = new ArrayList<MessageBean>();
		for (HotCardBean i : cardBeans) {
			MessageBean blog = i.getMblog();
			if (blog != null) {
				hotMblogBeans.add(blog);
			}
		}
		return hotMblogBeans;
	}

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
