package org.zarroboogs.weibo.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 13-12-4
 */
public class Mid2IdDao {

	private String token;

	private String mid;

	public Mid2IdDao(String token, String mid) {
		this.token = token;
		this.mid = mid;
	}

	public String getId() throws WeiboException {
		String url = WeiBoURLs.MID_TO_ID;
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", token);
		map.put("mid", mid);
		map.put("type", "1");
		map.put("isBase62", "1");

		String json = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.optString("id", "0");
		} catch (JSONException e) {
			AppLoggerUtils.e(e.getMessage());
		}
		return "0";
	}

}
