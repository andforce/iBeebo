
package org.zarroboogs.weibo.loader;

import android.content.Context;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.dao.FriendListDao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FriendUserLoader extends AbstractAsyncNetRequestTaskLoader<UserListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String uid;
    private String page;
    private String cursor;

    public FriendUserLoader(Context context, String token, String uid, String cursor) {
        super(context);
        this.token = token;
        this.uid = uid;
        this.cursor = cursor;
    }

    public UserListBean loadData() throws WeiboException {
        FriendListDao dao = new FriendListDao(token, uid);
        dao.setCursor(cursor);

        UserListBean result = null;
        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }

        return result;
    }

}
