
package org.zarroboogs.weibo.loader;

import android.content.Context;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.bean.FavListBean;
import org.zarroboogs.weibo.dao.FavListDao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyFavMsgLoader extends AbstractAsyncNetRequestTaskLoader<FavListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String page;

    public MyFavMsgLoader(Context context, String token, String page) {
        super(context);
        this.token = token;
        this.page = page;
    }

    public FavListBean loadData() throws WeiboException {
        FavListDao dao = new FavListDao(token);
        dao.setPage(page);
        FavListBean result = null;
        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }

        return result;
    }

}
