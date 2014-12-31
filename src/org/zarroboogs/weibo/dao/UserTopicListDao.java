package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.setting.SettingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: qii Date: 12-11-18
 */
public class UserTopicListDao {

	private String getMsgListJson() throws WeiboException {
		String url = WeiBoURLs.TOPIC_USER_LIST;

		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);
		map.put("count", count);
		map.put("page", page);
		map.put("uid", uid);

		String jsonData = null;

		jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

		return jsonData;
	}

	public ArrayList<String> getGSONMsgList() throws WeiboException {

		String json = getMsgListJson();
		Gson gson = new Gson();

		ArrayList<TopicBean> value = null;
		try {
			value = gson.fromJson(json, new TypeToken<List<TopicBean>>() {
			}.getType());
		} catch (JsonSyntaxException e) {

			AppLoggerUtils.e(e.getMessage());
		}

		if (value != null) {
			ArrayList<String> msgList = new ArrayList<String>();
			for (TopicBean b : value) {
				msgList.add(b.hotword);
			}
			return msgList;
		}

		return new ArrayList<String>();
	}

	private String access_token;
	private String uid;
	private String count;
	private String page;

	public UserTopicListDao(String access_token, String uid) {

		this.access_token = access_token;
		this.count = SettingUtils.getMsgCount();
		this.uid = uid;
	}

	public UserTopicListDao setCount(String count) {
		this.count = count;
		return this;
	}

	public UserTopicListDao setPage(String page) {
		this.page = page;
		return this;
	}

	private static class TopicBean {
		private String num;
		private String trend_id;
		private String hotword;
	}
}
