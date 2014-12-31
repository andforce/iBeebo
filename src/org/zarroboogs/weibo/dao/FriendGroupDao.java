
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.GroupListBean;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 12-10-17
 */
public class FriendGroupDao {

    public GroupListBean getGroup() throws WeiboException {

        String url = WeiBoURLs.FRIENDSGROUP_INFO;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        GroupListBean value = null;
        try {
            value = gson.fromJson(jsonData, GroupListBean.class);
        } catch (JsonSyntaxException e) {
            AppLoggerUtils.e(e.getMessage());
        }

        return value;
    }

    public FriendGroupDao(String token) {
        this.access_token = token;
    }

    private String access_token;
}
