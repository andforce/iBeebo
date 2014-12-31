
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.UserBean;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 12-10-12
 */
public class FanDao {

    public FanDao(String token, String uid) {
        this.access_token = token;
        this.uid = uid;
    }

    public UserBean removeFan() throws WeiboException {
        String url = WeiBoURLs.FRIENDSHIPS_FOLLOWERS_DESTROY;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("uid", uid);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);
        try {
            UserBean value = new Gson().fromJson(jsonData, UserBean.class);
            if (value != null) {
                return value;
            }
        } catch (JsonSyntaxException e) {
            AppLoggerUtils.e(e.getMessage());
        }

        return null;
    }

    private String access_token;
    private String uid;
}
