
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.UnreadBean;

import java.util.HashMap;
import java.util.Map;

public class UnreadDao {

    protected String getUrl() {
        return WeiBoURLs.UNREAD_COUNT;
    }

    private String getMsgListJson() throws WeiboException {
        String url = getUrl();

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("uid", uid);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        return jsonData;
    }

    public UnreadBean getCount() throws WeiboException {

        String json = getMsgListJson();
        Gson gson = new Gson();

        UnreadBean value = null;
        try {
            value = gson.fromJson(json, UnreadBean.class);
        } catch (JsonSyntaxException e) {

            AppLoggerUtils.e(e.getMessage());
            return null;
        }

        return value;
    }

    private String access_token;
    private String uid;

    public UnreadDao(String access_token, String uid) {

        this.access_token = access_token;
        this.uid = uid;
    }

}
