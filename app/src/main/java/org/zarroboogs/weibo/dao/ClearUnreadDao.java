
package org.zarroboogs.weibo.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.UnreadBean;

import java.util.HashMap;
import java.util.Map;

public class ClearUnreadDao {

    public static final String STATUS = "app_message";
    public static final String CMT = "cmt";
    public static final String MENTION_STATUS = "mention_status";
    public static final String MENTION_CMT = "mention_cmt";

    private String access_token;
    private String type;

    public ClearUnreadDao(String access_token) {

        this.access_token = access_token;
    }

    public ClearUnreadDao(String access_token, String type) {

        this.access_token = access_token;
        this.type = type;
    }

    protected String getUrl() {
        return WeiBoURLs.UNREAD_CLEAR;
    }

    public boolean clearUnread() throws WeiboException {

        String url = getUrl();

        Map<String, String> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("type", type);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return jsonObject.optBoolean("result", false);
        } catch (JSONException e) {
            AppLoggerUtils.e(e.getMessage());
        }

        return false;
    }

    /**
     * first check server unread status,if unread count is the same,reset unread count
     */
    public boolean clearMentionStatusUnread(UnreadBean unreadBean, String accountId) throws WeiboException {
        int count = unreadBean.getMention_status();
        UnreadBean currentCount = new UnreadDao(access_token, accountId).getCount();
        if (currentCount == null) {
            return false;
        }
        // already reset or have new unread message
        return count == currentCount.getMention_status() && new ClearUnreadDao(access_token, ClearUnreadDao.MENTION_STATUS).clearUnread();
    }

    public boolean clearMentionCommentUnread(UnreadBean unreadBean, String accountId) throws WeiboException {
        int count = unreadBean.getMention_cmt();
        UnreadBean currentCount = new UnreadDao(access_token, accountId).getCount();
        if (currentCount == null) {
            return false;
        }
        // already reset or have new unread message
        return count == currentCount.getMention_cmt() && new ClearUnreadDao(access_token, ClearUnreadDao.MENTION_CMT).clearUnread();
    }

    public boolean clearCommentUnread(UnreadBean unreadBean, String accountId) throws WeiboException {
        int count = unreadBean.getCmt();
        UnreadBean currentCount = new UnreadDao(access_token, accountId).getCount();
        if (currentCount == null) {
            return false;
        }
        // already reset or have new unread message
        return count == currentCount.getCmt() && new ClearUnreadDao(access_token, ClearUnreadDao.CMT).clearUnread();
    }



}
