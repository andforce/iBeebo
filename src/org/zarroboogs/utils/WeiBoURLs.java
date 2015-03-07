
package org.zarroboogs.utils;

import org.zarroboogs.weibo.support.utils.Utility;

import android.R.string;

/**
 * User: qii Date: 12-7-28
 */
public class WeiBoURLs {
	
	
	  public static final String KEY_FLURRY = "3MVKXSRZ4PNHT4KG8HJF";
	  public static final String SINA_REDIRECT_URL = "http://oauth.weico.cc";
	  public static final String SINA_SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,invitation_write";
	  public static String passwordUrl = "http://m.weibo.cn/setting/forgotpwd?vt=4&wm=ig_0001_index";
	  public static String regUrl = "http://weibo.cn/dpool/ttt/h5/reg.php";
	  
	  
    // base url
    private static final String URL_SINA_WEIBO = "https://api.weibo.com/2/";

    // login
    public static final String UID = URL_SINA_WEIBO + "account/get_uid.json";
    
    //
    public static final String URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2/authorize";
    public static final String APP_KEY = "211160679";
    public static final String APP_SECRET = "63b64d531b98c2dbff2443816f274dd3";//Utility.rot47("57cag6gg226g35b`7a_cg`5`ch4gde65");
    public static final String DIRECT_URL = SINA_REDIRECT_URL;//Utility.rot47("9EEADi^^2A:]H6:3@]4@>^@2FE9a^5672F=E]9E>=");

    // main timeline
    public static final String FRIENDS_TIMELINE = URL_SINA_WEIBO + "statuses/friends_timeline.json";
    public static final String COMMENTS_MENTIONS_TIMELINE = URL_SINA_WEIBO + "comments/mentions.json";
    public static final String STATUSES_MENTIONS_TIMELINE = URL_SINA_WEIBO + "statuses/mentions.json";
    public static final String COMMENTS_TO_ME_TIMELINE = URL_SINA_WEIBO + "comments/to_me.json";
    public static final String COMMENTS_BY_ME_TIMELINE = URL_SINA_WEIBO + "comments/by_me.json";
    public static final String BILATERAL_TIMELINE = URL_SINA_WEIBO + "statuses/bilateral_timeline.json";
    public static final String TIMELINE_RE_CMT_COUNT = URL_SINA_WEIBO + "statuses/count.json";

    // group timeline
    public static final String FRIENDSGROUP_INFO = URL_SINA_WEIBO + "friendships/groups.json";
    public static final String FRIENDSGROUP_TIMELINE = URL_SINA_WEIBO + "friendships/groups/timeline.json";

    // general timeline
    public static final String COMMENTS_TIMELINE_BY_MSGID = URL_SINA_WEIBO + "comments/show.json";
    public static final String REPOSTS_TIMELINE_BY_MSGID = URL_SINA_WEIBO + "statuses/repost_timeline.json";

    // user profile
    public static final String STATUSES_TIMELINE_BY_ID = URL_SINA_WEIBO + "statuses/user_timeline.json";
    public static final String USER_SHOW = URL_SINA_WEIBO + "users/show.json";
    public static final String USER_DOMAIN_SHOW = URL_SINA_WEIBO + "users/domain_show.json";

    // browser
    public static final String STATUSES_SHOW = URL_SINA_WEIBO + "statuses/show.json";

    // short url
    public static final String SHORT_URL_SHARE_COUNT = URL_SINA_WEIBO + "short_url/share/counts.json";
    public static final String SHORT_URL_SHARE_TIMELINE = URL_SINA_WEIBO + "short_url/share/statuses.json";

    public static final String MID_TO_ID = URL_SINA_WEIBO + "statuses/queryid.json";

    // send weibo
    public static final String STATUSES_UPDATE = URL_SINA_WEIBO + "statuses/update.json";
    public static final String STATUSES_UPLOAD = URL_SINA_WEIBO + "statuses/upload.json";
    public static final String STATUSES_DESTROY = URL_SINA_WEIBO + "statuses/destroy.json";

    public static final String REPOST_CREATE = URL_SINA_WEIBO + "statuses/repost.json";

    public static final String COMMENT_CREATE = URL_SINA_WEIBO + "comments/create.json";
    public static final String COMMENT_DESTROY = URL_SINA_WEIBO + "comments/destroy.json";
    public static final String COMMENT_REPLY = URL_SINA_WEIBO + "comments/reply.json";

    // favourite
    public static final String MYFAV_LIST = URL_SINA_WEIBO + "favorites.json";

    public static final String FAV_CREATE = URL_SINA_WEIBO + "favorites/create.json";
    public static final String FAV_DESTROY = URL_SINA_WEIBO + "favorites/destroy.json";

    // relationship
    public static final String FRIENDS_LIST_BYID = URL_SINA_WEIBO + "friendships/friends.json";
    public static final String FOLLOWERS_LIST_BYID = URL_SINA_WEIBO + "friendships/followers.json";

    public static final String FRIENDSHIPS_CREATE = URL_SINA_WEIBO + "friendships/create.json";
    public static final String FRIENDSHIPS_DESTROY = URL_SINA_WEIBO + "friendships/destroy.json";
    public static final String FRIENDSHIPS_FOLLOWERS_DESTROY = URL_SINA_WEIBO + "friendships/followers/destroy.json";

    // gps location info
    public static final String GOOGLELOCATION = "http://maps.google.com/maps/api/geocode/json";

    // search
    public static final String AT_USER = URL_SINA_WEIBO + "search/suggestions/at_users.json";
    public static final String TOPIC_SEARCH = URL_SINA_WEIBO + "search/topics.json";

    // topic
    public static final String TOPIC_USER_LIST = URL_SINA_WEIBO + "trends.json";
    public static final String TOPIC_FOLLOW = URL_SINA_WEIBO + "trends/follow.json";
    public static final String TOPIC_DESTROY = URL_SINA_WEIBO + "trends/destroy.json";
    public static final String TOPIC_RELATIONSHIP = URL_SINA_WEIBO + "trends/is_follow.json";

    // unread messages
    public static final String UNREAD_COUNT = URL_SINA_WEIBO + "remind/unread_count.json";
    public static final String UNREAD_CLEAR = URL_SINA_WEIBO + "remind/set_count.json";

    // remark
    public static final String REMARK_UPDATE = URL_SINA_WEIBO + "friendships/remark/update.json";

    public static final String TAGS = URL_SINA_WEIBO + "tags.json";

    public static final String EMOTIONS = URL_SINA_WEIBO + "emotions.json";

    // group
    public static final String GROUP_MEMBER_LIST = URL_SINA_WEIBO + "friendships/groups/listed.json";
    public static final String GROUP_MEMBER_ADD = URL_SINA_WEIBO + "friendships/groups/members/add.json";
    public static final String GROUP_MEMBER_DESTROY = URL_SINA_WEIBO + "friendships/groups/members/destroy.json";

    public static final String GROUP_CREATE = URL_SINA_WEIBO + "friendships/groups/create.json";
    public static final String GROUP_DESTROY = URL_SINA_WEIBO + "friendships/groups/destroy.json";
    public static final String GROUP_UPDATE = URL_SINA_WEIBO + "friendships/groups/update.json";

    // nearby
    public static final String NEARBY_USER = URL_SINA_WEIBO + "place/nearby/users.json";
    public static final String NEARBY_STATUS = URL_SINA_WEIBO + "place/nearby_timeline.json";

    // map
    public static final String STATIC_MAP = URL_SINA_WEIBO + "location/base/get_map_image.json";

    public static final String BAIDU_GEO_CODER_MAP = "http://api.map.baidu.com/geocoder/v2/?ak=AAacde37a912803101fe91fb2de38c30&coordtype=wgs84ll&output=json&pois=0&location=%f,%f";

    /**
     * black magic
     */

    // oauth2 and refresh token
    public static final String OAUTH2_ACCESS_TOKEN = URL_SINA_WEIBO + "oauth2/access_token";

    // search
    public static final String STATUSES_SEARCH = URL_SINA_WEIBO + "search/statuses.json";
    public static final String USERS_SEARCH = URL_SINA_WEIBO + "search/users.json";

    // direct message
    public static final String DM_RECEIVED = URL_SINA_WEIBO + "direct_messages.json";
    public static final String DM_SENT = URL_SINA_WEIBO + "direct_messages/new.json";
    public static final String DM_USERLIST = URL_SINA_WEIBO + "direct_messages/user_list.json";
    public static final String DM_CONVERSATION = URL_SINA_WEIBO + "direct_messages/conversation.json";
    public static final String DM_CREATE = URL_SINA_WEIBO + "direct_messages/new.json";
    public static final String DM_DESTROY = URL_SINA_WEIBO + "direct_messages/destroy.json";
    public static final String DM_BATH_DESTROY = URL_SINA_WEIBO + "direct_messages/destroy_batch";

    // edit my profile
    public static final String MYPROFILE_EDIT = URL_SINA_WEIBO + "account/profile/basic_update.json";
    public static final String AVATAR_UPLOAD = URL_SINA_WEIBO + "account/avatar/upload.json";
    
    // heart
    // give heart
    public static final String GIVE_HEART = URL_SINA_WEIBO + "attitudes/create.json";
    // delete heart
    public static final String DELETE_HEART = URL_SINA_WEIBO + "attitudes/destroy.json";
    // show heart
    public static final String SHOW_HEART = URL_SINA_WEIBO + "attitudes/show.json";
    
    
    public static String hotWeiboMeiTu(String gsid, int page) {
		String url = "http://api.weibo.cn/2/cardlist?"
				+ "gsid=" + gsid
				+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_2899_-_ctg1_2899&fid=102803_ctg1_2899_-_ctg1_2899&lfid=2305090002_1573&"
				+ "page=" + page;
		return url;
	}
    // lvxing
    public static String hotWeiboTravel(String gsid, int page) {
		String url = "http://api.weibo.cn/2/cardlist?"
				+ "gsid=" + gsid
				+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_2599_-_ctg1_2599&fid=102803_ctg1_2599_-_ctg1_2599&lfid=2305090002_1573&"
				+ "page=" + page;
		return url;
	}
    // 科技
    public static String hotWeiboKeji(String gsid, int page){
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_2099_-_ctg1_2099&fid=102803_ctg1_2099_-_ctg1_2099&lfid=2305090002_1573&"
    			+ "page=" + page;
    	return url;
    }
    // 美女
    public static String hotWeiboMeiNv(String gsid, int page){
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_2299_-_ctg1_2299&fid=102803_ctg1_2299_-_ctg1_2299&lfid=2305090002_1573&"
    			+ "page=" + page;
    	return url;
    }
    // 萌宠
    public static String hotWeiboPet(String gsid, int page){
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_2799_-_ctg1_2799&fid=102803_ctg1_2799_-_ctg1_2799&lfid=2305090002_1573&"
    			+ "page=" + page;
    	return url;
    }
    
    // 囧人糗事
    public static String hotWeioJiushi(String gsid, int page) {
		String url = "http://api.weibo.cn/2/cardlist?"
				+ "gsid=" + gsid
				+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_6199_-_ctg1_6199&fid=102803_ctg1_6199_-_ctg1_6199&lfid=2305090002_1573&"
				+ "page=" + page;
		return url;
	}
    //笑话
    public static String hotWeiboXiaoHua(String gsid, int page) {
		String url = "http://api.weibo.cn/2/cardlist?"
				+ "gsid=" + gsid
				+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_1899_-_ctg1_1899&fid=102803_ctg1_1899_-_ctg1_1899&lfid=2305090002_1573&"
				+ "page=" + page;
		return url;
	}
    // 爆料
    public static String hotWeiboBaoLiao(String gsid, int page) {
		String url = "http://api.weibo.cn/2/cardlist?"
				+ "gsid=" + gsid
				+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_1799_-_ctg1_1799&fid=102803_ctg1_1799_-_ctg1_1799&lfid=2305090002_1573&"
				+ "page=" + page;
		return url;
	}
    // 视频
    public static String hotWeiboVideo(String gsid, int page){
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_1199_-_ctg1_1199&fid=102803_ctg1_1199_-_ctg1_1199&lfid=2305090002_1573&"
    			+ "page=" + page;
    	return url;
    }
    // 神最右
    public static String hotWeiboZuiyou(String gsid, int page){
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051393010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.3__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_6399_-_ctg1_6399&fid=102803_ctg1_6399_-_ctg1_6399&lfid=2305090002_1573&"
    			+ "page=" + page;
    	return url;
    }
    public static String hotWeiboYestoday(String gsid, int page){
    	
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_8899_-_ctg1_8899&fid=102803_ctg1_8899_-_ctg1_8899&lfid=102803_ctg1_8999_-_ctg1_8999&"
    			+ "page=" + page;
    	return url;
    	
    }
    
    
    public static String hotWeiboQianTian(String gsid, int page){
    	
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=10&containerid=102803_ctg1_8799_-_ctg1_8799&fid=102803_ctg1_8799_-_ctg1_8799&lfid=102803_ctg1_8899_-_ctg1_8899&"
    			+ "page=" + page;
    	return url;
    	
    }
    
    
    // hack 小时热门微博
    public static String hotWeiboUrl(String gsid, int page){
		String url = "http://api.weibo.cn/2/cardlist?"
				+ "gsid="+ gsid + "&"
				+ "wm=3333_2001&"
				+ "i=d5d6f09&"
				+ "b=1&"
				+ "from=1051293010&"
				+ "c=iphone&"
				+ "v_p=18&"
				+ "skin=default&"
				+ "v_f=1&"
				+ "s=a57eef07&"
				+ "lang=zh_CN&"
				+ "ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&"
				+ "uicode=10000011&"
				+ "luicode=10000011&"
				+ "count=10&"
				+ "containerid=102803_ctg1_8999_-_ctg1_8999&"
				+ "fid=102803_ctg1_8999_-_ctg1_8999&"
				+ "lfid=102803&"
				+ "page="+page+"";
		return url;
    }

    //消费数码
    	public static String hotHuatiDigit(String gsid, int page) {
			String url = "http://api.weibo.cn/2/cardlist?"
					+ "gsid=" + gsid
					+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&containerid=100803_ctg1_131_-_page_topics_ctg1__131&fid=100803_ctg1_131_-_page_topics_ctg1__131&lfid=100803_-_page_ctg1_list&"
					+ "page=" + page;
			return url;
		}
    //it互联网
    	public static String hotHuatiIT(String gsid, int page) {
    		String url = "http://api.weibo.cn/2/cardlist?"
    		+ "gsid=" + gsid
    		+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&containerid=100803_ctg1_138_-_page_topics_ctg1__138&fid=100803_ctg1_138_-_page_topics_ctg1__138&lfid=100803_-_page_ctg1_list&"
    		+ "page=" + page;
    		return url;
    	}

    //幽默搞笑
    public static String hotHuatiHumor(String gsid, int page) {
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&containerid=100803_ctg1_140_-_page_topics_ctg1__140&fid=100803_ctg1_140_-_page_topics_ctg1__140&lfid=100803_-_page_ctg1_list&"
    			+ "page=" + page;
    	return url;
    }
    
//    动物萌宠
    public static String hotHuatiDog(String gsid, int page) {
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&containerid=100803_ctg1_128_-_page_topics_ctg1__128&fid=100803_ctg1_128_-_page_topics_ctg1__128&lfid=100803_-_page_ctg1_list&"
    			+ "page=" + page;
    	return url;
    }
    
//    创意征集
    public static String hotHuatiOriginality(String gsid, int page) {
    	String url = "http://api.weibo.cn/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&containerid=100803_ctg1_9_-_page_topics_ctg1__9&fid=100803_ctg1_9_-_page_topics_ctg1__9&lfid=100803_-_page_ctg1_list&"
    			+ "page=" + page;
    	return url;
    }
    
    	// 摄影
    public static String hotHuaTiShot(String gsid, int page) {
		String url = "http://api.weibo.cn/2/cardlist?"
				+ "gsid=" + gsid
				+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&containerid=100803_ctg1_123_-_page_topics_ctg1__123&fid=100803_ctg1_123_-_page_topics_ctg1__123&lfid=100803_-_page_ctg1_list&"
				+ "page=" + page;
		return url;
	}
    // 电影
    public static String hotHuaTiFilm(String gsid, int page) {
		String url ="http://api.weibo.cn/2/cardlist?"
				+ "gsid=" + gsid
				+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&containerid=100803_ctg1_100_-_page_topics_ctg1__100&fid=100803_ctg1_100_-_page_topics_ctg1__100&lfid=100803_-_page_ctg1_list&"
				+ "page=" + page;
		return url;
	}
    public static String hotHuaTiOneHouOur(String gsid, int page){
    	
    	String url = "http://api.weibo.cn/2/cardlist?uicode=10000011&lcardid=uve_popular_topics&c=android&i=8764dac&s=b48eb5c9&ua=smartisan-SM701__weibo__5.1.1__android__android4.4.2&wm=14010_0013&fid=100803&uid=1878230075&v_f=2&v_p=18&from=1051195010&"
    			+ "gsid=" + gsid
    			+ "&imsi=460020364311738&lang=zh_CN&lfid=1001000002_1582&"
    			+ "page="+ page
    			+ "&skin=default&count=20&oldwm=14010_0013&containerid=100803&luicode=10000010";
    	return url;
    }
    
    public static String hotModel(String gsid, int page){
    	String url = "http://mapi.weibo.com/2/cardlist?"
    			+ "gsid=" + gsid
    			+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&containerid=101903_-_girl_pic_lst&fid=101903_-_girl_pic_lst&lfid=101903&"
    			+ "page=" + page;
    	return url;
    }
    
    public static String hotModelDetail(String gsid, int page, String extparam) {
		String url = "http://api.weibo.cn/2/cardlist?"
				+ "gsid=4uHYb8fe39JWyZ1DDAPjP7SC21d"
				+ "&wm=3333_2001&i=d5d6f09&b=1&from=1051293010&c=iphone&v_p=18&skin=default&v_f=1&s=a57eef07&lang=zh_CN&ua=iPhone6,1__weibo__5.1.2__iphone__os8.1.2&uicode=10000011&luicode=10000011&count=20&"
				+ "extparam=" + extparam
				+ "&containerid=101903_-_girl_pic_dtl&fid=101903_-_girl_pic_dtl&lfid=101903_-_girl_pic_lst&sourcetype=page&"
				+ "page=" + page;
		return url;
	}
}
