
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.CommentBean;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 12-9-11
 */
public class DestroyCommentDao {
    private String access_token;
    private String cid;

    public DestroyCommentDao(String access_token, String cid) {
        this.access_token = access_token;
        this.cid = cid;
    }

    public boolean destroy() throws WeiboException {
        String url = WeiBoURLs.COMMENT_DESTROY;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("cid", cid);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);
        Gson gson = new Gson();

        try {
            CommentBean value = gson.fromJson(jsonData, CommentBean.class);
        } catch (JsonSyntaxException e) {
            AppLoggerUtils.e(e.getMessage());
            return false;
        }

        return true;

    }
}
