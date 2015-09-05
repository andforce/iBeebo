
package org.zarroboogs.utils.crashmanager;

import android.text.TextUtils;

import org.zarroboogs.utils.file.FileManager;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler previousHandler;

    public ExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        this.previousHandler = handler;
    }

    private String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");// 设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        return df.format(new Date());
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        final Date now = new Date();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        exception.printStackTrace(printWriter);

        try {

            String logDir = FileManager.getLogDir();
            if (TextUtils.isEmpty(logDir)) {
                return;
            }

            String filename = getCurrentTime();
            String path = logDir + File.separator + filename + "_crash.txt";

            BufferedWriter write = new BufferedWriter(new FileWriter(path));
            write.write("Package: " + CrashManagerConstants.APP_PACKAGE + "\n");
            write.write("Version: " + CrashManagerConstants.APP_VERSION + "\n");
            write.write("Android: " + CrashManagerConstants.ANDROID_VERSION + "\n");
            write.write("Manufacturer: " + CrashManagerConstants.PHONE_MANUFACTURER + "\n");
            write.write("Model: " + CrashManagerConstants.PHONE_MODEL + "\n");
            write.write("Date: " + now + "\n");
            write.write("\n");
            write.write(result.toString());
            write.flush();
            write.close();
        } catch (Exception another) {

        } finally {
            previousHandler.uncaughtException(thread, exception);
        }

    }
}
