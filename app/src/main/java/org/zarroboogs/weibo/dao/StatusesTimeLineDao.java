
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;
import org.zarroboogs.weibo.support.utils.TimeUtility;

import java.util.HashMap;
import java.util.Map;

public class StatusesTimeLineDao {

    public MessageListBean getGSONMsgList() throws WeiboException {

        String url = WeiBoURLs.STATUSES_TIMELINE_BY_ID;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("uid", uid);
        map.put("since_id", since_id);
        map.put("max_id", max_id);
        map.put("count", count);
        map.put("page", page);
        map.put("screen_name", screen_name);
        map.put("base_app", base_app);
        map.put("feature", feature);
        map.put("trim_user", trim_user);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        MessageListBean value = null;
        try {
            value = gson.fromJson(jsonData, MessageListBean.class);
        } catch (JsonSyntaxException e) {

            AppLoggerUtils.e(e.getMessage());
        }

        if (value != null && value.getSize() > 0) {
            for (MessageBean b : value.getItemList()) {
                TimeUtility.dealMills(b);
                TimeLineUtility.addJustHighLightLinks(b);

            }
        }

        return value;
    }

    public StatusesTimeLineDao(String token, String uid) {
        this.access_token = token;
        this.uid = uid;
        this.count = SettingUtils.getMsgCount();
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public void setSince_id(String since_id) {
        this.since_id = since_id;
    }

    public void setMax_id(String max_id) {
        this.max_id = max_id;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setBase_app(String base_app) {
        this.base_app = base_app;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setTrim_user(String trim_user) {
        this.trim_user = trim_user;
    }

    private String access_token;
    private String uid;
    private String screen_name;
    private String since_id;
    private String max_id;
    private String count;
    private String page;
    private String base_app;
    private String feature;
    private String trim_user;
}
