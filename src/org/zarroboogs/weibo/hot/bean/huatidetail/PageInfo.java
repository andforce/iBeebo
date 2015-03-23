package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;


public class PageInfo {
	
    private String pageId;
    private double actStatus;
    private String content1;
    private double type;
    private String objectType;
    private String content2;
    private String scheme;
    private String pageTitle;
    private String pageUrl;
    private String objectId;
    private String content3;
    private String pagePic;
    private MediaInfo mediaInfo;
    private String typeIcon;
    private String preload;
    
    
	public PageInfo () {
		
	}	
        
    public PageInfo (JSONObject json) {
    
        this.pageId = json.optString("page_id");
        this.actStatus = json.optDouble("act_status");
        this.content1 = json.optString("content1");
        this.type = json.optDouble("type");
        this.objectType = json.optString("object_type");
        this.content2 = json.optString("content2");
        this.scheme = json.optString("scheme");
        this.pageTitle = json.optString("page_title");
        this.pageUrl = json.optString("page_url");
        this.objectId = json.optString("object_id");
        this.content3 = json.optString("content3");
        this.pagePic = json.optString("page_pic");
        this.mediaInfo = new MediaInfo(json.optJSONObject("media_info"));
        this.typeIcon = json.optString("type_icon");
        this.preload = json.optString("preload");

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

    public String getContent2() {
        return this.content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageUrl() {
        return this.pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
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

    public MediaInfo getMediaInfo() {
        return this.mediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public String getTypeIcon() {
        return this.typeIcon;
    }

    public void setTypeIcon(String typeIcon) {
        this.typeIcon = typeIcon;
    }

    public String getPreload() {
        return this.preload;
    }

    public void setPreload(String preload) {
        this.preload = preload;
    }


    
}
