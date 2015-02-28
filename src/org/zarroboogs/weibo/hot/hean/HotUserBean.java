package org.zarroboogs.weibo.hot.hean;

import android.os.Parcel;
import android.os.Parcelable;

public class HotUserBean implements Parcelable {

	private long id = 0;
	private String idstr = "5187664653";
	private int clazz = 1;
	private String screen_name = "邓超";
	private String name = "邓超";
	private String province = "11";
	private String city = "1000";
	private String location = "北京";
	private String description = "";
	private String url = "";
	private String profile_image_url = "http://tp2.sinaimg.cn/5187664653/50/40058043651/1";
	private String cover_image_phone = "http://ww1.sinaimg.cn/crop.0.0.640.640.640/6cf8d7ebjw1ehfr4xa8psj20hs0hsgpg.jpg";
	private String profile_url = "dengchao";
	private String domain = "dengchao";
	private String weihao = "";
	private String gender = "m";
	private int followers_count = 20250419;
	private int friends_count = 107;
	private int pagefriends_count = 1;
	private int statuses_count = 365;
	private int favourites_count = 0;
	private String created_at = "Sat Jun 21 03:12:31 +0800 2014";
	private boolean following = false;
	private boolean allow_all_act_msg = false;
	private boolean geo_enabled = true;
	private boolean verified = true;
	private int verified_type = 0;
	private String remark = "";
	private int ptype = 5;
	private boolean allow_all_comment = true;
	private String avatar_large = "http://tp2.sinaimg.cn/5187664653/180/40058043651/1";
	private String avatar_hd = "http://ww4.sinaimg.cn/crop.0.0.638.638.1024/005F4Uyxgw1ehlsromhx8j30hs0hs0w3.jpg";
	private String verified_reason = "演员、导演邓超";
	private String verified_trade = "1017";
	private String verified_reason_url = "";
	private String verified_source = "";
	private String verified_source_url = "";
	private int verified_state = 0;
	private int verified_level = 1;
	private String verified_reason_modified = "";
	private String verified_contact_name = "";
	private String verified_contact_email = "";
	private String verified_contact_mobile = "";
	private boolean follow_me = false;
	private int online_status = 0;
	private int bi_followers_count = 99;
	private String lang = "zh-cn";
	private int star = 0;
	private int mbtype = 11;
	private int mbrank = 3;
	private int block_word = 0;
	private int block_app = 1;
	private int level = 2;
	private int type = 1;
	private long ulevel = 0;
	private int credit_score = 80;
	private int urank = 14;
	private String badge_top = "";

	private HotBadgeBean badge = null;
	private HotExtendBean extend = null;

	public HotBadgeBean getBadge() {
		return badge;
	}

	public int getCredit_score() {
		return credit_score;
	}

	public void setCredit_score(int credit_score) {
		this.credit_score = credit_score;
	}

	public int getUrank() {
		return urank;
	}

	public void setUrank(int urank) {
		this.urank = urank;
	}

	public String getBadge_top() {
		return badge_top;
	}

	public void setBadge_top(String badge_top) {
		this.badge_top = badge_top;
	}

	public HotExtendBean getExtend() {
		return extend;
	}

	public void setExtend(HotExtendBean extend) {
		this.extend = extend;
	}

	public void setBadge(HotBadgeBean badge) {
		this.badge = badge;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdstr() {
		return idstr;
	}

	public void setIdstr(String idstr) {
		this.idstr = idstr;
	}

	public int getClazz() {
		return clazz;
	}

	public void setClazz(int clazz) {
		this.clazz = clazz;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public String getCover_image_phone() {
		return cover_image_phone;
	}

	public void setCover_image_phone(String cover_image_phone) {
		this.cover_image_phone = cover_image_phone;
	}

	public String getProfile_url() {
		return profile_url;
	}

	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getWeihao() {
		return weihao;
	}

	public void setWeihao(String weihao) {
		this.weihao = weihao;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getFollowers_count() {
		return followers_count;
	}

	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	public int getFriends_count() {
		return friends_count;
	}

	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}

	public int getPagefriends_count() {
		return pagefriends_count;
	}

	public void setPagefriends_count(int pagefriends_count) {
		this.pagefriends_count = pagefriends_count;
	}

	public int getStatuses_count() {
		return statuses_count;
	}

	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}

	public int getFavourites_count() {
		return favourites_count;
	}

	public void setFavourites_count(int favourites_count) {
		this.favourites_count = favourites_count;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public boolean isFollowing() {
		return following;
	}

	public void setFollowing(boolean following) {
		this.following = following;
	}

	public boolean isAllow_all_act_msg() {
		return allow_all_act_msg;
	}

	public void setAllow_all_act_msg(boolean allow_all_act_msg) {
		this.allow_all_act_msg = allow_all_act_msg;
	}

	public boolean isGeo_enabled() {
		return geo_enabled;
	}

	public void setGeo_enabled(boolean geo_enabled) {
		this.geo_enabled = geo_enabled;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public int getVerified_type() {
		return verified_type;
	}

	public void setVerified_type(int verified_type) {
		this.verified_type = verified_type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getPtype() {
		return ptype;
	}

	public void setPtype(int ptype) {
		this.ptype = ptype;
	}

	public boolean isAllow_all_comment() {
		return allow_all_comment;
	}

	public void setAllow_all_comment(boolean allow_all_comment) {
		this.allow_all_comment = allow_all_comment;
	}

	public String getAvatar_large() {
		return avatar_large;
	}

	public void setAvatar_large(String avatar_large) {
		this.avatar_large = avatar_large;
	}

	public String getAvatar_hd() {
		return avatar_hd;
	}

	public void setAvatar_hd(String avatar_hd) {
		this.avatar_hd = avatar_hd;
	}

	public String getVerified_reason() {
		return verified_reason;
	}

	public void setVerified_reason(String verified_reason) {
		this.verified_reason = verified_reason;
	}

	public String getVerified_trade() {
		return verified_trade;
	}

	public void setVerified_trade(String verified_trade) {
		this.verified_trade = verified_trade;
	}

	public String getVerified_reason_url() {
		return verified_reason_url;
	}

	public void setVerified_reason_url(String verified_reason_url) {
		this.verified_reason_url = verified_reason_url;
	}

	public String getVerified_source() {
		return verified_source;
	}

	public void setVerified_source(String verified_source) {
		this.verified_source = verified_source;
	}

	public String getVerified_source_url() {
		return verified_source_url;
	}

	public void setVerified_source_url(String verified_source_url) {
		this.verified_source_url = verified_source_url;
	}

	public int getVerified_state() {
		return verified_state;
	}

	public void setVerified_state(int verified_state) {
		this.verified_state = verified_state;
	}

	public int getVerified_level() {
		return verified_level;
	}

	public void setVerified_level(int verified_level) {
		this.verified_level = verified_level;
	}

	public String getVerified_reason_modified() {
		return verified_reason_modified;
	}

	public void setVerified_reason_modified(String verified_reason_modified) {
		this.verified_reason_modified = verified_reason_modified;
	}

	public String getVerified_contact_name() {
		return verified_contact_name;
	}

	public void setVerified_contact_name(String verified_contact_name) {
		this.verified_contact_name = verified_contact_name;
	}

	public String getVerified_contact_email() {
		return verified_contact_email;
	}

	public void setVerified_contact_email(String verified_contact_email) {
		this.verified_contact_email = verified_contact_email;
	}

	public String getVerified_contact_mobile() {
		return verified_contact_mobile;
	}

	public void setVerified_contact_mobile(String verified_contact_mobile) {
		this.verified_contact_mobile = verified_contact_mobile;
	}

	public boolean isFollow_me() {
		return follow_me;
	}

	public void setFollow_me(boolean follow_me) {
		this.follow_me = follow_me;
	}

	public int getOnline_status() {
		return online_status;
	}

	public void setOnline_status(int online_status) {
		this.online_status = online_status;
	}

	public int getBi_followers_count() {
		return bi_followers_count;
	}

	public void setBi_followers_count(int bi_followers_count) {
		this.bi_followers_count = bi_followers_count;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public int getMbtype() {
		return mbtype;
	}

	public void setMbtype(int mbtype) {
		this.mbtype = mbtype;
	}

	public int getMbrank() {
		return mbrank;
	}

	public void setMbrank(int mbrank) {
		this.mbrank = mbrank;
	}

	public int getBlock_word() {
		return block_word;
	}

	public void setBlock_word(int block_word) {
		this.block_word = block_word;
	}

	public int getBlock_app() {
		return block_app;
	}

	public void setBlock_app(int block_app) {
		this.block_app = block_app;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	public long getUlevel() {
		return ulevel;
	}

	public void setUlevel(long ulevel) {
		this.ulevel = ulevel;
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
