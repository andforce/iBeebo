package org.zarroboogs.utils;

import org.zarroboogs.weibo.auth.BeeboAuthUtils;

public class WeiboOAuthConstances {

    public static final String URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2/authorize";
    
    public static final String SINA_SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,invitation_write,follow_app_official_microblog";
    public static final String APP_KEY = BeeboAuthUtils.getAppKey();
    public static final String APP_SECRET = BeeboAuthUtils.getAppSecret();
    public static final String DIRECT_URL = BeeboAuthUtils.getRedirectUrl();
    public static final String PACKAGE_NAME = "org.zarroboogs.weibo";
    public static final String KEY_HASH = "cff0cb6f08361500eac0082a522ea613";
    
    
    public static final String HACK_SINA_SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,invitation_write";
    public static final String HACK_APP_KEY = BeeboAuthUtils.getHackAppKey();
    public static final String HACK_APP_SECRET = BeeboAuthUtils.getHackAppSecret();
    public static final String HACK_DIRECT_URL = BeeboAuthUtils.getHackRedirectUrl();
    public static final String HACK_PACKAGE_NAME = BeeboAuthUtils.getHackPackageName();
    public static final String HACK_KEY_HASH = BeeboAuthUtils.getHackKeyHash();
    
}
