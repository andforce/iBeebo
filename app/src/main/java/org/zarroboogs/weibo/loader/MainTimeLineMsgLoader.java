
package org.zarroboogs.weibo.loader;

import android.content.Context;
import android.text.TextUtils;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.dao.BilateralTimeLineDao;
import org.zarroboogs.weibo.dao.FriendGroupTimeLineDao;
import org.zarroboogs.weibo.dao.MainTimeLineDao;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.Utility;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainTimeLineMsgLoader extends AbstractAsyncNetRequestTaskLoader<MessageListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String sinceId;
    private String maxId;
    private String currentGroupId;

    private final int MAX_RETRY_COUNT; // 1*50+6*49=344 new messages count

    public MainTimeLineMsgLoader(Context context,String token, String groupId, String sinceId, String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.currentGroupId = groupId;
        MAX_RETRY_COUNT = 1;
    }

    public MessageListBean loadData() throws WeiboException {
        MessageListBean result = null;
        MessageListBean tmp = getMessageListBean(token, currentGroupId, sinceId, maxId);
        result = tmp;
        if (isLoadNewData() && Utility.isWifi(getContext()) && SettingUtils.isWifiUnlimitedMsgCount()) {
            int retryCount = 0;
            while (tmp.getReceivedCount() >= Integer.valueOf(SettingUtils.getMsgCount()) && retryCount < MAX_RETRY_COUNT) {
                String tmpMaxId = tmp.getItemList().get(tmp.getItemList().size() - 1).getId();
                tmp = getMessageListBean(token, currentGroupId, sinceId, tmpMaxId);
                result.addOldData(tmp);
                retryCount++;
            }
            if (tmp.getReceivedCount() >= Integer.valueOf(SettingUtils.getMsgCount())) {
                result.getItemList().add(null);
            }
        } else {
            return result;
        }

        return result;
    }

    private boolean isLoadNewData() {
        return !TextUtils.isEmpty(sinceId) && TextUtils.isEmpty(maxId);
    }

    private MessageListBean getMessageListBean(String token, String groupId, String sinceId, String maxId) throws WeiboException {
        MainTimeLineDao dao;
        switch (currentGroupId) {
            case Constants.BILATERAL_GROUP_ID:
                dao = new BilateralTimeLineDao(token);
                break;
            case Constants.ALL_GROUP_ID:
                dao = new MainTimeLineDao(token);
                break;
            default:
                dao = new FriendGroupTimeLineDao(token, currentGroupId);
                break;
        }

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
