
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.GroupBean;

import java.util.HashMap;
import java.util.Map;

/**
 * http://open.weibo.com/wiki/2/friendships/groups/update
 */
public class UpdateGroupNameDao {

    public GroupBean update() throws WeiboException {

        String url = WeiBoURLs.GROUP_UPDATE;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("name", name);
        map.put("list_id", list_id);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

        Gson gson = new Gson();

        GroupBean value = null;
        try {
            value = gson.fromJson(jsonData, GroupBean.class);
        } catch (JsonSyntaxException e) {
            AppLoggerUtils.e(e.getMessage());
        }

        return value;
    }

    public UpdateGroupNameDao(String token, String list_id, String name) {
        this.access_token = token;
        this.name = name;
        this.list_id = list_id;
    }

    private String access_token;
    private String name;
    private String list_id;

}
