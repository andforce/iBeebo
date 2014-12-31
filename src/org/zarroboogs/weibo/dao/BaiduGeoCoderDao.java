package org.zarroboogs.weibo.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.WeiBoURLs;

/**
 * doc: http://developer.baidu.com/map/webservice-geocoding.htm
 */
public class BaiduGeoCoderDao {

	public String get() throws WeiboException {

		final String url = String.format(WeiBoURLs.BAIDU_GEO_CODER_MAP, lat, long_fix);

		String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, null);

		try {

			final JSONObject json = new JSONObject(jsonData);
			final JSONObject result = json.getJSONObject("result");
			final String formatAddress = result.optString("formatted_address", null);
			return formatAddress;
		} catch (JSONException exception) {
			return null;
		}

	}

	public BaiduGeoCoderDao(double lat, double long_fix) {
		this.lat = (float) lat;
		this.long_fix = (float) long_fix;
	}

	private float lat;

	private float long_fix;

}
