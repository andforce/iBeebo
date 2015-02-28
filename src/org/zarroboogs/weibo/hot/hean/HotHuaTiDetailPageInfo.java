package org.zarroboogs.weibo.hot.hean;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class HotHuaTiDetailPageInfo implements Parcelable {

	private String v_p = "18";
	private String containerid = "100808bbba7092c180e4f6474f31b5f538d5ab";
	private String page_type_name = "话题";
	private String title_top = "话题";
	private String nick = "#柴静雾霾调查#";
	private String page_title = "#柴静雾霾调查#";
	private String page_attr = "柴静雾霾调查";
	private String page_url = "http://weibo.com/p/100808bbba7092c180e4f6474f31b5f538d5ab";
	private int display_arrow = 1;
	private int attitudes_status = 0;
	private String desc_scheme = "sinaweibo://cardlist?title=%E8%AF%9D%E9%A2%98%E8%AF%A6%E6%83%85&containerid=107303bbba7092c180e4f6474f31b5f538d5ab_-_ext_intro&extparam=";
	private String desc = "主持人：优酷";
	private String portrait = "http://ww2.sinaimg.cn/thumbnail/61ecbb3djw1epp1naf60nj2050050mx7.jpg";
	private int total = 37;
	private String page_type = "08";
	private String background = "http://u1.sinaimg.cn/upload/page-cover/page_cover_topic_2x.jpg";

	private List<HotHuaTiButtons> buttons = null;

	// ==================================================//

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getV_p() {
		return v_p;
	}

	public void setV_p(String v_p) {
		this.v_p = v_p;
	}

	public String getContainerid() {
		return containerid;
	}

	public void setContainerid(String containerid) {
		this.containerid = containerid;
	}

	public String getPage_type_name() {
		return page_type_name;
	}

	public void setPage_type_name(String page_type_name) {
		this.page_type_name = page_type_name;
	}

	public String getTitle_top() {
		return title_top;
	}

	public void setTitle_top(String title_top) {
		this.title_top = title_top;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPage_title() {
		return page_title;
	}

	public void setPage_title(String page_title) {
		this.page_title = page_title;
	}

	public String getPage_attr() {
		return page_attr;
	}

	public void setPage_attr(String page_attr) {
		this.page_attr = page_attr;
	}

	public String getPage_url() {
		return page_url;
	}

	public void setPage_url(String page_url) {
		this.page_url = page_url;
	}

	public int getDisplay_arrow() {
		return display_arrow;
	}

	public void setDisplay_arrow(int display_arrow) {
		this.display_arrow = display_arrow;
	}

	public int getAttitudes_status() {
		return attitudes_status;
	}

	public void setAttitudes_status(int attitudes_status) {
		this.attitudes_status = attitudes_status;
	}

	public String getDesc_scheme() {
		return desc_scheme;
	}

	public void setDesc_scheme(String desc_scheme) {
		this.desc_scheme = desc_scheme;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getPage_type() {
		return page_type;
	}

	public void setPage_type(String page_type) {
		this.page_type = page_type;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public List<HotHuaTiButtons> getButtons() {
		return buttons;
	}

	public void setButtons(List<HotHuaTiButtons> buttons) {
		this.buttons = buttons;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
