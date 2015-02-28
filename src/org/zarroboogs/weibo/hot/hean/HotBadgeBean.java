package org.zarroboogs.weibo.hot.hean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotBadgeBean implements Parcelable {

	private int uc_domain = 0;
	private int enterprise = 0;
	private int anniversary = 0;
	private int taobao = 0;
	private int travel2013 = 0;
	private int gongyi = 0;
	private int gongyi_level = 0;
	private int bind_taobao = 0;
	private int hongbao_2014 = 0;
	private int suishoupai_2014 = 0;
	private int dailv = 0;
	private int zongyiji = 0;
	private int hongbao_2015 = 1;

	public int getUc_domain() {
		return uc_domain;
	}

	public void setUc_domain(int uc_domain) {
		this.uc_domain = uc_domain;
	}

	public int getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(int enterprise) {
		this.enterprise = enterprise;
	}

	public int getAnniversary() {
		return anniversary;
	}

	public void setAnniversary(int anniversary) {
		this.anniversary = anniversary;
	}

	public int getTaobao() {
		return taobao;
	}

	public void setTaobao(int taobao) {
		this.taobao = taobao;
	}

	public int getTravel2013() {
		return travel2013;
	}

	public void setTravel2013(int travel2013) {
		this.travel2013 = travel2013;
	}

	public int getGongyi() {
		return gongyi;
	}

	public void setGongyi(int gongyi) {
		this.gongyi = gongyi;
	}

	public int getGongyi_level() {
		return gongyi_level;
	}

	public void setGongyi_level(int gongyi_level) {
		this.gongyi_level = gongyi_level;
	}

	public int getBind_taobao() {
		return bind_taobao;
	}

	public void setBind_taobao(int bind_taobao) {
		this.bind_taobao = bind_taobao;
	}

	public int getHongbao_2014() {
		return hongbao_2014;
	}

	public void setHongbao_2014(int hongbao_2014) {
		this.hongbao_2014 = hongbao_2014;
	}

	public int getSuishoupai_2014() {
		return suishoupai_2014;
	}

	public void setSuishoupai_2014(int suishoupai_2014) {
		this.suishoupai_2014 = suishoupai_2014;
	}

	public int getDailv() {
		return dailv;
	}

	public void setDailv(int dailv) {
		this.dailv = dailv;
	}

	public int getZongyiji() {
		return zongyiji;
	}

	public void setZongyiji(int zongyiji) {
		this.zongyiji = zongyiji;
	}

	public int getHongbao_2015() {
		return hongbao_2015;
	}

	public void setHongbao_2015(int hongbao_2015) {
		this.hongbao_2015 = hongbao_2015;
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
