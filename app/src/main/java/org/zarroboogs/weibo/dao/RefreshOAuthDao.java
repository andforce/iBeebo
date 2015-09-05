
package org.zarroboogs.weibo.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.utils.WeiboOAuthConstances;

import java.util.HashMap;
import java.util.Map;

public class RefreshOAuthDao {

    public String refreshToken() throws WeiboException {
        String url = WeiBoURLs.OAUTH2_ACCESS_TOKEN;
        Map<String, String> map = new HashMap<String, String>();
        map.put("code", code);
        map.put("redirect_uri", redirect_uri);
        map.put("client_id", client_id);
        map.put("client_secret", client_secret);
        map.put("grant_type", grant_type);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

        if ((jsonData != null) && (jsonData.contains("{"))) {
            try {
                JSONObject localJSONObject = new JSONObject(jsonData);
                return localJSONObject.optString("access_token");
            } catch (JSONException localJSONException) {

            }

        }
        return "";

    }

    public RefreshOAuthDao(String code) {
        this.code = code;
    }

    private String code;
    private String redirect_uri = WeiboOAuthConstances.DIRECT_URL;
    private String client_id = WeiboOAuthConstances.APP_KEY;
    private String client_secret = WeiboOAuthConstances.APP_SECRET;
    private String grant_type = "authorization_code";

}
