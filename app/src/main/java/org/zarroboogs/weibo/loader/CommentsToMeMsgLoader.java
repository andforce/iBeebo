
package org.zarroboogs.weibo.loader;

import android.content.Context;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.bean.CommentListBean;
import org.zarroboogs.weibo.dao.MainCommentsTimeLineDao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class CommentsToMeMsgLoader extends AbstractAsyncNetRequestTaskLoader<CommentListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String sinceId;
    private String maxId;
    private String accountId;

    public CommentsToMeMsgLoader(Context context, String accountId, String token, String sinceId, String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.accountId = accountId;
    }

    public CommentListBean loadData() throws WeiboException {
        MainCommentsTimeLineDao dao = new MainCommentsTimeLineDao(token);
        dao.setSince_id(sinceId);
        dao.setMax_id(maxId);
        CommentListBean result = null;
        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }

        return result;
    }

}
