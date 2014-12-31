package org.zarroboogs.weibo.dao;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.bean.CommentListBean;

/**
 * User: qii Date: 12-12-16
 */
public interface ICommentsTimeLineDao {
	public CommentListBean getGSONMsgList() throws WeiboException;

	public void setSince_id(String since_id);

	public void setMax_id(String max_id);
}
