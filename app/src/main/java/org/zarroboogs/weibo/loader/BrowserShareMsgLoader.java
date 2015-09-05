
package org.zarroboogs.weibo.loader;

import android.content.Context;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.bean.ShareListBean;
import org.zarroboogs.weibo.dao.ShareShortUrlTimeLineDao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BrowserShareMsgLoader extends AbstractAsyncNetRequestTaskLoader<ShareListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String maxId;
    private String url;

    public BrowserShareMsgLoader(Context context, String token, String url, String maxId) {
        super(context);
        this.token = token;
        this.maxId = maxId;
        this.url = url;

    }

    public ShareListBean loadData() throws WeiboException {
        ShareShortUrlTimeLineDao dao = new ShareShortUrlTimeLineDao(token, url);
        dao.setMaxId(maxId);
        ShareListBean result = null;

        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }

        return result;
    }

}
