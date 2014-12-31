
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.bean.data.SearchStatusListBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.TimeUtility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: qii Date: 12-9-8
 */
public class SearchDao {
    public UserListBean getUserList() throws WeiboException {

        String url = WeiBoURLs.USERS_SEARCH;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("count", count);
        map.put("page", page);
        map.put("q", q);

        String jsonData = null;

        jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        UserListBean value = null;
        try {
            value = gson.fromJson(jsonData, UserListBean.class);
        } catch (JsonSyntaxException e) {

            AppLoggerUtils.e(e.getMessage());
        }

        return value;
    }

    public SearchStatusListBean getStatusList() throws WeiboException {

        String url = WeiBoURLs.STATUSES_SEARCH;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("count", count);
        map.put("page", page);
        map.put("q", q);

        String jsonData = null;

        jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        SearchStatusListBean value = null;
        try {
            value = gson.fromJson(jsonData, SearchStatusListBean.class);
            List<MessageBean> list = value.getItemList();

            Iterator<MessageBean> iterator = list.iterator();
            while (iterator.hasNext()) {
                MessageBean msg = iterator.next();
                // message is deleted by sina
                if (msg.getUser() == null) {
                    iterator.remove();
                } else {
                    msg.getListViewSpannableString();
                    TimeUtility.dealMills(msg);
                }
            }
        } catch (JsonSyntaxException e) {

            AppLoggerUtils.e(e.getMessage());
        }

        return value;
    }

    private String access_token;
    private String q;
    private String count;
    private String page;

    public SearchDao(String access_token, String q) {

        this.access_token = access_token;
        this.q = q;
        this.count = SettingUtils.getMsgCount();
    }

    public SearchDao setCount(String count) {
        this.count = count;
        return this;
    }

    public SearchDao setPage(String page) {
        this.page = page;
        return this;
    }

}
