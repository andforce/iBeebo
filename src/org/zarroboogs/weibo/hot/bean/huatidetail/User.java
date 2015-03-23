package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;
import java.util.ArrayList;

public class User {
	
    private String coverImagePhone;
    private double id;
    private double biFollowersCount;
    private double urank;
    private String profileImageUrl;
    private ArrayList<Icons> icons;
    private double classProperty;
    private String verifiedContactEmail;
    private String province;
    private String abilityTags;
    private boolean verified;
    private boolean geoEnabled;
    private String url;
    private boolean followMe;
    private double statusesCount;
    private String description;
    private double type;
    private double followersCount;
    private String verifiedContactMobile;
    private String location;
    private double mbrank;
    private String avatarLarge;
    private String badgeTop;
    private String verifiedTrade;
    private double star;
    private String coverImage;
    private double onlineStatus;
    private String weihao;
    private String profileUrl;
    private String verifiedContactName;
    private String verifiedSourceUrl;
    private String screenName;
    private double pagefriendsCount;
    private String verifiedReason;
    private String name;
    private double friendsCount;
    private double mbtype;
    private double blockApp;
    private String avatarHd;
    private double creditScore;
    private String remark;
    private String createdAt;
    private double blockWord;
    private double ulevel;
    private boolean allowAllActMsg;
    private double verifiedState;
    private String domain;
    private String verifiedReasonModified;
    private double level;
    private boolean allowAllComment;
    private double verifiedLevel;
    private String verifiedReasonUrl;
    private String gender;
    private double favouritesCount;
    private String idstr;
    private double verifiedType;
    private String city;
    private String verifiedSource;
    private Badge badge;
    private Extend extend;
    private String lang;
    private double ptype;
    private boolean following;
    
    
	public User () {
		
	}	
        
    public User (JSONObject json) {
    
        this.coverImagePhone = json.optString("cover_image_phone");
        this.id = json.optDouble("id");
        this.biFollowersCount = json.optDouble("bi_followers_count");
        this.urank = json.optDouble("urank");
        this.profileImageUrl = json.optString("profile_image_url");

        this.icons = new ArrayList<Icons>();
        JSONArray arrayIcons = json.optJSONArray("icons");
        if (null != arrayIcons) {
            int iconsLength = arrayIcons.length();
            for (int i = 0; i < iconsLength; i++) {
                JSONObject item = arrayIcons.optJSONObject(i);
                if (null != item) {
                    this.icons.add(new Icons(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("icons");
            if (null != item) {
                this.icons.add(new Icons(item));
            }
        }

        this.classProperty = json.optDouble("class");
        this.verifiedContactEmail = json.optString("verified_contact_email");
        this.province = json.optString("province");
        this.abilityTags = json.optString("ability_tags");
        this.verified = json.optBoolean("verified");
        this.geoEnabled = json.optBoolean("geo_enabled");
        this.url = json.optString("url");
        this.followMe = json.optBoolean("follow_me");
        this.statusesCount = json.optDouble("statuses_count");
        this.description = json.optString("description");
        this.type = json.optDouble("type");
        this.followersCount = json.optDouble("followers_count");
        this.verifiedContactMobile = json.optString("verified_contact_mobile");
        this.location = json.optString("location");
        this.mbrank = json.optDouble("mbrank");
        this.avatarLarge = json.optString("avatar_large");
        this.badgeTop = json.optString("badge_top");
        this.verifiedTrade = json.optString("verified_trade");
        this.star = json.optDouble("star");
        this.coverImage = json.optString("cover_image");
        this.onlineStatus = json.optDouble("online_status");
        this.weihao = json.optString("weihao");
        this.profileUrl = json.optString("profile_url");
        this.verifiedContactName = json.optString("verified_contact_name");
        this.verifiedSourceUrl = json.optString("verified_source_url");
        this.screenName = json.optString("screen_name");
        this.pagefriendsCount = json.optDouble("pagefriends_count");
        this.verifiedReason = json.optString("verified_reason");
        this.name = json.optString("name");
        this.friendsCount = json.optDouble("friends_count");
        this.mbtype = json.optDouble("mbtype");
        this.blockApp = json.optDouble("block_app");
        this.avatarHd = json.optString("avatar_hd");
        this.creditScore = json.optDouble("credit_score");
        this.remark = json.optString("remark");
        this.createdAt = json.optString("created_at");
        this.blockWord = json.optDouble("block_word");
        this.ulevel = json.optDouble("ulevel");
        this.allowAllActMsg = json.optBoolean("allow_all_act_msg");
        this.verifiedState = json.optDouble("verified_state");
        this.domain = json.optString("domain");
        this.verifiedReasonModified = json.optString("verified_reason_modified");
        this.level = json.optDouble("level");
        this.allowAllComment = json.optBoolean("allow_all_comment");
        this.verifiedLevel = json.optDouble("verified_level");
        this.verifiedReasonUrl = json.optString("verified_reason_url");
        this.gender = json.optString("gender");
        this.favouritesCount = json.optDouble("favourites_count");
        this.idstr = json.optString("idstr");
        this.verifiedType = json.optDouble("verified_type");
        this.city = json.optString("city");
        this.verifiedSource = json.optString("verified_source");
        this.badge = new Badge(json.optJSONObject("badge"));
        this.extend = new Extend(json.optJSONObject("extend"));
        this.lang = json.optString("lang");
        this.ptype = json.optDouble("ptype");
        this.following = json.optBoolean("following");

    }
    
    public String getCoverImagePhone() {
        return this.coverImagePhone;
    }

    public void setCoverImagePhone(String coverImagePhone) {
        this.coverImagePhone = coverImagePhone;
    }

    public double getId() {
        return this.id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getBiFollowersCount() {
        return this.biFollowersCount;
    }

    public void setBiFollowersCount(double biFollowersCount) {
        this.biFollowersCount = biFollowersCount;
    }

    public double getUrank() {
        return this.urank;
    }

    public void setUrank(double urank) {
        this.urank = urank;
    }

    public String getProfileImageUrl() {
        return this.profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public ArrayList<Icons> getIcons() {
        return this.icons;
    }

    public void setIcons(ArrayList<Icons> icons) {
        this.icons = icons;
    }

    public double getClassProperty() {
        return this.classProperty;
    }

    public void setClassProperty(double classProperty) {
        this.classProperty = classProperty;
    }

    public String getVerifiedContactEmail() {
        return this.verifiedContactEmail;
    }

    public void setVerifiedContactEmail(String verifiedContactEmail) {
        this.verifiedContactEmail = verifiedContactEmail;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAbilityTags() {
        return this.abilityTags;
    }

    public void setAbilityTags(String abilityTags) {
        this.abilityTags = abilityTags;
    }

    public boolean getVerified() {
        return this.verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean getGeoEnabled() {
        return this.geoEnabled;
    }

    public void setGeoEnabled(boolean geoEnabled) {
        this.geoEnabled = geoEnabled;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getFollowMe() {
        return this.followMe;
    }

    public void setFollowMe(boolean followMe) {
        this.followMe = followMe;
    }

    public double getStatusesCount() {
        return this.statusesCount;
    }

    public void setStatusesCount(double statusesCount) {
        this.statusesCount = statusesCount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getType() {
        return this.type;
    }

    public void setType(double type) {
        this.type = type;
    }

    public double getFollowersCount() {
        return this.followersCount;
    }

    public void setFollowersCount(double followersCount) {
        this.followersCount = followersCount;
    }

    public String getVerifiedContactMobile() {
        return this.verifiedContactMobile;
    }

    public void setVerifiedContactMobile(String verifiedContactMobile) {
        this.verifiedContactMobile = verifiedContactMobile;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getMbrank() {
        return this.mbrank;
    }

    public void setMbrank(double mbrank) {
        this.mbrank = mbrank;
    }

    public String getAvatarLarge() {
        return this.avatarLarge;
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public String getBadgeTop() {
        return this.badgeTop;
    }

    public void setBadgeTop(String badgeTop) {
        this.badgeTop = badgeTop;
    }

    public String getVerifiedTrade() {
        return this.verifiedTrade;
    }

    public void setVerifiedTrade(String verifiedTrade) {
        this.verifiedTrade = verifiedTrade;
    }

    public double getStar() {
        return this.star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public String getCoverImage() {
        return this.coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public double getOnlineStatus() {
        return this.onlineStatus;
    }

    public void setOnlineStatus(double onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getWeihao() {
        return this.weihao;
    }

    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }

    public String getProfileUrl() {
        return this.profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getVerifiedContactName() {
        return this.verifiedContactName;
    }

    public void setVerifiedContactName(String verifiedContactName) {
        this.verifiedContactName = verifiedContactName;
    }

    public String getVerifiedSourceUrl() {
        return this.verifiedSourceUrl;
    }

    public void setVerifiedSourceUrl(String verifiedSourceUrl) {
        this.verifiedSourceUrl = verifiedSourceUrl;
    }

    public String getScreenName() {
        return this.screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public double getPagefriendsCount() {
        return this.pagefriendsCount;
    }

    public void setPagefriendsCount(double pagefriendsCount) {
        this.pagefriendsCount = pagefriendsCount;
    }

    public String getVerifiedReason() {
        return this.verifiedReason;
    }

    public void setVerifiedReason(String verifiedReason) {
        this.verifiedReason = verifiedReason;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFriendsCount() {
        return this.friendsCount;
    }

    public void setFriendsCount(double friendsCount) {
        this.friendsCount = friendsCount;
    }

    public double getMbtype() {
        return this.mbtype;
    }

    public void setMbtype(double mbtype) {
        this.mbtype = mbtype;
    }

    public double getBlockApp() {
        return this.blockApp;
    }

    public void setBlockApp(double blockApp) {
        this.blockApp = blockApp;
    }

    public String getAvatarHd() {
        return this.avatarHd;
    }

    public void setAvatarHd(String avatarHd) {
        this.avatarHd = avatarHd;
    }

    public double getCreditScore() {
        return this.creditScore;
    }

    public void setCreditScore(double creditScore) {
        this.creditScore = creditScore;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getBlockWord() {
        return this.blockWord;
    }

    public void setBlockWord(double blockWord) {
        this.blockWord = blockWord;
    }

    public double getUlevel() {
        return this.ulevel;
    }

    public void setUlevel(double ulevel) {
        this.ulevel = ulevel;
    }

    public boolean getAllowAllActMsg() {
        return this.allowAllActMsg;
    }

    public void setAllowAllActMsg(boolean allowAllActMsg) {
        this.allowAllActMsg = allowAllActMsg;
    }

    public double getVerifiedState() {
        return this.verifiedState;
    }

    public void setVerifiedState(double verifiedState) {
        this.verifiedState = verifiedState;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getVerifiedReasonModified() {
        return this.verifiedReasonModified;
    }

    public void setVerifiedReasonModified(String verifiedReasonModified) {
        this.verifiedReasonModified = verifiedReasonModified;
    }

    public double getLevel() {
        return this.level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public boolean getAllowAllComment() {
        return this.allowAllComment;
    }

    public void setAllowAllComment(boolean allowAllComment) {
        this.allowAllComment = allowAllComment;
    }

    public double getVerifiedLevel() {
        return this.verifiedLevel;
    }

    public void setVerifiedLevel(double verifiedLevel) {
        this.verifiedLevel = verifiedLevel;
    }

    public String getVerifiedReasonUrl() {
        return this.verifiedReasonUrl;
    }

    public void setVerifiedReasonUrl(String verifiedReasonUrl) {
        this.verifiedReasonUrl = verifiedReasonUrl;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getFavouritesCount() {
        return this.favouritesCount;
    }

    public void setFavouritesCount(double favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public String getIdstr() {
        return this.idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public double getVerifiedType() {
        return this.verifiedType;
    }

    public void setVerifiedType(double verifiedType) {
        this.verifiedType = verifiedType;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVerifiedSource() {
        return this.verifiedSource;
    }

    public void setVerifiedSource(String verifiedSource) {
        this.verifiedSource = verifiedSource;
    }

    public Badge getBadge() {
        return this.badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public Extend getExtend() {
        return this.extend;
    }

    public void setExtend(Extend extend) {
        this.extend = extend;
    }

    public String getLang() {
        return this.lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public double getPtype() {
        return this.ptype;
    }

    public void setPtype(double ptype) {
        this.ptype = ptype;
    }

    public boolean getFollowing() {
        return this.following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }


    
}
