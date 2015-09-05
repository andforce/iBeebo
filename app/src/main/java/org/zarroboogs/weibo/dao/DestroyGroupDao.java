
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

public class DestroyGroupDao {

    public boolean destroy() throws WeiboException {

        String url = WeiBoURLs.GROUP_DESTROY;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("list_id", list_id);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

        Gson gson = new Gson();

        Result value = null;
        try {
            value = gson.fromJson(jsonData, Result.class);
        } catch (JsonSyntaxException e) {
            AppLoggerUtils.e(e.getMessage());
        }

        return (value != null);

    }

    /**
     * http://open.weibo.com/wiki/2/friendships/groups/destroy suggest use idstr
     */

    public DestroyGroupDao(String token, String list_id) {
        this.access_token = token;
        this.list_id = list_id;
    }

    private String access_token;
    private String list_id;

    private class Result {
        String id;
        String idstr;
        String name;
    }
}
