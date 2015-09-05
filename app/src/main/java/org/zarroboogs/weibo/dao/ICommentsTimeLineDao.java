
package org.zarroboogs.weibo.dao;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.bean.CommentListBean;

public interface ICommentsTimeLineDao {
    CommentListBean getGSONMsgList() throws WeiboException;

    void setSince_id(String since_id);

    void setMax_id(String max_id);
}
