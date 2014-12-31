
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.MessageBean;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 12-8-13 Time: 下午10:50
 */
public class RepostNewMsgDao {

    public MessageBean sendNewMsg() throws WeiboException {
        String url = WeiBoURLs.REPOST_CREATE;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("id", id);
        map.put("status", status);
        map.put("is_comment", is_comment);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

        Gson gson = new Gson();

        MessageBean value = null;
        try {
            value = gson.fromJson(jsonData, MessageBean.class);
        } catch (JsonSyntaxException e) {

            AppLoggerUtils.e(e.getMessage());
        }

        return value;

    }

    public RepostNewMsgDao(String token, String id) {

        this.access_token = token;
        this.id = id;
        this.is_comment = DISABLE_COMMENT;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIs_comment(String value) {

        this.is_comment = value;

    }

    private String access_token;
    private String id;
    private String status;
    private String is_comment;

    public static final String DISABLE_COMMENT = "0";
    public static final String ENABLE_COMMENT = "1";
    public static final String ENABLE_ORI_COMMENT = "2";
    public static final String ENABLE_COMMENT_ALL = "3";
}
