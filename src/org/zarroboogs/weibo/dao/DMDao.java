
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.data.DMUserBean;
import org.zarroboogs.weibo.bean.data.DMUserListBean;
import org.zarroboogs.weibo.setting.SettingUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 12-11-14
 */
public class DMDao {

    private String access_token;
    private String cursor = "0";
    private String count;

    public DMDao(String token) {
        this.access_token = token;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
        this.count = SettingUtils.getMsgCount();
    }

    public DMUserListBean getUserList() throws WeiboException {
        String url = WeiBoURLs.DM_USERLIST;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("count", count);
        map.put("cursor", cursor);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
        DMUserListBean value = null;
        try {
            value = new Gson().fromJson(jsonData, DMUserListBean.class);
            for (DMUserBean b : value.getItemList()) {
                b.getListViewSpannableString();
                b.getListviewItemShowTime();
            }
        } catch (JsonSyntaxException e) {

            AppLoggerUtils.e(e.getMessage());

        }
        return value;
    }

}
