
package org.zarroboogs.weibo.loader;

import android.content.Context;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.dao.MentionsWeiboTimeLineDao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MentionsWeiboMsgLoader extends AbstractAsyncNetRequestTaskLoader<MessageListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String sinceId;
    private String maxId;
    private String accountId;

    public MentionsWeiboMsgLoader(Context context, String accountId, String token, String sinceId, String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.accountId = accountId;
    }

    public MessageListBean loadData() throws WeiboException {
        MentionsWeiboTimeLineDao dao = new MentionsWeiboTimeLineDao(token);
        dao.setSince_id(sinceId);
        dao.setMax_id(maxId);
        MessageListBean result = null;
        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }

        return result;
    }

}
