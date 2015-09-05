
package org.zarroboogs.weibo.support.lib;

import org.zarroboogs.utils.AppLoggerUtils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LogOnExceptionScheduledExecutor extends ScheduledThreadPoolExecutor {

    public LogOnExceptionScheduledExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(wrapRunnable(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(wrapRunnable(command), initialDelay, delay, unit);
    }

    private Runnable wrapRunnable(Runnable command) {
        return new LogOnExceptionRunnable(command);
    }

    private class LogOnExceptionRunnable implements Runnable {

        private Runnable runnable;

        public LogOnExceptionRunnable(Runnable runnable) {
            super();
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {

                AppLoggerUtils.e("error in executing: " + runnable + ". It will no longer be run!");
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }
    }

}
