
package org.zarroboogs.weibo.loader;

import android.content.Context;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.dao.SearchDao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SearchUserLoader extends AbstractAsyncNetRequestTaskLoader<UserListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String searchWord;
    private String page;

    public SearchUserLoader(Context context, String token, String searchWord, String page) {
        super(context);
        this.token = token;
        this.searchWord = searchWord;
        this.page = page;
    }

    public UserListBean loadData() throws WeiboException {
        SearchDao dao = new SearchDao(token, searchWord);
        dao.setPage(page);

        UserListBean result = null;
        lock.lock();

        try {
            result = dao.getUserList();
        } finally {
            lock.unlock();
        }

        return result;
    }

}
