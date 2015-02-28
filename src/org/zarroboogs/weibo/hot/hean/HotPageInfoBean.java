package org.zarroboogs.weibo.hot.hean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotPageInfoBean implements Parcelable {

	private String page_id = "10080840beb8cb2a625f324d8da540a540c938";
	private int type = 0;
	private String page_title = "#秦思瀚我们在等你#";
	private String page_url = "sinaweibo://pageinfo?containerid=10080840beb8cb2a625f324d8da540a540c938&containerid=10080840beb8cb2a625f324d8da540a540c938&extparam=%E7%A7%A6%E6%80%9D%E7%80%9A%E6%88%91%E4%BB%AC%E5%9C%A8%E7%AD%89%E4%BD%A0";
	private String page_pic = "http://ww2.sinaimg.cn/thumbnail/6605f4e1jw1epmkygq1xlj20go0m8gnc.jpg";
	private String page_desc = "谢谢大家！@秦思瀚 能有这么多朋友的关心是他的福气！我也希望他的福气能一直延续直到他康复那天！这是今天秦思瀚和成都那边的献血点最新的情况，血小板合格人数现在还不知道，等知道后给大家公布。";
	private String object_type = "topic";
	private String tips = "4397人关注";
	private String object_id = "1022:10080840beb8cb2a625f324d8da540a540c938";

	// =======================================================/

	public String getPage_id() {
		return page_id;
	}

	public void setPage_id(String page_id) {
		this.page_id = page_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPage_title() {
		return page_title;
	}

	public void setPage_title(String page_title) {
		this.page_title = page_title;
	}

	public String getPage_url() {
		return page_url;
	}

	public void setPage_url(String page_url) {
		this.page_url = page_url;
	}

	public String getPage_pic() {
		return page_pic;
	}

	public void setPage_pic(String page_pic) {
		this.page_pic = page_pic;
	}

	public String getPage_desc() {
		return page_desc;
	}

	public void setPage_desc(String page_desc) {
		this.page_desc = page_desc;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
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
