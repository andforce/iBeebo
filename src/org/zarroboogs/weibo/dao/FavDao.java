package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.FavBean;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 12-8-22
 */
public class FavDao {
	private String access_token;
	private String id;

	public FavDao(String token, String id) {
		this.access_token = token;
		this.id = id;
	}

	public FavBean favIt() throws WeiboException {

		String url = WeiBoURLs.FAV_CREATE;
		return executeTask(url);
	}

	public FavBean unFavIt() throws WeiboException {

		String url = WeiBoURLs.FAV_DESTROY;
		return executeTask(url);

	}

	private FavBean executeTask(String url) throws WeiboException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);
		map.put("id", id);

		String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

		try {
			FavBean value = new Gson().fromJson(jsonData, FavBean.class);
			if (value != null)
				return value;
		} catch (JsonSyntaxException e) {
			AppLoggerUtils.e(e.getMessage());
		}

		return null;
	}
}
