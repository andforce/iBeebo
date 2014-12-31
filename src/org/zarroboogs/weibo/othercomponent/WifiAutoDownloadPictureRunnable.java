
package org.zarroboogs.weibo.othercomponent;

import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.ImageUtility;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.utils.file.FileManager;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.support.asyncdrawable.TaskCache;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.VelocityListView;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: qii Date: 13-12-29
 */
public class WifiAutoDownloadPictureRunnable implements Runnable {

    private MessageListBean msgList;

    private static HashMap<String, Boolean> result = new HashMap<String, Boolean>();

    public WifiAutoDownloadPictureRunnable(MessageListBean source, int position, int listViewScrollOrientation) {
        this.msgList = new MessageListBean();
        switch (listViewScrollOrientation) {
            case VelocityListView.TOWARDS_BOTTOM:
                for (int i = position; i < source.getSize(); i++) {
                    MessageBean msg = source.getItem(i);
                    if (msg != null) {
                        this.msgList.getItemList().add(msg);
                    }
                }
                break;
            case VelocityListView.TOWARDS_TOP:
                for (int i = position; i >= 0; i--) {
                    MessageBean msg = source.getItem(i);
                    if (msg != null) {
                        this.msgList.getItemList().add(msg);
                    }
                }
                break;
        }

        AppLoggerUtils.i("WifiAutoDownloadPictureRunnable new Runnable");
    }

    @Override
    public void run() {

        for (MessageBean msg : this.msgList.getItemList()) {
            if (!continueHandleMessage(msg)) {
                return;
            }
        }
    }

    private boolean continueHandleMessage(MessageBean msg) {

        if (Thread.currentThread().isInterrupted()) {
            return false;
        }

        if (!Utility.isWifi(GlobalContext.getInstance())) {
            return false;
        }

        if (msg == null) {
            return true;
        }

        Boolean done = result.get(msg.getId());
        if (done != null && done) {
            AppLoggerUtils.i("already done skipped");
            return true;
        }

        // wait until other download tasks are finished
        synchronized (TaskCache.backgroundWifiDownloadPicturesWorkLock) {
            while (!TaskCache.isDownloadTaskFinished() && !Thread.currentThread().isInterrupted()) {
                try {
                    AppLoggerUtils.i("WifiAutoDownloadPictureRunnable wait for lock");
                    TaskCache.backgroundWifiDownloadPicturesWorkLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        AppLoggerUtils.i("WifiAutoDownloadPictureRunnable" + msg.getId() + "start");
        startDownload(msg);
        AppLoggerUtils.i("WifiAutoDownloadPictureRunnable" + msg.getId() + "finished");

        result.put(msg.getId(), true);
        return true;
    }

    private void startDownload(MessageBean msg) {

        if (!msg.isMultiPics()) {
            String url = msg.getOriginal_pic();
            if (!TextUtils.isEmpty(url)) {
                downloadPic(url);
            }
        } else {
            ArrayList<String> urls = msg.getHighPicUrls();
            for (String url : urls) {
                downloadPic(url);
            }
        }

        MessageBean reTweetedMsg = msg.getRetweeted_status();

        if (reTweetedMsg != null) {
            if (!reTweetedMsg.isMultiPics()) {
                String url = reTweetedMsg.getOriginal_pic();
                if (!TextUtils.isEmpty(url)) {
                    downloadPic(url);
                }
            } else {
                ArrayList<String> urls = reTweetedMsg.getHighPicUrls();
                for (String url : urls) {
                    downloadPic(url);
                }
            }
        }

        UserBean user = msg.getUser();
        if (user != null) {
            downloadAvatar(user.getAvatar_large());
        }
    }

    private void downloadPic(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String path = FileManager.getFilePathFromUrl(url, FileLocationMethod.picture_large);
        if (ImageUtility.isThisBitmapCanRead(path) && TaskCache.isThisUrlTaskFinished(url)) {
            return;
        }
        TaskCache.waitForPictureDownload(url, null, path, FileLocationMethod.picture_large);

    }

    private void downloadAvatar(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String path = FileManager.getFilePathFromUrl(url, FileLocationMethod.avatar_large);
        if (ImageUtility.isThisBitmapCanRead(path) && TaskCache.isThisUrlTaskFinished(url)) {
            return;
        }
        TaskCache.waitForPictureDownload(url, null, path, FileLocationMethod.avatar_large);

    }

}
