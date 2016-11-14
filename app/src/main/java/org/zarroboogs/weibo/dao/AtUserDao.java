
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.AtUserBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtUserDao {

    private String access_token;
    private String q;
    private String count = "10";
    private String type = "0";
    private String range = "2";

    public List<AtUserBean> getUserInfo() throws WeiboException {
        String url = WeiBoURLs.AT_USER;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("q", q);
        map.put("count", count);
        map.put("type", type);
        map.put("range", range);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        List<AtUserBean> value = null;
        try {
            value = gson.fromJson(jsonData, new TypeToken<List<AtUserBean>>() {
            }.getType());
        } catch (JsonSyntaxException e) {

            AppLoggerUtils.e(e.getMessage());
        }

        return value;

    }

    public AtUserDao(String token, String q) {
        this.access_token = token;
        this.q = q;
    }

}
