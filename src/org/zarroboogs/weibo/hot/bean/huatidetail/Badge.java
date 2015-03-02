package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class Badge {
	
    private double gongyi;
    private double gongyiLevel;
    private double enterprise;
    private double zongyiji;
    private double suishoupai2014;
    private double travel2013;
    private double anniversary;
    private double taobao;
    private double hongbao2014;
    private double bindTaobao;
    private double ucDomain;
    private double dailv;
    private double hongbao2015;
    
    
	public Badge () {
		
	}	
        
    public Badge (JSONObject json) {
    
        this.gongyi = json.optDouble("gongyi");
        this.gongyiLevel = json.optDouble("gongyi_level");
        this.enterprise = json.optDouble("enterprise");
        this.zongyiji = json.optDouble("zongyiji");
        this.suishoupai2014 = json.optDouble("suishoupai_2014");
        this.travel2013 = json.optDouble("travel2013");
        this.anniversary = json.optDouble("anniversary");
        this.taobao = json.optDouble("taobao");
        this.hongbao2014 = json.optDouble("hongbao_2014");
        this.bindTaobao = json.optDouble("bind_taobao");
        this.ucDomain = json.optDouble("uc_domain");
        this.dailv = json.optDouble("dailv");
        this.hongbao2015 = json.optDouble("hongbao_2015");

    }
    
    public double getGongyi() {
        return this.gongyi;
    }

    public void setGongyi(double gongyi) {
        this.gongyi = gongyi;
    }

    public double getGongyiLevel() {
        return this.gongyiLevel;
    }

    public void setGongyiLevel(double gongyiLevel) {
        this.gongyiLevel = gongyiLevel;
    }

    public double getEnterprise() {
        return this.enterprise;
    }

    public void setEnterprise(double enterprise) {
        this.enterprise = enterprise;
    }

    public double getZongyiji() {
        return this.zongyiji;
    }

    public void setZongyiji(double zongyiji) {
        this.zongyiji = zongyiji;
    }

    public double getSuishoupai2014() {
        return this.suishoupai2014;
    }

    public void setSuishoupai2014(double suishoupai2014) {
        this.suishoupai2014 = suishoupai2014;
    }

    public double getTravel2013() {
        return this.travel2013;
    }

    public void setTravel2013(double travel2013) {
        this.travel2013 = travel2013;
    }

    public double getAnniversary() {
        return this.anniversary;
    }

    public void setAnniversary(double anniversary) {
        this.anniversary = anniversary;
    }

    public double getTaobao() {
        return this.taobao;
    }

    public void setTaobao(double taobao) {
        this.taobao = taobao;
    }

    public double getHongbao2014() {
        return this.hongbao2014;
    }

    public void setHongbao2014(double hongbao2014) {
        this.hongbao2014 = hongbao2014;
    }

    public double getBindTaobao() {
        return this.bindTaobao;
    }

    public void setBindTaobao(double bindTaobao) {
        this.bindTaobao = bindTaobao;
    }

    public double getUcDomain() {
        return this.ucDomain;
    }

    public void setUcDomain(double ucDomain) {
        this.ucDomain = ucDomain;
    }

    public double getDailv() {
        return this.dailv;
    }

    public void setDailv(double dailv) {
        this.dailv = dailv;
    }

    public double getHongbao2015() {
        return this.hongbao2015;
    }

    public void setHongbao2015(double hongbao2015) {
        this.hongbao2015 = hongbao2015;
    }


    
}
