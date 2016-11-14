
package org.zarroboogs.weibo.othercomponent;

import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.service.FetchNewMsgService;
import org.zarroboogs.weibo.service.KeepCookieService;
import org.zarroboogs.weibo.setting.SettingUtils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BeeboAlermManager {

    public static final boolean DEBUG = false;

    private static final int REQUEST_CODE = 195;

    public static void startAlarm(boolean debug, Context context, boolean silent) {

        String value = SettingUtils.getFrequency();

        long time = AlarmManager.INTERVAL_DAY;

        if (value.equals("1")) {
            time = (1000 * 30);
        }
        if (value.equals("2")) {
            time = (1000 * 60 * 2);
        }
        if (value.equals("3")) {
            time = (1000 * 60 * 5);
        }

        if (DEBUG) {
            time = 1000 * 5;// 5秒
        }

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, FetchNewMsgService.class);
        PendingIntent sender = PendingIntent.getService(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, time, sender);
        if (!silent) {
            Toast.makeText(context, context.getString(R.string.start_fetch_msg), Toast.LENGTH_SHORT).show();
        }
    }

    public static void keepCookie(Context context, String cookie) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, KeepCookieService.class);
        intent.putExtra(KeepCookieService.COOKIE_KEEP, cookie);
        PendingIntent sender = PendingIntent.getService(context, KeepCookieService.KeepCookie, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, 1000 * 60 * 10, sender);
    }

    public static void stopAlarm(Context context, boolean clearNotification) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, FetchNewMsgService.class);
        PendingIntent sender = PendingIntent.getService(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm.cancel(sender);
        if (clearNotification) {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Long.valueOf(BeeboApplication.getInstance().getCurrentAccountId()).intValue());
        }
    }
}
