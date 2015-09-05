package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class PageInfo {
	
    private String pageDesc;
    private String pageId;
    private double actStatus;
    private PicInfo picInfo;
    private MediaInfo mediaInfo;
    private String content1;
    private double type;
    private String objectType;
    private String pageUrl;
    private String preload;
    private String tips;
    private String pageTitle;
    private String objectId;
    private String content3;
    private String pagePic;
    private String content2;
    private String typeIcon;
    private double oid;
    
    
	public PageInfo () {
		
	}	
        
    public PageInfo (JSONObject json) {
    
        this.pageDesc = json.optString("page_desc");
        this.pageId = json.optString("page_id");
        this.actStatus = json.optDouble("act_status");
        this.picInfo = new PicInfo(json.optJSONObject("pic_info"));
        this.mediaInfo = new MediaInfo(json.optJSONObject("media_info"));
        this.content1 = json.optString("content1");
        this.type = json.optDouble("type");
        this.objectType = json.optString("object_type");
        this.pageUrl = json.optString("page_url");
        this.preload = json.optString("preload");
        this.tips = json.optString("tips");
        this.pageTitle = json.optString("page_title");
        this.objectId = json.optString("object_id");
        this.content3 = json.optString("content3");
        this.pagePic = json.optString("page_pic");
        this.content2 = json.optString("content2");
        this.typeIcon = json.optString("type_icon");
        this.oid = json.optDouble("oid");

    }
    
    public String getPageDesc() {
        return this.pageDesc;
    }

    public void setPageDesc(String pageDesc) {
        this.pageDesc = pageDesc;
    }

    public String getPageId() {
        return this.pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public double getActStatus() {
        return this.actStatus;
    }

    public void setActStatus(double actStatus) {
        this.actStatus = actStatus;
    }

    public PicInfo getPicInfo() {
        return this.picInfo;
    }

    public void setPicInfo(PicInfo picInfo) {
        this.picInfo = picInfo;
    }

    public MediaInfo getMediaInfo() {
        return this.mediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public String getContent1() {
        return this.content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public double getType() {
        return this.type;
    }

    public void setType(double type) {
        this.type = type;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getPageUrl() {
        return this.pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPreload() {
        return this.preload;
    }

    public void setPreload(String preload) {
        this.preload = preload;
    }

    public String getTips() {
        return this.tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContent3() {
        return this.content3;
    }

    public void setContent3(String content3) {
        this.content3 = content3;
    }

    public String getPagePic() {
        return this.pagePic;
    }

    public void setPagePic(String pagePic) {
        this.pagePic = pagePic;
    }

    public String getContent2() {
        return this.content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public String getTypeIcon() {
        return this.typeIcon;
    }

    public void setTypeIcon(String typeIcon) {
        this.typeIcon = typeIcon;
    }

    public double getOid() {
        return this.oid;
    }

    public void setOid(double oid) {
        this.oid = oid;
    }


    
}
