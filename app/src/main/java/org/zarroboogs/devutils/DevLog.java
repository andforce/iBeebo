package org.zarroboogs.devutils;

import android.util.Log;

/**
 * Created by wangdiyuan on 16-2-19.
 */
public class DevLog {
    private static void d(String tag, String msg) {
        if (msg.trim().contains("\n")) {
            String[] lineStrings = msg.split("\n");
            for (String string : lineStrings) {
                Log.d(tag, string);
            }
        } else {
            Log.d(tag, msg);
        }

    }

    public static void printLog(String tag, String msg) {
        if (msg.length() > 3000) {
            Log.d(tag, "\r\n\r\n\r\n\r\n\r\n------------------------------------>>");
            int len = msg.length();
            int devideNumber = len / 3000;
            int j = 0;
            for (int i = 0; i < devideNumber; i++) {
                d(tag, msg.substring(j, j + 3000));
                j += 3000;
            }
            d(tag, msg.substring(j, msg.length()));
            Log.d(tag, "\r\n\r\n\r\n\r\n\r\n<<------------------------------------");
        } else {
            Log.d(tag, "\r\n\r\n\r\n\r\n\r\n<<<------------------------------------>>>");
            d(tag, msg);
        }
    }

    /**
     * description: 打印函数调用关系
     *
     * @param tag
     * @editer
     */
    public static void printStackTraces(boolean isDebug, String tag) {
        if (!isDebug) {
            return;
        }
        Log.d(tag, "stack start ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        java.util.Map<Thread, StackTraceElement[]> ts = Thread.getAllStackTraces();
        StackTraceElement[] ste = ts.get(Thread.currentThread());
        for (StackTraceElement s : ste) {
            Log.d(tag, "" + s);
        }
        Log.d(tag, "stack end------------------------------------------------------------------");
    }
}
