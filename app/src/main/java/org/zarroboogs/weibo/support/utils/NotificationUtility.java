
package org.zarroboogs.weibo.support.utils;

import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.CommentListBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.bean.UnreadBean;
import org.zarroboogs.weibo.setting.SettingUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationUtility {

    private NotificationUtility() {
        // Forbidden being instantiated.
    }

    public static int getCount(UnreadBean unreadBean) {
        int count = 0;

        if (SettingUtils.allowMentionToMe()) {
            count += unreadBean.getMention_status();
        }

        if (SettingUtils.allowCommentToMe()) {
            count += unreadBean.getCmt();
        }

        if (SettingUtils.allowMentionCommentToMe()) {
            count += unreadBean.getMention_cmt();
        }

        return count;

    }

    @Deprecated
    public static String getTicker(UnreadBean unreadBean, MessageListBean mentionsWeibo, CommentListBean mentionsComment,
            CommentListBean commentsToMe) {
        int unreadMentionCmt = unreadBean.getMention_cmt();
        int unreadMentionStatus = unreadBean.getMention_status();
        int mention = 0;
        if (SettingUtils.allowMentionToMe() && unreadMentionStatus > 0 && mentionsWeibo != null) {
            int actualFetchedSize = mentionsWeibo.getSize();
            if (actualFetchedSize < Integer.valueOf(SettingUtils.getMsgCount())) {
                mention += actualFetchedSize;
            } else {
                mention += Math.max(actualFetchedSize, unreadMentionStatus);
            }
        }
        if (SettingUtils.allowMentionCommentToMe() && unreadMentionCmt > 0 && mentionsComment != null) {
            int actualFetchedSize = mentionsComment.getSize();
            if (actualFetchedSize < Integer.valueOf(SettingUtils.getMsgCount())) {
                mention += actualFetchedSize;
            } else {
                mention += Math.max(actualFetchedSize, unreadMentionCmt);

            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (mention > 0) {
            String txt = String
                    .format(BeeboApplication.getInstance().getString(R.string.new_mentions), String.valueOf(mention));
            stringBuilder.append(txt);
        }

        int unreadCmt = unreadBean.getCmt();

        int cmt = 0;

        if (SettingUtils.allowCommentToMe() && unreadCmt > 0 && commentsToMe != null) {
            //
            int actualFetchedSize = commentsToMe.getSize();
            if (actualFetchedSize < Integer.valueOf(SettingUtils.getMsgCount())) {
                cmt += actualFetchedSize;
            } else {
                cmt += Math.max(actualFetchedSize, unreadCmt);
            }

            if (mention > 0) {
                stringBuilder.append("、");
            }

            if (cmt > 0) {
                String txt = String
                        .format(BeeboApplication.getInstance().getString(R.string.new_comments), String.valueOf(cmt));
                stringBuilder.append(txt);
            }

        }
        return stringBuilder.toString();
    }

    @Deprecated
    public static String getTicker(UnreadBean unreadBean) {
        int unreadMentionCmt = unreadBean.getMention_cmt();
        int unreadMentionStatus = unreadBean.getMention_status();
        int unreadCmt = unreadBean.getCmt();

        int messageCount = unreadMentionCmt + unreadMentionStatus + unreadCmt;

        String txt = String.format(BeeboApplication.getInstance().getString(R.string.new_unread_messages),
                String.valueOf(messageCount));

        return txt;
    }

    public static void show(Notification notification, int id) {
        NotificationManager notificationManager = (NotificationManager) BeeboApplication.getInstance().getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    public static void cancel(int id) {
        NotificationManager notificationManager = (NotificationManager) BeeboApplication.getInstance().getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

}
