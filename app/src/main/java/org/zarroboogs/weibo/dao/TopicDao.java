
package org.zarroboogs.weibo.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

public class TopicDao {
    private String access_token;

    public TopicDao(String token) {
        this.access_token = token;
    }

    public boolean follow(String trend_name) throws WeiboException {
        String url = WeiBoURLs.TOPIC_FOLLOW;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("trend_name", trend_name);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            Integer id = jsonObject.optInt("topicid", -1);
            return id > 0;
        } catch (JSONException e) {

        }
        return false;

    }

    public boolean destroy(String trend_name) throws WeiboException {
        String url = WeiBoURLs.TOPIC_RELATIONSHIP;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("trend_name", trend_name);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            boolean isFollowing = jsonObject.optBoolean("is_follow", false);
            if (!isFollowing) {
                return false;
            }
            String trend_id = jsonObject.optString("trend_id", "");

            url = WeiBoURLs.TOPIC_DESTROY;
            map = new HashMap<String, String>();
            map.put("access_token", access_token);
            map.put("trend_id", trend_id);

            jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

            try {
                jsonObject = new JSONObject(jsonData);
                return jsonObject.optBoolean("result", false);

            } catch (JSONException e) {

            }
            return false;

        } catch (JSONException e) {

        }
        return false;
    }

}
