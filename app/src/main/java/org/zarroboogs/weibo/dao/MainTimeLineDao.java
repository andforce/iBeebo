
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;

import java.util.HashMap;
import java.util.Map;

public class MainTimeLineDao {

    protected String access_token;

    protected String since_id;

    protected String max_id;

    protected String count;

    protected String page;

    protected String base_app;

    protected String feature;

    protected String trim_user;

    public MainTimeLineDao(String access_token) {

        this.access_token = access_token;
        this.count = SettingUtils.getMsgCount();
    }

    protected String getUrl() {
        return WeiBoURLs.FRIENDS_TIMELINE;
    }

    private String getMsgListJson() throws WeiboException {
        String url = getUrl();

        Map<String, String> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("since_id", since_id);
        map.put("max_id", max_id);
        map.put("count", count);
        map.put("page", page);
        map.put("base_app", base_app);
        map.put("feature", feature);
        map.put("trim_user", trim_user);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
        try {
            new ClearUnreadDao(access_token, ClearUnreadDao.STATUS).clearUnread();
        } catch (WeiboException ignored) {

        }
        return jsonData;
    }

    public MessageListBean getGSONMsgList() throws WeiboException {

        String json = getMsgListJson();
        Gson gson = new Gson();

        MessageListBean value = null;
        try {
            value = gson.fromJson(json, MessageListBean.class);
        } catch (JsonSyntaxException e) {

            AppLoggerUtils.e(e.getMessage());
            throw new WeiboException(e.getMessage());
        }
        if (value != null && value.getItemList().size() > 0) {
            TimeLineUtility.filterMessage(value);
            TimeLineUtility.filterHomeTimeLineSinaWeiboAd(value);
        }

        return value;
    }


    public MainTimeLineDao setSince_id(String since_id) {
        this.since_id = since_id;
        return this;
    }

    public MainTimeLineDao setMax_id(String max_id) {
        this.max_id = max_id;
        return this;
    }

    public MainTimeLineDao setCount(String count) {
        this.count = count;
        return this;
    }

    public MainTimeLineDao setPage(String page) {
        this.page = page;
        return this;
    }

    public MainTimeLineDao setBase_app(String base_app) {
        this.base_app = base_app;
        return this;
    }

    public MainTimeLineDao setFeature(String feature) {
        this.feature = feature;
        return this;
    }

    public MainTimeLineDao setTrim_user(String trim_user) {
        this.trim_user = trim_user;
        return this;
    }

}
