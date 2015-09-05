
package org.zarroboogs.weibo.dao;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

public class SendDMDao {

    public boolean send() throws WeiboException {
        String apiUrl = WeiBoURLs.DM_SENT;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("text", text);
        map.put("uid", uid);
        HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, apiUrl, map);

        return true;

    }

    public SendDMDao(String token, String uid, String text) {
        this.access_token = token;
        this.uid = uid;
        this.text = text;
    }

    private String access_token;
    private String text;
    private String uid;
}
