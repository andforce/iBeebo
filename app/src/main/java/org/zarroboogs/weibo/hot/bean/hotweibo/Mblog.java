package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;

import java.util.ArrayList;

public class Mblog {

    private boolean favorited;
    private double expireTime;
    private double attitudesStatus;
    private boolean truncated;
    private double id;
    private String createdAt;
    private PageInfo pageInfo;
    private String inReplyToScreenName;
    private String mblogid;
    private ArrayList<TopicStruct> topicStruct;
    private String text;
    private String idstr;
    private ArrayList<Buttons> buttons;
    private double sourceType;
    private String geo;
    private User user;
    private double commentsCount;
    private String thumbnailPic;
    private String source;
    private double recomState;
    private double sourceAllowclick;
    private String mblogtypename;
    private ArrayList<Annotations> annotations;
    private String bmiddlePic;
    private String scheme;
    private Visible visible;
    private String inReplyToStatusId;
    private String mid;
    private ArrayList<String> picIds;
    private double repostsCount;
    private double mlevel;
    private double attitudesCount;
    private ArrayList<String> darwinTags;
    private PicInfos picInfos;
    private String inReplyToUserId;
    private ArrayList<UrlStruct> urlStruct;
    private String originalPic;


    public Mblog() {

    }

    public Mblog(JSONObject json) {

        this.favorited = json.optBoolean("favorited");
        this.expireTime = json.optDouble("expire_time");
        this.attitudesStatus = json.optDouble("attitudes_status");
        this.truncated = json.optBoolean("truncated");
        this.id = json.optDouble("id");
        this.createdAt = json.optString("created_at");
        this.pageInfo = new PageInfo(json.optJSONObject("page_info"));
        this.inReplyToScreenName = json.optString("in_reply_to_screen_name");
        this.mblogid = json.optString("mblogid");

        this.topicStruct = new ArrayList<TopicStruct>();
        JSONArray arrayTopicStruct = json.optJSONArray("topic_struct");
        if (null != arrayTopicStruct) {
            int topicStructLength = arrayTopicStruct.length();
            for (int i = 0; i < topicStructLength; i++) {
                JSONObject item = arrayTopicStruct.optJSONObject(i);
                if (null != item) {
                    this.topicStruct.add(new TopicStruct(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("topic_struct");
            if (null != item) {
                this.topicStruct.add(new TopicStruct(item));
            }
        }

        this.text = json.optString("text");
        this.idstr = json.optString("idstr");

        this.buttons = new ArrayList<Buttons>();
        JSONArray arrayButtons = json.optJSONArray("buttons");
        if (null != arrayButtons) {
            int buttonsLength = arrayButtons.length();
            for (int i = 0; i < buttonsLength; i++) {
                JSONObject item = arrayButtons.optJSONObject(i);
                if (null != item) {
                    this.buttons.add(new Buttons(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("buttons");
            if (null != item) {
                this.buttons.add(new Buttons(item));
            }
        }

        this.sourceType = json.optDouble("source_type");
        this.geo = json.optString("geo");
        this.user = new User(json.optJSONObject("user"));
        this.commentsCount = json.optDouble("comments_count");
        this.thumbnailPic = json.optString("thumbnail_pic");
        this.source = json.optString("source");
        this.recomState = json.optDouble("recom_state");
        this.sourceAllowclick = json.optDouble("source_allowclick");
        this.mblogtypename = json.optString("mblogtypename");

        this.annotations = new ArrayList<Annotations>();
        JSONArray arrayAnnotations = json.optJSONArray("annotations");
        if (null != arrayAnnotations) {
            int annotationsLength = arrayAnnotations.length();
            for (int i = 0; i < annotationsLength; i++) {
                JSONObject item = arrayAnnotations.optJSONObject(i);
                if (null != item) {
                    this.annotations.add(new Annotations(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("annotations");
            if (null != item) {
                this.annotations.add(new Annotations(item));
            }
        }

        this.bmiddlePic = json.optString("bmiddle_pic");
        this.scheme = json.optString("scheme");
        this.visible = new Visible(json.optJSONObject("visible"));
        this.inReplyToStatusId = json.optString("in_reply_to_status_id");
        this.mid = json.optString("mid");

        this.picIds = new ArrayList<String>();
        JSONArray arrayPicIds = json.optJSONArray("pic_ids");
        if (null != arrayPicIds) {
            int picIdsLength = arrayPicIds.length();
            for (int i = 0; i < picIdsLength; i++) {
                String item = arrayPicIds.optString(i);
                if (null != item) {
                    this.picIds.add(item);
                }
            }
        } else {
            String item = json.optString("pic_ids");
            if (null != item) {
                this.picIds.add(item);
            }
        }

        this.repostsCount = json.optDouble("reposts_count");
        this.mlevel = json.optDouble("mlevel");
        this.attitudesCount = json.optDouble("attitudes_count");

        this.darwinTags = new ArrayList<String>();
        JSONArray arrayDarwinTags = json.optJSONArray("darwin_tags");
        if (null != arrayDarwinTags) {
            int darwinTagsLength = arrayDarwinTags.length();
            for (int i = 0; i < darwinTagsLength; i++) {
                String item = arrayDarwinTags.optString(i);
                if (null != item) {
                    this.darwinTags.add(item);
                }
            }
        } else {
            String item = json.optString("darwin_tags");
            if (null != item) {
                this.darwinTags.add(item);
            }
        }

        this.picInfos = new PicInfos();
        this.inReplyToUserId = json.optString("in_reply_to_user_id");

        this.urlStruct = new ArrayList<UrlStruct>();
        JSONArray arrayUrlStruct = json.optJSONArray("url_struct");
        if (null != arrayUrlStruct) {
            int urlStructLength = arrayUrlStruct.length();
            for (int i = 0; i < urlStructLength; i++) {
                JSONObject item = arrayUrlStruct.optJSONObject(i);
                if (null != item) {
                    this.urlStruct.add(new UrlStruct(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("url_struct");
            if (null != item) {
                this.urlStruct.add(new UrlStruct(item));
            }
        }

        this.originalPic = json.optString("original_pic");

    }

    public boolean getFavorited() {
        return this.favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public double getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(double expireTime) {
        this.expireTime = expireTime;
    }

    public double getAttitudesStatus() {
        return this.attitudesStatus;
    }

    public void setAttitudesStatus(double attitudesStatus) {
        this.attitudesStatus = attitudesStatus;
    }

    public boolean getTruncated() {
        return this.truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public double getId() {
        return this.id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public PageInfo getPageInfo() {
        return this.pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getInReplyToScreenName() {
        return this.inReplyToScreenName;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public String getMblogid() {
        return this.mblogid;
    }

    public void setMblogid(String mblogid) {
        this.mblogid = mblogid;
    }

    public ArrayList<TopicStruct> getTopicStruct() {
        return this.topicStruct;
    }

    public void setTopicStruct(ArrayList<TopicStruct> topicStruct) {
        this.topicStruct = topicStruct;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIdstr() {
        return this.idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public ArrayList<Buttons> getButtons() {
        return this.buttons;
    }

    public void setButtons(ArrayList<Buttons> buttons) {
        this.buttons = buttons;
    }

    public double getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(double sourceType) {
        this.sourceType = sourceType;
    }

    public String getGeo() {
        return this.geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getCommentsCount() {
        return this.commentsCount;
    }

    public void setCommentsCount(double commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getThumbnailPic() {
        return this.thumbnailPic;
    }

    public void setThumbnailPic(String thumbnailPic) {
        this.thumbnailPic = thumbnailPic;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getRecomState() {
        return this.recomState;
    }

    public void setRecomState(double recomState) {
        this.recomState = recomState;
    }

    public double getSourceAllowclick() {
        return this.sourceAllowclick;
    }

    public void setSourceAllowclick(double sourceAllowclick) {
        this.sourceAllowclick = sourceAllowclick;
    }

    public String getMblogtypename() {
        return this.mblogtypename;
    }

    public void setMblogtypename(String mblogtypename) {
        this.mblogtypename = mblogtypename;
    }

    public ArrayList<Annotations> getAnnotations() {
        return this.annotations;
    }

    public void setAnnotations(ArrayList<Annotations> annotations) {
        this.annotations = annotations;
    }

    public String getBmiddlePic() {
        return this.bmiddlePic;
    }

    public void setBmiddlePic(String bmiddlePic) {
        this.bmiddlePic = bmiddlePic;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public Visible getVisible() {
        return this.visible;
    }

    public void setVisible(Visible visible) {
        this.visible = visible;
    }

    public String getInReplyToStatusId() {
        return this.inReplyToStatusId;
    }

    public void setInReplyToStatusId(String inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public ArrayList<String> getPicIds() {
        return this.picIds;
    }

    public void setPicIds(ArrayList<String> picIds) {
        this.picIds = picIds;
    }

    public double getRepostsCount() {
        return this.repostsCount;
    }

    public void setRepostsCount(double repostsCount) {
        this.repostsCount = repostsCount;
    }

    public double getMlevel() {
        return this.mlevel;
    }

    public void setMlevel(double mlevel) {
        this.mlevel = mlevel;
    }

    public double getAttitudesCount() {
        return this.attitudesCount;
    }

    public void setAttitudesCount(double attitudesCount) {
        this.attitudesCount = attitudesCount;
    }

    public ArrayList<String> getDarwinTags() {
        return this.darwinTags;
    }

    public void setDarwinTags(ArrayList<String> darwinTags) {
        this.darwinTags = darwinTags;
    }

    public PicInfos getPicInfos() {
        return this.picInfos;
    }

    public void setPicInfos(PicInfos picInfos) {
        this.picInfos = picInfos;
    }

    public String getInReplyToUserId() {
        return this.inReplyToUserId;
    }

    public void setInReplyToUserId(String inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
    }

    public ArrayList<UrlStruct> getUrlStruct() {
        return this.urlStruct;
    }

    public void setUrlStruct(ArrayList<UrlStruct> urlStruct) {
        this.urlStruct = urlStruct;
    }

    public String getOriginalPic() {
        return this.originalPic;
    }

    public void setOriginalPic(String originalPic) {
        this.originalPic = originalPic;
    }


}
