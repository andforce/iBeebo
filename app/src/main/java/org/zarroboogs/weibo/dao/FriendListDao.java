
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.setting.SettingUtils;

import java.util.HashMap;
import java.util.Map;

public class FriendListDao {

    private String access_token;
    private String uid;
    private String screen_name;
    private String count;
    private String cursor;
    private String trim_status;
    
    public FriendListDao(String token, String uid) {
        this.access_token = token;
        this.uid = uid;
        this.count = SettingUtils.getMsgCount();
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public void setTrim_status(String trim_status) {
        this.trim_status = trim_status;
    }


    
    public UserListBean getGSONMsgList() throws WeiboException {

        String url = WeiBoURLs.FRIENDS_LIST_BYID;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("uid", uid);
        map.put("cursor", cursor);
        map.put("trim_status", trim_status);
        map.put("count", count);
        map.put("screen_name", screen_name);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        UserListBean value = null;
        try {
            value = gson.fromJson(jsonData, UserListBean.class);
        } catch (JsonSyntaxException e) {
            AppLoggerUtils.e(e.getMessage());
        }

        return value;
    }


}
