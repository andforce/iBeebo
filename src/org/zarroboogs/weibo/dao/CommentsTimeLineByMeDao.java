
package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.bean.CommentListBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.TimeUtility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: qii Date: 12-9-12
 */
public class CommentsTimeLineByMeDao implements ICommentsTimeLineDao {

    public void setSince_id(String since_id) {
        this.since_id = since_id;
    }

    public void setMax_id(String max_id) {
        this.max_id = max_id;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setPage(String page) {
        this.page = page;
    }

    private String access_token;
    private String since_id;
    private String max_id;
    private String count;
    private String page;
    private String filter_by_source;

    public CommentsTimeLineByMeDao(String access_token) {

        this.access_token = access_token;
        this.count = SettingUtils.getMsgCount();
    }

    public CommentListBean getGSONMsgList() throws WeiboException {

        String url = WeiBoURLs.COMMENTS_BY_ME_TIMELINE;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("since_id", since_id);
        map.put("max_id", max_id);
        map.put("count", count);
        map.put("page", page);
        map.put("filter_by_source", filter_by_source);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        CommentListBean value = null;
        try {
            value = gson.fromJson(jsonData, CommentListBean.class);
        } catch (JsonSyntaxException e) {
            AppLoggerUtils.e(e.getMessage());
        }

        if (value != null && value.getSize() > 0) {
            List<CommentBean> msgList = value.getItemList();
            Iterator<CommentBean> iterator = msgList.iterator();
            while (iterator.hasNext()) {

                CommentBean msg = iterator.next();
                if (msg.getUser() == null) {
                    iterator.remove();
                } else {
                    msg.getListViewSpannableString();
                    TimeUtility.dealMills(msg);
                }
            }

        }
        return value;
    }
}
