package org.zarroboogs.weibo.widget.galleryview;


import java.io.File;
import java.util.Random;

import org.zarroboogs.weibo.activity.ToolBarAppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

public abstract class AbstractViewPagerActivity extends ToolBarAppCompatActivity {

    public static final int START_PROGRESS = 0x30001;
    public static final int PLUS_PROGRESS = 0x30002;
    public static final int END_PROGRESS = 0x30003;
    public static final int HIDE_PROGRESS_BAR = 0x30004;
    public static final int NOTIFYDATASETCHANGED = 0x40001;
    private ProgressBar mProgressBar;
    protected boolean downloadFinish = false; // 是否已经下载完毕

    private Random random = new Random();

    public void showPinWheelDialog() {
    }

    Handler updateProgressBar = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case START_PROGRESS: {
                    if (downloadFinish) {
                        mProgressBar.setProgress(100);
                    } else {
                        mProgressBar.setProgress(random.nextInt(5));
                        this.sendEmptyMessageDelayed(PLUS_PROGRESS, 200);
                    }
                    break;
                }
                case PLUS_PROGRESS: {
                    if (downloadFinish) {
                        mProgressBar.setProgress(100);
                    } else {
                        mProgressBar.setProgress(mProgressBar.getProgress() + random.nextInt(5));
                        if (mProgressBar.getProgress() < 80) {
                            this.sendEmptyMessageDelayed(PLUS_PROGRESS, 200);
                        }
                    }

                    break;
                }
                case END_PROGRESS: {
                    this.removeMessages(PLUS_PROGRESS);
                    this.removeMessages(START_PROGRESS);
                    mProgressBar.setProgress(100);
                    this.sendEmptyMessageDelayed(HIDE_PROGRESS_BAR, 300);

                    break;
                }

                case HIDE_PROGRESS_BAR: {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mProgressBar.setProgress(0);
                    break;
                }
                default:
                    break;
            }
        }

    };

    protected void updateProgressBar(ProgressBar mProgressBar) {
        downloadFinish = false;
        this.mProgressBar = mProgressBar;
        this.mProgressBar.setVisibility(View.VISIBLE);
        updateProgressBar.sendEmptyMessage(START_PROGRESS);
    }

    protected void stopProgressBar(ProgressBar mProgressBar) {
        updateProgressBar.sendEmptyMessage(END_PROGRESS);
        /*
         * this.mProgressBar = mProgressBar; this.mProgressBar.setProgress(0);
         * this.mProgressBar.setVisibility(View.INVISIBLE);
         */
    }

    public void hidePinWheelDialog() {
    }

    public void disableView(View view) {
        if (view != null) {
            view.setEnabled(false);
            view.setClickable(false);
            view.setAlpha(0.3f);
        }
    }

    public void enableView(View view) {
        if (view != null) {
            view.setEnabled(true);
            view.setClickable(true);
            view.setAlpha(1.0f);
        }
    }

    /**
     * Description: Date:Jun 14, 2013
     *
     * @param file
     * @return
     * @author WangDiYuan
     */
    public boolean isFileExists(File file) {
        if (file != null) {
            return file.exists();
        }
        return false;
    }

    /**
     * Description: Date:Jun 14, 2013
     *
     * @param filePath
     * @return
     * @author WangDiYuan
     */
    public boolean isFileExists(String filePath) {
        if (filePath != null) {
            File mFile = new File(filePath);
            return mFile.exists();
        }
        return false;
    }

    public abstract File getLargeFile(String filePath);

    public abstract File getMiddleFile(String filePath);

    public abstract File getSmallFile(String filePath);

    public abstract String getLargeFilePath();

    public abstract String getMiddleFilePath();

    public abstract String getSmallFilePath();

}
