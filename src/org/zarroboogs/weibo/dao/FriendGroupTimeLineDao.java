package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 12-10-17
 */
public class FriendGroupTimeLineDao extends MainFriendsTimeLineDao {

	protected String getUrl() {
		return WeiBoURLs.FRIENDSGROUP_TIMELINE;
	}

	private String getMsgListJson() throws WeiboException {
		String url = getUrl();

		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);
		map.put("since_id", since_id);
		map.put("max_id", max_id);
		map.put("count", count);
		map.put("page", page);
		map.put("base_app", base_app);
		map.put("feature", feature);
		map.put("trim_user", trim_user);
		map.put("list_id", list_id);

		String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

		return jsonData;
	}

	public MessageListBean getGSONMsgList() throws WeiboException {

		String json = getMsgListJson();
		Gson gson = new Gson();

		MessageListBean value = null;
		try {
			value = gson.fromJson(json, MessageListBean.class);
		} catch (JsonSyntaxException e) {

			AppLoggerUtils.e(e.getMessage());
			return null;
		}
		if (value != null && value.getItemList().size() > 0) {
			TimeLineUtility.filterMessage(value);

		}

		return value;
	}

	public FriendGroupTimeLineDao(String access_token, String list_id) {

		super(access_token);
		this.list_id = list_id;
	}

	private String list_id;
}
