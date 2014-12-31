
package org.zarroboogs.weibo.dao;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.WeiBoURLs;

/**
 * User: qii Date: 12-10-21
 */
public class MentionsCommentTimeLineDao extends MainCommentsTimeLineDao {
    public MentionsCommentTimeLineDao(String access_token) {
        super(access_token);
    }

    @Override
    protected String getUrl() {
        return WeiBoURLs.COMMENTS_MENTIONS_TIMELINE;
    }

    protected void clearUnread() {
        try {
            new ClearUnreadDao(access_token, ClearUnreadDao.MENTION_CMT).clearUnread();
        } catch (WeiboException ignored) {

        }
    }
}
