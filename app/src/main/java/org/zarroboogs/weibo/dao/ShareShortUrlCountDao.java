
package org.zarroboogs.weibo.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

public class ShareShortUrlCountDao {
    private String token;
    private String shortUrl;

    public ShareShortUrlCountDao(String token, String shortUrl) {
        this.token = token;
        this.shortUrl = shortUrl;
    }

    public int getCount() throws WeiboException {
        String url = WeiBoURLs.SHORT_URL_SHARE_COUNT;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", token);
        map.put("url_short", shortUrl);

        String json = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("urls");
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            return jsonObject1.optInt("share_counts", 0);
        } catch (JSONException e) {
            AppLoggerUtils.e(e.getMessage());
        }
        return 0;
    }
}
